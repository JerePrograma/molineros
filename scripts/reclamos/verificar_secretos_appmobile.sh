#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

TARGET=ext-impl/src/ar/com/ospim/desarrolloAppMobile/beans/ClienteAppMobile.java
[[ -f "$TARGET" ]] || {
  printf 'ERROR: no existe %s\n' "$TARGET" >&2
  exit 2
}

PATTERN='private static final String (API_KEY|EMAIL|PASSWORD)[[:space:]]*=[[:space:]]*"[^"$]+'

if grep -En "$PATTERN" "$TARGET"; then
  cat >&2 <<'EOF'
CRITICAL: ClienteAppMobile.java contiene credenciales literales.

Acciones obligatorias:
  1. rotar los valores en AppMobile;
  2. reemplazarlos por APP_BACKOFFICE_API_KEY, APP_BACKOFFICE_EMAIL y
     APP_BACKOFFICE_PASSWORD;
  3. migrar consumidores de ClienteAppMobile.obtenerToken();
  4. evaluar limpieza del historial Git.
EOF
  exit 2
fi

printf 'OK: no se detectaron credenciales AppMobile literales\n'
