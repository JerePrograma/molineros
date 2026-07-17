#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"
printf '%s\n' '__RECLAMO_VIEW_JSPF_BEGIN__'
cat ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jspf
printf '%s\n' '__RECLAMO_VIEW_JSPF_END__'
printf '%s\n' 'ERROR: extracción temporal controlada del view' >&2
exit 1
