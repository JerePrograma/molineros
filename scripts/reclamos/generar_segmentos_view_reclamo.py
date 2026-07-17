#!/usr/bin/env python3
from pathlib import Path
import hashlib

BASE = Path("ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales")
SOURCE = BASE / "view_reclamo.jspf"

PARTS = [
    ("view_reclamo_contexto.jspf", 1, 210),
    ("view_reclamo_inicio_formulario.jspf", 211, 353),
    ("view_reclamo_cabecera.jspf", 354, 734),
    ("view_reclamo_afiliado_diagnostico.jspf", 735, 843),
    ("view_reclamo_prestaciones.jspf", 844, 1226),
    ("view_reclamo_seguimiento_cierre.jspf", 1227, 1593),
    ("view_reclamo_acciones.jspf", 1594, 1694),
    ("view_reclamo_configuracion.jspf", 1695, 1835),
]

original = SOURCE.read_bytes()
fragment_paths = [BASE / filename for filename, _, _ in PARTS]

if all(path.is_file() for path in fragment_paths) and original.startswith(b"<%@ include file="):
    print("Segments already generated")
    raise SystemExit(0)

required_tokens = [
    b"<%@ page",
    b"WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA",
    b"ReclamoPrestacionalCompraContexto",
    b"<form name=\"fm\"",
    b"window.ReclamoPrestacionalViewConfig",
    b"</form>",
]
for token in required_tokens:
    if token not in original:
        raise SystemExit("Missing structural token: %r" % token)

lines = original.splitlines(keepends=True)
if len(lines) != 1835:
    raise SystemExit("Unexpected source line count: %d" % len(lines))

rebuilt = []
for filename, start, end in PARTS:
    fragment = b"".join(lines[start - 1:end])
    (BASE / filename).write_bytes(fragment)
    rebuilt.append(fragment)

if b"".join(rebuilt) != original:
    raise SystemExit("Fragment concatenation differs from checked-out source")

prefix = "/html/portlet/autorizaciones/reclamos_prestacionales/"
assembly = "".join(
    f'<%@ include file="{prefix}{filename}" %>'
    for filename, _, _ in PARTS
).encode("ascii")
SOURCE.write_bytes(assembly)

print("Original bytes: %d" % len(original))
print("Original SHA256: %s" % hashlib.sha256(original).hexdigest())
print("Fragments: %d" % len(PARTS))
