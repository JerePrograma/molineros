# PROYECTO MOLINEROS - FORMA OBLIGATORIA DE TRABAJO

## 1. Contexto fijo

Repositorio: https://github.com/JerePrograma/molineros.git  
Ruta local obligatoria: `C:\wsmolineros\ext`  
Rama de trabajo y publicacion: `main` / `origin/main`

Antes de cualquier inspeccion o comando Git:

```powershell
Set-Location -LiteralPath 'C:\wsmolineros\ext'
$root = (git rev-parse --show-toplevel).Trim()
$origin = (git remote get-url origin).Trim()

if ($LASTEXITCODE -ne 0 -or
    $root -ne 'C:/wsmolineros/ext' -or
    $origin -ne 'https://github.com/JerePrograma/molineros.git') {
    throw "Repositorio o ruta incorrectos. No modificar archivos ni remotos."
}
```

No operar sobre otro clon, ruta, repositorio o remoto. Cualquier ruta local anterior queda invalidada.

## 2. Codificacion obligatoria

Todo archivo de texto creado o modificado debe guardarse explicitamente en ISO-8859-1 sin BOM. No depender de la codificacion predeterminada del editor, PowerShell, Java, Python ni otra herramienta. No convertir binarios ni archivos ajenos a la tarea.

Antes del commit, verificar por cada archivo de texto afectado:

1. Es decodificable como ISO-8859-1.
2. No tiene BOM UTF-8, UTF-16 LE ni UTF-16 BE.
3. No contiene mojibake visible: secuencias iniciadas por `0xC3`, `0xC2` ni el caracter Unicode U+FFFD.
4. El contenido se lee correctamente.
5. Se informa el metodo exacto de verificacion.

## 3. Regla principal de Git

Trabajar directamente sobre `main` local y `origin/main`. No crear ramas de trabajo, fix o feature ni pull requests, salvo pedido expreso.

Antes de modificar archivos:

```powershell
Set-Location -LiteralPath 'C:\wsmolineros\ext'
git status --short
git branch --show-current
git rev-parse HEAD
git log -1 --oneline
git fetch origin --prune
git switch main
git merge --ff-only origin/main
```

Si existen cambios locales, preservarlos. No descartarlos, sobrescribirlos ni ocultarlos. Detenerse cuando puedan interferir e informar las rutas exactas.

Prohibido:

```text
git reset --hard
git clean -fd
git checkout -- .
git restore .
git stash
git rebase
git push --force
git push --force-with-lease
```

Tambien quedan prohibidas operaciones destructivas equivalentes. No usar `git add .` ni `git add -A`; agregar solo rutas exactas pertenecientes a la tarea.

## 4. Alcance de analisis

Analizar y modificar unicamente archivos directamente pertinentes al caso solicitado y sus dependencias directas indispensables.

No hacer auditorias generales. No inspeccionar Struts, Tiles, SQL, modulos ajenos, servicios no relacionados, configuraciones globales ni clases indirectas salvo incompatibilidad concreta demostrada.

Cuando se nombren clases, JSP, JavaScript, metodos o archivos:

1. Son el alcance principal.
2. Revisar solo callers, contratos y tests directos necesarios.
3. No ampliar el alcance por curiosidad tecnica.
4. No hacer cleanup, modernizacion o refactorizacion no solicitada.
5. No reformatear archivos completos.
6. No modificar comportamiento ajeno al defecto o funcionalidad solicitada.

## 5. Implementacion

Cuando se solicite un cambio, implementarlo realmente; no entregar solo plan o pseudocodigo.

Mantener compatibilidad con:

- Java 8.
- JSP legacy.
- Liferay 5.2.
- Struts y Tiles existentes.
- JavaScript ES5.
- jQuery legacy.
- ISO-8859-1 sin BOM.

No introducir tecnologias incompatibles, APIs no disponibles, cambios arquitectonicos generales ni renombrar parametros, IDs, URLs, forwards o atributos de sesion.

Aplicar el cambio minimo que resuelva el caso completo. Conservar seguridad, validacion, fail-closed, concurrencia, nonce, permisos, AJAX y contratos legacy existentes.

## 6. Pruebas y verificaciones

Despues de implementar, ejecutar como minimo:

```powershell
git diff --check
git status --short
git diff --stat
git diff -- <rutas-exactas-modificadas>
```

Ejecutar tests, contratos, compilaciones y validaciones focalizadas del codigo afectado. Verificar la codificacion de cada archivo de texto modificado.

No ejecutar suites o builds ajenos al alcance sin razon concreta. No ocultar fallos con `|| true`, redirecciones enganosas ni equivalentes.

Distinguir en el informe:

- pruebas aprobadas;
- pruebas fallidas;
- fallos preexistentes;
- pruebas no ejecutadas;
- limitaciones del entorno.

No afirmar que el cambio esta listo para produccion o totalmente garantizado sin build completo y smoke tests reales.

## 7. Commit y push directo a main

Salvo indicacion contraria:

1. Trabajar en `main`.
2. Validar el resultado.
3. Ejecutar `git add -- <rutas exactas>`.
4. Crear un commit detallado en espanol.
5. Ejecutar `git fetch origin --prune`.
6. Si `origin/main` avanzo, integrarlo mediante merge; nunca rebase.
7. Resolver solo conflictos relacionados con la tarea.
8. Repetir las pruebas afectadas despues del merge.
9. Publicar con `git push origin main:main`.
10. Verificar:

```powershell
git rev-parse main
git rev-parse origin/main
git merge-base --is-ancestor <commit-creado> origin/main
git status --short --branch
```

`main` local y `origin/main` deben finalizar en el mismo SHA. Si el push es rechazado, existe divergencia o aparece un conflicto no trivial, detenerse. Nunca usar force-push.

## 8. Megaprompts para Codex

Un megaprompt debe ser unico, completo y listo para copiar. No ejecutar cambios salvo pedido explicito. Debe exigir:

- ruta, repositorio y remoto verificados;
- trabajo directo en `main` y `origin/main`, sin ramas ni PR;
- preservacion de cambios locales;
- alcance limitado a archivos y modulos relacionados;
- implementacion real y criterios funcionales verificables;
- compatibilidad legacy;
- tests focalizados;
- ISO-8859-1 sin BOM y control de mojibake;
- `git diff --check` y revision del diff;
- `git add` focalizado;
- commit detallado y push a `origin/main`;
- prohibicion de rebase, force-push, stash, reset destructivo y cleanup general;
- informe final con archivos, motivos, pruebas, resultados y riesgos.

## 9. Cambios especificos

Para cada modificacion concreta:

1. Indicar ruta exacta y bloque afectado.
2. Explicar el defecto verificable.
3. Implementar el diff minimo.
4. Revisar solo callers y tests directos.
5. Mantener contratos publicos y comportamientos no relacionados.
6. Agregar o actualizar una prueba focalizada cuando sea razonable.
7. Informar que cambio, que no se toco y como se verifico.

## 10. Entrega obligatoria

Informar:

- repositorio y ruta local;
- rama usada;
- HEAD inicial y final;
- archivos modificados y motivo preciso;
- codificacion y metodo de verificacion por archivo;
- comandos ejecutados, codigo de salida y resultado;
- commit creado y resultado del push;
- SHA de `main` local y `origin/main`;
- pruebas pendientes;
- limitaciones y riesgos residuales.

Priorizar precision, evidencia y cambios minimos.
