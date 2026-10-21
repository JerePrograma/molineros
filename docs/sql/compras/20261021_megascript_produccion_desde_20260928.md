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

Son **14 bloques incrementales**. La regla de INSUMOS DIABETES solo con
nomenclador 10 fue confirmada por el usuario el 21/10/2026 e incorporada como
bloque 14 en la revision posterior al commit `1d2d3298`.
Se integra ademas el **prerrequisito 00 de comparativas del 23/09**, faltante
en el catalogo informado por el usuario despues del commit `15c15161`.
Los cuerpos funcionales y los controles
originales provienen exclusivamente de `docs/sql`; no se importo codigo de
`compras_schema.sql`, snapshots, conexiones de QA ni otras carpetas al entregable.
Los originales no se modificaron. Se normalizaron los finales de linea a LF,
se quitaron sus 14 pares exteriores BEGIN/COMMIT y se agregaron el encabezado,
la transaccion unica, el preflight y la validacion final. Los LF tambien
conservan los fragmentos comparados por las adaptaciones dinamicas originales.

**Este trabajo no ejecuto el megascript ni ninguna migracion sobre la base
del portal, QA o produccion.** Solo se usaron bases sinteticas de clusters
temporales propios, escuchando en `127.0.0.1:54396` (9.6.24) y
`127.0.0.1:54395` (9.6.5).

## Contrato de destino

- PostgreSQL **9.6.x**, con `server_encoding = UTF8`, como el catalogo fuente
  documentado el 22/09. La ejecucion en otra version/codificacion se rechaza.
- Base existente con las tablas y funciones anteriores al corte. No es una
  instalacion desde cero ni una actualizacion universal de cualquier revision.
- El prerrequisito 00 incorpora las dos tablas de comparativas del 23/09 cuando
  AMBAS faltan. Usa una sola vez el DDL canonico del archivo `.sql` y comprueba
  las claves padre de requerimiento, prestador y nomenclador.
- Si ambas tablas de comparativas existen, conserva sus datos/secuencias y
  valida columnas, tipos, nulabilidad, PK, UNIQUE, FK y defaults. Si existe
  solamente una o su contrato es distinto, aborta sin borrarla ni reemplazarla.
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
| 00 | `compras/20260923_requerimiento_comparativa.sql` | Antes del bloque 01 | Dependencia anterior al corte: crear ambas comparativas solo si faltan; conservar y validar si existen. |
| 01 | `compras/20260925_cotizaciones_empresas_alta_adjudicacion.sql` | 275-786 | Alta de cotizaciones de Empresas y adjudicacion; contrato incorporado a Git el 30/09. |
| 02 | `20260930_buscar_medicamentos_droga_compras.sql` | 791-832 | Busqueda de medicamentos por droga; su cuerpo final se vuelve a instalar en el bloque 11. |
| 03 | `compras/20261006_cotizaciones_identificacion_fiscal_diferida.sql` | 837-1378 | Identificacion fiscal diferida y reconstruccion del indice fiscal de Empresas. |
| 04 | `compras/20261007_edicion_empresa_cotizacion.sql` | 1383-1538 | Edicion de Empresa sobre la misma cotizacion, conservando documento y adjudicacion. |
| 05 | `20261008_observacion_interna_requerimiento_compras.sql` | 1543-1618 | Columna observacion_interna y sobrecarga de guardar_requerimiento de 24 argumentos. |
| 06 | `compras/20261013_sector_nomenclador.sql` | 1623-2628 | Configuracion de nomencladores por sector/rubro y funciones de busqueda/guardado. |
| 07 | `compras/20261013_corregir_item_antes_rp.sql` | 2633-2944 | Correccion del mismo item antes del RP y bloqueo de comparativas obsoletas. |
| 08 | `compras/20261013_autorizacion_requerimiento.sql` | 2949-3214 | Estados 6/7, descripciones, listado y adaptacion conocida de validaciones. |
| 09 | `compras/20261014_baja_vinculo_reclamo_prestacional.sql` | 3219-3503 | Baja logica de vinculo RP, trigger, anulacion y conciliacion de bajas anteriores. |
| 10 | `compras/20261015_autorizacion_orden_compra_empresas.sql` | 3508-4090 | Autorizacion corporativa 1 -> 6 -> 7 -> 5 y proteccion de la ruta generica. |
| 11 | `20261020_normalizar_drogas_compras.sql` | 4095-4137 | Busqueda por droga con normalizacion de tildes; version final del bloque 02. |
| 12 | `compras/20261020_prestadores_visibles_autorizacion.sql` | 4142-4196 | Prestadores notificados visibles tambien en estados 6/7. |
| 13 | `compras/20261021_observacion_interna_autorizador.sql` | 4201-4240 | Guardado exclusivo de observacion interna en requerimientos activos 1 a 7. |
| 14 | `compras/20261013_insumos_diabetes_nomenclador.sql` | 4247-4319 | Regla confirmada por el usuario: INSUMOS DIABETES exclusivamente con nomenclador 10. |

### SHA-256 de los originales

- **00** `c2155fe158a3d1aa346703a3a8c01dde792197744d2a4350af62921a02acec2b`
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
- **14** `54ac776a1a7caf24d11c130aa152e84300dabb21295301db788d53f27ed4a61b`

SHA-256 del megascript final: `e90886fc863c52f6230cb2e5321efc4f0a93027287ea6452c79e6e6b4bf6a8a6`.

## Exclusiones explicitas

El inventario Git del periodo hasta el HEAD fuente contiene 15 archivos SQL
agregados o modificados: 14 incluidos y una exclusion:

`20261013_sector_nomenclador_consultas.sql`: consultas operativas, con su propia
transaccion READ ONLY/ROLLBACK. No es una migracion y no debe incrustarse en una
transaccion de instalacion. Puede usarse despues como consulta.

### Inclusion confirmada de INSUMOS DIABETES

El usuario confirmo expresamente **"Incluir INSUMOS DIABETES exclusivamente
con nomenclador 10"**. Esa confirmacion satisface la condicion funcional
indicada por `20261013_insumos_diabetes_nomenclador.sql`. Su encabezado historico
se conserva intacto y el encabezado del bloque 14 aclara su inclusion autorizada.

El bloque resuelve el ID real del rubro en el sector activo PRESTACIONES MEDICAS;
no supone que su ID sea 8. Reemplaza solo las asociaciones propias de ese rubro
por una unica asociacion al tipo 10. Su conjunto efectivo deja de sumar la
configuracion general del sector. No modifica otros rubros, sus maestros ni
prestaciones historicas. Si falta un rubro unico o el nomenclador 10, aborta
toda la transaccion. La validacion final comprueba nuevamente el conjunto solo 10.

Los Markdown explicativos se usaron para el alcance y las advertencias, no como
SQL ejecutable. La unica dependencia anterior al corte incorporada es el DDL
de comparativas del 23/09; los otros incrementales previos y el relevamiento
del 22/09 no se reinstalan. El duplicado sin extension del SQL23 no se ejecuta.
No se importan reparaciones manuales de datos de otras carpetas
o conversaciones. Los cambios de Java/JSP, reportes, documentos y permisos del
portal no se despliegan por ejecutar SQL.

## Cambios de datos que SI contiene

No es una migracion que solo defina funciones:

- El prerrequisito 00 crea dos tablas vacias y sus secuencias/PK/UNIQUE/FK
  solamente si ambas faltan. No carga datos ni modifica las tablas padre.
- El bloque 06 crea `compras.sector_nomenclador` y carga la configuracion inicial
  solo si no existe. Si existe con el contrato reconocido, conserva la
  configuracion y no restaura asociaciones que se hayan quitado. La carga
  comprueba los IDs/rubros/nomencladores previstos por el archivo fuente.
- El bloque 09 concilia relaciones VINCULADO/ERROR cuyo RP ya tiene `baja_fecha`:
  las marca BAJA, conserva ambos IDs y registra la auditoria del RP. No cambia
  el estado del requerimiento. Ademas instala el trigger para futuras bajas.
- El bloque 14 reemplaza las asociaciones especificas de INSUMOS DIABETES por
  nomenclador 10. Si el rubro heredaba las generales despues del bloque 06,
  pasa a utilizar exclusivamente esta configuracion propia. Si ya tenia
  asociaciones propias, se reemplazan solo las suyas.

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
maestros de Empresas o Prestadores. La configuracion de DIABETES integra la
misma transaccion del resto de la entrega.

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
Eso incluye las tablas y secuencias creadas por el prerrequisito 00: un error
posterior tambien revierte esa creacion. No aplicar SQL23 por separado como
atajo para quitar el error del preflight.

### Diagnostico que motivo incorporar el prerrequisito 00

El usuario aporto una lectura de `devmolineros`, PostgreSQL 9.6.5, UTF8:
5 cuerpos finales coincidentes, 8 diferentes y 13 funciones ausentes. Tambien
faltaban ambas comparativas, la tabla de configuracion, las cuatro columnas
nuevas, el trigger y dos indices de la entrega. Ese estado no corresponde
a la instalacion completa del consolidado. No es una diferencia de formato.

La version inicial exigia las comparativas ya instaladas y, en ese estado,
su preflight se detenia antes del bloque 01. No se puede reconstruir que
seleccion/archivo se ejecuto originalmente solo desde el mensaje generico de
exito del cliente. El diagnostico actual no demuestra que se haya confirmado
la transaccion completa ni que las cinco coincidencias provengan de esa ejecucion.

Se identificaron los ocho hashes diferentes como versiones anteriores conocidas:
anulacion (18/09), baja de cotizacion y registro de presupuesto (27/08),
confirmacion de OC (01/09), cambio de autorizacion (13/10), ambos guardados de
detalle y listado batch de relaciones RP anteriores a sus respectivas revisiones.
En particular, `74ed87bb5dd09b0f1dac72b9683bc159` y
`f2eca47dd6573aae231c0a061634e143` son los contratos de detalle expresamente
admitidos por el SQL de nomencladores. Se conservan todos los controles;
no se fuerza la instalacion sobre cuerpos desconocidos.

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

**PostgreSQL 9.6.24 y PostgreSQL 9.6.5 reales**, temporales y aislados, base UTF8
y cliente LATIN1. La revision del prerrequisito se ejecuto tambien en 9.6.5,
la misma version informada por el usuario. Se usaron paquetes binarios Windows
x64 de PostgreSQL distribuidos por EDB,
extraido solo en `.codex/artifacts/megascript-produccion-20261021/`.
No se instalo un servicio ni se cambio la instalacion de PostgreSQL del equipo.

La instalacion completa llego a COMMIT y sus verificaciones finales pasaron.
Se comprobaron los cuerpos de **26 funciones finales** contra la ultima fuente
incluida y los marcadores de las adaptaciones dinamicas. Se verificaron las
columnas, indices, trigger de baja RP y conciliacion antes de confirmar.

- **36 comprobaciones funcionales**, repetidas tambien en otra base con el
  CHECK de estados conocido y validador anterior sin la adaptacion del 14/10.
  Cubren nomencladores y DIABETES exclusivamente con 10, conciliacion, estados,
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
- Pruebas especificas de DIABETES: busqueda y validacion limitada a 10,
  ID real del rubro distinto de 8, reemplazo de asociaciones previas, conservacion
  de otros rubros y rechazo/rollback si el rubro no existe o esta duplicado.
- Cobertura exhaustiva del inventario Git hasta el HEAD fuente: 14 bloques
  incluidos y una exclusion explicita; huellas de todos los originales conservadas.
- Prerrequisito 00: instalacion completa con ambas comparativas ausentes,
  conservacion de IDs e importes existentes, rollback hasta eliminar tambien
  sus tablas/secuencias nuevas y rechazo no destructivo de instalacion parcial,
  FK/defaults desconocidos, SERIAL sin default, UNIQUE y defaults adicionales.
- Reproduccion sintetica en 9.6.5 de los 26 estados de funcion informados:
  5 coincidentes, 8 cuerpos anteriores y 13 ausentes, sin ambas comparativas.
  Desde ese punto, el archivo completo llego a COMMIT, el control de solo
  lectura en una conexion nueva devolvio APROBADO y pasaron los 36 controles
  funcionales. No se copiaron los datos de la base real.
- ISO-8859-1 sin BOM, sin mojibake y revision de espacios/diff de los archivos.

Evidencia inicial: `.codex/artifacts/megascript-produccion-20261021/`, con
`instalacion.log`, `funcional.log`, `ensayo-rollback.log`, `error-tardio.log`,
`instalacion-cabecera-previa-check.log`, `funcional-cabecera-previa-check.log`,
`resultados.json`, `resultados-ampliados.json` y `manifest.json`.
La nueva ejecucion completa que incluye DIABETES y sus pruebas especificas
esta en `diabetes-confirmado/` dentro de esa carpeta; se revalidaron instalacion,
matriz funcional, variantes de catalogo y rollback del archivo actualizado.
La revision que incorpora el prerrequisito 00 agrega evidencia en
`prerrequisito-comparativas/` dentro de esa misma carpeta.
La ejecucion equivalente en PostgreSQL 9.6.5 esta en
`prerrequisito-comparativas-965/`; ambos conjuntos verifican las mismas ramas
de creacion, preservacion y rechazo/rollback del nuevo prerrequisito.
El ensayo desde los contratos informados se registra en esa ultima carpeta:
`resultado-catalogo-informado.json`, `diagnostico-antes-informado.log`,
`instalacion-desde-contratos-informados.log`,
`verificacion-desde-contratos-informados.log` y
`funcional-desde-contratos-informados.log`.
Los rechazos de las pruebas negativas son resultados esperados, no instalaciones
fallidas sobre ambientes reales. Los primeros ajustes del generador/fixture se
resolvieron antes de ejecutar estas verificaciones finales.

Las estructuras/datos de la prueba son sinteticos y las funciones previas usan
capturas locales ya disponibles. Hay dobles limitados para el padron de Empresas,
documentos, compatibilidad/emails de prestadores y tipos externos. Esos soportes
no se incorporaron al megascript. La prueba acredita ejecucion SQL en 9.6.24 y 9.6.5;
no acredita el padron real, Document Library, SMTP, roles efectivos ni el portal.
Se uso solamente sintaxis disponible en la rama 9.6 y se conservaron los cuerpos
fuente previstos para esa rama. Los servidores temporales escucharon solamente
en loopback; la prueba 9.6.5 uso el puerto 54395 y no la direccion real informada.

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
