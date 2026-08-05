# Alta de Reclamo Prestacional desde Compras

## Alcance y nivel de evidencia

Este documento describe exclusivamente el flujo iniciado por `Crear Reclamo Prestacional` desde un requerimiento de Compras. El análisis se realizó sobre los bytes del working tree laboral de `C:\wsmolineros\ext` el 2026-08-05.

El commit `d56c2dbbe2fcbb6ff1afbde8d06f4898bcff8330` es un antecedente histórico de la integración, no evidencia de la vigencia ni de la ejecución actual. El HEAD inicial verificado para este cambio fue `311ac5c118917da521ddc493d5158620621f30d0`, igual a `origin/main` al iniciar.

Los contratos indicados como verificados estáticamente se comprobaron contra código fuente y contratos ejecutables. No equivalen a una prueba funcional en Liferay, una persistencia real en PostgreSQL ni un smoke test de navegador.

## Estados funcionales

| Estado | Entrada y forward | Contexto | UI esperada | Operación |
| --- | --- | --- | --- | --- |
| A. Alta manual de reclamo | `/autorizaciones/editar_reclamosprestaciones_entry`; forward legacy de edición/alta | `cmd=ADD`; reclamo persistido nulo; prestación en edición nula | `Grabar`; sin `Actualizar`; revisión según reglas legacy; comprobante vacío | `SAVE` crea el reclamo |
| B. Alta desde Compras | `/compras/iniciar_reclamo_prestacional`; redirect al editor con `cmd=ADD`, `origen=compras` y nonce | borrador de reclamo con ID `0`; contexto Compras validado en sesión; prestación en edición nula | título `Nuevo Reclamo Prestacional`; `Grabar`; sin `Actualizar`; `Agregar revisión` con cero revisiones activas; comprobante vacío | `SAVE` revalida nonce y requerimiento, reserva, inserta y vincula |
| C. Edición de reclamo | `/autorizaciones/editar_reclamosprestaciones_entry` con ID persistido; forward legacy de edición | `cmd=EDIT`; reclamo existente; listas cargadas; prestación en edición nula hasta selección | `Actualizar`; título con número persistido; controles según estado | `UPDATE` modifica el reclamo existente |
| D. Alta de prestación | función legacy de agregar prestación y `/autorizaciones/lista_prestaciones_reclamos` | reclamo en alta o edición; nueva fila temporal; sin prestación seleccionada para edición | formulario inicial legacy y grilla temporal | agrega una prestación temporal; se persiste con el reclamo |
| E. Edición de prestación | `EditarPrestacionReclamoAction` desde `Editar` de una fila | selecciona por `idRegistro`, recupera exactamente la fila con `indexOf/get`, publica `PRESTACION_EN_PROCESO_DE_EDICION` | abre `datos_edicion_prestacion.jsp` y recién allí carga comprobante | actualiza la prestación seleccionada y consume el atributo temporal |

La confusión del estado B con edición tenía tres causas concretas:

1. `EditarReclamosEntryAction.render(...)` reemplazaba incondicionalmente `cmd` por `EDIT` aun cuando la entrada era `ADD`.
2. `view_reclamo.jsp` ocultaba `botonsavereclamo` ante cualquier bean no nulo; el borrador de Compras es un bean válido, pero todavía tiene ID `0`.
3. La precarga publicaba la primera prestación como `PRESTACION_EN_PROCESO_DE_EDICION`, por lo que el include de edición podía consumirla como si el usuario hubiera seleccionado `Editar`.

## Entrada y validación desde Compras

El botón de `_botonera.jsp` conserva la action `/compras/iniciar_reclamo_prestacional`. La action resuelve el identificador del requerimiento en servidor y no confía en datos de cabecera enviados por el cliente para persistir.

Antes de crear el borrador, el contrato existente valida:

- existencia y vigencia del requerimiento;
- permiso de creación del usuario;
- estado `COTIZADO`;
- afiliado informado;
- relación previa o reserva existente para impedir duplicados según el contrato actual;
- coincidencia entre el afiliado persistido y el contexto al volver a guardar.

La action genera un nonce, guarda un contexto acotado en sesión y redirige con `cmd=ADD`. La pantalla sólo fuerza `ADD` cuando el handoff de Compras, el usuario y el nonce coinciden. Un contexto inválido falla cerrado y no se transforma en edición de otro reclamo.

## Precarga permitida

| Dato destino | Fuente de servidor | Validación o transformación | Ausencia o inconsistencia |
| --- | --- | --- | --- |
| afiliado titular e integrante | requerimiento persistido | CUIL normalizado y coincidencia con contexto | bloquea el handoff si no es válido |
| fechas de alta y OSPIM | reloj del servidor | misma fecha para el borrador | no usa fecha enviada por cliente |
| sector | descripción persistida del requerimiento | `mapearSector(...)` | bloquea si el sector no admite RP |
| tipo y estado inicial | contrato legacy de RP | `EXCEPCION` y estado cargado/pendiente | no se inventan alternativas |
| recupero y SURGE | indicadores del requerimiento | se trasladan a cabecera y prestación | `false` cuando el requerimiento validado no los posee |
| tercerizadora y débitos | cargo e ID persistidos | ID sólo cuando el porcentaje de tercerizadora es mayor a cero | ID nulo sin cargo aplicable |
| referencia de prestación | detalle cotizado | referencia técnica existente o referencia temporal admitida por el contrato | bloquea si no hay referencia válida |
| cantidad, importe y cargos | cotización persistida | recalcula total, valida consistencia y distribuye cargos con redondeo | bloquea ítems o importes inválidos |
| fecha de prestación | ninguna | queda pendiente de confirmación | `null` |
| comprobante | ninguna durante el alta | no se ejecutan setters de comprobante | todos sus campos quedan nulos |

No se copian número, tipo, letra, sucursal, fecha, CUIT, razón social, cantidad, importe ni total de comprobante desde la cotización. Una cotización no es un comprobante.

## Alta, revisión y comprobante

### Alta de reclamo

`EditarReclamosEntryAction.render(...)` conserva `ADD` cuando ése es el comando de entrada. El JSP usa un título de alta y sólo reemplaza el número cuando `id_reclamo > 0`. `Grabar` permanece visible; `Actualizar` continúa perteneciendo al bloque `EDIT`.

El borrador de Compras existe únicamente para precargar la cabecera y la grilla temporal. Su ID continúa en `0` y no representa un reclamo persistido.

### Agregar revisión

El control `botonrevision` aparece en `ADD` cuando no existe una revisión activa. Invoca la función ES5 namespaced `<namespace>agregarRevision()` y la render URL exclusiva `/autorizaciones/lista_revisiones_reclamo`.

`ListaRevisionesAction` agrega la revisión a `LISTADO_REVISIONES_RECLAMOS_EN_SESION`; no exige un ID persistido. La revisión se guarda junto con el reclamo. Las reglas legacy que ocultan el control por cierre, consulta o revisión activa se preservan. La persistencia final vuelve a validar el contexto Compras y el nonce; sin contexto válido no debe grabarse el alta.

### Carga diferida del comprobante

La precarga no establece `PRESTACION_EN_PROCESO_DE_EDICION` y no completa campos `setComprobante...`. La grilla admite `comprobanteTotal == null` y muestra vacío.

Al seleccionar `Editar` sobre una prestación, `EditarPrestacionReclamoAction` ubica la fila por `idRegistro`, publica esa instancia en sesión y `datos_edicion_prestacion.jsp` carga sus campos. El JSP elimina el atributo después de leerlo, por lo que no debe reutilizar datos de otra prestación en el siguiente render.

## Vínculo transaccional y doble envío

El guardado desde Compras conserva la secuencia existente:

1. revalidar contexto, nonce y requerimiento en servidor;
2. reservar la creación para el requerimiento;
3. insertar el reclamo y sus datos asociados;
4. finalizar la relación Compras--Reclamo;
5. limpiar el contexto sólo después de finalizar.

Si falla antes del insert, se libera la reserva. Si falla después del insert, se marca el error posterior para reconciliación. El servicio de reclamos ejecuta rollback y propaga la excepción. La reserva existente funciona además como tratamiento del doble envío. Esta evidencia es estática; no se ejecutó una transacción PostgreSQL real en esta validación.

## Archivos fuente involucrados

Modificados por este cambio:

- `ext-impl/src/ar/com/ospim/autorizaciones/action/EditarReclamosEntryAction.java`;
- `ext-impl/src/ar/com/ospim/compras/requerimientos/service/ReclamoPrestacionalCompraPrecargaServiceUtil.java`;
- `ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jsp`;
- `ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/lista_prestaciones_reclamos.jsp`;
- `ext-impl/src/ar/com/ospim/test/ComprasRequerimientosUiContractTest.java`.

Inspeccionados y preservados sin modificación:

- `IniciarReclamoPrestacionalCompraAction.java`;
- `ReclamoPrestacionServiceImpl.java`;
- `_datos_basicos.jsp` y `_botonera.jsp` de Compras;
- `editar_reclamosprestacionales_entry.jsp`;
- `view_reclamo_seccional.jsp`;
- `view_reclamos_entry.jsp`;
- `datos_edicion_prestacion.jsp`;
- `ListaRevisionesAction.java`;
- `EditarPrestacionReclamoAction.java`.

## Validaciones ejecutadas

| Validación | Resultado | Evidencia o límite |
| --- | --- | --- |
| repositorio, origin, rama y sincronía inicial | `PASS` | raíz y origin obligatorios; `main`; HEAD y `origin/main` en `311ac5c118917da521ddc493d5158620621f30d0` |
| contrato `ComprasRequerimientosUiContractTest` con Java 8 | `PASS` | `CONTRATO_INTEGRACION_COMPRAS_RECLAMO_OK` |
| contrato `ReclamoPrestacionalCompraPrecargaContractTest` con Java 8 | `PASS` | `CONTRATO_RECLAMO_PRESTACIONAL_COMPRAS_PRECARGA_OK` |
| `ant -f ext-service/build.xml compile` | `PASS` | 10 fuentes compiladas con JDK 8 |
| `ant -f ext-impl/build.xml compile` | `FAIL_BASELINE` | clase pública con nombre de archivo inconsistente y `WebKeysAfiliados` ausente en fuentes no relacionadas |
| compilación focal de las dos clases modificadas con classpath Ant | `BLOCKED_BY_BASELINE` | la clausura de dependencias vuelve a requerir `WebKeysAfiliados`; no se usaron stubs |
| `ant -f ext-web/build.xml compile` | `PASS` | target completo, código 0 |
| `ant -f ext-web/build.xml merge` posterior al último cambio JSP | `PASS` | webapp fusionado; hashes de los cinco JSP iguales a `docroot` |
| `ant -f ext-web/build.xml compile-tomcat` | `FAIL_ENVIRONMENT` | bundle Tomcat 6 sin `org.apache.jasper.JspC` y propiedad de salida sin resolver |
| traducción directa con Jasper/Tomcat 8.5 | `PARTIAL` | generó Java para `view_reclamo.jsp` (674155 bytes), `view_reclamo_seccional.jsp` (1337520 bytes) y `lista_prestaciones_reclamos.jsp` (116702 bytes) |
| wrappers JSP | `BLOCKED_BY_BASELINE` | `view_reclamos_entry.jsp` y `editar_reclamosprestacionales_entry.jsp` no resuelven el tag `AttributeTabsTag` porque `ext-impl` no se genera |
| compilación del Java generado | `BLOCKED_BY_BASELINE` | faltan clases de aplicación; no se obtuvo bytecode JSP válido |
| prueba funcional en navegador | `NOT_RUN` | no hay proceso Java ni listener en 8080, 8081, 8082 o 8443 |

La traducción Jasper confirma que los bloques modificados de `view_reclamo.jsp` y `lista_prestaciones_reclamos.jsp` son traducibles. No confirma una compilación JSP completa ni permite afirmar que el bytecode evita el límite de 65535 bytes. Esa comprobación queda pendiente hasta disponer del classpath completo de la aplicación.

## Preservación del JSP laboral

`view_reclamo.jsp` laboral es la fuente funcional de verdad y no debe sustituirse por la versión reducida histórica de Git. Deben preservarse:

- `trimDirectiveWhitespaces="true"`;
- `String reclamoPortletNamespace = renderResponse.getNamespace();`;
- las expresiones que reutilizan ese namespace;
- la ausencia de reintroducción masiva de `<portlet:namespace />`;
- el contenido legacy no relacionado.

La comparación normalizada de esta intervención elimina únicamente los cuatro bloques autorizados de `view_reclamo.jsp` (título ADD, número sólo persistido, ocultamiento de `Grabar` y visibilidad de revisión) y resulta idéntica para el resto. La grilla también resulta idéntica al respaldo al normalizar únicamente la expresión nulo-segura de `comprobanteTotal`.

Todos los JSP respaldados conservan ISO-8859-1 sin BOM y EOL LF. `view_reclamo_seccional.jsp`, `view_reclamos_entry.jsp` y `editar_reclamosprestacionales_entry.jsp` permanecen binariamente iguales a sus respaldos.

## Pruebas pendientes para QA

En un Liferay 5.2 desplegado con el classpath completo:

1. abrir un requerimiento activo, `COTIZADO`, con afiliado y cotización válidos;
2. pulsar `Crear Reclamo Prestacional`;
3. verificar título de alta, ID no persistido, `Grabar` visible y `Actualizar` ausente;
4. verificar datos de cabecera y prestación precargados, con comprobante vacío;
5. usar `Agregar revisión` y comprobar que la revisión aparece y se conserva en el alta;
6. grabar una sola vez y verificar la relación con el requerimiento;
7. repetir un envío para comprobar el tratamiento existente de reserva/doble envío;
8. provocar un fallo controlado antes y después del insert para verificar liberación y reconciliación;
9. entrar luego a `Editar` sobre una prestación específica y comprobar que recién entonces aparecen sus datos de comprobante;
10. editar otra prestación y confirmar que no hereda datos de la anterior.
