# Comparativa de presupuestos por prestador

Ejecutar manualmente `20260923_requerimiento_comparativa.sql` antes de desplegar
el cambio Java/JSP. El script crea dos tablas y las secuencias implícitas de SERIAL.
No modifica tablas, funciones ni datos existentes. No agrega grants: los scripts
actuales de Compras consultados no declaran permisos específicos por tabla.

- `compras.requerimiento_comparativa`: una cabecera vigente por requerimiento
  y prestador. Guarda fecha manual, días de pago, entrega, validez en horas,
  destino de envío e importes de IVA/IIBB.
- `compras.requerimiento_comparativa_detalle`: una fila por cabecera y
  prestación del nomenclador, con cantidad e importe unitario neto.

Las FK apuntan a `compras.requerimiento(id_requerimiento)`,
`public.prestador(id_prestador)` y
`autorizaciones.nomenclador(id_prestacion)`. Se verificaron sus PK mediante
metadatos de la base de desarrollo, sin modificarla.

## Carga y modificación

El botón se muestra cuando los presupuestos ya consultados por Compras incluyen
un presupuesto activo de prestador. El popup obtiene los mismos documentos
mediante `compras.listar_documentos_requerimiento(id, 1)`; no usa el universo
de prestadores ni los pedidos pendientes. Conserva los roles de consulta,
ABM y cotización de Compras. Sólo ABM/cotización pueden guardar.

La carga presenta todas las prestaciones para cada participante. La consulta
y el PDF muestran cabeceras y detalles guardados. Se puede modificar sin
restricción por estado del requerimiento; no hay estado propio de comparativa.

La cantidad es INTEGER nullable, como en requerimiento_detalle. El precio,
IVA e IIBB usan NUMERIC(18,2). Vacío conserva NULL y cero conserva 0.
Si ambos campos del detalle están vacíos, no se inserta. Si se vacían al
modificar, se elimina únicamente esa fila dentro de la misma transacción.
No hay acción de baja de comparativas ni historial.

Se actualizan las mismas filas con modi_fecha y modi_usr. El alta usa now()
y el screenName del usuario, con VARCHAR(100), como Compras.
Las UNIQUE impiden cabeceras y prestaciones duplicadas.

## Cálculos y presentación

No se proporcionó el Excel de referencia. Se adoptó la interpretación de
`liquidaciones/comprobantes/action/AgregarConceptoComprobantesExtendidoAction.java`:
IVA e IIBB son importes adicionales manuales del presupuesto, no alícuotas.
Los campos se rotulan "IVA (importe)" e "IIBB (importe)".

El Helper calcula con BigDecimal el subtotal (cantidad por precio neto),
el neto (suma de subtotales completos) y el total (neto más IVA e IIBB).
Un detalle incompleto no genera subtotal; si hay otros completos se informa
"Total parcial". Si no hay ningún subtotal, el neto y total quedan vacíos.
Estos resultados no se almacenan.

El adjudicado se obtiene de RequerimientoCompra.getIdPrestadorAdjudicadoInt(),
cuya fuente es el detalle vigente. Se presenta primero y los demás por ID.
No se duplica adjudicación en las tablas nuevas.

El PDF mantiene PdfServlet y Jasper 3.7.4. El JRXML recibe un datasource de
mapas preparado por el mismo Helper; no ejecuta SQL ni cálculos de negocio.
Se compila con las bibliotecas existentes del Tomcat Java 8.

## Referencias y límites

Cabecera/detalle: Liquidacion, LiquidacionPrestacion y
EditarLiquidacionServiceImpl. JDBC directo: PrestadorServiceImpl.getRubrosPrestador.
Formulario/popup: editar_liquidacion.jsp y el popup de Situación Médica de Compras.
Persistencia transaccional: ConnectionHelper.getConnectionForTransaction.
PDF: PdfServlet.generaRequerimientoCompra y requerimiento_compra.jrxml.

Un requerimiento con ítems sin id_prestacion (medicamentos u observaciones)
o prestaciones repetidas recibe una validación explícita: no se omiten ítems
ni se inventan identificadores. Si un presupuesto deja de estar activo, ese
prestador deja de participar en la presentación; su comparativa se conserva.

Pruebas focalizadas: RequerimientoCompraComparativaTest, en el árbol test existente.
El script no se ejecutó en la base compartida y no se hizo despliegue.
La prueba del flujo autenticado completo queda pendiente.
