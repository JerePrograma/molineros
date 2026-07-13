# Validación SQL/Nomenclador - Estado inicial

Fecha: 2026-07-12 (America/Buenos_Aires)

## Git

- Repositorio: `C:\devmolineros\ext`.
- Remoto: `https://github.com/JerePrograma/molineros.git`.
- Branch: `main`.
- HEAD: `183aebe91a383c0e2f4ae4f9265f7500dcb5179d` (`Cambio SQL`).
- `origin/main`: `183aebe91a383c0e2f4ae4f9265f7500dcb5179d` después de `git fetch origin main`.
- Ahead/behind: `0/0`.
- Working tree inicial: limpio.
- Archivos modificados: ninguno.
- Archivos eliminados locales: ninguno.
- Archivos nuevos locales: ninguno.
- Cambios locales no pusheados: no.

Comandos ejecutados antes de modificar archivos:

```powershell
git status
git status --short
git branch --show-current
git log -1 --oneline
git diff --stat
git diff --name-status
git status --short --branch
git remote -v
git fetch origin main
git rev-parse main
git rev-parse origin/main
git rev-list --left-right --count main...origin/main
```

Resultados relevantes:

```text
On branch main
Your branch is up to date with 'origin/main'.
nothing to commit, working tree clean
main
183aebe9 Cambio SQL
LOCAL_MAIN=183aebe91a383c0e2f4ae4f9265f7500dcb5179d
ORIGIN_MAIN=183aebe91a383c0e2f4ae4f9265f7500dcb5179d
AHEAD_BEHIND=0  0
```

## Schema en `src` y `classes`

Antes del build, existía únicamente:

`ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql`

SHA-256 inicial:

```text
73786FEB14E6971A80982EFD5D2CD05DBA61A575AD6C3A63BFCBAF26136708A4
```

La copia `ext-impl/classes/ar/com/ospim/compras/sql/compras_schema.sql` no existía porque el commit `183aebe9` la eliminó y `ext-impl/classes/` está ignorado. El build soportado de `ext-impl` copia recursos no Java desde `src` a `classes`; después de `ant -f ext-impl/build.xml compile`, ambas copias existen y tienen el mismo SHA-256. `git diff --no-index` devuelve `0`.

No se reintroducirá el recurso generado como archivo rastreado: la fuente de verdad es `src` y el build es el mecanismo que evita una copia vieja en `classes`.

## Baseline de build y tests

Con `JAVA_HOME=C:\Program Files\Java\jdk1.8.0_251`:

- `ant -f ext-service/build.xml compile`: exit `0`, `BUILD SUCCESSFUL`.
- `ant -f ext-impl/build.xml compile`: exit `0`, `BUILD SUCCESSFUL`, 2062 fuentes, 10 warnings legacy.
- `ant -f ext-web/build.xml compile`: exit `0`, `BUILD SUCCESSFUL`.
- `ant -f ext-web/build.xml merge`: exit `0`, `BUILD SUCCESSFUL`.
- `ComprasRequerimientosUiContractTest`: exit `1`, fallo preexistente por mensaje contradictorio.
- `EditarRequerimientoCompraServiceImplTest`: exit `0`.
- `EditarRequerimientoCompraActionTest`: exit `0`.
- `WebKeysComprasTransicionesTest`: exit `0`.
- `NotificarCotizacionPrestadorServiceImplTest`: primer intento exit `1` por classpath sin `javax.mail.Address`; reejecución con `lib/development/mail.jar` y `activation.jar` completó sus escenarios y logs esperados.

El build exitoso no demuestra que el SQL ni el flujo runtime funcionen; esas fallas se documentan en el diagnóstico.
