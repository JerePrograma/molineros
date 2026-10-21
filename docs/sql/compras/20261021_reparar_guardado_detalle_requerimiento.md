# Reparar el guardado del detalle de requerimiento

Ejecutar solamente `20261021_reparar_guardado_detalle_requerimiento.sql`.
Es un parche incremental para completar las tres funciones de guardado despues
de una instalacion parcial. No requiere reejecutar los instaladores completos.

## Causa comprobada

El error del 21/10/2026 muestra que la funcion clasificada instalada llama a
`compras.guardar_requerimiento_detalle` con 14 argumentos, incluyendo
`p_id_tipo_prestacion` antes de `p_usuario`. Esa sobrecarga falta en el destino.
El valor `idDetalle=null` corresponde a un alta; no explica esta excepcion.
El contrato Java ya usa correctamente la funcion clasificada de 14 argumentos.

La variante `20261021_instalar_compras_sin_paso66_y_sin_sobrecarga.sql` omite
los pasos 32/33/34 y conserva los dos llamadores que encuentre instalados.
Puede conservar un estado mixto como el del error; el log por si solo no prueba
que esa variante haya sido el archivo ejecutado. Su guia ya exige un
incremental especifico para completar los tres guardados.

## Que instala y que comprueba

Usa los tres CREATE OR REPLACE canonicos de
`20261013_sector_nomenclador.sql`, en este orden:

1. `compras.guardar_requerimiento_detalle` de 14 argumentos (faltante).
2. `compras.guardar_requerimiento_detalle` de 13 argumentos (compatible).
3. `compras.guardar_requerimiento_detalle_clasificado` de 14 argumentos.

Valida cuerpos conocidos de los llamadores y auxiliares, la metadata de las
funciones de guardado y las dependencias de nomencladores antes de reemplazar.
Rechaza los triggers legacy activos que la migracion fuente tampoco admite.
Si falta `sector_nomenclador`, su resolver o su validador, se detiene sin
instalar parcialmente. No reconstruye configuracion ni reemplaza auxiliares.

Una sobrecarga nueva recibe el propietario y las autorizaciones EXECUTE de la
firma de 13 argumentos. Las funciones ya existentes conservan propietario y
permisos. El control final exige que todo rol autorizado en alguno de los dos
llamadores pueda ejecutar la sobrecarga interna, considerando PUBLIC y
membresias. Si hay diferencias, devuelve los roles afectados sin ampliarlos.
CREATE OR REPLACE conserva ACL/owner existentes segun PostgreSQL:
https://www.postgresql.org/docs/9.6/sql-createfunction.html
La descomposicion de ACL usa aclexplode/acldefault, presentes en 9.6.5:
https://www.postgresql.org/docs/9.6/release-9-6-5.html

No modifica filas de requerimientos, detalles ni configuracion. Conserva las
asociaciones por sector/rubro, incluida la configuracion especifica de DIABETES
si ya estaba instalada. No cambia Java, JSP, estados, triggers ni permisos del
portal. Al usar las funciones reparadas siguen vigentes las validaciones de
negocio: estado PENDIENTE, rubro, prestacion activa y nomenclador configurado.

## Ejecucion en pgAdmin

1. Usar la base destino de la aplicacion, con respaldo disponible y la
   aplicacion detenida durante la instalacion. Ensayar primero en una copia.
2. Abrir una conexion nueva con un rol autorizado a reemplazar los guardados,
   crear la sobrecarga y asignarle el propietario/permisos de la firma antigua.
3. Abrir y ejecutar TODO `20261021_reparar_guardado_detalle_requerimiento.sql`.
   Es ASCII sin BOM, valido tanto como ISO-8859-1 como UTF8. Las tildes de los
   cuerpos originales se preservan mediante constantes Unicode de PostgreSQL.
4. Leer la fila final en Data Output, devuelta despues del COMMIT:

| resultado | Interpretacion |
|---|---|
| REPARACION_CONFIRMADA | Tres cuerpos canonicos y autorizaciones verificadas; transaccion confirmada. |
| ERROR_REPARACION_REVERTIDA | Fallo en la etapa indicada; todos los cambios del bloque se revirtieron. |

El mensaje generico de exito del cliente y su exit code 0 NO sustituyen esa
columna. El parche captura el primer error para mostrar etapa, SQLSTATE,
mensaje, detalle y sugerencia en la grilla. Si hay error, conservar esa fila;
no eliminar controles ni volver a ejecutar el historial para continuar.
Una desconexion o cancelacion puede impedir obtener resultado: en ese caso
relevar las funciones desde una conexion nueva antes de asumir exito.

## Confirmacion posterior desde otra conexion

Ejecutar esta consulta de solo lectura. Debe devolver tres filas con
`verificada = true`; comprueba firma y cuerpo, no solo el nombre.

```sql
SELECT firma, COALESCE(md5(replace(p.prosrc, E'\r\n', E'\n')) = huella, FALSE) AS verificada
FROM (VALUES
  ('compras.guardar_requerimiento_detalle(integer,integer,character varying,integer,integer,character varying,character varying,integer,integer,character varying,integer,text,integer,character varying)', 'fbb7d73f6122436ae6aed19ad8ff8f5b'),
  ('compras.guardar_requerimiento_detalle(integer,integer,character varying,integer,integer,character varying,character varying,integer,integer,character varying,integer,text,character varying)', '7b9ccfce93c709b3c402cf2d267f6d86'),
  ('compras.guardar_requerimiento_detalle_clasificado(integer,integer,character varying,integer,integer,character varying,character varying,integer,integer,character varying,integer,text,integer,character varying)', 'd90183584e6ce4c578dd26a96f09cee2')
) AS esperado(firma, huella)
LEFT JOIN pg_proc p ON p.oid = to_regprocedure(esperado.firma);
```

Retomar la aplicacion y repetir el alta desde Compras. Que la dependencia
tecnica este completa no garantiza que la prestacion 10809/tipo 9 pertenezca
al sector y rubro del requerimiento 1375: eso se valida con datos del destino.
No usar una llamada de escritura al requerimiento real como diagnostico SQL.

## Trazabilidad y validacion

Fuente: migracion de nomencladores del 13/10, cuerpos conservados sin cambios.
Referencia de contrato: NomencladorServiceImpl de Autorizaciones, linea 30.
Referencia de propagacion del error: ReintegroServiceImpl.cambiarEstadoCuota
de Liquidaciones, linea 2997. Envoltura de resultado/rollback: instalador
pgAdmin del 21/10. Las fuentes historicas permanecen intactas.

Validacion local el 21/10/2026: PostgreSQL 17.5 temporal en
127.0.0.1:54394, UTF8, archivo CRLF. Inicio 15:27:56 -03. Se reprodujo el error
original antes de corregir. Los diez grupos del arnes terminaron aprobados:

- Error original y firma inexistente desde la clasificada.
- Instalacion desde ese estado y tres cuerpos canonicos exactos.
- Alta con los IDs del log en datos sinteticos y edicion por ambos llamadores.
- Seis rechazos de negocio sin detalles adicionales.
- Reejecucion con dump completo identico.
- Instalacion y guardado desde las dos revisiones legacy reconocidas.
- Rechazo de un cuerpo desconocido con dump completo identico.
- Fallo provocado en validacion final y rollback completo del DDL.
- Propietario distinto, PUBLIC revocado, GRANT OPTION y default privileges.
- Permisos incompatibles entre caller e interna: rechazo con rol y rollback.

Evidencia local ignorada: tmp/reparar-detalle-20261021/results.json,
patch_manifest.json, harness.py, verify_patch.py y logs adyacentes.
El arnes final termino con exit code 0. La primera prueba detecto una constante
Unicode incorrecta en la envoltura; fue corregida y todo el arnes repetido.
El servidor temporal se detuvo correctamente, exit code 0; puerto libre.

Ejecucion real en PostgreSQL 9.6 PENDIENTE: los binarios oficiales de EDB
estaban disponibles, pero su descarga se interrumpio por timeout y se detuvo.
La sintaxis usada se contrasto con documentacion 9.6 y los cuerpos de negocio
son exactos a la migracion existente; esto no sustituye un ensayo sobre una
copia restaurada de la base de produccion. PRUEBA UI MANUAL PENDIENTE.
No hubo build Java/JSP ni despliegue porque el cambio es exclusivamente SQL.
No se accedio a produccion ni se ejecuto el parche contra una base real.

## Registro de entrega local

- Repositorio: https://github.com/JerePrograma/molineros.git.
- Ruta: C:/wsmolineros/ext. Rama: main.
- HEAD inicial y final: d4b5fc5ca79a76c1fad611c7d05da26a31909e08.
- Dos archivos nuevos: este documento y su SQL homonimo; fuentes de guardado
  intactas. Durante el trabajo aparecio un cambio ajeno en
  20261021_incorporar_prestador_1297_sin_correo.sql; se preservo sin intervenir.
- Sin commit ni push: entrega local para revision y ejecucion del operador.
- Ambos archivos escritos explicitamente como ISO-8859-1 sin BOM; todos sus
  bytes son ASCII. Se verificaron BOM, mojibake, espacios y hashes de cuerpos.
- Comprobaciones: baseline Git/fetch/switch/fast-forward, arnes SQL completo,
  comparacion independiente de payloads y controles de bytes: exit code 0.
- git diff --check/status/stat/diff ejecutados; las dos rutas son nuevas y
  aun no agregadas. Tambien se reviso su contenido completo y sus espacios.
- La consulta de listeners 54393/54394 no encontro conexiones (exit code 1
  de esa consulta opcional); ambos clusters propios estan detenidos.
- Consultas auxiliares de exploracion con rutas opcionales ausentes no
  afectaron archivos ni pruebas. No hay fallos funcionales finales pendientes
  en la base sintetica. Las limitaciones de version/datos/UI siguen arriba.
- Riesgo residual: contratos, permisos y datos reales distintos del fixture.
  El preflight detiene revisiones desconocidas; no prueba las reglas reales
  del requerimiento 1375 sin relevar el destino y completar su prueba UI.
