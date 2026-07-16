#!/usr/bin/env bash
set -euo pipefail

PENDING_MAX_MINUTES="${PENDING_MAX_MINUTES:-10}"
MAX_ATTEMPTS="${MAX_ATTEMPTS:-5}"
PSQL_BIN="${PSQL_BIN:-psql}"

fail() {
  printf 'CRITICAL: %s\n' "$1" >&2
  exit 2
}

warn() {
  printf 'WARNING: %s\n' "$1" >&2
  exit 1
}

require_integer() {
  [[ "$2" =~ ^[0-9]+$ ]] || fail "$1 debe ser un entero no negativo"
}

require_integer PENDING_MAX_MINUTES "$PENDING_MAX_MINUTES"
require_integer MAX_ATTEMPTS "$MAX_ATTEMPTS"
command -v "$PSQL_BIN" >/dev/null 2>&1 \
  || fail "No se encontró psql en PSQL_BIN=$PSQL_BIN"

PSQL_ARGS=(-X -qAt -v ON_ERROR_STOP=1)
if [[ -n "${DATABASE_URL:-}" ]]; then
  PSQL_ARGS+=("$DATABASE_URL")
fi

query_scalar() {
  "$PSQL_BIN" "${PSQL_ARGS[@]}" -c "$1"
}

TABLE_NAME="autorizaciones.reclamo_appmobile_outbox"
EXISTS="$(query_scalar "SELECT CASE WHEN to_regclass('$TABLE_NAME') IS NULL THEN 0 ELSE 1 END;")"
[[ "$EXISTS" == "1" ]] || fail "No existe $TABLE_NAME"

SUMMARY="$(query_scalar "
SELECT
  count(*) FILTER (WHERE procesado_en IS NULL) || '|' ||
  count(*) FILTER (WHERE estado_proceso = 'PROCESANDO' AND procesado_en IS NULL) || '|' ||
  count(*) FILTER (WHERE estado_proceso = 'PROCESADO')
FROM $TABLE_NAME;
")"

IFS='|' read -r PENDING PROCESSING PROCESSED <<< "$SUMMARY"

OVERDUE="$(query_scalar "
SELECT count(*)
FROM $TABLE_NAME
WHERE procesado_en IS NULL
  AND proximo_intento <= NOW() - ($PENDING_MAX_MINUTES * INTERVAL '1 minute');
")"

EXPIRED_LEASES="$(query_scalar "
SELECT count(*)
FROM $TABLE_NAME
WHERE procesado_en IS NULL
  AND estado_proceso = 'PROCESANDO'
  AND bloqueado_hasta IS NOT NULL
  AND bloqueado_hasta < NOW();
")"

EXCESSIVE_ATTEMPTS="$(query_scalar "
SELECT count(*)
FROM $TABLE_NAME
WHERE procesado_en IS NULL
  AND intentos >= $MAX_ATTEMPTS;
")"

OLDEST_MINUTES="$(query_scalar "
SELECT COALESCE(
  floor(EXTRACT(EPOCH FROM (NOW() - min(creado_en))) / 60)::bigint,
  0
)
FROM $TABLE_NAME
WHERE procesado_en IS NULL;
")"

printf 'outbox pending=%s processing=%s processed=%s overdue=%s expired_leases=%s excessive_attempts=%s oldest_pending_minutes=%s\n' \
  "$PENDING" \
  "$PROCESSING" \
  "$PROCESSED" \
  "$OVERDUE" \
  "$EXPIRED_LEASES" \
  "$EXCESSIVE_ATTEMPTS" \
  "$OLDEST_MINUTES"

if (( EXPIRED_LEASES > 0 )); then
  fail "Hay $EXPIRED_LEASES leases vencidos"
fi

if (( EXCESSIVE_ATTEMPTS > 0 )); then
  fail "Hay $EXCESSIVE_ATTEMPTS eventos con al menos $MAX_ATTEMPTS intentos"
fi

if (( OVERDUE > 0 )); then
  warn "Hay $OVERDUE pendientes vencidos por más de $PENDING_MAX_MINUTES minutos"
fi

printf 'OK: outbox AppMobile saludable\n'
exit 0
