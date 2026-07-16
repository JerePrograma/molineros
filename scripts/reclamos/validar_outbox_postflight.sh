#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

PSQL_BIN="${PSQL_BIN:-psql}"
POSTFLIGHT=sql/postgresql/autorizaciones/reclamo_appmobile_outbox_postflight.sql

command -v "$PSQL_BIN" >/dev/null 2>&1 || {
  printf 'ERROR: no se encontró psql en PSQL_BIN=%s\n' "$PSQL_BIN" >&2
  exit 2
}

[[ -f "$POSTFLIGHT" ]] || {
  printf 'ERROR: falta %s\n' "$POSTFLIGHT" >&2
  exit 2
}

PSQL_ARGS=(-X -v ON_ERROR_STOP=1)
if [[ -n "${DATABASE_URL:-}" ]]; then
  PSQL_ARGS+=("$DATABASE_URL")
fi

"$PSQL_BIN" "${PSQL_ARGS[@]}" -f "$POSTFLIGHT"
printf 'OUTBOX_POSTFLIGHT_COMPLETED\n'
