# Certificacion de escritores de efectos SQL - QA, 02/11/2026

**Resultado: retiro NO certificado. Conservar los cinco triggers y las seis
funciones de efectos.** La prueba real demuestra ingresos SQL que omiten los
reemplazos Java; no se trata solamente de un inventario externo incompleto.

## Alcance y baseline

- Repositorio: `https://github.com/JerePrograma/molineros.git`.
- Ruta: `C:/wsmolineros/ext`; rama `main`.
- HEAD de fuente y pruebas: `2876dfbe491369fcfa711d5e4f941a9b30e6999f`.
- Destino autorizado: QA PostgreSQL `12.1.1.12:5432/devmolineros` y una base de
  prueba nueva en ese mismo host. Produccion `12.1.1.28` quedo excluida.
- Captura actual: 02/11/2026, 14:39 UTC; doce consultas de solo lectura,
  transaccion revertida, conexion cerrada, cero escrituras.
- Fixture conservada: `compras_efectos_test_20261102_150557462000`.
  Todos sus datos son sinteticos; no se copiaron afiliados ni datos personales.
- Esta subtarea de certificacion no modifico Java, configuracion de QA, datos de `devmolineros`,
  funciones/triggers de esa base, archivos del 30/10, Tomcat ni produccion.
  Los cambios de DDL/DML y habilitacion de triggers ocurrieron exclusivamente
  en la fixture. No se elimino la base de prueba.

Antes de crear este documento se leyeron AGENTS y las referencias concretas;
se validaron raiz/remoto, estado limpio, rama y HEAD; `fetch`, `switch main` y
`merge --ff-only origin/main` terminaron en cero, sin cambios de baseline.
El protocolo externo registra alcance, defecto reproducido, contratos, diff,
pruebas y riesgos antes de la primera edicion del documento.

## Referencia legacy y criterio aplicado

La referencia de conexion es
`ext-impl/src/ar/com/ospim/liquidaciones/services/EditarLiquidacionServiceImpl.java`,
metodo `getComprobantesLiquidaciones(..., Connection)`, lineas 550-605 en este
baseline: conserva la conexion recibida y cierra solamente sus recursos.
Las referencias de configuracion del megaprompt se volvieron a leer:
`ext-web/docroot/html/portlet/liquidaciones/reportes/reporte_debitos_terciarizadoras.jsp`
lineas 11-19 y
`ext-impl/src/ar/com/ospim/liquidaciones/ordenespago/action/EditarOrdenesPagoOspimAction.java`
lineas 230-234. Se reutiliza la lectura por operacion y el procesamiento Java,
sin cache ni dependencia nueva entre modulos.

La referencia del propio modulo para el retiro condicionado es
`20261030_refactor_java_efectos_avance_bloqueado.sql`. La seccion 7 del
megaprompt exige verificar el reemplazo para todos los escritores antes de
retirar un trigger. Este documento agrega evidencia y conserva ese bloqueo.

## Definiciones actuales y reemplazos Java

La captura QA verifica estos seis cuerpos de efectos, sin simplificarlos:

| Funcion instalada | MD5 de pg_get_functiondef |
| --- | --- |
| `compras.agregar_gastos_envio_requerimiento()` | `ef9951e6eac71d8301599030962c816e` |
| `compras.baja_vinculo_reclamo_prestacional(integer, character varying)` | `a05aa540deefc8d2a5a93f042b2ca102` |
| `compras.baja_vinculo_reclamo_prestacional_trigger()` | `a33c36ade63ab98bceba282309bf283f` |
| `compras.calcular_total_detalle_fila()` | `757c0cf62a2b28b0782fdfad56e49d1a` |
| `compras.completar_baja_requerimiento_fila()` | `f9c7d6751c6580ffce70bbb448a15def` |
| `compras.validar_codigo_prestacion_detalle_fila()` | `ecd4d086066dc445e06d1e8152d57966` |

El ultimo hash corresponde al cuerpo instalado en QA12; no se lo reemplazo
por el mensaje de otra base. Para ejecutar los cuerpos se copio tambien la
dependencia `compras.normalizar_usuario(character varying)`, MD5
`9f5395581d8c537874902cc27ed46bbd`. Esa dependencia no agrega un septimo efecto.

| Trigger activo | Efecto y coordinador Java existente | Ingreso SQL que sigue dependiendo del efecto |
| --- | --- | --- |
| `trg_compras_requerimiento_gastos_envio` | INSERT de cabecera agrega concepto. `EditarRequerimientoCompraHelper.asegurarConceptoAutomatico`, misma TX de alta, lee concepto/rubro/cantidad de DB y evita duplicar la fila ya creada por el trigger. | INSERT directo y sobrecargas de `compras.guardar_requerimiento` de 23/24 argumentos. |
| `tr_compras_detalle_codigo_prestacion` | ID o codigo canonico normalizado, con fallback al snapshot y bloqueo de cabecera. `EditarRequerimientoCompraHelper.validarDetalleNuevoNoGeneraDuplicado`, `guardarDetalle`, `corregirItem` y coordinacion de cotizacion. | INSERT/UPDATE directo, `compras.guardar_requerimiento_detalle` y correccion SQL. No se acredita una restriccion declarativa equivalente. |
| `trg_compras_detalle_calcular_total` | Calcula ante total no informado y conserva total explicito. `WebKeysCompras.calcularPrecioTotal`, correccion/cotizacion en `EditarRequerimientoCompraHelper`, persistencia de importes con la misma Connection. | `compras.finalizar_cotizacion_requerimiento(integer, integer[], numeric[], integer, character varying)` actualiza unitario y consume el total generado por el trigger. |
| `trg_compras_requerimiento_completar_baja` | Completa fecha/usuario al pasar a 99. `EditarRequerimientoCompraHelper.anularEnTransaccion` y `EditarRequerimientoCompraServiceImpl.persistirEstado(..., baja=true)` guardan ambos en la misma TX. | `compras.cambiar_estado_requerimiento(integer, integer, character varying)` guarda estado/usuario, pero deja que el trigger complete la fecha. UPDATE directo tambien puede omitirla. |
| `trg_compras_baja_vinculo_reclamo` | Sincroniza RP/vinculo con timestamp exacto, auditoria y limpieza de token. `RequerimientoCompraReclamoPrestacionalHelper.borrarReclamoPrestacional`, transaccion RP y overload de Autorizaciones conservan la misma Connection. | `autorizaciones.borra_reclamo_prestacional(integer, character varying)` escribe RP/cuentas y depende del trigger para Compras; su firma SQL y el bridge JDBC siguen disponibles. |

Los ServiceUtil delegan; los ServiceImpl conservan JDBC parametrizado y los
Helpers coordinan los caminos funcionales Java. Los controles de los Actions
no demuestran que un ingreso SQL independiente pase por ellos.

En RP el trigger se ejecuta ante cualquier UPDATE cuyo `NEW.baja_fecha` sea
no nulo. No basta inventariar solamente la funcion que asigna la baja: otros
UPDATE de una RP ya dada de baja tambien pueden producir su efecto. La
busqueda focalizada identifico ocho cuerpos con DML sobre la tabla base RP.

## Escritores y permisos observados

Se relevaron 180 rutinas candidatas por relaciones/nombres concretos, 121
filas de dependencias y cinco reglas/vistas referentes. La clasificacion
textual encontro 33 firmas con DML sobre las cuatro tablas objetivo:

- 14 escriben `compras.requerimiento`.
- 7 escriben `compras.requerimiento_detalle`.
- 5 escriben `compras.requerimiento_reclamo_prestacional`.
- 8 escriben `autorizaciones.reclamos_prestacionales`.

Los conteos por tabla se superponen: una firma puede escribir varias tablas.
No son 180 escritores ni 48 escritores. La matriz externa conserva las 33
firmas completas, hash del cuerpo, fragmentos DML y sus limites de deteccion.
Tambien registra 21 sentencias Java focales; no equivale a certificar cada
llamada potencial desde otras aplicaciones.

La captura encontro tres roles, sin membresias. Dos admiten login y son
superusuarios: OID 10 y 3022984, fingerprints tecnicos
`e8a48653851e28c69d0506508fb27fc5` y `9f3d5cb2122db048e12efba3adb1beb1`.
Ambos tienen privilegios efectivos de INSERT/UPDATE/DELETE/TRUNCATE/TRIGGER
en las cuatro tablas objetivo. No hay RLS activo ni grants de columna
adicionales. Se obtuvieron 48 observaciones de permiso EXECUTE (pares
rol/funcion), incluyendo los contratos SQL anteriores. No se guardaron
usuarios de conexion, passwords ni DSN en esta evidencia.

Estos permisos permiten ejecutar DML o funciones sin recorrer los Helpers.
La presencia de dos superusuarios tampoco permite prometer exclusividad
Java mediante un GRANT ordinario. La politica de uso de esos roles, sus
clientes y los procedimientos de mantenimiento debe formar parte del corte.

`dblink` esta instalado. No se encontraron pg_cron/pgAgent entre las
extensiones consultadas; eso no acredita ausencia de cron del sistema
operativo, aplicaciones remotas ni SQL manual. Una rutina de consulta con
EXECUTE aparece como candidata en la matriz; no se la conto como escritor
base por ese solo hecho.

## Pruebas ejecutadas y alcance

| Prueba | Resultado | Alcance real |
| --- | --- | --- |
| `efectos_transaccion_test.ps1` | 24/24; javac 0, java 0 | Clases JDBC reales con Connection simulada; ninguna DB, red, Tomcat o UI. |
| `asociaciones_configuracion_test.ps1 -EvidenceName certificacion-escritores-20261102` | 169/169; javac 0 y cuatro ejecuciones Java 0 | 37 asociaciones, 70 core/correccion/auditoria, 33 duplicados y 29 reservas RP; fronteras simuladas, sin DB/E2E. |
| Fixture SQL aislada | 40/40; proceso 0 | Siete cuerpos y cinco triggers copiados con MD5 exacto; DDL minimo y datos sinteticos sobre PostgreSQL real QA12. |
| Extension de contratos SQL instalados | 18/18; proceso 0 | Tres funciones adicionales exactas; llamadas SQL reales ON/OFF y repeticion fisica de cotizacion; mismo DB fixture. |

La fixture original reprodujo los cinco contraejemplos al deshabilitar
exclusivamente sus propios triggers:

1. INSERT de cabecera Medicas sin detalle automatico.
2. Dos detalles del mismo canonico admitidos.
3. Unitario informado y total persistido NULL.
4. Cambio a estado 99 con fecha/usuario de baja ausentes.
5. RP de baja con vinculo Compras todavia VINCULADO y token conservado.

Con los triggers activos se verificaron gasto Medicas11503 y Otros10609,
exclusiones Sistemas/RRHH, duplicado por ID y por codigo canonico con distinto
ID, recalculo de total, preservacion de total explicito, unitario NULL,
fecha/usuario de anulacion, baja RP desde VINCULADO y ERROR, timestamp con
microsegundos, repeticion sin reescribir el vinculo y rollback conjunto.

La concurrencia utilizo dos conexiones escritoras y un observador, sin red
HTTP ni Tomcat: el segundo INSERT espero `wait_event_type=Lock` sobre la
cabecera; tras el primer commit rechazo con SQLSTATE `P0001`, quedando un solo
detalle. Con el trigger inactivo los dos INSERT confirmaron el duplicado.
Esto demuestra la necesidad del protocolo de bloqueo; no certifica todos los
flujos Java concurrentes por extrapolacion.

La extension llamo estas funciones instaladas, sin convertirlas en mocks:

- `compras.cambiar_estado_requerimiento(integer, integer, character varying)`,
  MD5 `053d77b93a30c80250ddaaa32ee40375`: sin trigger guarda 99 sin fecha.
- `compras.finalizar_cotizacion_requerimiento(integer, integer[], numeric[], integer, character varying)`,
  MD5 `576ce89ef73a78e4782f41070ee8d28d`: ON devuelve 3 y total23.31;
  OFF devuelve 2 y totalNULL. Con ON, repetir COTIZADO conserva xmin/ctid,
  sin reemplazar el precio por el enviado en la repeticion.
- `autorizaciones.borra_reclamo_prestacional(integer, character varying)`,
  MD5 `24a347d9819e73222b643e82da14f689`: ambas variantes devuelven1 y dan
  de baja la cuenta; OFF deja el vinculo VINCULADO, ON lo pasa a BAJA.

El recibo original de 40 casos se preservo. El de 18 casos es adicional y
no lo sobrescribe. La primera etapa registro cuatro rollbacks y sus
secuencias finales (cabecera8, detalle12); las secuencias pueden avanzar aun
cuando una sentencia es rechazada. La extension crea mas datos sinteticos.
Ambas etapas cerraron todas sus conexiones y dejaron cinco triggers ON en
la fixture; no ejecutaron DROP DATABASE ni retiro alguno en QA real. Una
inspeccion final adicional de solo lectura confirmo cinco triggers ON, diez
funciones, catorce cabeceras, once detalles, cinco RP y cinco vinculos. Las
secuencias de cabecera/detalle quedaron en 14/14; los huecos por rechazos son
esperados y no se intento retroceder secuencias. Su conexion tambien se cerro.

## Contrato que falta cerrar antes de DROP

El faltante es material: aun existe un contrato SQL utilizable que necesita
los efectos de los triggers, probado mediante llamadas reales. Para un corte
seguro se requiere, con sus responsables y evidencias:

1. Inventariar cada consumidor soportado de las firmas SQL y DML objetivo:
   aplicacion Java, jobs, integraciones, herramientas y mantenimiento manual.
   La matriz de 33 firmas y los permisos actuales son el punto de partida.
2. Migrar esos ingresos al coordinador Java con permisos, estado autoritativo,
   bloqueo, misma Connection, atomicidad e idempotencia equivalentes; o cerrar
   expresamente el contrato antiguo. Una wrapper SQL que siga omitiendo total,
   fecha, automatico o baja de vinculo no equivale al reemplazo Java.
3. Definir y aplicar una politica de roles/clientes que permita verificar el
   ingreso soportado. No se modificaron permisos ni roles en esta tarea.
4. Ejecutar integracion de esos caminos Java contra un esquema representativo
   sin los efectos, incluyendo concurrencia real, rollback, historicos y
   repeticion; verificar los caminos externos restantes con sus propietarios.
5. Recien entonces revisar la migracion coordinada y su recuperacion, conservar
   hashes y artefactos y habilitar el retiro exacto que haya quedado cubierto.

No se dispone de lectura ni certificacion de los jobs del sistema operativo
que ejecuta PostgreSQL en `12.1.1.12`: `/etc/crontab`, `/etc/cron.d`, crontabs
de cuentas de servicio y unidades/timers de integraciones. Tampoco se recibio
un inventario aprobado de clientes remotos o un procedimiento de mantenimiento
que cierre el DML manual. Esos materiales deben identificarse/validarse en el
host correspondiente; `pg_depend` no los sustituye. El bloqueo actual no se
atribuye solamente a ese inventario pendiente: las APIs SQL dependientes y los
permisos amplios ya estan demostrados.

Estas pruebas de escritores no certifican PASS de retiro, Java exclusivo,
UI autenticada, flujo end-to-end ni aptitud de produccion. El DDL de fixture es deliberadamente minimo, no una
copia completa de constraints, datos, permisos o integraciones de QA.

## Evidencia y hashes

Carpeta externa:
`C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/cierre_20261102`.
Los siguientes nombres son relativos a esa carpeta; los JSON usan texto ASCII
compatible ISO-8859-1, sin BOM y sin credenciales.

| Evidencia | SHA-256 |
| --- | --- |
| `certificacion_escritores_QA12_readonly_20261102.json` | `6c59ea03c72dbd701f6f247102151722f08ca4a5ac7c61035a86f09cd5b47aa2` |
| `resultado_fixture_efectos_QA12_20261102.json` | `4a6fa5d87f60f9511f3e5e222654460b93b9afffd82fa1e6141fdc176568009e` |
| `resultado_fixture_escritores_SQL_QA12_20261102.json` | `7869bfcf11256dda8514575e2d1f687c6ca6eaf32b79b5fbbd19bb4171f849af` |
| `estado_fixture_efectos_final_QA12_20261102.json` | `1a9087a01693d3c6a77c1791f7af49b96e268dfa4f43eea5a7011308d96c3959` |
| `matriz_escritores_efectos_QA12_20261102.json` | `1087560574393d4e05192935bfec1352a4ab686a992799f667947972c64e4002` |
| `matriz_escritores_efectos_QA12_20261102.csv` | `ec67187a37f1631254d7201ba968b8d490d8d88e58fef5f375360e6f68a54898` |
| `probar_efectos_fixture_QA12_20261102.py` | `977355adf5b631c0b9781b51f8503eaa725a01c5dc0a1548704cbb11bcca4b78` |
| `probar_escritores_SQL_fixture_QA12_20261102.py` | `247873c90abd6aa3985da9857917e5173e1d6452ce2e7063d5036cc19d0f069b` |

Logs de las 24 verificaciones: subcarpeta `efectos_transaccion`.
Logs de las 169 verificaciones:
`C:/wsmolineros/ext/output/asociaciones-configuracion-qa/certificacion-escritores-20261102`.
Los runners de fixture se ejecutaron mediante el Python instalado de pgAdmin
con `-S` y las librerias existentes. La autenticacion guardada se uso solamente
en memoria; el destino se valido por host, puerto, base y rol antes de operar.

El archivo nuevo se guardo explicitamente ISO-8859-1 sin BOM. La revision
incluye el diff completo, `git diff --check` y auditoria de encoding; la
publicacion corresponde al cierre coordinado de la tarea principal.
