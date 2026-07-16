#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

CONTRACT_DIR="${TMPDIR:-/tmp}/molineros-reclamos-contracts"
JAVA_ENCODING="UTF-8"

fail() {
  printf 'ERROR: %s\n' "$1" >&2
  exit 1
}

info() {
  printf 'OK: %s\n' "$1"
}

require_file() {
  [[ -f "$1" ]] || fail "Falta el archivo requerido: $1"
}

require_command() {
  command -v "$1" >/dev/null 2>&1 || fail "Falta el comando requerido: $1"
}

require_command javac
require_command java
require_command grep

CONTRACTS=(
  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java
  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalEditorContractTest.java
  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalTabGuardContractTest.java
  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalDraftScopeContractTest.java
  ext-impl/src/ar/com/ospim/test/ReclamoAppMobileSyncContractTest.java
  ext-impl/src/ar/com/ospim/test/ReclamoAppMobileOutboxContractTest.java
  ext-impl/src/ar/com/ospim/test/ReclamoAppMobileHttpTimeoutContractTest.java
)

for contract in "${CONTRACTS[@]}"; do
  require_file "$contract"
done

require_file sql/postgresql/autorizaciones/reclamo_appmobile_outbox.sql
require_file docs/autorizaciones/RECLAMOS_PRESTACIONALES_P0_DEPLOY.md
require_file docs/autorizaciones/RECLAMOS_APPMOBILE_OUTBOX_OPERACION.md
require_file ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jsp
require_file ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_tab_guard.js
require_file ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_editor_patch.js
require_file ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_p0_patch.js

rm -rf "$CONTRACT_DIR"
mkdir -p "$CONTRACT_DIR"

javac -encoding "$JAVA_ENCODING" \
  -d "$CONTRACT_DIR" \
  "${CONTRACTS[@]}"

TEST_CLASSES=(
  ar.com.ospim.test.ReclamoPrestacionalP0ContractTest
  ar.com.ospim.test.ReclamoPrestacionalEditorContractTest
  ar.com.ospim.test.ReclamoPrestacionalTabGuardContractTest
  ar.com.ospim.test.ReclamoPrestacionalDraftScopeContractTest
  ar.com.ospim.test.ReclamoAppMobileSyncContractTest
  ar.com.ospim.test.ReclamoAppMobileOutboxContractTest
  ar.com.ospim.test.ReclamoAppMobileHttpTimeoutContractTest
)

for test_class in "${TEST_CLASSES[@]}"; do
  java -cp "$CONTRACT_DIR" "$test_class"
done
info "Contratos textuales compilados y ejecutados"

VIEW_JSP=ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jsp
for asset in \
  view_reclamo.js \
  view_reclamo_tab_guard.js \
  view_reclamo_editor_patch.js \
  view_reclamo_p0_patch.js; do
  grep -q "${asset}?v=20260716-p0-4" "$VIEW_JSP" \
    || fail "Asset ausente o sin versión p0-4: $asset"
done
info "Assets p0-4 conectados"

MIGRATION=sql/postgresql/autorizaciones/reclamo_appmobile_outbox.sql
grep -q 'CREATE TABLE IF NOT EXISTS autorizaciones.reclamo_appmobile_outbox' "$MIGRATION" \
  || fail "La migración no crea la tabla de outbox"
grep -q 'ux_reclamo_appmobile_outbox_pendiente' "$MIGRATION" \
  || fail "Falta el índice único parcial de outbox"
grep -q 'ix_reclamo_appmobile_outbox_proceso' "$MIGRATION" \
  || fail "Falta el índice operativo de outbox"
info "Migración de outbox estructuralmente presente"

AUTH_CLIENT=ext-impl/src/ar/com/ospim/autorizaciones/services/ReclamoAppMobileAuthClient.java
for config_key in \
  APP_HOST_WEBSERVICE \
  APP_BACKOFFICE_API_KEY \
  APP_BACKOFFICE_EMAIL \
  APP_BACKOFFICE_PASSWORD; do
  grep -q "$config_key" "$AUTH_CLIENT" \
    || fail "El cliente seguro no exige $config_key"
done
info "Cliente reparado usa configuración externa"

LEGACY_CLIENT=ext-impl/src/ar/com/ospim/desarrolloAppMobile/beans/ClienteAppMobile.java
if grep -Eq 'private static final String (API_KEY|EMAIL|PASSWORD)[[:space:]]*=[[:space:]]*"[^"$]+' "$LEGACY_CLIENT"; then
  if [[ "${ALLOW_LEGACY_APPMOBILE_SECRETS:-0}" != "1" ]]; then
    fail "ClienteAppMobile legacy todavía contiene credenciales literales. Rotarlas y retirarlas antes de producción. Para una validación técnica no productiva puede usarse ALLOW_LEGACY_APPMOBILE_SECRETS=1."
  fi
  printf 'ADVERTENCIA: se permitió continuar con secretos legacy mediante override.\n' >&2
else
  info "No se detectaron credenciales literales en ClienteAppMobile legacy"
fi

if grep -R -n -E 'async[[:space:]]*:[[:space:]]*false' \
  ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_*patch.js; then
  fail "Se detectó AJAX síncrono en los parches nuevos"
fi
info "Parches nuevos sin AJAX síncrono"

if grep -R -n 'view_reclamo.*p0-2' \
  docs/autorizaciones/RECLAMOS_PRESTACIONALES_P0_DEPLOY.md \
  ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jsp; then
  fail "Persisten referencias operativas a assets p0-2"
fi
info "Documentación y JSP sin versión p0-2 obsoleta"

cat <<'EOF'

VALIDACIÓN AUTOMÁTICA COMPLETADA.

Bloqueos manuales todavía obligatorios:
  1. ejecutar la migración de outbox en la base objetivo;
  2. confirmar las cuatro claves AppMobile en configuración;
  3. compilar el proyecto completo con el entorno Liferay real;
  4. inspeccionar el WAR generado;
  5. ejecutar smoke tests de alta, Compras, edición, revisión, pestañas y baja;
  6. verificar idempotencia de AN en AppMobile;
  7. revisar outbox, logs y estados externos durante 24 horas.
EOF
