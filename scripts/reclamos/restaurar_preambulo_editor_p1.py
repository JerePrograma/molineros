#!/usr/bin/env python3
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
EDITOR = ROOT / "ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/datos_edicion_prestacion.jsp"

raw = EDITOR.read_bytes()
try:
    text = raw.decode("utf-8")
    encoding = "utf-8"
except UnicodeDecodeError:
    text = raw.decode("iso-8859-1")
    encoding = "iso-8859-1"

marker = """Calendar fechaPrestacion  = Calendar.getInstance();


if (prestacionEnEdicion != null) {
"""

replacement = """Calendar fechaPrestacion  = Calendar.getInstance();


if(prestacionEnEdicion != null  ){
\t tipoedicion = (Integer) request.getAttribute(\"tipoEdicion\");
\t if(prestacionEnEdicion.getComprobanteFecha() != null){
\t\t fechaseccional.setTime(prestacionEnEdicion.getComprobanteFecha());
\t }
\t if(prestacionEnEdicion.getFechaPrestacion() !=null){
\t\t fechaPrestacion.setTime(prestacionEnEdicion.getFechaPrestacion());
\t }
}

String captionbotoncancelar=\"Cancelar Edicion de la Prestacion\";
String captionlabelproceso=\"PRESTACION EN PROCESO DE EDICION\";
String estiloLabel=\"\";

if (tipoedicion==1) {
\tcaptionbotoncancelar=\"Cancelar Autorizacion de la Prestacion\";
\tcaptionlabelproceso=\"PRESTACION EN PROCESO DE AUTORIZACION\";
\testiloLabel=\"style='color:green;'\";
}
if (tipoedicion==2) {
\tcaptionbotoncancelar=\"Cancelar Rechazo de la Prestacion\";
\tcaptionlabelproceso=\"PRESTACION EN PROCESO DE RECHAZO\";
\testiloLabel=\"style='color:red;'\";
}

ocultarSeccional = (String) request.getAttribute(\"ocultar\");

if (prestacionEnEdicion != null) {
"""

count = text.count(marker)
if count != 1:
    raise RuntimeError(
        "preámbulo del editor: se esperaba una coincidencia y se encontraron %d" % count
    )

text = text.replace(marker, replacement, 1)

required = (
    'tipoedicion = (Integer) request.getAttribute("tipoEdicion")',
    'fechaseccional.setTime(prestacionEnEdicion.getComprobanteFecha())',
    'fechaPrestacion.setTime(prestacionEnEdicion.getFechaPrestacion())',
    'captionbotoncancelar="Cancelar Edicion de la Prestacion"',
    'captionlabelproceso="PRESTACION EN PROCESO DE EDICION"',
    'ocultarSeccional = (String) request.getAttribute("ocultar")',
    'HtmlUtil.escapeJS(codigoPrestacionEdicion)',
)
for value in required:
    if value not in text:
        raise RuntimeError("falta invariantes del editor: %s" % value)

EDITOR.write_bytes(text.encode(encoding))
print("RECLAMOS_P1_EDITOR_PREAMBLE_RESTORED")
