# Normalizacion de descripcion_prestador de presupuestos de Compras - QA

Estado final del producto: APLICADO_QA_CON_UI_PENDIENTE.
Fecha: 02/11/2026. La normalizacion esta publicada, aplicada en QA, instalada,
arrancada y verificada por SQL independiente, hashes, HTTP anonimo y logs.
No se declara exito total: falta la prueba UI autenticada, la compilacion real
de los JSP afectados y los perfiles que requieren la sesion G.

Destino unico autorizado y utilizado: 12.1.1.12:5432/devmolineros.
Produccion y cualquier otra base quedaron fuera del alcance. Esta etapa no
creo bases de datos ni uso una base de fixture distinta de devmolineros.

## Repositorio, baseline y coordinacion

- Repositorio: https://github.com/JerePrograma/molineros.git.
- Ruta unica: C:\wsmolineros\ext; rama main/origin/main.
- HEAD inicial de esta normalizacion: 86d87e9a64fa52e3174c8ebbca248a6ae66f2051.
- Commit del producto: 2706b32d2f5e6d1ac9bef2c781d64b128d5b487e,
  "Normaliza la descripcion del prestador de presupuestos de Compras".
- Commit de correccion de sintaxis: b85c0bc87205b204a0ef0e300e54c82296ebe335,
  publicado en main. Modifica solo los parentesis CASE de dos lineas SQL.
- Cambio OC concurrente coordinado: 3f60e79d8ee96ad50f2d13240a9858ddb5924640,
  "Implementar listado y consulta de ordenes de compra emitidas".
- El commit de producto tiene exactamente las diez rutas de la tabla siguiente.
  Al cerrar este producto, main y origin/main coinciden en
  b85c0bc87205b204a0ef0e300e54c82296ebe335 y el checkout esta limpio. Si este
  informe se publica en un commit documental posterior, su SHA se registrara
  en el recibo externo de publicacion; no altera el SHA del producto o del JAR.
- La ventana, detencion, instalacion, arranque y publicacion pertenecen a root.
  La ejecucion operativa fue realizada por root. La subtarea de revision
  preparo SQL, schema, harness e informe y verifico sus recibos; no opero Tomcat.

## Defecto demostrado y referencia legacy

El corte instalado tenia 100 documentos de presupuesto/orden/Empresa. En el
campo descripcion_prestador existian 49 copias derivables de proveedores reales:
45 activas y 4 dadas de baja. No aparecieron nombres libres ni proveedores
huerfanos en esa copia. Las otras 51 filas tenian descripcion_prestador nula.
Los nombres libres de Empresa son otro dato y se conservan.

Los lectores nativos y la persistencia Java todavia dependian de la copia. Al
ensayar el contrato sin columna sobre el baseline se reprodujeron 11 fallos de
lectura y 7 de insercion. Las pruebas distinguen que esos fallos son la
reproduccion del defecto; no son pruebas aprobadas contra el codigo inicial.

REFERENCIA LEGACY ELEGIDA:

- En Compras: vista existente compras.requerimiento_orden_compra_consulta,
  proyecciones JDBC explicitas, siete cuerpos realmente instalados y el bean
  PrestadorCotizacion.getDescripcionVisible()/getEtiquetaVisible(). Se conserva
  el formateo Java con Locale.ROOT; no se lo reimplementa en SQL Unicode.
- En Liquidaciones: ext-impl/src/ar/com/ospim/liquidaciones/services/
  EditarLiquidacionServiceImpl.java, getComprobantesLiquidaciones(...Connection),
  lineas 550-605, para estructura JDBC y propiedad de los recursos/conexion.
- Consulta por identidad en Liquidaciones: EditarLiquidacionServiceUtil.java,
  getImagenes(Liquidacion), linea 759, usa
  PrestadorServiceUtil.getPrestador(l.getId_prestador()). Se reutiliza la
  convencion de resolver el maestro por ID; no sus detalles de manejo de error
  ni una dependencia runtime nueva entre modulos.
- Referencia concreta de acceso al maestro: ext-impl/src/ar/com/ospim/
  liquidaciones/services/PrestadorServiceImpl.java, getPrestador(int), lineas
  184-198: CallableStatement buscar_prestador_by_id(?), ID como parametro y
  Prestador.getMapping. Lectura focal confirmada; no se modifica Liquidaciones.

Los datos instalados son la autoridad. El schema fuente contenia el CHECK
ck_compras_orden_medica_datos ligado a la copia, pero la captura real confirmo
que no estaba instalado en QA. Se quita del recurso fuente; no se inventa un
CHECK tautologico ni se cuenta como constraint eliminado de QA.

## Diez rutas de producto y motivo

| Ruta relativa a C:\wsmolineros\ext | Motivo preciso |
| --- | --- |
| docs/sql/compras/20261102_normalizar_descripcion_prestador_presupuesto.sql | Avance atomico: adapta tres escritores y cuatro lectores, crea la vista de consulta estable de 22 campos, actualiza proveedor de OC desde el maestro y retira la copia fisica con guardas. |
| docs/sql/compras/20261102_normalizar_descripcion_prestador_presupuesto_reversion.sql | Reversion minima: agrega nuevamente varchar(500) nullable al final, reconstruye desde maestro actual y restaura tres escritores/OC, conservando vista y orden de lectura de 22 campos. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/service/BusquedaRequerimientoCompraServiceImpl.java | Lectura por ID con proyeccion explicita, descripcion cruda actual y marcador de proveedor no resuelto; bloqueo solo de rp, preservando snapshot Empresa y conexion caller. |
| ext-impl/src/ar/com/ospim/compras/requerimientos/service/EditarRequerimientoCompraServiceImpl.java | El INSERT documental omite la copia; preserva los otros parametros, auditoria y pertenencia de la transaccion. |
| ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql | Recurso de schema alineado con siete cuerpos instalados y normalizados, vista estable y retiro de columna/CHECK solo fuente. No limpieza de otras funciones. |
| test/compras/ComprasDocumentosTransaccionTest.java | Regresion de coordinacion documental y contrato de binds tras omitir la copia. |
| test/compras/ComprasPresupuestoPrestadorInsercionTest.java | Reproduce baseline y valida INSERT sin copia, 16 binds, auditoria, metadatos y propagacion de error. |
| test/compras/ComprasPresupuestoPrestadorLecturaTest.java | Verifica lecturas vigentes, proveedor ausente, Unicode, Empresa cruda para lock y conexion caller. |
| test/compras/presupuesto_prestador_normalizacion_test.ps1 | Runner Java focal con JDK8, source/target1.5, salida aislada y logs de baseline/resultado. |
| test/compras/presupuesto_prestador_temporal_sql_test.py | Prueba real de los siete cuerpos adaptando identificadores fijos a TEMP dentro de devmolineros, datos sinteticos y ROLLBACK. |

## Contratos conservados y retiro acotado

Los parametros legacy de los tres escritores permanecen, incluido el parametro
de descripcion que deja de persistirse. No se agregan reglas funcionales nuevas
a esos cuerpos. El cambio respecto del cuerpo instalado es omitir columna y
valor del INSERT. La compatibilidad Empresa libre ya estaba instalada y se
conserva; el recurso fuente anterior estaba desactualizado.

Los cuatro lectores conservan nombres, aridades y los mismos 22 nombres, tipos,
typmods y orden de campos. Pasan a retornar la nueva vista
compras.requerimiento_presupuesto_consulta. La primera migracion cambia sus OID
y el OID interno del tipo de retorno. Es un cambio interno declarado: el shape
cliente sigue siendo de 22 campos. Las dependencias entrantes instaladas se
verifican antes de DROP/CREATE RESTRICT. Re-forward tras reversion conserva los
cuatro OID y usa CREATE OR REPLACE con el mismo retorno.

La tabla normalizada queda con 21 campos fisicos. Para la descripcion de la
vista se usa: ID nulo -> NULL; ID sin maestro -> [PRESTADOR NO RESUELTO]; maestro
existente -> descripcion cruda actual. No se generaliza upper PostgreSQL a
Unicode. La comprobacion previa de etiquetas antiguas usa equivalencia Java
solo en ASCII y aborta ante etiquetas libres/no resolubles.

Se preservan los otros 21 valores, IDs, fechas, auditoria, documentos DL, nombres
originales/persistidos, importes y campos Empresa. No se cambia ningun maestro,
FK, cache, historico ni configuracion funcional. Se mantienen 104 funciones y
cinco triggers: cambian siete definiciones objetivo y se controlan las otras
97. No se retiran efectos SQL por este cambio.

El retiro solicitado es una sola copia fisica:
compras.requerimiento_presupuesto.descripcion_prestador. El retiro operativo
esta APLICADO y verificado en QA12/devmolineros en este corte. No se eliminan sus aliases de consulta, firmas
publicas, nombres libres de Empresa ni documentos. El CHECK solo fuente citado
arriba tampoco se conserva como sustituto artificial.

La reversion no reconstruye la tabla ni recupera etiquetas historicas copiadas:
reintroduce la columna al final y completa unicamente esa columna desde el
maestro actual. La vista de 22 campos permanece para no alterar su orden.

## Evidencia disponible

| Verificacion | Resultado y limite |
| --- | --- |
| Reproduccion Java baseline | Lectura: 11 fallos; insercion: 7 fallos. Evidencia conservada en output/presupuesto-prestador-normalizacion-qa/baseline-86d87 y baseline-replay-final. JDBC simulado, sin DB/UI. |
| Java focal final | 80 controles de lectura + 78 de insercion = 158, todos aprobados, javac/java exit0; actual-86d87-2. Sigue siendo JDBC simulado. |
| Regresion documental | 48 casos aprobados informados por el runner de documentos, sin declararlos UI ni prueba DB. |
| SQL TEMP real | 164 controles aprobados sobre QA12/devmolineros. Tres escritores, cuatro lectores, Unicode crudo, marcador, orden/cantidad de 22 campos y lock nativo. ROLLBACK confirmado, TEMP ausentes y conexion cerrada. No crea DB ni usa una fixture en otra base. |
| Limites TEMP | No prueba concurrencia de sesiones, permisos/DDL permanentes ni Empresa con sucursal positiva/AFIP. El ensayo no aplica las migraciones operativas. |
| OC real de solo lectura | Siete SELECT literales con 28 controles aprobados, resultados agregados/huellas y conexion cerrada. No DML ni navegacion UI. |
| Baseline RO antes de ventana | 37 controles aprobados, 558 filas agregadas en 14 tablas y 171 columnas; conexion cerrada. No hubo cambios de DB en esa captura. |
| Compilacion focal conjunta | Tres targets Java configurados con exit0, nueve fuentes Java de la union y schema congelados; sin ant clean. No se compilaron los seis JSP por estos targets. |
| Seis JSP coordinados | Fuentes congeladas y copias byte-exact verificadas en runtime. Compilacion JSP y UI autenticada pendientes. |
| JAR nativo conjunto | Ant jar exit0; primera salida61134 y salida final6446. Se regeneraron 14 clases de 13 familias: 13 fuentes byte-iguales y 14 clases byte-iguales respecto de72115. Instalacion runtime verificada por recibo separado. |
| Ensayo DO operativo real | 474 controles, cero fallos, cuatro DO, dos ROLLBACK, restauracion total y conexion cerrada; exit0. Usa las migraciones corregidas027/74a exactas. No confirma la normalizacion permanente. |
| Aplicacion QA permanente | 445 controles, cero fallos, tres forward, dos COMMIT, conexion cerrada y exit0. Repeticion en transaccion nueva sin mutacion fisica. |
| Verificacion independiente despues | 175 controles aprobados: 14 tablas/558 filas/170 columnas, presupuesto21/vista22, 104 funciones/5 triggers/46 indices/48 constraints/8 secuencias/3 vistas. Huellas de 14 tablas, secuencias y metadatos protegidos sin deriva no prevista. |
| Instalacion runtime | Exit0, siete destinos verificados, siete estados de respaldo (cinco archivos previos y dos ausencias), 36 archivos protegidos preservados. |
| Arranque y estado runtime | Accion configurada exit0; un unico PID23972, JDK8jre y listener8080. Verificador RO: 7 destinos, 36 protecciones y 102 fuentes, 145 huellas estables. |
| Smoke HTTP anonimo | 6/6 respuestas200, cero timeouts; 1581/187/88/168/91/89ms. No prueba flujo autenticado ni JSP privados. |
| Logs posteriores | Cinco segmentos estables; cero errores nuevos, 20GET: 16x200+3x302+1x301, cero5xx. Se contabilizan tambien redirecciones/health, no veinte pruebas UI. |
| OC despues del SQL | Siete SELECT/28 controles aprobados y 61 comparaciones exactas contra antes, sin diferencias; conexion cerrada. |
| Revisiones | Diff completo/ISO/checks estaticos y dos pares de ojos sobre SQL/harness, incluida la correccion de dos lineas. |

Recibos de esta carpeta de evidencia externa:

- captura_RO_descripcion_prestador_20261102T195708714000Z/
  resultado_RO_descripcion_prestador_sin_PII.json:
  SHA256 42e7ab58fae40fa264171a4f1e9e5bd57fa50b4ed3a4bf34b01cd4a098720142.
- canon_views_TEMP_en_devmolineros_20261102.json:
  SHA256 8322e82e06cf0cd7b4116ad259929b9db9fe84addcfb1b29e1599739371490ff.
  Canones exactos PG9.6: query22=099173d3ffea37f61109578b54c06a77;
  OCnuevo=2d4abcf36abcd2f30cc4d5276b352123.
- TEMP_ejecucion_QA_dev_20261102T204123331000Z/
  resultado_TEMP_presupuesto_prestador.json: 164 aprobados, estado
  APROBADO_TEMP_ROLLBACK_CERRADO. Ejecucion de Fechas/root, no repetida por esta subtarea.
- SQL_OC_antes_2706.json:
  SHA256 507a3b86df093f20d5f03b0571d7773ab988e4c7a99c3dbfff1a063ec50d168e.
- estado_SQL_antes_2706.json:
  SHA256 230906ca1e4f190d3cab7ec31a7edecadedf5a7d4c19f73632b4a139a34bf1bd.
- build_java_union_final_20261102.json:
  SHA256 6c5267810c5e7b85d460ae16e1cd3343fe9a255246838245071149638f7155f4.
- union/resultado_jar_nativo_2706_final.json: Ant exit0, fuentes congeladas,
  cero conexiones SQL y cero escrituras de runtime por ese empaquetado.

## Pines de fuentes y artefactos

| Elemento | SHA256 |
| --- | --- |
| SQL avance | 0276763c58519160d55c00ef02a003f790a60a977204999d7b4259c4ec577645 |
| SQL reversion | 74a97673ee67e948da27f24705cac6cf72706b1ac191d6a0db2413d0be9d08cb |
| compras_schema.sql | f4ea328d53b0bf4ce5fe53bf16fbe40e9f99898ed9daca3ffc51b60723f7be0e |
| Busqueda ServiceImpl | f75964080d2ca6bda244b4e546fbad40263ceb8a8965273a0c513c6484db6680 |
| Editar ServiceImpl | b044cce1ec1b80eb4cf17774b4463a04f0e3d4cfb11f1c0cac97482fd7539c02 |
| JAR nativo final instalado en QA | 6446e73865204138db7c680dda200dbe3a7286f5d6f69272b35db636c6cdb89e |
| Harness DO externo corregido/revisado | dae215c8fb3710efc603b4fe49a4e75a079a2fdba0922e549d4636579199b70b |

Los diez archivos de texto de producto se verificaron individualmente como
ISO-8859-1 sin BOM, sin U+00C2/U+00C3 de mojibake ni U+FFFD, con diff focal sin
recodificacion masiva. SQL/schema tienen ademas verificacion de igualdad de los
siete cuerpos incrustados. El presente informe tambien se escribe de forma
explicita en ISO-8859-1 sin BOM.

## Resultado del ensayo operativo y fallo corregido

Los dos primeros ensayos terminaron honestamente con SQLSTATE42601, ROLLBACK y
conexion cerrada; no hubo COMMIT. La copia diagnostica identifico posicion23113,
linea167: IF ... IS DISTINCT FROM CASE ... END THEN necesitaba parentesis sobre
el CASE en PLPGSQL9.6. Se corrigio solamente esa linea en avance y reversion
(+2bytes cada archivo). Los siete cuerpos y el schema conservaron sus bytes.
Los originales4ad4/f315, el harness2258 y el diagnostico46a quedaron preservados.

Root ejecuto luego DO_ensayo_20261102T211046609000Z/resultado_sin_PII.json:
SHA256 f95757c0d72210bb25a01a7d6398a2f8cd423ec74e50c22ec0f6d3f36f35d265.
Resultado ENSAYO_APROBADO_ROLLBACK_TOTAL: 474 controles, 0 fallos, 4 DO exactos
(forward, repeat, reverse, re-forward), 2 ROLLBACK y exit0. La conexion cerro.
La huella del snapshot inicial y posterior al ROLLBACK fue identica:
0cbc28d78f2fadce64f4e9096a1413d309b11e37c6be23b921ba75df6f4c3044.
Forward y su repeticion fueron fisicamente iguales:
24a81db0399d0b6add9103d85c28aa726e8a4e28f6aea942f66349fcf4e19ee5.

La prueba real acredita 21 campos fisicos, consulta de 22 campos y cuatro
lectores, siete definiciones y preservacion de los otros97/5triggers, valores,
secuencias y atributos capturados. Reverse/re-forward conservaron los OID de
los lectores; el ROLLBACK restauro el estado inicial completo. No se invocaron
escritores de negocio ni nextval de negocio. El UPDATE declarado de reverse
quedo dentro del ROLLBACK. La aplicacion permanente no se infiere del ensayo: se acredita separadamente
con los recibos posteriores que se describen a continuacion.

Los 164 controles TEMP corresponden a los cuerpos7 de las fuentes originales;
esos cuerpos permanecieron byte-identicos tras el ajuste de guarda. Los 474
del ensayo operativo usan las fuentes finales027/74a. Los controles Java no
cambiaron por un ajuste exclusivo de sintaxis de guarda de migracion.

## Aplicacion QA e instalacion verificadas

Root ejecuto DO_aplicar_20261102T211627298000Z/resultado_sin_PII.json:
SHA256 5a6a6fb70c2a7df1dd0e2ff5228432e3f841530c101d2b285a4be6d92537ee85.
Resultado APLICADO_FORWARD_Y_REPEAT_NOOP_QA12: 445 controles, cero fallos,
tres forward exactos, dos COMMIT, conexion cerrada y exit0. La segunda
transaccion verifica nuevamente forward y la igualdad fisica del snapshot.

El verificador independiente estado_SQL_despues_b85c.json quedo APROBADO:
SHA256 a9ed7bb1924a3c5dc462b2ed8509b450c2eb4c143ea2a1fcf0b9dc0ae2b13a12.
Son 175 controles. Se conservan 14 tablas y 558 filas; las columnas pasan de171
a170 por la unica copia retirada. Presupuesto tiene21 campos fisicos y la vista
conserva22; siguen104funciones,5triggers,46indices,48constraints y8secuencias,
con3vistas. Los otros valores de las14tablas y sus huellas, estados de
secuencias y metadatos protegidos coinciden sin deriva no prevista.

La comprobacion OC posterior SQL_OC_despues_b85c.json tambien aprobo siete
SELECT y28controles, conexion cerrada:
SHA256 4a920d466e987a35a2cd20ef4905905665fafcff7d7ff8a5a85f1598bdfcabf7.
La comparacion coordinada de61aspectos contra el recibo anterior fue exacta.
Esto acredita lectura SQL y compatibilidad de esas consultas, no interfaz UI.

El instalador dejo UNION_7DESTINOS_36PROTECCIONES_OK_SMOKE_PENDIENTE:
C:\wsmolineros\ext\output\qa-presupuesto-descripcion-20261102\
union_20261102211442634\resultado.json,
SHA256 a682d567ed1874f75c8c356a15575de671ebb74c35768ba0975006aa1fa50911.
Verifico los siete destinos y preservo36archivos. Hay cinco respaldos de
archivos existentes y dos registros de ausencia previa. El ext-impl.jar de
runtime tiene hash6446e73865204138db7c680dda200dbe3a7286f5d6f69272b35db636c6cdb89e.
Los otros seis destinos son los JSP coordinados de OC; sus copias verificadas
no equivalen a compilacion JSP ni prueba UI.

## Arranque, hashes, HTTP y logs posteriores

arranque_b85c_exit.json confirma exit0 de la accion configurada:
SHA256 4dc4bc0eb2945b2e4614c61a4eda9cc5343ce43a27c868a8dbfbe402274f3403.
stdout351bytes: SHA256
4919202142095c6afa6aaf1b147c92b262269ae7b98339af22404d29ad36bb4c;
stderr0bytes. Se verifico una sola instancia de Tomcat, PID23972, con
C:\Program Files\Java\jdk1.8.0_251\jre\bin\java.exe y listener8080.

union/runtime_RO_20261102212125838.json:
SHA256 32d1698e3b0d5e595afbced10624f41c14eaaab02216d3b5acaa40a461f43c6d.
Estado APROBADO_RO_7TARGETS_36PROTECCIONES_102FUENTES_PID8080. Se comprobaron
7destinos+36archivos protegidos+102fuentes=145huellas estables, identidad del
runtime, main/origin iguales y linaje de fuentes. La captura no modifico runtime,
no solicito HTTP, no abrio SQL ni realizo autocorrecciones.

HTTP_post_b85c_6GET.json:
SHA256 25eb2e60330d96467a688c6ff2267cd19584848f66d6ef75c0874d42dc7d9bab.
Se hicieron tres pares de GET anonimos a /web/guest y /c/portal/login. Las seis
respuestas finales fueron200, sin timeout; duraciones1581,187,88,168,91,89ms.
Las URLs finales se normalizaron quitando identificadores de sesion. El login
anonimo responde /web/guest/login; no se infiere autenticacion de ese200.

logs_POST_arranque_HTTP_b85c.json:
SHA256 9526c70e772d663d22ab81a29f3f1d417f784492619ffd63bfc435fe5a623580.
Los cinco segmentos posteriores al baseline conservaron longitud estable.
No se detectaron errores nuevos en las categorias revisadas. El access log
incluye20GET:16x200,3x302,1x301 y0x5xx. Son solicitudes de arranque/health,
redirecciones y smoke; no veinte casos funcionales ni una prueba UI. El
capturador exporta conteos/clases y hashes, sin mensajes crudos ni datos de filas.

## Comandos y resultados relevantes

| Accion/comando | Exit y alcance |
| --- | --- |
| Guardas de raiz/remoto y registro Git; fetch/ff-only de baseline coordinados por root | 0. Solo C:\wsmolineros\ext, remoto autorizado, main. |
| test/compras/presupuesto_prestador_normalizacion_test.ps1, baseline | javac0; java1/1 esperados para reproducir los fallos de lectura e insercion. |
| Mismo runner sobre codigo final | javac0, ambos java0; 158 controles, sin DB/UI. |
| Runner documental focal | 0; 48 casos. Sin adjudicar UI/DB real a sus fixtures. |
| presupuesto_prestador_temporal_sql_test.py, modo revisado y pines | 0; 164 controles en TEMP dentro de devmolineros, ROLLBACK y cierre. |
| Accion configurada Build Java focalizado | 0 en los tres targets Java. No ant clean ni compilacion de los seis JSP. |
| ant.bat -f ext-impl/build.xml jar y regeneracion controlada | 0 finales; JDK8/Ant configurados, fuentes congeladas y JAR6446 verificado. |
| DIAG_CASE_FIX.py --modo ensayo --run-reviewed | 0; 474 controles, 4DO, 2ROLLBACK y cierre. |
| DIAG_CASE_FIX.py --modo aplicar --run-reviewed --ensayo recibo_aprobado | 0; 445 controles, 3forward, 2COMMIT y cierre. |
| Verificador SQL RO antes/despues | 0/0; 37/175 controles, 14 tablas y huellas protegidas, sin DML. |
| Siete SELECT OC antes/despues y comparador | 0/0/0; 28 controles por captura y 61 comparaciones sin diferencias. |
| Instalador de siete destinos, accion configurada Iniciar Tomcat y verificador runtime RO | 0/0/0; recibos separados, sin segunda instancia. |
| Smoke HTTP, captura de logs y git diff --check | 0; resultados y limites descritos arriba. |

Se conservaron tambien los fallos intermedios reales: dos ensayos42601 antes de
la correccion de parentesis, con ROLLBACK y sin COMMIT; primer launcher Ant con
exit1, seguido del lanzamiento corregido y Ant0; primer parser PowerShell del
reporte con exit1 e intento fallido de hashes de logs, corregidos mediante
lectura SharedRead. Estos ultimos fueron fallos de herramienta/informe, no del
producto. No se ocultaron ni se usaron para declarar exito. Las reproducciones
baseline y los fallos intermedios se distinguen de los recibos finales aprobados.

## Resumen de lo eliminado y comportamiento resultante

El cambio actual elimina UNA copia fisica:
compras.requerimiento_presupuesto.descripcion_prestador. Conserva alias y
contratos de consulta de22campos; el ID del prestador resuelve su nombre actual
desde public.prestador. No se borran nombres libres de Empresa ni adjuntos.

El acumulado de las etapas coordinadas es29columnas:17copias previas de OC,
11copias previas de afiliado y1copia de prestador en esta etapa. El indice
redundante y el CHECK duplicado de version OC se eliminaron en etapas previas;
no se cuentan como nuevas eliminaciones de este commit. El CHECK solo fuente
ck_compras_orden_medica_datos no estaba instalado: retirarlo del recurso no es
un DROP operativo adicional.

Cambiar CUIL+inte en un requerimiento en borrador cambia la referencia completa al padron y las lecturas toman
el afiliado seleccionado con sus datos vigentes; despues del borrador se conserva la identidad conforme a las validaciones Java. El ID del prestador resuelve el nombre actual del maestro.
No se conservan esas copias como fuente de verdad paralela.

## Pendientes y limites del cierre

- PRUEBA UI MANUAL PENDIENTE: navegacion autenticada, URL, controles, requests,
  respuestas, persistencia, estado final, consola y backend. El control CUA
  quedo en CONTROL STOP por entorno/URL Windows no verificable. No hay sesion
  autenticada preparada para G; sus perfiles y permisos UI siguen pendientes.
  No hubo bypass y no se declara end-to-end por Java, TEMP o HTTP anonimo.
- Compilacion JSP real pendiente: los seis archivos fueron copiados y
  verificados por hash, pero los targets Java no compilan los JSP ni los
  seis GET anonimos ejercitan necesariamente esas vistas privadas.
- Se mantienen los cinco triggers y las funciones con efectos. Su retiro exige
  demostrar y cerrar los contratos de todos los escritores externos; esta
  normalizacion no aporta esa certificacion ni autoriza DROP de esos efectos.
- Estado de cierre APLICADO_QA_CON_UI_PENDIENTE. No se declara exito total ni
  preparacion para produccion. No hubo acciones sobre produccion en esta etapa.
