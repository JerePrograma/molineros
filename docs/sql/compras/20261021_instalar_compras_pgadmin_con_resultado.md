# Instalar Compras con resultado visible en pgAdmin

## Archivo que se ejecuta

`20261021_instalar_compras_pgadmin_con_resultado.sql` es un instalador completo,
alternativo al consolidado original. Ejecutar uno solo. No es el verificador
de solo lectura ni un parche que desactive controles.

Conserva las 66 sentencias del consolidado publicado en `75a072ae`, en el mismo
orden y sin cambios en sus cuerpos. Solo cambia la envoltura de ejecucion y
reporte. Incluye el prerrequisito de comparativas del 23/09 y los 14 bloques,
con INSUMOS DIABETES exclusivamente en nomenclador 10 ya confirmado.

Origen: `20261021_megascript_produccion_desde_20260928.sql`.
SHA-256 del origen: `e90886fc863c52f6230cb2e5321efc4f0a93027287ea6452c79e6e6b4bf6a8a6`.
SHA-256 de este instalador: `c55ec05cc76b9bd9b64b9ff3a371b576ceeae33fd1eeea94720b30109b3b075e`.
Las fuentes y efectos funcionales se detallan en la guia homonima del origen.

## Motivo

El cliente mostro solamente "Query returned successfully" al ejecutar el archivo
terminado en COMMIT. Ese mensaje no demuestra la instalacion. El segundo
inventario de produccion mantiene 5 cuerpos coincidentes, 8 anteriores y
13 ausentes; ahora ambas comparativas existen, pero la configuracion, columnas
y trigger nuevos siguen ausentes. No se atribuye la creacion de esas tablas a
una ejecucion especifica sin su historial o log.

Esta variante termina con SELECT despues del COMMIT para que la ultima
sentencia devuelva una fila en Data Output. El estado real se informa en la
columna resultado, incluso si pgAdmin muestra un mensaje generico de exito.

## Ejecucion

1. Confirmar host, puerto, base, usuario y version. El inventario aportado
   corresponde a devmolineros en 12.1.1.28:5432, PostgreSQL 9.6.5 UTF8.
2. Disponer del backup verificado y detener la aplicacion para aplicar cambios.
   Ensayar primero sobre una copia restaurada del destino.
3. Abrir una CONEXION NUEVA al destino y el archivo como ISO-8859-1/LATIN1.
   No ejecutar dentro de una transaccion previa ni en la sesion con un error.
4. Ejecutar TODO el archivo. Al finalizar abrir Data Output y copiar su fila.
   No quitar COMMIT ni ejecutar solamente el SELECT final.
5. Si resultado es INSTALACION_CONFIRMADA, verificar desde otra conexion con
   verificar_instalacion_solo_lectura.sql: APROBADO, 26 funciones y DIABETES 10.
6. Si resultado es ERROR_MIGRACION_REVERTIDA, la instalacion fallo. Copiar
   bloque, paso, sqlstate, mensaje, detalle y sugerencia; contexto y comando
   identifican la sentencia original. No reintentar sin resolver ese error.

| resultado | Significado |
|---|---|
| INSTALACION_CONFIRMADA | Las sentencias y controles originales finalizaron, y el SELECT se ejecuta despues del COMMIT. |
| ERROR_MIGRACION_REVERTIDA | Se capturo el primer error SQL. Los cambios de negocio de ese bloque protegido fueron revertidos. |

Un mensaje generico de exito o el codigo 0 de psql NO reemplaza esa columna.
Esta variante captura el error para devolverlo en la grilla: el proceso SQL
puede terminar con codigo 0 aunque resultado indique ERROR_MIGRACION_REVERTIDA.
Para automatizacion que dependa del codigo de salida, usar el consolidado
original con ON_ERROR_STOP y su verificador posterior.

## Transaccion y preservacion

No se crean funciones auxiliares permanentes. Solo se usa una tabla temporal
propia de esta conexion para el reporte. Al reabrir el archivo en la misma
sesion se limpia exclusivamente ese reporte temporal antes de una nueva
ejecucion; no se eliminan tablas ni datos de Compras como parte del reporte.

Todas las sentencias de negocio, el preflight y la validacion final estan en
un mismo bloque protegido. Ante un error SQL, PostgreSQL revierte ese bloque
antes de guardar SQLSTATE, mensaje, detalle, sugerencia y contexto del error.
El COMMIT posterior conserva solo el reporte temporal de la ejecucion fallida.
La tabla del reporte desaparece al cerrar la conexion.

Se mantiene la misma restriccion de reejecucion del origen: no es un instalador
universal para cualquier revision y rechaza una autorizacion corporativa ya
instalada. No sobrescribe contratos desconocidos para avanzar.

Una cancelacion, un error del cliente, un fallo antes del bloque protegido o
la perdida de conexion pueden impedir que se devuelva la fila. En esos casos
no asumir exito: relevar desde otra conexion y conservar el primer error.
Despues de un COMMIT confirmado, la reversa exige una migracion especifica o
la restauracion aprobada; no se deshace con ROLLBACK.

## Verificacion del 21/10/2026

PostgreSQL 9.6.5 temporal, UTF8, cliente LATIN1, solamente 127.0.0.1:54395.
Se probaron 7 casos, con codigo 0 del arnes y todos los controles aprobados:

- Replica de 5/8/13 con comparativas existentes: fila INSTALACION_CONFIRMADA.
- Conexion nueva: APROBADO con 26 funciones finales y DIABETES 10.
- Las 36 comprobaciones funcionales existentes pasaron.
- Comparativas ausentes: creacion, instalacion y verificacion posterior.
- Error tardio en bloque 14: fila de error completa y dump anterior/posterior
  identico, incluyendo rollback de las nuevas comparativas y secuencias.
- Contrato de comparativas desconocido: error en prerrequisito 00, preservado
  sin cambios, comprobado con hashes de dump completo.
- Reejecucion sobre instalacion final: primer error de preflight visible,
  sin modificar el estado previo ni devolver exito falso.

La igualdad de las 66 sentencias se verifico por comparacion exacta de cada
payload con el origen. Los archivos se guardaron con encode('latin1'), sin
BOM, sin C3/C2 ni U+FFFD; se revisaron diff y espacios.
Evidencia local: .codex/artifacts/megascript-produccion-20261021/
pgadmin-con-resultado/resultados.json y logs adyacentes; manifiesto separado
manifest-pgadmin-resultado.json. Las estructuras y datos son sinteticos.

No se accedio a produccion ni se ejecuto esta variante en el pgAdmin del
usuario. PRUEBA UI MANUAL PENDIENTE. No hubo build Java/JSP ni despliegue.
El arnes final y las comprobaciones de SQL terminaron con codigo 0; el arranque
temporal y la asercion del diagnostico preliminar necesitaron ajustes antes
de esta validacion final. No se interpretan esos ajustes como fallos del destino.

Referencia de negocio: el consolidado original y sus fuentes en docs/sql.
Referencia legacy de propagacion de errores: ReintegroServiceImpl.cambiarEstadoCuota,
ext-impl/src/ar/com/ospim/liquidaciones/services/ReintegroServiceImpl.java:2997.
Referencia de contrato de nomencladores: NomencladorServiceImpl,
ext-impl/src/ar/com/ospim/autorizaciones/services/NomencladorServiceImpl.java:30.
La unica diferencia necesaria es presentar el primer error SQL en una fila
explicita con estado de fallo y rollback, en lugar de depender de NOTICE.
No cambian Java, JSP, permisos, fuentes historicas ni datos reales.

SQLSTATE y rollback de bloque siguen el contrato de PostgreSQL 9.6:
https://www.postgresql.org/docs/9.6/plpgsql-control-structures.html
La separacion entre Messages y Data Output esta documentada por pgAdmin:
https://www.pgadmin.org/docs/pgadmin4/latest/query_tool.html
