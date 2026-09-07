# Exportar requerimientos de Compras

Esta implementacion agrega Exportar a la derecha de Nuevo requerimiento en la
solapa Requerimientos. El permiso es de consulta, independiente del alta.

## Criterios adoptados para esta tarea

Estos criterios de trabajo no constituyen un relevamiento funcional confirmado:

- Excel real .xlsx, generado con Apache POI 4.1.0 ya instalado.
- Todas las filas de la ultima busqueda exitosa que quedo mostrada, en su orden.
- Se vuelven a consultar los datos con esos criterios. No hay snapshot historico:
  la paridad se verifica con datos estables entre busqueda y descarga.
- Se exportan los valores de datos de la grilla, sin Acciones.
- Prestador adjudicado es la unica columna agregada y solo aparece en el archivo.

Columnas actuales: Id; Estado; Sector; Apellido y nombre; DNI; Tercerizadora;
Cargo Ospim; Cargo Tercerizadora; SURGE; Fecha Alta; Id RP; Prestador adjudicado.
Id RP se incluye segun el indicador del resultado. Actualmente se muestra tanto
en Requerimientos como en Cotizados. Esta tarea no agrega Exportar a Cotizados.

Los titulos se resuelven con las claves existentes del idioma del resultado.
Los valores de grilla y Excel se preparan en valoresListado, antes del escape HTML.
Se preservan el fallback CUIL/integrante, DNI normalizado como texto, codigo de
tercerizadora, porcentajes (25%), descripciones, fecha sin hora e Id RP valido.

## Busqueda y permisos

Ruta efectiva: /xlsservlet/?reporte=COMPRAS_REQUERIMIENTOS&compras_exportacion_token=...
El contexto del portal se antepone a /xlsservlet/ cuando corresponde.

El Action guarda los criterios ya aplicados en servidor y publica un token solo
despues de completar una busqueda exitosa. Cada respuesta tiene su propio token.
Se conservan hasta 20 contextos por sesion, ligados al usuario, sin filas ni libros
Excel. Si un contexto fue desalojado, se solicita volver a buscar.

El JavaScript obtiene el token de la respuesta que quedo insertada por AJAX,
incluso si las respuestas llegan fuera de orden. Exportar queda deshabilitado
mientras hay consultas pendientes, ante error o cuando no existe resultado valido.
Editar el formulario o Limpiar sin una nueva busqueda conserva el resultado aplicado.
Cada pestana del navegador mantiene su estado propio.

El endpoint revalida usuario, roles y token de sesion antes de consultar. Usa
buscarRequerimientosListado, conservando sus filtros, orden y semantica Cotizados/RP.
Ignora filtros e IDs adicionales enviados al descargar. La busqueda valida fechas
completas, rango y tipos de parametros; integrante 0 y SURGE false son valores reales.
Los controles auxiliares del selector de afiliado no se convierten en nuevos filtros.

## Adjudicacion y dependencia SQL

La identidad proviene de compras.requerimiento_detalle.id_prestador en detalles
activos. guardar_cotizacion guarda el mismo prestador para todos los detalles.
EditarRequerimientoCompraHelper.obtenerPrestadorAdjudicadoUnico y
BusquedaRequerimientoCompraServiceUtil.resolverIdPrestadorAdjudicado confirman
la cardinalidad unica. No se deduce por cotizaciones, pedidos, documentos ni RP.

La denominacion proviene de public.prestador.descripcion. Sin asignacion vigente,
la celda queda vacia. Se excluyen detalles dados de baja y requerimientos anulados
o dados de baja del enriquecimiento, conservando la fila original del listado.
Varios prestadores distintos, detalle parcialmente adjudicado o denominacion
faltante impiden entregar un archivo presentado como correcto.

Se agrega compras.listar_prestadores_adjudicados_batch(text), mediante CALL y
un arreglo de IDs construido exclusivamente con enteros de la consulta comun.
La funcion usa DISTINCT y no une documentos: no multiplica filas por adjuntos.
Se realizan una consulta de listado, una de RP y una de adjudicatarios, con cero
consultas de enriquecimiento cuando no hay filas. La semantica compartida de
Cotizados puede hacer dos consultas de listado, como antes.

No hay definiciones de Compras en sql-scripts en el estado local revisado.
Fuente canonica existente: ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql.
Migracion incremental preparada: docs/sql/20260907_exportar_requerimientos_compras.sql.

La migracion NO fue ejecutada. Su instalacion y prueba en un entorno autorizado
son dependencias pendientes. No ejecutar el esquema completo como migracion.

## Descarga y verificacion

Se reutiliza XLSServlet con una rama exclusiva de Compras; las ramas previas
permanecen intactas. Referencias: ReporteLiquidacionesFarmaciaExcel para POI y
reporte_debitos_terciarizadoras_search_result.jsp para boton/ruta legacy.
ActualizarContactoAfiliadoCompraToken es la referencia local de contextos acotados.

El archivo se genera completamente antes de enviar Content-Disposition y MIME.
Los errores devuelven estado HTTP 400/403/500 y texto, sin encabezados de adjunto.
El iframe conserva la busqueda y permite mostrar los errores de descarga.
Las celdas se escriben como texto, nunca como formulas. Se cierran los workbooks.
Los limites de filas y caracteres de Excel producen error explicito, sin truncar.

Pruebas ejecutables:
- ar.com.ospim.test.ExportarRequerimientosCompraTest: Action, servicios JDBC,
  XLSServlet, permisos y POI real; HTTP/JDBC sinteticos sin conexion a bases.
- test/compras/exportar_requerimientos_test.js: JavaScript de la JSP con
  DOM y AJAX simulados. No es una prueba de navegador.
- ComprasSurgeSelectContractTest: contrato de SURGE adaptado al helper compartido.
- ComprasRequerimientoConnectionContractTest: preservacion de cierre JDBC legacy.

Se compilaron ext-service, ext-impl y ext-web con sus targets compile y JDK 8.
Jasper compilo Listado, Resultado y Cotizados en salida aislada, sin deploy.
Los fuentes se mantienen en ISO-8859-1, sin BOM, con sus EOL LF.

Pendientes: funcion SQL instalada, adjudicaciones anuladas/reemplazadas y documentos
multiples sobre PostgreSQL real, paridad con pantalla servida, navegacion autenticada,
consola, requests y log del backend desplegado. PRUEBA UI MANUAL PENDIENTE.

Esta funcionalidad no cierra la integracion SVN ni habilita publicar commits
retenidos. No se desplego, no se ejecuto DDL ni se hizo push.
