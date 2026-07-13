# Validacion SQL/Nomenclador - Pruebas

Fecha: 2026-07-12

## Entorno

- Java: JDK 1.8.0_251, solo para los procesos Ant/Java de validacion.
- PostgreSQL: 17 descartable, cluster LATIN1 local, puerto 55452, DB `compras_nomenclador_audit`.
- `PGCLIENTENCODING=UTF8` para convertir correctamente la fuente UTF-8 al servidor LATIN1.
- No se uso produccion ni el servicio local del puerto 5432.

## Builds soportados

Comandos finales:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk1.8.0_251'
ant -f ext-service/build.xml clean compile
ant -f ext-impl/build.xml clean compile
ant -f ext-web/build.xml clean compile
ant -f ext-web/build.xml merge
```

Resultados:

- `ext-service`: exit 0, BUILD SUCCESSFUL, 10 fuentes.
- `ext-impl`: exit 0, BUILD SUCCESSFUL, 2063 fuentes, 10 warnings legacy por APIs internas/deprecadas fuera de Compras.
- `ext-web clean compile`: exit 0, BUILD SUCCESSFUL.
- `ext-web merge`: exit 0, BUILD SUCCESSFUL, webapp ensamblada y `build-webxml` completado.

## JSPC

El target soportado por el repo se intento sin falsificar la version:

```powershell
ant -f ext-web/build.xml compile-tomcat
```

Resultado: exit 1. Con `app.server.tomcat.version=8.5`, el target legacy solo configura ramas 5.5/6.0; no define `jspc.classpath`/`jspc.classes.dir`, no carga `org.apache.jasper.JspC` y luego falla por el directorio literal `${jspc.classes.dir}`.

Se ejecuto JspC real de `C:\apache-tomcat-8.5.23` en forma dirigida, sin editar el build ni forzar Tomcat 6, con el classpath completo del proyecto:

```text
org.apache.jasper.JspC -uriroot ext-web/tmp -compile -source 1.7 -target 1.7
  html/portlet/compras/requerimientos/requerimiento_edicion.jsp
  html/portlet/compras/requerimientos/buscar_item_tecnico_result.jsp
```

Resultado: exit 0. Jasper informo `Built File` para ambas paginas. Los temporales JSPC fueron eliminados.

## Java

Classpath: `ext-impl/classes`, librerias existentes y `modules/*`.

| Test | Resultado |
| --- | --- |
| `ComprasRequerimientosUiContractTest` | exit 0, `CONTRATO_UI_COMPRAS_OK` |
| `ComprasDetalleTecnicoServiceContractTest` | exit 0, `CONTRATO_DETALLE_TECNICO_COMPRAS_OK` |
| `EditarRequerimientoCompraServiceImplTest` | exit 0 |
| `EditarRequerimientoCompraActionTest` | exit 0 |
| `WebKeysComprasTransicionesTest` | exit 0 |
| `NotificarCotizacionPrestadorServiceImplTest` | exit 0, `NOTIFICACIONES_TEST_OK` |

El test tecnico cubre mapeo medicamento/nomenclador, campos cruzados, sector incompatible, IDs inexistentes, texto manipulado, firma/orden de 13 parametros, retorno de ID y ausencia de metodos de articulo.

Los logs ERROR del test de notificacion son escenarios deliberados (`smtp caido`, persistencia falsa y `db caida`); el proceso finaliza con exit 0 y verifica que los errores se registran y no se ocultan.

## PostgreSQL

Instalacion final:

```powershell
psql -X -v ON_ERROR_STOP=1 `
  -f .\ext-impl\src\ar\com\ospim\compras\sql\compras_schema.sql
```

Resultado: exit 0, una sola secuencia `BEGIN ... COMMIT`, 47 objetos Compras creados sin DDL externo.

Smoke final:

```powershell
psql -X -v ON_ERROR_STOP=1 `
  -v med_id=101 -v med_troquel=12345 `
  -v "med_nombre=MED SMOKE 10 MG" `
  -v nom_id=201 -v nom_tipo=8 `
  -v "nom_codigo=NM-001" `
  -v "nom_descripcion=PRESTACION SMOKE" `
  -f .\docs\compras\evidencias\compras_smoke_test.sql
```

Resultado: exit 0, rechazos esperados con SQLSTATE/SQLERRM, `SQL_SMOKE_OK`, `ROLLBACK`.

La DB descartable no contiene los catalogos externos de Farmacia/Autorizaciones. Por eso el archivo no tiene IDs por defecto: exige variables obtenidas de registros canonicos activos. Los valores 101/201 usados arriba son fixtures tecnicos exclusivos del cluster descartable y no se incorporan a produccion ni al schema.

Introspeccion final:

- 21 columnas de `requerimiento_detalle` y 11 constraints esperados.
- trigger `BEFORE INSERT OR UPDATE` presente.
- `guardar_requerimiento_detalle` expone exactamente 13 argumentos.
- no existen `guardar_articulo` ni `borrar_articulo`.
- consulta de `pg_get_functiondef` por `id_articulo`/`compras.articulo`: 0 filas.

## SQL src/classes

```text
SHA-256 src     = 461F48C2A33D9E8E8759A388282FBA08B1D64D2A9AB41FEB8DBE6684F836A4AE
SHA-256 classes = 461F48C2A33D9E8E8759A388282FBA08B1D64D2A9AB41FEB8DBE6684F836A4AE
git diff --no-index: exit 0
```

`classes` es salida ignorada del build; no se vuelve a rastrear. La sincronizacion se prueba mediante el build y hashes.

## Jasper/PDF

- JRXML final SHA-256: `0E27C0920C4F8BAB52CD1201D0DB5C16B3F6076C664C0EB405CA2EAE8EB74E46`.
- Jasper final SHA-256: `AB0C1BE21AD34723EA676C8869D9CC536BA498D4558E7F00D60CCBD560EFCD2C`.
- Compilacion: Java 8 + JasperReports 3.7.4 + `JRJavacCompiler`, exit 0.
- Fill/export contra PostgreSQL descartable: medicamento `JASPER_PDF_OK pages=1`; nomenclador `JASPER_PDF_OK pages=1`.
- Render Poppler a imagen e inspeccion visual: tipo, troquel/codigo, nombre/descripcion, cantidad y observaciones legibles; sin recorte, superposicion ni fila vacia.
- Los archivos temporales generados por esta auditoria fueron eliminados.

## Busquedas y Git

- Evidencia articulos: 65 lineas. Solo aparecen literales negativos en el smoke y el test que impiden la reaparicion del modelo viejo.
- Evidencia buscadores: 3183 lineas, incluidos consumidores globales y el componente nuevo.
- `git diff --check`: exit 0. Los mensajes CRLF son advertencias de autocrlf, no errores de whitespace.
- `git fetch origin main`: exit 0; HEAD y `origin/main` son `183aebe91a383c0e2f4ae4f9265f7500dcb5179d`; ahead/behind `0/0`.

## Fallos diagnosticos separados

- Baseline SQL: schema exit 3 por DDL externo duplicado; guardado roto por `NEW.id_articulo`; busqueda rota por `d.id_articulo`.
- Primer intento final sin `PGCLIENTENCODING=UTF8`: falso positivo de instalacion con texto UTF-8 interpretado como LATIN1. Al corregir client encoding se detectaron seis `U+FFFD`; fueron eliminados y la instalacion definitiva paso.
- `compile-tomcat`: limitacion preexistente del target para Tomcat 8.5; el JspC dirigido real paso.

No queda una falla de compilacion, SQL, JSPC dirigido, Jasper ni test introducida por los cambios.

## Checklist manual de runtime

La infraestructura local no incluye un Liferay/Tomcat levantado con catalogos canonicos y sesion de usuario. Queda por ejecutar en QA antes de considerar satisfecha la aceptacion funcional live:

| Sector | Escenario | Verificacion |
| --- | --- | --- |
| Farmacia | Alta/edicion | buscar nombre/presentacion/troquel, seleccionar, limpiar, editar texto, guardar, reabrir |
| Prestaciones Medicas | Alta/edicion | buscar codigo/descripcion/tipo, seleccionar ambos IDs, limpiar, guardar, reabrir |
| Legales | Alta/edicion | mismo flujo de nomenclador, sin IDs editables |
| Todos | Cambio de sector | limpiar seleccion previa y mostrar solo el panel permitido |
| Todos | PDF | generar desde UI y comparar el detalle visible |
| Todos | Enviar a cotizar | confirmar cuerpo de mail y PDF adjunto en buzón QA, sin envio productivo |

Debe verificarse en DB que los textos persistidos coinciden con los catalogos reales y que los campos opuestos son NULL. No se envio correo real durante esta auditoria.
