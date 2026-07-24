# Codificacion ISO-8859-1

ISO-8859-1 es una codificacion de caracteres, no cifrado. Todo texto creado o modificado debe guardarse sin BOM.

## PowerShell

```powershell
$enc = [System.Text.Encoding]::GetEncoding('ISO-8859-1')
$text = [System.IO.File]::ReadAllText($path, $enc)
[System.IO.File]::WriteAllText($path, $text, $enc)
```

## Python

```python
from pathlib import Path
p = Path(r"ruta")
text = p.read_text(encoding="iso-8859-1")
p.write_bytes(text.encode("iso-8859-1"))
```

## Verificacion

- Rechazar prefijos BOM UTF-8 `EF BB BF` y UTF-16 `FF FE`/`FE FF`.
- Decodificar y recodificar como ISO-8859-1.
- Buscar las secuencias U+00C3, U+00C2 y U+FFFD asociadas a mojibake o reemplazo.
- Revisar visualmente tildes y enes.
- Ejecutar `git diff --check` y revisar el diff.

ISO-8859-1 puede decodificar cualquier byte; la comprobacion util es que el texto esperado sea legible, no tenga BOM ni mojibake y pueda volver a codificarse sin sustituciones. No convertir masivamente el repositorio.
