# Megascript de Compras para produccion: desde el 28/09/2026

## Entrega y alcance

Archivo ejecutable: `20261021_megascript_produccion_desde_20260928.sql`.
Preparado el 21/10/2026 sobre `main`, HEAD fuente
`ba7b5fc575d8a46a05259478ca7f8c5f42e0cc1c`, repositorio
`https://github.com/JerePrograma/molineros.git`, checkout `C:\wsmolineros\ext`.

El corte es **lunes 28/09/2026 inclusive hasta ese HEAD del 21/10/2026**.
Se reviso todo el historial de `docs/sql` del periodo, incluidas sus subcarpetas.
La seleccion usa la incorporacion/modificacion en Git, no solamente la fecha
del nombre: `20260925_cotizaciones_empresas_alta_adjudicacion.sql` entro el
30/09/2026 en `0d91cc9c` y por eso SI esta incluido.

Son **13 migraciones incrementales**. Los cuerpos funcionales y los controles
originales provienen exclusivamente de `docs/sql`; no se importo codigo de
`compras_schema.sql`, snapshots, conexiones de QA ni otras carpetas al entregable.
Los originales no se modificaron. Se normalizaron los finales de linea a LF,
se quitaron sus 13 pares exteriores BEGIN/COMMIT y se agregaron el encabezado,
la transaccion unica, el preflight y la validacion final. Los LF tambien
conservan los fragmentos comparados por las adaptaciones dinamicas originales.

**Este trabajo no ejecuto el megascript ni ninguna migracion sobre la base
del portal, QA o produccion.** Solo se usaron bases sinteticas de un cluster
temporal propio, escuchando en `127.0.0.1:54396`.

## Contrato de destino

- PostgreSQL **9.6.x**, con `server_encoding = UTF8`, como el catalogo fuente
  documentado el 22/09. La ejecucion en otra version/codificacion se rechaza.
- Base existente con las tablas y funciones anteriores al corte. No es una
  instalacion desde cero ni una actualizacion universal de cualquier revision.
- Deben existir las dos tablas de comparativas del 23/09. El script de creacion
  del 23/09 y su duplicado sin extension son anteriores al corte y no se reejecutan.
- Los contratos originales comprueban firmas, cuerpos conocidos de funciones,
  nomencladores, rubros, sectores, columnas, indices y restricciones instaladas.
  No se quitaron ni relajaron esos controles para hacer pasar el consolidado.
- Usuario ejecutor con propiedad/permisos para las funciones, tablas, indices
  y el trigger sobre `autorizaciones.reclamos_prestacionales`.
- Aplicacion detenida durante toda la migracion, como exige el SQL de nomencladores.
  `lock_timeout = 10s` corta la espera por bloqueos; no desactiva restricciones.

Antes de decidir la ejecucion hay que relevar la **base destino real** con
`20260922_compras_fuente_verdad_produccion.sql` y guardar su salida, definiciones
de funciones, constraints y triggers. Ese SQL es de solo lectura y ya esta en
esta carpeta. La validacion aislada no acredita que produccion tenga el mismo
catalogo, volumen, datos o permisos.

## Orden y trazabilidad de fuentes

Las rutas de la tabla son relativas a `docs/sql`. Las lineas corresponden al
megascript entregado. Las huellas siguientes se calculan sobre los bytes
originales de cada archivo, antes de normalizar los finales de linea.

| Bloque | Fuente | Lineas | Efecto |
|---|---|---|---|
| 01 | `compras/20260925_cotizaciones_empresas_alta_adjudicacion.sql` | 86-597 | Alta de cotizaciones de Empresas y adjudicacion; contrato incorporado a Git el 30/09. |
| 02 | `20260930_buscar_medicamentos_droga_compras.sql` | 602-643 | Busqueda de medicamentos por droga; su cuerpo final se vuelve a instalar en el bloque 11. |
| 03 | `compras/20261006_cotizaciones_identificacion_fiscal_diferida.sql` | 648-1189 | Identificacion fiscal diferida y reconstruccion del indice fiscal de Empresas. |
| 04 | `compras/20261007_edicion_empresa_cotizacion.sql` | 1194-1349 | Edicion de Empresa sobre la misma cotizacion, conservando documento y adjudicacion. |
| 05 | `20261008_observacion_interna_requerimiento_compras.sql` | 1354-1429 | Columna observacion_interna y sobrecarga de guardar_requerimiento de 24 argumentos. |
| 06 | `compras/20261013_sector_nomenclador.sql` | 1434-2439 | Configuracion de nomencladores por sector/rubro y funciones de busqueda/guardado. |
| 07 | `compras/20261013_corregir_item_antes_rp.sql` | 2444-2755 | Correccion del mismo item antes del RP y bloqueo de comparativas obsoletas. |
| 08 | `compras/20261013_autorizacion_requerimiento.sql` | 2760-3025 | Estados 6/7, descripciones, listado y adaptacion conocida de validaciones. |
| 09 | `compras/20261014_baja_vinculo_reclamo_prestacional.sql` | 3030-3314 | Baja logica de vinculo RP, trigger, anulacion y conciliacion de bajas anteriores. |
| 10 | `compras/20261015_autorizacion_orden_compra_empresas.sql` | 3319-3901 | Autorizacion corporativa 1 -> 6 -> 7 -> 5 y proteccion de la ruta generica. |
| 11 | `20261020_normalizar_drogas_compras.sql` | 3906-3948 | Busqueda por droga con normalizacion de tildes; version final del bloque 02. |
| 12 | `compras/20261020_prestadores_visibles_autorizacion.sql` | 3953-4007 | Prestadores notificados visibles tambien en estados 6/7. |
| 13 | `compras/20261021_observacion_interna_autorizador.sql` | 4012-4051 | Guardado exclusivo de observacion interna en requerimientos activos 1 a 7. |

### SHA-256 de los originales

- **01** `8e2a233f65a4cf29cdd3e05b92dc92a1c699543edeaef4805782be6b44b1b522`
- **02** `9fe11339879a73a8fd35c1e5cf396ff8e86c4eec19fa9388f96a3d49299d2359`
- **03** `8f463fa7ad7bad05bd4edc4dedf6b00e499eb4332719121ea6599a18ab0e7133`
- **04** `543a3471a5666f08a3cf34789dbf14d0e5d7d88f5d53fbcc24d3abf593b8f653`
- **05** `9acd0a0f2987059940355f8ff94be9d5b44caef7f8356ce82a818d56eb9cc5d2`
- **06** `6c96ea89a27b6eeee6889640ee584dc56d9eb1a3dfe4230438f1a45267274c21`
- **07** `675ea6ab119d5fbe7253057e4cbd9eaa2db0c2e0324ad0b5194319bd6ca7da7b`
- **08** `037afb77bcc64cbe89bb11eecf37bd09c300b57736ea774ed03cdbd151c05417`
- **09** `837a4029b8e6ba0f05fc48a6bb839b5512bb3848a25ecdf96b588123f445f899`
- **10** `d4bb68e9d8d205c41af24296f65a6901827eb748fc96cc8cb98463df823257e1`
- **11** `1beab9206638a5429b7fb64b8d0d6d57d35aaa88792b74f5e2a8130f80c09ebd`
- **12** `6ef3136166caae03b34f216c83c37d1f211fb1e678ea0f3ad1571b453e9eed0c`
- **13** `dd5ab0e32b95fb67651ca04b460de72e477d30ff17fb166f66f57aacdbcda82d`

SHA-256 del megascript final: `8323e317461c321194cb42f70864810b24f0779d64d258142eb795a4d607c221`.

## Exclusiones explicitas

El inventario Git desde el corte contiene 15 archivos SQL agregados o modificados:
los 13 incluidos y estos dos casos separados:

1. `20261013_sector_nomenclador_consultas.sql`: consultas operativas, con su
   propia transaccion READ ONLY/ROLLBACK. No es una migracion y no debe incrustarse
   en una transaccion de instalacion. Puede usarse despues como consulta.
2. `20261013_insumos_diabetes_nomenclador.sql`: su encabezado dice
   "SOLO ejecutar despues de confirmar funcionalmente que INSUMOS DIABETES
   debe buscar y guardar exclusivamente el nomenclador 10" y que "Este cambio
   NO forma parte de la migracion compatible". Se conserva como ejemplo separado;
   el consolidado mantiene la herencia general de DIABETES prevista en la
   migracion de nomencladores. No se infiere una regla nueva de su nombre.

Los Markdown explicativos se usaron para el alcance y las advertencias, no como
SQL ejecutable. Los incrementales anteriores al corte y el relevamiento del 22/09
no se reinstalan. No se importan reparaciones manuales de datos de otras carpetas
o conversaciones. Los cambios de Java/JSP, reportes, documentos y permisos del
portal no se despliegan por ejecutar SQL.

## Cambios de datos que SI contiene

No es una migracion que solo defina funciones:

- El bloque 06 crea `compras.sector_nomenclador` y carga la configuracion inicial
  solo si no existe. Si existe con el contrato reconocido, conserva la
  configuracion y no restaura asociaciones que se hayan quitado. La carga
  comprueba los IDs/rubros/nomencladores previstos por el archivo fuente.
- El bloque 09 concilia relaciones VINCULADO/ERROR cuyo RP ya tiene `baja_fecha`:
  las marca BAJA, conserva ambos IDs y registra la auditoria del RP. No cambia
  el estado del requerimiento. Ademas instala el trigger para futuras bajas.

El documento de autorizacion corporativa advierte que la migracion del 14/10
no es un prerequisito automatico de la del 15/10. Aqui se incluye por su propio
alcance funcional dentro de la entrega completa desde el 28/09.

Revisar antes de ejecutar el universo que conciliara el bloque 09:

```sql
SELECT rr.id_requerimiento, rr.id_reclamo_prestacional, rr.estado,
       rp.baja_fecha, rp.baja_usr
FROM compras.requerimiento_reclamo_prestacional rr
JOIN autorizaciones.reclamos_prestacionales rp
  ON rp.id = rr.id_reclamo_prestacional
WHERE rp.baja_fecha IS NOT NULL AND rr.estado IN ('VINCULADO', 'ERROR')
ORDER BY rr.id_requerimiento, rr.id_reclamo_prestacional;
```

El script no invoca notificaciones, reservas de envio ni correos. No modifica
maestros de Empresas o Prestadores ni aplica el ejemplo opcional de DIABETES.

## Ejecucion controlada

1. Confirmar host, puerto, base, usuario y version. Obtener backup completo,
   comprobar su restauracion y guardar el catalogo actual del destino.
2. Revisar las filas de conciliacion anteriores, la configuracion de sectores
   y los contratos originales. Si el ambiente tiene otra revision, adaptar
   puntualmente sobre evidencia; no desactivar triggers ni controles para avanzar.
3. Ensayar el archivo completo en una copia restaurada de produccion.
   Para comprobar el rollback, hacer una **copia del archivo**, reemplazar
   solamente su ultimo `COMMIT;` por `ROLLBACK;` y ejecutar esa copia completa.
   No ejecutar solo el bloque final ni modificar los COMMIT dentro de comentarios.
4. Para la aplicacion efectiva, detener la aplicacion y ejecutar el archivo
   original completo en una conexion nueva, sin otra transaccion pendiente.
   Abrir/importar el archivo como **ISO-8859-1/LATIN1**. Es SQL puro: no necesita
   comandos de psql ni includes y tambien puede cargarse en un editor SQL que
   respete la codificacion y envie el archivo completo.
5. Con psql, usar cliente de PostgreSQL 9.6, codificacion y detencion ante errores.
   Este es un ejemplo para PowerShell; reemplazar TODOS los datos del destino
   y las rutas antes de usarlo. No incluye credenciales:

```powershell
$env:PGCLIENTENCODING = 'LATIN1'
& 'C:\RUTA_CLIENTE_96\bin\psql.exe' -X -v ON_ERROR_STOP=1 `
  -h HOST_DESTINO -p PUERTO_DESTINO -U USUARIO_DESTINO -d BASE_DESTINO `
  -f 'C:\wsmolineros\ext\docs\sql\compras\20261021_megascript_produccion_desde_20260928.sql' `
  *> 'C:\RUTA_LOG\megascript-compras-20261021.log'
if ($LASTEXITCODE -ne 0) { throw 'Migracion fallida: revisar el primer ERROR del log.' }
```

6. Exigir codigo de salida 0, aviso de controles finales aprobados y cierre
   **COMMIT**. Un mensaje ROLLBACK o una conexion abortada no acredita instalacion.
   Las verificaciones finales corren antes del COMMIT; no hay un mensaje de
   exito incondicional despues de un posible rollback.
7. Abrir una conexion nueva y volver a relevar el catalogo. Confirmar los estados
   6/7 y que la consulta de conciliacion no devuelve pendientes. Desplegar las
   clases/JSP compatibles mediante el circuito habitual. Crear/asignar en el
   portal el rol `AUTORIZAR_Compras`, segun la documentacion del 13/10 y 15/10;
   este SQL no crea roles de Liferay ni asigna permisos a personas.
8. Comprobar el portal autenticado con cotizador y autorizador: transiciones,
   doble envio, OC, RP, cotizaciones, observacion interna, persistencia al
   recargar, solicitudes/respuestas y log. **PRUEBA UI MANUAL PENDIENTE**.

El consolidado tiene una unica transaccion y un bloqueo asesor por base para
evitar dos ejecuciones simultaneas del mismo archivo. Si falla cualquier bloque
o control final, no puede confirmarse una instalacion parcial de los anteriores.

### Errores, reejecucion y reversa

- Si un editor deja la sesion abortada, ejecutar solamente `ROLLBACK;` y obtener
  el **primer ERROR**. `25P02` es la consecuencia de un error previo.
- No volver a ejecutar todo el historial sobre una base mas avanzada. En
  particular, el preflight rechaza una base que ya tenga la proteccion de
  autorizacion corporativa del 15/10. Las adaptaciones antiguas reconocen cuerpos
  concretos y no son universalmente reejecutables despues de otras revisiones.
  Comparar el inventario y usar solo los incrementales faltantes revisados.
- Antes del COMMIT, ROLLBACK revierte el conjunto. Despues del COMMIT no existe
  un ROLLBACK que deshaga una transaccion ya confirmada: la reversa exige revisar
  los datos y contratos usados desde entonces, preparar una migracion puntual
  o seguir el procedimiento aprobado de restauracion del backup.
- No convertir BAJA a VINCULADO, renumerar estados 6/7 ni reinstalar funciones
  antiguas para intentar volver atras sin esa revision. Se conserva el historial.

## Validacion ejecutada el 21/10/2026

**PostgreSQL 9.6.24 real**, temporal y aislado, base UTF8 y cliente LATIN1.
Se uso el paquete binario Windows x64 de PostgreSQL distribuido por EDB,
extraido solo en `.codex/artifacts/megascript-produccion-20261021/`.
No se instalo un servicio ni se cambio la instalacion de PostgreSQL del equipo.

La instalacion completa llego a COMMIT y sus verificaciones finales pasaron.
Se comprobaron los cuerpos de **26 funciones finales** contra la ultima fuente
incluida y los marcadores de las adaptaciones dinamicas. Se verificaron las
columnas, indices, trigger de baja RP y conciliacion antes de confirmar.

- **36 comprobaciones funcionales**, repetidas tambien en otra base con el
  CHECK de estados conocido y validador anterior sin la adaptacion del 14/10.
  Cubren nomencladores y herencia de DIABETES, conciliacion, estados,
  adjudicacion, envio/aprobacion, proteccion de la ruta generica, rechazo de
  OC anticipada y doble envio, OC idempotente, observacion interna, prestadores
  en 6/7, trigger de baja RP, anulacion, correccion de item y busqueda por droga.
- Ensayo completo terminado en ROLLBACK: dump de estructura y datos identico
  antes y despues (comparacion SHA-256).
- Error provocado en el bloque 12: rollback completo, incluidos la nueva
  configuracion y los datos conciliados del bloque 09; dump identico.
- Rechazo de prerrequisito ausente, cuerpo desconocido de nomenclador,
  trigger de asociaciones activo fuera del contrato verificado y reejecucion
  sobre autorizacion corporativa instalada. En cada caso, dump identico antes
  y despues; el trigger ajeno permanece activo.
- Cobertura exhaustiva del inventario Git: 13 migraciones incluidas y dos
  exclusiones explicitas; huellas de todos los originales conservadas.
- ISO-8859-1 sin BOM, sin mojibake y revision de espacios/diff de los archivos.

Evidencia local: `.codex/artifacts/megascript-produccion-20261021/`, con
`instalacion.log`, `funcional.log`, `ensayo-rollback.log`, `error-tardio.log`,
`instalacion-cabecera-previa-check.log`, `funcional-cabecera-previa-check.log`,
`resultados.json`, `resultados-ampliados.json` y `manifest.json`.
Los rechazos de las pruebas negativas son resultados esperados, no instalaciones
fallidas sobre ambientes reales. Los primeros ajustes del generador/fixture se
resolvieron antes de ejecutar estas verificaciones finales.

Las estructuras/datos de la prueba son sinteticos y las funciones previas usan
capturas locales ya disponibles. Hay dobles limitados para el padron de Empresas,
documentos, compatibilidad/emails de prestadores y tipos externos. Esos soportes
no se incorporaron al megascript. La prueba acredita ejecucion SQL en 9.6.24;
no acredita el padron real, Document Library, SMTP, roles efectivos ni el portal.
No se ejecuto exactamente el parche 9.6.5; se uso solamente sintaxis disponible
en la rama 9.6 y se conservaron los cuerpos fuente previstos para esa rama.

No hubo build Java/JSP ni despliegue porque los unicos archivos de producto
agregados son SQL y esta guia. La preparacion y el push de estos archivos no
equivalen a autorizacion de lanzamiento ni a validacion del destino real.

## Referencias legacy y cierre del cambio

Se conservaron las referencias documentadas en los SQL originales:
`EditarLiquidacionServiceImpl.actualizaLiquidacionPrestacionEntry` para la
correccion de items, `view_prestador_externo.jsp` para identificacion fiscal,
`ReintegroServiceImpl.cambiarEstadoCuota` para estados y
`autorizaciones.busca_nomenclador` para nomencladores. No se crearon capas,
funciones de negocio nuevas ni dependencias runtime entre modulos.

Archivos agregados: solamente el `.sql` y este `.md` homonimos. El motivo del SQL
es consolidar la instalacion incremental en orden y hacerla atomica; el de la
guia es documentar fuentes, efectos de datos, limites, ensayo y operacion.
No se cambiaron Java, JSP, configuraciones, historicos SQL ni datos reales.

Los archivos se guardaron explicitamente con `encode('latin1')`, sin BOM,
y se verificaron sus bytes, ausencia de C3/C2 y U+FFFD, huellas y diff.
La revision Git usa rutas exactas, `git diff --check`, diff del index,
commit en espanol y push normal a `origin/main`. El informe final del chat
registra el commit resultante y los SHA local/remoto verificados.

Los pasos de construccion, validacion final aislada y comprobacion de bytes
terminaron con codigo 0. Las pruebas de rechazo usaron intencionalmente codigo
3 de psql y validaron ese resultado y el rollback. No se encontraron fallos
preexistentes de los archivos fuente durante la instalacion final.
