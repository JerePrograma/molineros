# Refactor de Compras: entrega Java del 30/10/2026

Esta entrega implementa los caminos de Compras en Java y prepara la configuracion
para una migracion coordinada. La aclaracion final del usuario identifica QA
como 12.1.1.12; produccion solo se puede consultar para copiar el catalogo.
La ejecucion y el despliegue se acreditan por separado. Publicar Git no
equivale a activar esta entrega.

## Base y trabajo local

Repositorio https://github.com/JerePrograma/molineros.git, ruta unica
C:\wsmolineros\ext, rama main. HEAD inicial
`e0094bd2d721483d5e552ac5863201e2ae683ee7`.
El avance inicial ff-only aborto para preservar `view_reclamo.jsp`.
Tras autorizacion expresa se conservaron y comprometieron los cambios locales
previos en `1528ccdc`; luego se integro origin/main mediante merge
`152cff1e11a9c5b2bbe4fecb1354a71b00b4022b`, incluyendo el minimo
`f51a0aca94adb60c4273c0b511e666688d229885`. No se usaron stash, reset,
clean, rebase, ramas nuevas ni force-push.

## Implementacion

- `ConfiguracionComprasHelper`: lectura estricta por operacion de system_config,
  validacion tipada y fallo cerrado. No hay cache de configuracion estatica,
  en sesion ni entre operaciones. La conexion transaccional del caller se
  conserva en las operaciones autoritativas. Claves duplicadas o errores JDBC
  no se confunden con configuracion vacia.
- `WebKeysCompras` conserva las claves y las fachadas legacy; delega reglas,
  normalizacion e importes a los Helpers de Compras. Sus callers consumen
  roles, estados, limites y valores funcionales configurados. Los codigos de
  protocolo, claves HTTP/sesion y limites estructurales de columnas se conservan.
- `NomencladorCompraBusquedaHelper` interpreta las filas de
  `compras.sector_nomenclador`: especificas sustituyen generales, AND dentro
  de una fila y OR entre filas. No hay whitelist Java de tipos admitidos.
  Se validan todas las filas efectivas y el catalogo real. El selector y el
  alta comparten catalogo y exclusiones configuradas; errores no habilitan todo.
- Cabecera, detalle, correccion, duplicados, cotizacion, estados, anulacion,
  observacion y baja RP validan en Helper y persisten mediante JDBC bajo la
  misma transaccion. Se conservan bloqueos, orden de locks, CAS, identidad,
  auditoria, idempotencia, snapshots y rollback ante escritura parcial.
- Notificaciones resuelven candidatos, contactos, reservas y finalizacion
  en Java; JDBC mantiene locks, ON CONFLICT y CAS. La normalizacion tecnica
  `lower(btrim(contacto))` conserva LC_CTYPE instalado, sin decidir elegibilidad
  en SQL. No se compensa Document Library si el resultado transaccional es incierto.
- Presupuestos, recetas, comparativas y documentos coordinan validacion,
  persistencia y compensacion DL. Un fallo del lote revierte la transaccion;
  la compensacion solo se ejecuta tras rollback confirmado. Las opciones y
  defaults de Comparativa son configurables y se validan antes de persistir.
- Ordenes de Compra reconstruyen el contexto bloqueado dentro de su transaccion,
  verifican adjudicacion, referencias RP/presupuestos y versiones, y actualizan
  el estado mediante CAS. Los bridges legacy pasan por el mismo Helper.
- La propagacion de fecha de comprobante pre-valida todas las filas activas
  con la regla existente de ValidarReclamo antes de mutar cualquier fila. El
  rechazo identifica la fila destino y conserva todas las filas intactas.
  Consentimientos OTR/numero/fecha conservan sus alcances separados.
- La identidad entre fila temporal RP y detalle de Compras se verifica con
  informacion del servidor; los flags HTTP no reclasifican un origen NOMENCLADOR.

No se modifica la estetica ni se agregan frameworks/dependencias. Se conserva
Java 8, source/target 1.5, Liferay 5.2, JSP y JavaScript ES5. Las nuevas fuentes
y los archivos modificados se verifican como ISO-8859-1 sin BOM y sin mojibake.

## Referencias elegidas

- Liquidaciones: `ext-web/docroot/html/portlet/liquidaciones/reportes/reporte_debitos_terciarizadoras.jsp:11-19`,
  interpretacion Java de configuracion DB por operacion.
- Liquidaciones: `ext-impl/src/ar/com/ospim/liquidaciones/ordenespago/action/EditarOrdenesPagoOspimAction.java:163-168,230-234`,
  validacion antes de persistir y lectura de listas configuradas.
- Liquidaciones: `ext-web/docroot/html/portlet/liquidaciones/ordenes_pago/editar_orden_pago_ospim.jsp:66-75`,
  presentacion de errores en la vista existente.
- Liquidaciones: `ext-impl/src/ar/com/ospim/liquidaciones/services/ConceptoServiceUtil.java`,
  conexion transaccional, commit y rollback.
- Autorizaciones: `ext-impl/src/ar/com/ospim/autorizaciones/action/CuentaDocumentoHelper.java:90`,
  consulta de datos y decision de duplicidad en Java.
- Compras: Helpers y contratos existentes, contrastados con las definiciones
  realmente instaladas; no con scripts historicos como fuente de verdad.

## Diagnostico y estado de 1388

Se reutilizo el diagnostico previo de esta PC y se completo evidencia faltante
con transacciones REPEATABLE READ READ ONLY y timeouts. Se relevaron las 103
funciones de Compras, cinco funciones externas directas, cinco triggers activos,
constraints, indices, vistas, reglas y escritores RP. Los cuerpos completos y
sus hashes se conservaron fuera del repositorio, sin credenciales.

El estado actual de 446 difiere del diagnostico previo: canonico 2573/tipo 3,
snapshot codigo 9100045. El usuario confirmo una correccion externa autorizada.
No se restauro 11452 ni se reclasificaron canonicos compartidos. Al inicio, 445 conservaba
10415/tipo 3; el cambio externo posterior a11503 se detalla mas abajo. Las pruebas cubren el historico original 446/11452/tipo 10 y el
actual 446/2573/tipo 3 como no-op, sin UPDATE ni normalizacion de snapshots.
No se utilizo 1388 como dato descartable.

## Reconciliacion posterior al merge remoto

Antes del push, origin/main avanzo a 0cf7882d y se integro sin conflictos en
247a3143. Agrega una migracion 10415 -> 11503 y su prueba SQL. El usuario
confirmo la correccion externa de los historicos. Esta ejecucion no hizo
ese reemplazo ni restauro datos.

La lectura posterior de 12.1.1.28/devmolineros confirma 445/11503/tipo3 y
446/2573/tipo3. La funcion de gasto tiene MD5
`ef9951e6eac71d8301599030962c816e`. Se conserva cobertura de los historicos
originales y se agregan dos casos 11503; la suite core final pasa 169.
La semilla del sector 2 usa 11503. El instalador de configuracion rechaza una
funcion de gasto distinta, para impedir activar semillas de otro corte.

## Rectificacion de ambiente y operaciones previas

Se habia identificado equivocadamente 12.1.1.28 como QA al interpretar una
respuesta anterior. La aclaracion posterior del usuario establece que QA es
12.1.1.12. Las evidencias previas con nombres QA referidas al host 28 son
historicas: el nombre del archivo no demuestra el ambiente.

Antes de esa aclaracion se aplicaron las 67 claves nuevas y el marcador de
version en 12.1.1.28:5432/devmolineros, entre 17:49:44 y 17:49:46 UTC del
30/10/2026. El backup objetivo estaba vacio. Avance y repeticion devolvieron 0,
quedaron 68 claves unicas y la repeticion conservo xmin/ctid, sin UPDATE.
SHA256 de los valores: 3b63c688f2fdf9006f4b2793d564194a97352961aa3626a53792046395dc683c.
No se modificaron funciones, triggers ni datos de Compras/Autorizaciones
mediante esa aplicacion. Es incorrecto afirmar que no hubo escrituras en
ese host; quedan registradas y no se revierten sin una instruccion expresa.

Tambien se creo alli la base separada
compras_gastos_medicas_test_20261030173521055000 y se ejecutaron fixtures
SQL de prueba. Se conserva su identificacion y no se elimina automaticamente.
No se uso devmolineros como fixture descartable. El fixture sintetico tipo 15
no equivale a crear el catalogo real de Diabetes.

Tres validadores SQL y una sonda Java/JDBC de 314 comprobaciones pasaron en
sesiones de solo lectura del host 28, sin inicializar pools globales. Estos
resultados corresponden a ese servidor, no acreditan QA 12 ni despliegue.
Desde la aclaracion, cualquier nueva escritura queda limitada a QA 12 y el
host 28 se utiliza exclusivamente para lecturas autorizadas de catalogo.
El informe externo identifica por host los resultados posteriores.

## Scripts y ejecucion controlada de QA

1. `20261030_refactor_java_configuracion.sql`: agrega 67 claves nuevas y una
   version. Verifica la funcion instalada trae_system_config, bloquea la tabla,
   rechaza instalaciones parciales/valores diferentes y admite repeticion
   identica. No pisa claves existentes.
2. `20261030_refactor_java_configuracion_reversion.sql`: retira solo esas
   claves si version y valores siguen exactamente iguales. Detiene la reversion
   ante drift. Aplicar unicamente coordinado con el artefacto correspondiente.
3. `20261030_refactor_java_diabetes_15_catalogo_qa.sql`: altas dedicadas en QA12,
   con tres maestros, tres planes y 16 conceptos de fuente comprobada. Resuelve
   las secuencias reales del DEFAULT y avanza solo con nextval, incluso si estan
   atrasadas; nunca retrocede ni renumera filas. Rechaza deriva/duplicados y la
   repeticion conserva xmin/ctid y secuencias. No cambia la asociacion.
   `20261030_refactor_java_diabetes_15_catalogo_qa_reversion.sql` conserva las
   identidades activas y detiene un rollback destructivo; no borra cargas.
   `20261030_refactor_java_diabetes_15_condicionado.sql` activa solamente la
   asociacion de nuevas selecciones de Diabetes en QA12, despues de instalar
   Java compatible. El tipo real es 15, codigo ID, INSUMOS DIABETES. Comprueba
   host/base, version de configuracion, catalogo activo y fila objetivo exacta.
4. `20261030_refactor_java_efectos_validar.sql`,
   `20261030_refactor_java_notificaciones_validar.sql` y
   `20261030_refactor_java_orden_validar.sql`: verificaciones de definiciones
   instaladas y precondiciones para la ventana de migracion.
5. `20261030_refactor_java_efectos_avance_bloqueado.sql`: bloquea expresamente
   el retiro de triggers. Los DROP son solo comentarios. Falta certificar todos
   los escritores externos y su acceso al camino Java; no basta un flag manual.
6. `20261030_refactor_java_efectos_reversion.sql`: permite reponer solo triggers
   ausentes con funciones exactas del diagnostico; rechaza diferencias.

Esta ejecucion no retira funciones ni triggers. El host 28 conserva cinco
triggers activos; el cuerpo de gasto fue cambiado externamente a 11503 como se detalla
arriba. El reemplazo Java se prueba preservando su coexistencia con los valores
actuales. La configuracion funcional nueva no debe activarse contradiciendo
triggers de reglas todavia instalados: por ejemplo, cambiar el concepto
automatico mientras el trigger anterior sigue insertando otro concepto.
La ventana coordinada debe certificar los escritores y retirar esos efectos
antes de habilitar valores incompatibles. El avance permanece bloqueado
mientras esa cobertura no se demuestre; las pruebas aisladas no certifican
la convivencia de cualquier configuracion con triggers antiguos.
Antes de retirar efectos SQL deben certificarse los escritores externos,
las firmas legacy aun accesibles y el orden coordinado de activacion.

La reversion del Java viejo requiere verificar si hubo cargas nuevas tipo 15.
Nunca borrar esas cargas para hacer que una reversion pase. Conservar el
artefacto compatible o preparar una correccion hacia adelante si el Java viejo
no puede representarlas.

## Validacion y limites

Se ejecutaron los tres targets `compile` de la accion configurada Build Java
focalizado, con JDK 1.8.0_251 y Ant 1.10.17; no se ejecuto ant clean.
Los runners persistentes de `test/compras` registran hora, HEAD, contexto,
salida de javac y resultado Java. Prueban fuentes reales y simulan solamente
las fronteras JDBC/DL/HTTP necesarias para no escribir produccion ni enviar
correos reales. Los casos de rollback/SMTP/DL inyectan fallos deliberados y
comprueban que no se informa exito falso.

Los logs finales, conteos, SHA256 por archivo y clase, matriz SQL -> Java,
comandos/codigos de salida y SHA publicado se entregan en el informe externo
consolidado de esta ejecucion. Las advertencias del compilador por source 1.5,
APIs deprecated y logging legacy se preservan en esos logs.

PRUEBA UI MANUAL PENDIENTE. El acceso inicial de automatizacion a la sesion
existente fallo. El informe de ejecucion debe identificar cualquier prueba
posterior por URL, solicitudes, persistencia y log. No se acreditan smoke tests
reales mediante pruebas simuladas.

El Tomcat Windows local estaba escuchando en 8080 y su configuracion apunta a
12.1.1.12. El servidor Linux/Tomcat 8.5.20 observado en 12.1.1.28 no es el
Tomcat Windows. Los hashes preparados y desplegados se informan por separado.
La identificacion QA 12 obliga a contrastar sus definiciones instaladas antes
de activar el artefacto. No se habilita Java con configuracion SQL incompatible.

La entrega no acredita cierre total mientras falte el catalogo 15 real,
certificar escritores externos antes de retirar efectos SQL, aplicar el
artefacto compatible y ejecutar smoke/UI reales. No deben borrarse cargas
tipo 15 para facilitar una reversion.

## QA12: ejecucion y control del despliegue local

El usuario confirmo QA como PostgreSQL 12.1.1.12/devmolineros y despliegue
Java LOCAL en C:\apache-tomcat-8.5.23. El servidor remoto .12 detras de Apache
no es el destino de los JAR de esta tarea. La DB QA usa PostgreSQL 9.6.15.

Se relevaron 104 funciones de Compras y cinco triggers en QA12. El anexo del
informe registra cinco diferencias de cuerpo frente al corte .28 y una funcion
legacy extra sin caller Java conocido. La variante de duplicidad solo cambia
el mensaje; la compatibilidad legacy omite una excepcion puntual que el Java
lee de configuracion. Esa excepcion no tiene requerimiento ni par vigente en
QA. Los validadores aceptan exclusivamente las dos variantes QA conocidas,
por firma/hash y solo en el host .12; no se equiparan sus reglas ni se cambian
funciones. Las 104 definiciones y los cinco triggers se conservaron.

Se aplicaron en QA12 las 67 claves nuevas mas el marcador, con backup objetivo
vacio. Avance y repeticion terminaron en 0; las 68 entradas son unicas y el
segundo paso no cambio xmin/ctid. Una sonda Java/JDBC real de solo lectura paso
314 comprobaciones usando las clases reales y sin inicializar pools globales.

El catalogo dedicado se obtuvo solo de tres prestaciones activas con uso real
en el rubro 8 de produccion: 11504/9100059 SET DE INFUSION MMT 397,
11505/9100060 SENSORES 7020 y 11506/9100061 RESERVORIO MMT 332. Se excluyeron
Farmacia, la bomba sin uso en ese rubro y los genericos compartidos. En QA se
crearon 11507, 11508 y 11509 de tipo 15, sus tres planes y sus 16 conceptos,
incluidas las versiones solapadas originales. No se reclasificaron maestros.
La prueba aislada en QA12 aprobo 110 casos, incluida secuencia atrasada,
repeticion y rechazo de deriva/duplicados. La aplicacion real y su repeticion
aprobaron 15 comprobaciones; las demas filas y asociaciones quedaron intactas.

Los JAR instalados son ext-service SHA256
70c14877943cd1866e4eff932d4585683ecdaa689b7bae8c7b7baefe1f7a7338 y
ext-impl SHA256
836ac9831bee44c95ce598b57d49e0e271732b77f7e64a2ff1f62b348a29b66f.
Los targets existentes usados fueron ext-service deploy y ext-impl
deploy-impl-jar, sin deploy-properties ni despliegue global. Se respaldaron
los dos JAR y 28 archivos web; las cuatro propiedades runtime no cambiaron.

El primer arranque fallo con HTTP 500 por un error de esta instalacion: se
copiaron struts-config.xml y tiles-defs.xml fuente sobre las bases runtime.
El contrato legacy usa BASE y EXTENSION separadas: el target ext-web merge
conserva las bases de modules/portal-web.war y lleva los fuentes a *-ext.xml.
La ausencia de message-resources explico PortalInstances._initCompany NPE;
la clase Liferay no fue modificada ni hubo un defecto de Compras en esa causa.
Se restauraron las dos bases, identicas a WAR y backup, y se verificaron las
extensiones contra los fuentes. El paquete web runtime corregido de 26 JSP y
dos *-ext.xml tiene SHA256
b6a21acb3f6023ecd64f64247b94b7021122fdf595d8afa7b90a95a2de8e3367.

La parada graceful cerro 8080 pero dejo hilos Quartz no daemon. Con autorizacion
expresa posterior se termino solo el PID 91036, previamente identificado; no
se inicio otra instancia mientras seguia vivo. El segundo arranque y el estado
final HTTP/asociacion se acreditan en el informe externo de esta ejecucion.

La automatizacion de navegador se detuvo porque no pudo determinar con certeza
la URL activa de Windows. No se eludio ese control ni se ejecutaron clics
funcionales. PRUEBA UI MANUAL PENDIENTE. Las pruebas JDBC/SQL/HTTP no equivalen
a una ejecucion UI ni end-to-end. El retiro de efectos SQL sigue condicionado
a certificar todos los escritores externos; no hay DROP habilitado.

El segundo arranque recupero HTTP 200, pero PostgreSQL rechazo conexiones con
SQLSTATE 53300. El diagnostico posterior al apagado confirmo max_connections
100 y unas 50 sesiones restantes; el Tomcat local habia mantenido 50 conexiones
TCP. Se mitigo exclusivamente el perfil local, sin cambiar el servidor DB ni
cerrar clientes ajenos: pool principal 2/2/10, portal-ospim 2/2/10,
portal-lportal 2/2/3 y reportes 0/0/2 (min/inicial/max). El techo local es 25,
minimo 6. Empleadores, URLs y credenciales conservaron sus bytes. Las cuatro
claves nuevas del pool principal y cinco numeros XML son el unico cambio en
portal-ext.properties y c3p0-config.xml; la inversa restaura sus hashes iniciales.
Los hashes finales son 5eae14cd26cbb8840bcfb441f2b60aae13ce23cf0889fea05b784b528444d461
para portal-ext.properties y
34a3f9d13b6f653b1b5c355bfaee37d81ce19d26b7c463a5ba0dcb5cbadf4334 para c3p0-config.xml.
No se copiaron configuraciones con credenciales a la evidencia ni al repositorio.

El tercer arranque termino en 0 con TOMCAT_OK HTTP=200. Se observaron 23
conexiones de la instancia, dentro del techo de 25, sin nuevos rechazos de
capacidad en ese segmento. La asociacion sector2/rubro8 se activo en tipo 15;
avance y repeticion terminaron en 0, con 41 comprobaciones aprobadas. Las otras
21 asociaciones, los tres maestros, tres planes, 16 conceptos y las secuencias
quedaron intactos; la repeticion tampoco modifico xmin/ctid.

La sonda LIVE final paso 118 comprobaciones en el mismo JVM3220: antes de la
activacion, el Helper real rechazo los tres canonicostipo15 con asociacion10;
despues los acepto con asociacion15, abriendo una conexion READ ONLY nueva,
sin reiniciar el JVM ni inicializar sus ocho pools globales. Insumos/rubro6
mantuvo tipo10 y rechazo los mismos15. Los intentos previos vencidos o fallidos
por capacidad y los dos errores del adaptador Python2 previos a SQL se
conservaron en el informe, junto al reintento aprobado. No son pruebas UI.

El control HTTP posterior registro dos timeouts de 20 segundos y una respuesta
200 de 5453 ms en tres solicitudes consecutivas. Por eso no se declara un
smoke HTTP uniformemente aprobado. El jstack del PID 88392 mostro dos workers
esperando el BlockingCache/FinderCache de Liferay al leer organizaciones de
usuario; las clases de ese portal no forman parte del cambio de Compras.
Esa latencia y la prueba UI siguen como riesgos abiertos, junto a la
certificacion de escritores necesaria para retirar los efectos SQL conservados.
