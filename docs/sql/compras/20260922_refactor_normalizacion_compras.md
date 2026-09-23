# Compras - Implementacion y validacion del refactor

Estado: implementacion local y build focalizado completados. La validacion de
integracion visual no esta completada. Estrategia SQL corregida el 2026-09-23:
FASE 1 preserva datos y las 18 columnas; FASE 2 queda diferida. El incremental
monolitico mencionado en resultados historicos esta inhabilitado. Ver la guia
vigente `20260923_despliegue_refactor_compras.md` y su evidencia de ensayo.

## A. Resultado y alcance

Solo se modificaron fuentes de Compras y su documentacion. Se conserva el patron
Helper -> ServiceUtil -> ServiceImpl -> CallableStatement -> funcion PostgreSQL.
No se incorporaron dependencias de la aplicacion, capas, tablas de workflow ni
servicios paralelos. Java mantiene el source/target 1.5 del build con JDK 8.

La fuente fisica de cargos es `cargo_tercerizadora`. `cargo_ospim` y `recupero`
quedan como salidas logicas derivadas. `surge` sigue siendo un booleano independiente.
`precio_total_estimado` sigue siendo un valor fisico que admite edicion explicita.
Los datos descriptivos de afiliados, nomenclador, prestadores y empresas se leen
de sus maestros actuales.

Referencia legacy elegida antes de editar:

- `ext-impl/src/ar/com/ospim/liquidaciones/services/CatastroServiceImpl.java`,
  metodo `buscaCatastro`: consulta centralizada con CallableStatement y mapeo al
  bean. Se adapto esa convencion en el Service existente de Compras.
- `ext-web/docroot/html/portlet/liquidaciones/busqueda_liquidaciones.jsp`: opciones
  provenientes de catalogos preparados en Java. Se conservaron los componentes
  de Compras y se sustituyeron solamente las decisiones internas.
- Compras: `BusquedaRequerimientoCompraServiceImpl.mapSector/mapRequerimiento`,
  helpers de edicion y precarga RP, y funciones atomicas instaladas.
- Afiliado: contrato existente `public.busca_afiliado_domicilio`, con domicilio
  del integrante y respaldo en el titular; se limita a una fila. No se modifico
  Afiliados ni se creo una identidad alternativa basada en id_ospim.

La divergencia reproducida por inspeccion fue la persistencia de copias del
maestro y las matrices repetidas de sector/nomenclador. Java ya intentaba asignar
recupero desde los cargos en el Action y el Helper, pero la columna permitia drift
en escrituras independientes. No hay evidencia suficiente para atribuir el origen
historico concreto de la fila 1186 a un caller determinado.

## B. Modelo final

Diez tablas de Compras:

| Grupo | Tablas |
|---|---|
| Transaccionales | requerimiento, requerimiento_detalle, requerimiento_cotizacion_prestador, requerimiento_pedido_cotizacion, requerimiento_presupuesto, requerimiento_reclamo_prestacional |
| Configuracion existente | sector_requerimiento, tipo_prestacion |
| Configuracion nueva | estado_requerimiento, tipo_prestacion_tipo_nomenclador |

FKs internas agregadas cuando no existen: requerimiento.id_sector -> sector;
requerimiento.estado -> estado; detalle.id_requerimiento -> requerimiento;
detalle.id_tipo_prestacion -> tipo_prestacion; relacion N:M.id_tipo_prestacion ->
tipo_prestacion. No se agregaron FKs a maestros externos.

Se conserva `requerimiento_base_row` como contrato compuesto de salida. Agrega las
capacidades del sector, los nomencladores admitidos y el codigo/presentacion del
estado; sus campos anteriores siguen disponibles.

Identidades conservadas: afiliado por CUIL titular e integrante; nomenclador por
id_prestacion; prestador por id_prestador; empresa por CUIT y sucursal.
`id_tipo_prestacion` sigue siendo clasificacion propia de Compras.

## C. Dieciocho columnas a retirar solo en FASE 2

| Tabla | Columnas |
|---|---|
| requerimiento (13) | afiliado_id_ospim, afiliado_nombre, afiliado_apellido, afiliado_documento_tipo, afiliado_documento_nro, afiliado_direccion, afiliado_localidad, afiliado_provincia, afiliado_celular, afiliado_telefono, afiliado_email, cargo_ospim, recupero |
| requerimiento_detalle (3) | id_tipo_nomenclador, codigo_nomenclador, descripcion_nomenclador |
| requerimiento_presupuesto (2) | descripcion_prestador, descripcion_empresa |

En FASE 2 se retirara `ix_compras_requerimiento_afiliado_id_ospim`; FASE 1 lo
conserva junto con las 18 columnas. Se conserva el indice por
CUIL titular/integrante y los demas indices del modelo. Las firmas JDBC de escritura
conservan parametros antiguos para compatibilidad, pero las funciones ya no los
utilizan para persistir las copias ni para decidir recupero/cargo OSPIM.

## D. Configuracion

Estados: 1/PENDIENTE, 2/A_COTIZAR, 3/COTIZADO, 4/RECLAMO_RP,
5/ORDEN_COMPRA y 99/ANULADO. La tabla contiene codigo, descripcion, orden y activo.
La descripcion de 2 es **A COTIZAR**. El atributo nullable `descripcion_visual`
conserva **ENVIADO A COTIZAR** para la presentacion Java/JSP que ya utilizaba ese
texto. PDF mantiene la descripcion SQL anterior. No hay tabla de transiciones.
Los seis identificadores tecnicos Java quedan centralizados en WebKeysCompras;
se retiraron su lista y sus descripciones duplicadas.

| Sector | tipo_item | Alta | Cotizacion empresa | OC directa |
|---|---|---|---|---|
| Farmacia | NOMENCLADOR | si | no | no |
| Prestaciones Medicas | NOMENCLADOR | si | no | no |
| Sistemas | OBSERVACION | si | si | si |
| RRHH | OBSERVACION | si | si | si |
| Legales | OBSERVACION | no | no | no |
| Otros | OBSERVACION | no | no | no |

Se agregaron tres atributos adicionales necesarios para contratos existentes:
`busqueda_nomenclador_medica` elige el buscador especializado que ya utilizaba
Prestaciones Medicas; `permite_medicamento_legacy` conserva la compatibilidad de
Farmacia; `sector_reclamo_prestacional` conserva el codigo que recibe RP.
Estos atributos evitan volver a comparar descripciones humanas.
No se agregaron sectores Discapacidad ni Odontologia. La correccion de tildes del
texto visible en WebKeysCompras queda como presentacion, sin decidir capacidades.

Relacion N:M: 23 pares, obtenidos del universo del runtime y de la correccion
funcional de Insumos. No se uso solamente la muestra de transacciones:

| Tipo prestacion | Nomencladores |
|---|---|
| 1 ALIMENTACION | 9 |
| 2 MEDICAMENTOS | 9 |
| 3 PROTESIS TRAUMATOLOGIA | 2, 3, 4, 6, 14 |
| 4 PROTESIS CARDIOLOGIA | 2, 3, 4, 6, 14 |
| 5 PROTESIS GENERAL | 2, 3, 4, 6, 14 |
| 6 INSUMOS | 10, 14 |
| 7 PANALES | 2, 3, 4, 6 |

`rubro_prestador` guarda ALIMENTACION, MEDICAMENTOS, PROTESIS_TRAUMATOLOGIA,
PROTESIS_CARDIOLOGIA, PROTESIS_GENERAL, INSUMOS y el rubro de panales con su enie
original. Los JOIN comparan directamente public.prestador_rubro.rubro con esa
configuracion, sin normalizar la descripcion del tipo.

## E. Funciones SQL

Se conservaron las consultas centrales y sus llamadas JDBC. No se persiguio una
cuota de funciones. El inventario de la conexion local tenia 83 firmas; el
canonico objetivo tiene 78 firmas: ocho retiros y tres consultas nuevas. Este
conteo local no reemplaza el relevamiento de produccion de 81 funciones.
FASE 1 modifica 47 firmas existentes y crea tres consultas de Compras; conserva
las ocho funciones cuyo retiro corresponde exclusivamente a FASE 2.

Nuevas: `id_estado_requerimiento`, `listar_tipos_nomenclador_compras` y
`resolver_identidad_afiliado`. No se encontro un resolver reutilizable de CUIL
entre los contratos legacy inspeccionados. El resolver local sigue cambios
efectivos, evita ciclos y no elige arbitrariamente entre identidades empatadas.

Lecturas adaptadas: `requerimiento_base`, `get_requerimiento_detalle`,
`get_requerimiento_detalle_clasificado`, `buscar_requerimientos`, ambos historicos
de afiliado, `get_requerimiento_compra_pdf`, `existe_requerimiento_duplicado`,
catalogos de estados/sectores/tipos y consultas de prestadores/rubros.
El filtro p_recupero mantiene su firma y opera sobre el recupero derivado de la
lectura base. SURGE conserva su filtro independiente.

Las cuatro lecturas documentales cambian de SETOF tabla fisica a RETURNS TABLE:
`get_documento_requerimiento`, `listar_documentos_requerimiento`,
`listar_ordenes_medicas_requerimiento`, `listar_presupuestos_prestador`.
Devuelven las mismas propiedades, con descripciones actuales por JOIN.
Los DROP/CREATE de contratos conservan propietario y permisos efectivos EXECUTE.

`guardar_requerimiento_detalle` se reduce a persistencia y comprobaciones de
estado/pertenencia bajo lock. El Helper valida sector, tipo, maestro y cantidades.
`guardar_requerimiento` deja de persistir copias, cargo OSPIM y recupero.
Las operaciones de presupuesto dejan de persistir descripciones.
Las funciones atomicas consultan capacidades y codigos de estado; conservan las
comprobaciones de estado esperado y pertenencia que deben verificarse bajo lock.

Solo FASE 2 podra retirar con firmas explicitas y RESTRICT:

- validar_requerimiento_fila;
- validar_requerimiento_detalle_fila;
- validar_tipo_prestacion_detalle_fila;
- validar_tipo_prestacion_detalle_nuevo;
- validar_prestador_cotizacion_fila;
- completar_baja_requerimiento_fila;
- normalizar_rubro;
- estado_requerimiento_descripcion.

FASE 1 comprueba que los validadores no esten conectados a triggers y los
conserva. FASE 2 comprueba callers y dependencias antes de cada retiro con
RESTRICT. No se reactivan validadores historicos.

## F. Triggers

Durante FASE 1 quedan ambos triggers originales. El de total,
`trg_compras_detalle_calcular_total`, calcula en INSERT si no hay total;
en UPDATE recalcula cuando cambia cantidad/unitario y no cambio explicitamente
el total. Una actualizacion de adjudicacion que reenvia el mismo unitario conserva
el total manual. No hay CHECK de equivalencia con cantidad por unitario.

Solo FASE 2 retirara `trg_compras_requerimiento_completar_baja`, luego de QA y de
verificar los dos caminos oficiales: anular_requerimiento y cambiar_estado_requerimiento. Ambos
escriben estado y auditoria de baja/modificacion en la operacion atomica.

## G. Java y JSP

Los archivos y motivos se detallan en el inventario al final de este documento.
Los cambios relevantes son:

- Beans existentes transportan configuracion. RequerimientoCompra deriva
  recupero del cargo; los setters antiguos se conservan como compatibilidad y
  no almacenan un segundo valor. SURGE conserva sus setters/getters.
- WebKeysCompras concentra protocolos; las capacidades y admisiones reciben
  los beans de configuracion. Las descripciones de estados salen de la tabla.
- EditarRequerimientoCompraHelper mantiene validacion autoritativa de rango,
  suma, seleccionabilidad y maestro. Se retiran calculos duplicados de recupero.
- NomencladorCompraBusquedaHelper consulta los nomencladores admitidos por cada
  tipo y reutiliza los Services legacy existentes. Sigue excluyendo bajas.
- ServiceImpl agrega CALLs de catalogos y mapea sus resultados. No dispersa SQL
  nativo por Java. ServiceUtil y la precarga RP conservan sus fachadas existentes.
- JSP/ES5 reciben capacidades y listas. No agregan controles ni alteran estructura,
  CSS, botones, permisos, navegacion, columnas o distribucion.

No se tocaron JRXML, Jasper compilado, generador Excel, plantillas de correo ni
estructura de Document Library. Se preservaron aliases para esas salidas.
Esto es evidencia de alcance, no una prueba visual de los artefactos generados.

Diferencia funcional minima en errores de seleccion invalida: los mensajes que
afirmaban que el tipo 10 era exclusivo o enumeraban matrices fijas se sustituyeron
por los mensajes genericos ya existentes de seleccion valida. Mantener aquellas
frases hubiera contradicho la configuracion N:M y el soporte de Insumos tipo 14.
Las descripciones del selector tecnico ahora provienen del catalogo existente;
su igualdad visual exacta requiere el smoke pendiente.

## H. Concurrencia y contratos operacionales

Se preservaron FOR UPDATE, ON CONFLICT, advisory locks, tokens, reservas, intentos,
estados esperados e idempotencia de las funciones de notificacion, cotizacion,
presupuestos, orden medica y RP. Los tests estaticos comparan esos elementos con
las definiciones capturadas; no equivalen a una prueba concurrente real.

La receta utiliza la identidad actual antes de calcular su advisory lock y al
buscar duplicados, para conservar consistencia despues de un cambio de CUIL.
No se modifico el manejo de Connection, insercion/vinculacion/error/compensacion
de RP, redireccion QA, CCO, envio por requerimiento/prestador ni intentos DL/mail.
email_destino permanece fisico y operacional. El resolver conserva exclusivamente
tipo_contacto_e E; se retira la prioridad inalcanzable de F bajo ese filtro.

## I. Migracion

Estrategia vigente: `20260923_refactor_compras_fase1_expand.sql` y
`20260923_refactor_compras_fase2_cleanup.sql`. La segunda fase NO se ejecuta hasta
completar codigo desplegado, smoke UI/PDF/Excel/mail/DL/RP y QA. El incremental
monolitico `20260922_refactor_normalizacion_compras.sql` aborta sin cambios.

La guia `20260923_despliegue_refactor_compras.md` documenta precondiciones,
contadores, permisos y comandos exactos de ensayo con ROLLBACK. FASE 1 se aplica
sobre la DB existente con ALTER y CREATE OR REPLACE; conserva los valores de las
18 columnas y los dos triggers. Las lecturas nuevas usan maestros actuales y
valores derivados. No ejecuta DML sobre tablas transaccionales.

El canonico solo representa el modelo final desde cero. No incluye objetos de
otros schemas ni configuracion global. Nunca ejecutarlo sobre Compras existente.

## J. Pruebas de la iteracion inicial (2026-09-22)

Baseline: HEAD 308990e77a909ff49a88f47b33d0b36db2f7519d, arbol limpio al inicio.
La fecha inicial del relevamiento tecnico queda en pruebas_refactor/catalogo_antes.json.

| Control | Resultado |
|---|---|
| ant -f ext-service/build.xml compile | OK, exit 0 |
| ant -f ext-impl/build.xml compile | OK, exit 0, tambien despues de los ajustes |
| ant -f ext-web/build.xml compile | OK, exit 0; este target no acredita compilacion/render JSP |
| ComprasConfiguracionTest | OK, 44 verificaciones, exit 0 |
| verificar_contratos.py | OK, 187 verificaciones estaticas, exit 0 |
| verificar_javascript.js | OK, 10 controles aislados, exit 0 |
| git diff --check | OK, exit 0 |
| Primera prueba SQL en transaccion | Fallo por alias id_domicilio; ROLLBACK ejecutado. Se corrigio al contrato afidom_id_domicilio |
| Reejecucion SQL completa | Pendiente: desaparecio c3p0-config.xml desplegado junto con ROOT |
| UI, PDF, Excel, correo, DL y RP reales | PRUEBA UI MANUAL PENDIENTE; no se declaran aprobados |

La primera compilacion fallo por un import faltante en Compras. Fue corregido y
las compilaciones posteriores pasaron. Se observaron advertencias existentes de
BASE64Decoder/XMLGregorianCalendar en modulos externos, sin modificarlos.
Los scripts de comprobacion tuvieron ajustes de extraccion de sobrecargas y ramas
JSP durante su preparacion; los resultados reportados son los de su ultima ejecucion.

Se verificaron en Java: cargos 0/100, 100/0 e intermedios, rechazo de rangos/sumas
incorrectos, SURGE true/false, recupero derivado pese al setter contrario, N:M con
10/14 y un ID configurado arbitrario, exclusion de otro sector, capacidades de OC
y RP, falta de configuracion, total automatico/manual y cambios de cantidad/unitario,
descripcion visual del estado 2.

Los controles estaticos verificaron diez tablas, dieciocho retiros, aliases de
lectura, trigger de total, ausencia de CHECKs/CASCADE, claves de empresa, mecanismos
atomicos, objetos externos intactos, scope, encoding y plantillas PDF intactas.

## K. Regresiones de integracion pendientes

El runner `probar_migracion_rollback.py` fue actualizado para ensayar exclusivamente
FASE 1 con ROLLBACK. La ejecucion del 2026-09-23 se registra en la guia nueva;
no reemplaza la navegacion ni el render de documentos en el portal.
El script no envia correos, crea RP ni escribe documentos; usa IDs de prueba
explicitos para no consumir las secuencias de la base.

La navegacion real pendiente debe cubrir:

- Alta Farmacia, Prestaciones Medicas, Sistemas, RRHH y rechazo de sectores no seleccionables.
- Titular/integrante, datos y contacto actuales, baja y cambio de CUIL.
- Nomencladores 9/3/10/14, Insumos 10/14, protesis/panales y bajas del maestro.
- Busquedas por texto, afiliado, codigo/descripcion, cargos/recupero, SURGE, fecha, ID y estado.
- Envio/reintento, presupuestos, baja/reactivacion, cierre y adjudicacion.
- Empresa por CUIT/sucursal, razon social vigente, cotizacion y OC directa.
- Orden medica, adjuntos, descarga, PDF, Excel y correo.
- RP: creacion, doble intento, error posterior al insert y vinculacion, con logs.
- Comparacion visual y mensajes contra origin/main usando la misma sesion/datos.

## L. Riesgos concretos y limites

1. FASE 1 cuenta con ensayo de rollback sobre desarrollo 9.6.15, registrado
   aparte. Falta el ensayo con el volumen y version 9.6.5 de produccion. La fila
   1186 informada por negocio no fue verificada en desarrollo. FASE 2 no se ejecuto.
2. No hay prueba end-to-end ni equivalencia visual ejecutada. Falta comprobar el
   selector tecnico con sus descripciones actuales y las salidas reales.
3. El contrato RP existente rechaza diferencias superiores a su tolerancia entre
   cantidad por unitario y total informado (crearPrestacion/validarConsistenciaTotal).
   Se preservo esa regla de precarga para no modificar el comportamiento de RP.
   El total manual permanece valido y persistido en Compras; el paso de un caso
   asi a RP conserva esa restriccion preexistente. No se declaro ese caso aprobado.
4. El cambio de CUIL se implemento con el historial real, pero no se ejecuto una
   regresion con una cadena futura/ambigua en la base. Se evita elegir una
   identidad arbitraria ante empate; la migracion falla si no puede resolverla.
5. Los parametros descriptivos legacy de algunas funciones de escritura siguen
   presentes pero ignorados. Las dieciocho columnas fisicas permanecen durante
   FASE 1 como resguardo, sin ser fuente autoritativa del runtime nuevo.

## M. Git y encoding

Repositorio https://github.com/JerePrograma/molineros.git; ruta
C:\wsmolineros\ext; rama main. HEAD inicial, HEAD final y origin/main relevado:
308990e77a909ff49a88f47b33d0b36db2f7519d. No hubo commit, push, stash, reset,
rebase, rama nueva ni cambios en configuracion global/Tesoreria.

Todos los textos modificados o nuevos se escribieron explicitamente en Latin-1
(ISO-8859-1) sin BOM. Se verificaron bytes BOM, caracteres de mojibake, diff por
archivo y alcance. Se corrigio un caracter de apertura de pregunta mal codificado
en la documentacion existente, sin cambiar resultados historicos.

Inventario exacto de archivos y estado final: `pruebas_refactor/estado_final.txt`.
Resultados reproducibles y bitacora de build: carpeta `pruebas_refactor`.

## Inventario de fuentes modificadas

| Archivo | Motivo |
|---|---|
| `docs/sql/compras/20260922_compras_fuente_verdad_produccion.md` | Correcciones funcionales sin alterar resultados crudos. |
| `ext-impl/src/ar/com/ospim/compras/WebKeysCompras.java` | Capacidades configuradas y retiro de matrices/descripciones duplicadas; protocolos centralizados. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/action/BuscarItemTecnicoComprasAction.java` | Carga de configuracion del sector y coordinacion del buscador existente. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/action/EditarRequerimientoCompraAction.java` | Entrega catalogos a la vista y retira asignacion duplicada de recupero. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/beans/NotificacionCotizacionDetalle.java` | Reutiliza las constantes tecnicas de envio existentes. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/beans/RequerimientoCompra.java` | Getter de recupero derivado, estado visual de catalogo y capacidades del sector. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/beans/RequerimientoCompraEstado.java` | Transporta codigo, orden, activo y descripcion visual de la tabla. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/beans/RequerimientoCompraSector.java` | Transporta capacidades y nomencladores configurados. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/beans/TipoPrestacionCompra.java` | Transporta rubro y relacion N:M. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/helper/EditarRequerimientoCompraHelper.java` | Validacion funcional de alta, sector, N:M y maestro; calculo unico de recupero en el bean. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/helper/NomencladorCompraBusquedaHelper.java` | Busqueda por tipos admitidos y filtrado de maestros activos. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/helper/NotificarCotizacionPrestadorHelper.java` | Sustitucion de un literal por la constante existente; flujo de correo intacto. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/helper/ReclamoPrestacionalCompraPrecargaHelper.java` | Sector RP y admision de nomenclador/medicamento desde configuracion. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/helper/RequerimientoCompraReclamoPrestacionalHelper.java` | Descripcion del estado obtenida del catalogo. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/service/BusquedaRequerimientoCompraServiceImpl.java` | CALLs de catalogos y mapeo de capacidades/estados/N:M. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/service/BusquedaRequerimientoCompraServiceUtil.java` | Fachada de las consultas nuevas en el Service existente. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/service/ReclamoPrestacionalCompraPrecargaServiceUtil.java` | Adaptacion del contrato interno de mapeo de sector a su bean. |
| `ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql` | Modelo canonico de diez tablas, maestros actuales, catalogos y operaciones atomicas. |
| `ext-web/docroot/html/portlet/compras/requerimientos/partials/requerimiento_compra_acciones_componente.jsp` | Capacidad RP obtenida de configuracion. |
| `ext-web/docroot/html/portlet/compras/requerimientos/partials/requerimiento_compra_datos_basicos_componente.jsp` | Atributos internos de capacidades en opciones existentes. |
| `ext-web/docroot/html/portlet/compras/requerimientos/partials/requerimiento_compra_detalle_scripts_base_componente.jsp` | Cache N:M y capacidades de sector sin comparaciones por nombre. |
| `ext-web/docroot/html/portlet/compras/requerimientos/partials/requerimiento_compra_detalle_scripts_edicion_componente.jsp` | Selector y validacion UX desde catalogos; sin matrices de IDs. |
| `ext-web/docroot/html/portlet/compras/requerimientos/partials/requerimiento_compra_scripts_edicion_afiliado_componente.jsp` | Descripcion tecnica desde el mismo catalogo de la vista. |

## Referencia de compatibilidad SQL

Para la conservacion de permisos al recrear contratos se verifico la existencia
de `acldefault` y `aclexplode` en el [catalogo oficial de PostgreSQL 9.6](https://github.com/postgres/postgres/blob/REL9_6_STABLE/src/include/catalog/pg_proc.h).
Esto no sustituye la ejecucion pendiente sobre la version del entorno objetivo.
