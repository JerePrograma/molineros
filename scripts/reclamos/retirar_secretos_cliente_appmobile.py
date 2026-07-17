#!/usr/bin/env python3
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
TARGET = ROOT / "ext-impl/src/ar/com/ospim/desarrolloAppMobile/beans/ClienteAppMobile.java"

raw = TARGET.read_bytes()
try:
    text = raw.decode("utf-8")
    encoding = "utf-8"
except UnicodeDecodeError:
    text = raw.decode("iso-8859-1")
    encoding = "iso-8859-1"

secure_import = "import ar.com.ospim.autorizaciones.services.ReclamoAppMobileAuthClient;\n"
anchor_import = "import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;\n"
if secure_import not in text:
    if text.count(anchor_import) != 1:
        raise RuntimeError("No se encontró el import ancla del cliente seguro")
    text = text.replace(anchor_import, anchor_import + secure_import, 1)

text = re.sub(
    r'^\s*//\s*private static final String HOST\s*=.*?;\s*\r?\n',
    '',
    text,
    flags=re.MULTILINE,
)

for constant in ("API_KEY", "EMAIL", "PASSWORD", "LOGIN_URL"):
    pattern = (
        r'^\s*private static final String '
        + constant
        + r'\s*=.*?;\s*\r?\n'
    )
    text, count = re.subn(pattern, '', text, count=1, flags=re.MULTILINE)
    if count != 1:
        raise RuntimeError("No se pudo retirar la constante legacy " + constant)

start_marker = "\tpublic static String obtenerToken() {"
end_marker = "\n\tpublic static List<PreAutorizacion> getPreAutorizacionessByEstado"
start = text.find(start_marker)
end = text.find(end_marker, start)
if start < 0 or end <= start:
    raise RuntimeError("No se pudo delimitar ClienteAppMobile.obtenerToken()")

replacement = """\t/**
\t * Compatibilidad para consumidores legacy. La autenticación y sus secretos
\t * se resuelven exclusivamente desde configuración externa.
\t */
\tpublic static String obtenerToken() {
\t\treturn ReclamoAppMobileAuthClient.obtenerToken();
\t}
"""
text = text[:start] + replacement + text[end:]

for forbidden in (
    "private static final String API_KEY",
    "private static final String EMAIL",
    "private static final String PASSWORD",
    "private static final String LOGIN_URL",
    "post.addRequestHeader(\"api-key\", API_KEY)",
    "String.format(\"{\\\"email\\\"",
):
    if forbidden in text:
        raise RuntimeError("Persistió contenido de autenticación legacy: " + forbidden)

if "return ReclamoAppMobileAuthClient.obtenerToken();" not in text:
    raise RuntimeError("ClienteAppMobile no delega en el cliente seguro")

TARGET.write_bytes(text.encode(encoding))
print("CLIENTE_APPMOBILE_LEGACY_SECRETS_REMOVED")
