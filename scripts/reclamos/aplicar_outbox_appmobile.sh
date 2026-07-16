#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

PSQL_BIN="${PSQL_BIN:-psql}"
PREFLIGHT=sql/postgresql/autorizaciones/reclamo_appmobile_outbox_preflight.sql
MIGRATION=sql/postgresql/autorizaciones/reclamo_appmobile_outbox.sql
MODE="${1:---preflight}"

fail() {
  printf 'ERROR: %s\n' "$1" >&2
  exit 2
}

usage() {
  cat <<'EOF'
Uso:
  aplicar_outbox_appmobile.sh --preflight
  CONFIRM_OUTBOX_MIGRATION=YES aplicar_outbox_appmobile.sh --apply

Conexión:
  DATABASE_URL=postgresql://usuario:password@host:puerto/base

o variables estándar PGHOST, PGPORT, PGDATABASE, PGUSER y PGPASSWORD.
EOF
}

command -v "$PSQL_BIN" >/dev/null 2>&1 \
  || fail "No se encontró psql en PSQL_BIN=$PSQL_BIN"
[[ -f "$PREFLIGHT" ]] || fail "Falta $PREFLIGHT"
[[ -f "$MIGRATION" ]] || fail "Falta $MIGRATION"

PSQL_ARGS=(-X -v ON_ERROR_STOP=1)
if [[ -n "${DATABASE_URL:-}" ]]; then
  PSQL_ARGS+=("$DATABASE_URL")
fi

run_file() {
  local file="$1"
  printf 'Ejecutando %s\n' "$file"
  "$PSQL_BIN" "${PSQL_ARGS[@]}" -f "$file"
}

case "$MODE" in
  --preflight)
    run_file "$PREFLIGHT"
    printf 'PREFLIGHT COMPLETADO. No se modificó el esquema.\n'
    ;;
  --apply)
    [[ "${CONFIRM_OUTBOX_MIGRATION:-}" == "YES" ]] \
      || fail "Para aplicar la migración defina CONFIRM_OUTBOX_MIGRATION=YES"

    run_file "$PREFLIGHT"
    run_file "$MIGRATION"

    RESULT="$(
      "$PSQL_BIN" "${PSQL_ARGS[@]}" -qAt -c \
        "SELECT CASE WHEN to_regclass('autorizaciones.reclamo_appmobile_outbox') IS NULL THEN 'MISSING' ELSE 'OK' END;"
    )"
    [[ "$RESULT" == "OK" ]] \
      || fail "La tabla de outbox no quedó disponible después de migrar"

    printf 'OUTBOX_MIGRATION_OK\n'
    ;;
  --help|-h)
    usage
    ;;
  *)
    usage >&2
    fail "Modo inválido: $MODE"
    ;;
esac
