#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

OUTPUT="${1:-/tmp/reclamos-session-inventory.csv}"
TMP="${TMPDIR:-/tmp}/reclamos-session-inventory.$$.tmp"
trap 'rm -f "$TMP"' EXIT

SEARCH_ROOTS=(
  ext-impl/src/ar/com/ospim/autorizaciones
  ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales
)

KEYS=(
  RECLAMO_PRESTACION_EN_EDICION
  LISTA_RECLAMO_PRESTACIONES
  LISTA_RECLAMO_REVISIONES
  LISTA_RECLAMO_CONTACTOS
  PRESTACION_RECLAMO_EN_PROCESO
  RECLAMO_PRESTACIONES
  RECLAMO_REVISIONES
  RECLAMO_CONTACTOS
)

for root in "${SEARCH_ROOTS[@]}"; do
  [[ -d "$root" ]] || {
    printf 'ERROR: no existe %s\n' "$root" >&2
    exit 2
  }
done

printf 'key,file,line,operation,source\n' > "$TMP"

classify_operation() {
  local source="$1"
  if [[ "$source" == *"removeAttribute"* ]]; then
    printf 'REMOVE'
  elif [[ "$source" == *"setAttribute"* ]]; then
    printf 'WRITE'
  elif [[ "$source" == *"getAttribute"* ]]; then
    printf 'READ'
  else
    printf 'REFERENCE'
  fi
}

escape_csv() {
  local value="$1"
  value="${value//\"/\"\"}"
  printf '"%s"' "$value"
}

for key in "${KEYS[@]}"; do
  while IFS=: read -r file line source; do
    [[ -n "${file:-}" ]] || continue
    operation="$(classify_operation "$source")"
    {
      escape_csv "$key"
      printf ','
      escape_csv "$file"
      printf ',%s,' "$line"
      escape_csv "$operation"
      printf ','
      escape_csv "$(printf '%s' "$source" | sed 's/^[[:space:]]*//')"
      printf '\n'
    } >> "$TMP"
  done < <(
    grep -R -n -F "$key" "${SEARCH_ROOTS[@]}" \
      --include='*.java' \
      --include='*.jsp' \
      --include='*.jspf' \
      --exclude='*ContractTest.java' \
      || true
  )
done

sort -t, -k1,1 -k2,2 -k3,3n "$TMP" > "$OUTPUT"

COUNT="$(awk 'NR > 1 { count++ } END { print count + 0 }' "$OUTPUT")"
printf 'Inventario generado: %s (%s referencias)\n' "$OUTPUT" "$COUNT"

if [[ "$COUNT" -eq 0 ]]; then
  printf 'ADVERTENCIA: no se encontraron referencias; revisar raíces o nombres de claves.\n' >&2
  exit 1
fi
