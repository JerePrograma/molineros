# Molineros - Guia principal para agentes

## Identidad y alcance

Molineros es una aplicacion empresarial legacy basada en una extension de Liferay. La evidencia actual muestra modulos de afiliados, autorizaciones, reclamos prestacionales y Compras.

Esta guia no sustituye la inspeccion del codigo vigente. Commit documental base: `a7ffb6d92b9dee3a36abdd5ec6c8723dc611ddc6`.

## Repositorio obligatorio

- Repositorio: `https://github.com/JerePrograma/molineros`
- Clon operativo: `C:\devmolineros\ext`
- Rama local y remota: `main` y `origin/main`

Antes de inspeccionar o modificar:

```powershell
Set-Location -LiteralPath 'C:\devmolineros\ext'
git rev-parse --show-toplevel
git remote get-url origin
git status --short
git branch --show-current
git rev-parse HEAD
git fetch origin --prune
git switch main
git merge --ff-only origin/main
```

La raiz y el remoto deben coincidir exactamente con los valores anteriores. Preservar cualquier cambio local.

## Reglas obligatorias

- Trabajar directamente en `main`; no crear ramas ni PR salvo pedido expreso.
- No usar `reset --hard`, `clean -fd`, `restore .`, `checkout -- .`, `stash`, `rebase` ni force-push.
- No usar `git add .` ni `git add -A`.
- Crear o modificar textos en ISO-8859-1 sin BOM.
- Mantener Java 8, JSP/JSPF legacy, Liferay 5.2, Struts, Tiles, JavaScript ES5 y jQuery legacy.
- Limitar el analisis a archivos nombrados y dependencias directas indispensables.
- Implementar realmente cuando se pide una modificacion; evitar cleanup y refactor no solicitado.
- Preservar parametros, IDs, URLs, actions, forwards, atributos, nonces, permisos y comportamiento AJAX.

## Validacion minima

```powershell
git diff --check
git status --short
git diff --stat
git diff -- <rutas-exactas>
```

Ejecutar tambien tests o builds focalizados comprobados para el codigo afectado. Distinguir aprobados, fallidos, preexistentes y no ejecutados.

## Publicacion

Agregar solo rutas pertinentes, crear commit detallado en espanol, volver a ejecutar `git fetch origin --prune`, integrar avances mediante merge y publicar con:

```powershell
git push origin main:main
```

Verificar SHA local, SHA remoto, ancestro del commit y estado final.

## Informe final

Informar repositorio, ruta, rama, HEAD inicial/final, archivos, motivo, codificacion por archivo, validaciones y codigos de salida, commit, push, SHA local/remoto, pendientes y riesgos.

## Documentacion ampliada

Indice: [`docs/autoreferencia/README.md`](docs/autoreferencia/README.md).
