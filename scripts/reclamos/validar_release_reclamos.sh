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
require_command node
require_command grep

CONTRACTS=(
  ext-impl/src/ar/com/ospim/test/ClienteAppMobileLegacySecurityContractTest.java
  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java
  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalInitialViewContractTest.java
  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalLegacyFlowContractTest.java
  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP1CleanupContractTest.java
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
require_file docs/autorizaciones/RECLAMOS_PRESTACIONALES_P1_CLEANUP.md
require_file docs/autorizaciones/RECLAMOS_APPMOBILE_OUTBOX_OPERACION.md
require_file ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jsp
require_file ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js
require_file ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_tab_guard.js
require_file ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_editor_patch.js
require_file ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_p0_patch.js

for javascript in \
  ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js \
  ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_tab_guard.js \
  ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_editor_patch.js \
  ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_p0_patch.js; do
  node --check "$javascript"
done
info "JavaScript legacy y parches con sintaxis válida"

rm -rf "$CONTRACT_DIR"
mkdir -p "$CONTRACT_DIR"

javac -encoding "$JAVA_ENCODING" \
  -d "$CONTRACT_DIR" \
  "${CONTRACTS[@]}"

TEST_CLASSES=(
  ar.com.ospim.test.ReclamoPrestacionalInitialViewContractTest
  ar.com.ospim.test.ReclamoPrestacionalLegacyFlowContractTest
  ar.com.ospim.test.ClienteAppMobileLegacySecurityContractTest
  ar.com.ospim.test.ReclamoPrestacionalP0ContractTest
  ar.com.ospim.test.ReclamoPrestacionalP1CleanupContractTest
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
BASE_JS=ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js
P0_JS=ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_p0_patch.js
INITIAL_JS=ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_initial_state.js

grep -q 'view_reclamo.js?v=20260717-legacy-flows-1' "$VIEW_JSP" \
  || fail "El JavaScript legacy no está conectado con versión legacy-flows-1"
grep -q 'view_reclamo_p0_patch.js?v=20260717-legacy-flows-1' "$VIEW_JSP" \
  || fail "El P0 no está conectado con versión legacy-flows-1"
for asset in \
  view_reclamo_tab_guard.js \
  view_reclamo_editor_patch.js; do
  grep -q "${asset}?v=20260717-initial-state-1" "$VIEW_JSP" \
    || fail "Asset legacy ausente o sin versión esperada: $asset"
done
[[ ! -e "$INITIAL_JS" ]] \
  || fail "Persiste la segunda máquina de estado view_reclamo_initial_state.js"
if grep -q -E 'window\.(manejarTipoSector|cambioTipoPedido)' "$P0_JS"; then
  fail "El P0 sigue sobrescribiendo handlers legacy de Tipo Pedido/Sector"
fi
grep -q 'function manejarTipoSector()' "$BASE_JS" \
  || fail "Falta el handler legacy manejarTipoSector"
grep -q "sector == 'FARMACIA' && tipoPedido != 'EXCEPCION'" "$BASE_JS" \
  || fail "Falta la matriz legacy FARMACIA salvo EXCEPCION"
if grep -q '\.on(' "$VIEW_JSP"; then
  fail "Se detectó jQuery.on en la vista Liferay 5.2"
fi
info "Una sola matriz Tipo Pedido/Sector y APIs jQuery legacy"

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

bash scripts/reclamos/verificar_secretos_appmobile.sh

LEGACY_CLIENT=ext-impl/src/ar/com/ospim/desarrolloAppMobile/beans/ClienteAppMobile.java
grep -q 'return ReclamoAppMobileAuthClient.obtenerToken();' "$LEGACY_CLIENT" \
  || fail "ClienteAppMobile legacy no delega autenticación en el cliente seguro"
info "Cliente AppMobile legacy delega autenticación segura"

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
  2. rotar y configurar las cuatro claves AppMobile en QA y producción;
  3. compilar el proyecto completo con el entorno Liferay real;
  4. inspeccionar el WAR generado;
  5. ejecutar smoke tests de alta, Compras, edición, revisión, pestañas y baja;
  6. verificar idempotencia de AN en AppMobile;
  7. revisar outbox, logs y estados externos durante 24 horas.
EOF
