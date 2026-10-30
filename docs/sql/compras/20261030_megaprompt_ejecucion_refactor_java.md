# MOLINEROS - EJECUTAR EL REFACTOR DE COMPRAS: REGLAS JAVA, VALORES DB

Fecha de las decisiones: 30/10/2026, America/Buenos_Aires.

Este documento es un prompt de ejecucion y un traspaso entre PCs. No es un
instalador SQL. Las definiciones obtenidas de catalogos son evidencia, nunca
sentencias para ejecutar automaticamente.

## 1. Encargo y autorizacion del usuario

Continua esta tarea hasta completar diagnostico, implementacion, pruebas,
commit, push y los cambios pertinentes en el ambiente objetivo. El usuario
autorizo expresamente esos pasos y quiere que la PC conectada a produccion
termine el trabajo. No termines despues de entregar otro plan o megaprompt.

No pidas de nuevo permisos rutinarios para pasos incluidos en ese alcance.
Comunica los pasos materiales antes de ejecutarlos. Si falta una definicion
funcional realmente indispensable, consulta ese punto y continua lo independiente.
No inventes catalogos, equivalencias ni resultados para declarar exito.

Objetivo: conservar el comportamiento de Compras, flexibilizarlo y eliminar
las matrices fijas y decisiones de negocio duplicadas entre Java y SQL.

Decisiones ya confirmadas:

1. Usar siempre Liquidaciones como referencia legacy de implementacion.
2. Mantener compras.sector_nomenclador como datos de configuracion.
3. Toda validacion, decision funcional y flujo de Compras se ejecuta en Java.
4. Los valores, listas y relaciones que Java evalua se administran en DB.
5. Retirar de SQL las funciones y bloques de validacion, y los efectos de
   negocio de triggers, despues de tener reemplazos Java equivalentes.
6. No usar cache estatica, de sesion ni entre operaciones. Leer DB en cada
   operacion; reutilizar las variables locales dentro de esa operacion.
7. INSUMOS DIABETES utilizara nomenclador real 15 en nuevas selecciones.
   El 15 debe ser un dato de configuracion, no una nueva whitelist Java.
8. Preservar las selecciones historicas validas, incluidos 445/tipo 3 y
   446/tipo 10 del requerimiento 1388, con sus IDs canonicos originales.
9. Ser flexible con historicos; no imponer retroactivamente una politica
   nueva de seleccion a una referencia persistida que no cambio.
10. Ante configuracion obligatoria nula, vacia o malformada, informar el
    problema y detener solo la operacion afectada. No aceptar todo ni
    restaurar IDs fijos silenciosamente.
11. Conservar permisos, identidad, auditoria, integridad, atomicidad,
    idempotencia, bloqueos y compensaciones durante el traslado de logica.
12. El usuario autoriza commit y push de las rutas pertinentes a main.

## 2. Repositorio, sincronizacion y preservacion del trabajo

Lee primero C:\wsmolineros\ext\AGENTS.md.

Repositorio: https://github.com/JerePrograma/molineros.git
Ruta unica de trabajo: C:\wsmolineros\ext
Rama: main / origin/main. No crear ramas, worktrees ni PRs.

Antes de inspeccionar Git o modificar archivos, valida la raiz y el remoto
exactamente como exige AGENTS.md. Registra HEAD, rama, estado y rutas sucias.

El origen local de este traspaso es bc7336b746eb96720b142968a1058355758c95ad.
Este SHA identifica la base del analisis, no un refactor ya implementado.
El commit que incorpora este documento y cualquier avance posterior de
origin/main deben integrarse con las reglas de AGENTS.md.

La otra PC informo 15 cambios locales en un diagnostico anterior. No asumas
que siguen siendo los mismos: inventarialos. Preserva todo trabajo previo.
No uses reset --hard, clean, stash, restore global, rebase ni force-push.
No ejecutes git add . ni git add -A. Agrega solo rutas exactas revisadas.

Ejecuta fetch, switch main y avance ff-only cuando el estado lo permita.
Si los cambios locales interfieren con el avance, registra exactamente las
rutas y diferencias. No sobrescribas ni ocultes trabajo para sincronizar.
No declares un cambio publicado si existe solo en el arbol de trabajo.

Actualizar el checkout no despliega Java. Un push desde otra PC tampoco
actualiza este checkout. Identifica separadamente fuente, build y artefacto
realmente ejecutado por Tomcat.

## 3. Referencias legacy verificadas

Usar y volver a leer los callers concretos necesarios:

- ext-web/docroot/html/portlet/liquidaciones/reportes/reporte_debitos_terciarizadoras.jsp
  lineas 11-19 en la base del traspaso: getSystemConfig y lectura Java de
  pares codigo=descripcion separados por punto y coma.
- ext-impl/src/ar/com/ospim/liquidaciones/ordenespago/action/EditarOrdenesPagoOspimAction.java
  lineas 230-234: lectura de configuracion durante la operacion y lista Java.
- ext-impl/src/ar/com/ospim/compras/requerimientos/helper/NotificarCotizacionPrestadorHelper.java
  constantes CONFIG_REQUERIMIENTO_* y resolverEmailsCopiaCotizacion:
  clave constante, lectura DB e interpretacion Java dentro del modulo.
- ext-impl/src/ar/com/ospim/compras/requerimientos/helper/EditarRequerimientoCompraHelper.java
  nuevaSeleccion y validacion tecnica: conserva una seleccion persistida
  sin revalidar contra asociaciones retiradas si no cambia ID ni rubro.

Adaptar esos patrones; no copiar reglas a JSP. No auditar Liquidaciones
completo ni modificarlo por arrastre.

Atencion: DebitoTercerizadorasAction.getConfigDebitosTercerizadorasTipoRaw
todavia devolvia un literal con un TODO en la base revisada. No tomar ese
origen como una lectura de DB ya implementada.

TraeListasServiceImpl.getSystemConfig abre una consulta por llamada, pero
devuelve cadena vacia tanto ante clave ausente como ante fallo JDBC; tambien
puede devolver null. El error local de Compras debe describir configuracion
no disponible o invalida, sin asegurar una causa que esa API no distingue.
Evitar modificar el servicio global si un ajuste local de Compras basta.

## 4. Hechos recibidos del diagnostico del 30/10/2026

Son evidencia aportada por el usuario, no una garantia de que la DB no cambio.
Confirmar contexto actual y completar solamente lo faltante.

- Servidor 12.1.1.28:5432, PostgreSQL 9.6.5, no replica.
- Conexion identificada como Produccion; la base efectiva es devmolineros.
- lportal no contenia los objetos. No deducir ambiente del nombre de base.
- Requerimiento 1388: estado 3/COTIZADO, sector 2/Prestaciones Medicas,
  activo, cargos 100/0, sin fila de vinculo RP incluso incluyendo bajas.
- Rubro 6: INSUMOS. Rubro 8: INSUMOS DIABETES.
- Detalle 445: rubro 8, canonico 10415, codigo 101002, tipo 3,
  gasto administrativo, cantidad 1, unitario/total 0, prestador 8338.
- Detalle 446: rubro 8, canonico 11452, codigo 9100045, tipo 10,
  insumos varios, cantidad 1, unitario/total 2534542.55, prestador 8338.
- Ambos canonicos activos, marca_rein_liq 3; snapshots coincidentes.
- Configuracion de sector 2: general 2/3/4/6; rubros 3/4/5 admiten
  2/3/4/6/14; rubros 6 y 8 tenian configuracion especifica solo 10.
- Las especificas reemplazan a las generales; criterios no nulos con AND
  dentro de cada fila y OR entre filas. Mantener esa semantica inicialmente.
- 445 no cumple la configuracion actual, pero conserva integridad canonica.
- 446 cumple configuracion actual y falla por la whitelist Java fija.
- validar_tipo_prestacion_detalle_nuevo y validar_requerimiento_detalle_fila
  existen, pero ningun trigger las referenciaba en el diagnostico recibido.
- Cuatro triggers de usuario activos en el alcance anterior: completar
  bajas, agregar gasto automatico, impedir duplicidad y calcular total.
- requerimiento_detalle solo tenia PK como constraint declarativa.

El informe anterior ejecuto 41 consultas en 12 bloques READ ONLY. Buscar los
archivos diagnostico_compras_1388_20261030.html y
consultas_compras_1388_20261030.sql en:

C:\Users\Programador 2\.codex\visualizations\2026\10\30\01a18cea-4c29-7873-9655-a4d9c456b18a

Si no estan disponibles, registrar el faltante y recuperar evidencia actual.
Los enlaces a archivos de otra PC no equivalen a haber leido su contenido.

Antecedentes del repositorio:

- docs/sql/compras/20260922_compras_fuente_verdad_produccion.sql
- docs/sql/compras/20260922_compras_fuente_verdad_produccion.md
- docs/sql/compras/20260922_megaprompt_codex_refactor_compras.md

Sus cantidades de septiembre no son actuales. Sus objetivos anteriores de
normalizacion no autorizan eliminar columnas o snapshots como parte de esta
tarea. Prevalecen las decisiones presentes y el catalogo instalado actual.

## 5. Completar primero el diagnostico real

Usa inicialmente bloques cortos en una misma conexion por bloque:

    BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ READ ONLY;
    SET LOCAL statement_timeout = '15s';
    SET LOCAL lock_timeout = '2s';
    SHOW transaction_read_only;
    -- Verificar on, consultar catalogos/datos tecnicos acotados.
    ROLLBACK;

No ejecutar funciones de negocio durante el diagnostico, aunque devuelvan
filas. Leer sus cuerpos con pg_get_functiondef. No probar escrituras con
rollback como supuesto diagnostico de solo lectura. Cerrar bloques fallidos.

Inventario completo de compras: tablas, columnas, tipos, defaults, constraints,
indices, reglas, vistas, RLS, funciones y todas sus sobrecargas. Recuperar
definiciones completas, firmas, atributos, longitud y hash. No confiar en
celdas truncadas. Reutilizar cuerpos anteriores solo si coincide su hash.

Incluir triggers de tablas Compras con funciones de cualquier esquema y
triggers de funciones Compras sobre tablas externas. Revisar especialmente
trg_compras_baja_vinculo_reclamo sobre autorizaciones.reclamos_prestacionales.

Leer funciones externas directamente relacionadas, pg_depend, referencias
textuales, search_path y callers Java. Distinguir dependencia detectada de
referencia dinamica no demostrable. Incluir familias de cabecera, detalle,
comparativas, prestadores, cotizaciones, notificaciones, autorizaciones,
ordenes de compra, documentos y RP; no limitarse a validar_*.

Relevar la tabla real de trae_system_config y sus sobrecargas. Consultar solo
claves pertinentes a Compras; ocultar valores de correos, secretos y tokens.
Conservar compras.sector_nomenclador y leer sus filas y catalogos relacionados.

Buscar escritores externos: triggers/reglas, funciones externas, jobs de
pgAgent/pg_cron si existen y llamadas del codigo. Descubrir tablas/columnas
antes de consultarlas. No ejecutar jobs. No inferir ausencia de integraciones
o cron del sistema operativo porque no aparezcan en pg_depend.

No usar prokind, pg_sequences, identity/generated ni sintaxis posterior a
PostgreSQL 9.6. Proteger pg_get_functiondef frente a agregados.

Exportar fuera del repositorio evidencia sin truncamiento: informe, JSON de
definiciones y anexo de consultas realmente ejecutadas. No volcar datos de
afiliados, observaciones clinicas, credenciales ni conexion autenticada.

## 6. Implementacion Java y configuracion DB

Antes de editar, registrar alcance, defecto, referencias exactas, contratos,
archivos, pruebas y riesgos segun AGENTS.md. No detenerse en ese registro.

Refactorizar WebKeysCompras completo en responsabilidades y sus callers
necesarios. Conservar claves tecnicas de request/sesion/forwards; evitar
renombrar contratos sin necesidad. Identificar valores funcionales que pueden
cambiar: sectores, rubros, nomencladores, marcas/codigos, estados/transiciones,
defaults, roles administrables, limites, vigencias y valores de precarga.
No renumerar IDs persistidos ni convertir identidades matematicas en opciones.

Para cada valor funcional: definir fuente DB unica, clave o relacion, tipo,
validacion de formato, comportamiento ante ausencia y consumidores. No crear
un motor generico de reglas ni introducir frameworks/dependencias modernos.

Fronteras: Action coordina HTTP/permisos; Helper contiene validaciones y
reglas; ServiceUtil delega; ServiceImpl consulta, mapea y persiste. No SQL en
JSP/Action/Helper ni reglas de negocio nuevas en ServiceImpl.

Una constante CONFIG_* identifica una clave. No inicializar valores DB en
static final ni cachearlos. La lectura requerida debe validar null/vacio y
formato; la configuracion opcional conserva su semantica existente explicitada.

Mover a Java la resolucion general/especifica y la evaluacion de asociaciones.
BusquedaRequerimientoCompraServiceImpl hoy llama resolver_sector_nomenclador;
adaptar a lectura de datos crudos, sin devolver una decision SQL de negocio.
El bean SectorNomencladorCompra hoy no conserva sector/rubro: ampliar ese bean
existente si hace falta, sin inventar otro DTO equivalente.
Reutilizar/adaptar la evaluacion AND/OR ya existente en Java y ubicarla en la
capa funcional. Parsear tokens exactos, no pertenencia por subcadenas.

Unificar busqueda, alta, edicion, correccion, cargas automaticas, precarga y
confirmacion RP. Retirar whitelist de tipos y comparaciones especiales como
rubro 6/tipo 10. Admitir cualquier tipo real autorizado por la configuracion.

Para referencias persistidas sin cambio, verificar identidad canonica,
existencia, activo y coincidencia del tipo, sin aplicar retroactivamente la
matriz de nuevas selecciones. La fuente es la BD autoritativa, no HTTP.
Si hay reseleccion tecnica en un RP de origen Compras, conservar identidad
fiable del detalle de origen; idRegistro temporal 1..N no es id_detalle.
No cambiar por arrastre la politica de RP manuales ajenos a Compras.

No quitar una validacion sin identificar su reemplazo y todos sus escritores.
Transportar validacion, relectura de estado, lock y escritura a la misma
conexion/transaccion cuando la decision dependa de concurrencia.

## 7. Migracion de funciones y triggers

Construir una matriz verificable: efecto SQL instalado -> callers ->
reemplazo Java -> configuracion DB -> prueba -> objeto SQL a simplificar/retirar.

Reproducir primero los efectos vigentes, incluyendo:

- Baja y auditoria al anular un requerimiento.
- Alta unica del concepto automatico y su seleccion por contexto.
- Duplicidad por ID o codigo canonico normalizado, excluyendo bajas.
- Calculo de total y tratamiento real de un total informado explicitamente.
- Correccion coordinada de detalle/comparativa, precios y adjudicacion.
- Reservas, tokens, reintentos e idempotencia de RP y notificaciones.
- Baja de RP y actualizacion de su relacion con Compras entre esquemas.

Conservar orden de bloqueos, exclusion de doble creacion y rollback total.
SQL puede conservar lectura/escritura e infraestructura transaccional; las
decisiones funcionales deben quedar en Java. Conservar PK/FK/UNIQUE e indices
estructurales existentes salvo cambio indispensable y fundamentado.

Crear scripts incrementales contra las firmas y cuerpos realmente instalados.
Usar precondiciones de existencia, firma y hashes; abortar si difieren. Guardar
definiciones anteriores y filas de configuracion afectadas. No DROP CASCADE.
No ejecutar compras_schema.sql ni una migracion historica completa por comodidad.

No retirar un trigger hasta verificar su reemplazo para todos los escritores.
Si un escritor externo no se puede migrar, resolver su ingreso al camino Java
o registrar el impedimento preciso; no dejarlo sin el efecto que necesita.

## 8. Tipo 15 y gastos administrativos

Comprobar que representa 15 y si ya existen canonicos activos propios.
Configurar INSUMOS DIABETES para sus nuevas selecciones de acuerdo con ello.
Si 15 ya existe con otro significado, no redefinirlo silenciosamente:
registrar el conflicto concreto de catalogo y resolverlo antes de esa carga.

La autorizacion NO permite convertir globalmente 11452 de tipo 10 a 15 ni
10415 de tipo 3 a 15. No ejecutar cambios masivos de tipos, snapshots o IDs
historicos. Ambos canonicos pueden estar referenciados por otras operaciones.

Si hacen falta prestaciones dedicadas de tipo 15, preparar altas con nueva
identidad solo con datos/mapeos comprobados, sin inventar un catalogo completo
a partir del nombre INSUMOS VARIOS. Si falta informacion para poblarlo, pedir
ese dato concreto y continuar Java, diagnostico y pruebas independientes.

Mantener el gasto administrativo como comportamiento configurable explicito,
diferenciado de la seleccion manual de insumos. Evitar que una regla solo 15
bloquee el cargo automatico tipo 3 o lo inserte dos veces. Investigar como el
trigger actual crea esa fila y asigna su rubro antes de elegir el reemplazo.

1388 debe seguir utilizando 445/10415/tipo 3 y 446/11452/tipo 10, sin cambiar
importes, prestador, observaciones, adjudicacion ni referencias relacionadas.

## 9. Cambios previos publicados que deben revisarse antes de desplegar

Al comenzar el traspaso habia cambios locales previos en:

- ext-impl/src/ar/com/ospim/autorizaciones/action/EditarPrestacionReclamoAction.java
- ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/datos_edicion_prestacion.jsp
- ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jsp

Agregan confirmaciones para copiar numero/fecha de comprobante a otras filas
activas durante un alta desde Compras. Durante la preparacion se detecto una
publicacion concurrente del commit 0644460d318604c1fe90b9b3a2ea7c66cfd37440,
que incluye esos tres archivos y la primera version de este documento.
Se verifico su presencia en refs/heads/main del remoto. Conservar ese trabajo;
no revertirlo ni descartarlo para sincronizar. No es el refactor terminado ni
su publicacion constituye una verificacion funcional o un despliegue.

Hallazgo de revision estatica a resolver antes de desplegar esos cambios: la fecha
copiada puede ser anterior a fechaPrestacion de otra fila destino. La UI
valida la fila origen, no todos los destinos. Ejemplo: origen 01/10, destino
20/10, emision copiada 10/10. Prevalidar destinos antes de mutar si se conserva
esa funcionalidad, reutilizando la regla de fechas de ValidarReclamo.
Comprobar que un rechazo deja todas las filas intactas y mostrar que fila
impide la propagacion. Revisar el diff publicado, corregir y probar este caso
como parte de la tarea antes de aplicar la entrega en produccion.

Revisar tambien las dos confirmaciones de numero cuando una fila pasa de OTR
a comprobante real; los consentimientos OTR y todas las filas tienen alcances
distintos. No confundirlos ni afirmar pruebas UI que no se ejecutaron.

## 10. Pruebas y entrega coordinada

Usar Java 8, Ant legacy, Liferay 5.2, Struts/JSP/JS ES5 y PostgreSQL 9.6.
Todos los textos ISO-8859-1 sin BOM. No modernizar ni reformatear por arrastre.
Usar las acciones/builds existentes; nunca ant clean ni otro JDK por defecto.

Antes de pruebas: registrar hora, HEAD, estado inicial y segmento del log.
Ejecutar build completo y pruebas focalizadas significativas antes de declarar
produccion lista. Registrar comandos, codigos de salida y limitaciones reales.
Si falta una herramienta, buscar la configuracion existente; no inventar exito.

Demostrar como minimo:

- Busqueda/alta/correccion con configuracion DB; una modificacion de valores
  se refleja sin reiniciar ni reutilizar cache de sesion.
- Nueva seleccion Diabetes tipo 15 y compatibilidad de Insumos tipo 10.
- Historicos mixtos 3/10 del 1388 aceptados sin alteracion de sus referencias.
- Gasto automatico una sola vez, valores/sector/rubro correctos.
- Protesis, Farmacia, Discapacidad, Odontologia, Legales y observaciones con
  el comportamiento que corresponda a sus configuraciones vigentes.
- Configuracion ausente/malformada y fallos de lectura sin permitir todo.
- Dos usuarios: duplicar codigo, corregir mientras otro crea RP, doble envio,
  vencimiento/recuperacion de reserva y rollback de escritura parcial.
- Baja RP sincronizada con Compras y efectos de documentos/compensacion.

Pruebas que crean o alteran operaciones deben ejecutarse con datos de prueba
identificados o en el entorno de prueba disponible. En produccion, priorizar
verificaciones de lectura y acciones funcionales expresamente parte del caso;
no enviar correos reales de prueba ni adjudicar/pagar por efecto colateral.
No usar el 1388 como dato descartable ni afirmar que se persiste una prueba
completa si solo se verifico una precarga.

Preparar un unico cambio funcional coherente, con commits revisables si hacen
falta. Mantener evidencia del artefacto anterior, SQL anterior y configuracion.
Probar precondiciones de avance y reversion. La reversion debe considerar que
el Java viejo puede no admitir nuevas cargas tipo 15: nunca borrar esas
cargas para hacer que una reversion pase.

Identificar el Tomcat real de produccion antes de operar. El stack recibido
corresponde a Linux/Tomcat 8.5.20; no asumir que las rutas Windows del entorno
de desarrollo son las del servidor. Usar el procedimiento real de servicio,
sin iniciar una segunda instancia ni matar procesos a ciegas.

La aplicacion no debe atender solicitudes con Java/SQL incompatibles. Si la
transicion no admite convivencia, drenar solicitudes y usar la ventana de
cambio/procedimiento existente para aplicar artefacto y migracion coordinados.
No retirar reglas mientras el Java ejecutado aun depende de ellas.

Revisar diff completo, encoding y git diff --check. Commit solo de rutas
exactas pertinentes, mensaje en espanol, fetch antes del push y publicacion a
origin main:main sin force. Integrar avances con las reglas de AGENTS.md.
Verificar SHA local/remoto, pertenencia del commit y estado del arbol.

Verificar que lo desplegado corresponde al SHA probado: comparar hashes de
artefactos/clases y definiciones SQL, arranque, logs y smoke tests reales.
No confundir un build aislado o una prueba estatica con un flujo end-to-end.

## 11. Criterio de cierre

Entregar:

- Diagnostico actual y JSON completo de definiciones, sin secretos.
- Diff y lista de archivos, referencias Liquidaciones y motivo por cambio.
- Matriz SQL -> Java sin efectos activos olvidados.
- Configuraciones/valores incorporados, incluido el alcance real de tipo 15.
- Scripts de avance y reversion adaptados al estado instalado.
- Pruebas aprobadas, fallidas, no ejecutadas y riesgos residuales.
- Resultado real del 1388 y de las nuevas selecciones tipo 15.
- Commits, push, SHA local/remoto y artefacto realmente desplegado.
- Estado final de servicio, logs y verificacion SQL posterior.

No termines solo con un plan. Completa los pasos autorizados y comunica un
impedimento real solo con evidencia concreta, continuando lo independiente.
No declares exito total si falta sustituir un efecto SQL, si el tipo 15 carece
del catalogo requerido, si no se aplico la entrega o si no se hicieron pruebas
obligatorias. Distinguir siempre trabajo implementado, publicado y desplegado.
