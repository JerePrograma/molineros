from pathlib import Path

SOURCE = Path(__file__).with_name("aplicar_normalizacion_vista_inicial_v2.py")
source = SOURCE.read_text(encoding="utf-8")

old = '''def read(path):
    return (ROOT / path).read_text(encoding="utf-8")


def write(path, text):
    (ROOT / path).write_text(text, encoding="utf-8", newline="")
'''

new = '''def _encoding(path):
    target = ROOT / path
    if not target.exists():
        return "utf-8"
    data = target.read_bytes()
    try:
        data.decode("utf-8")
        return "utf-8"
    except UnicodeDecodeError:
        return "latin-1"


def read(path):
    target = ROOT / path
    return target.read_bytes().decode(_encoding(path))


def write(path, text):
    target = ROOT / path
    encoding = _encoding(path)
    target.write_bytes(text.encode(encoding))
'''

if source.count(old) != 1:
    raise SystemExit("No se encontró el bloque de codificación a reemplazar")

source = source.replace(old, new, 1)
namespace = {
    "__file__": str(SOURCE),
    "__name__": "__main__",
}
exec(compile(source, str(SOURCE), "exec"), namespace, namespace)
