# Validacion SQL/Nomenclador - Implementacion

Fecha: 2026-07-12

## Criterio de implementacion

Se aplico la alternativa B para nomenclador: un Action, resultado y callback especificos de Compras reutilizan los services y funciones canonicas existentes sin cambiar el contrato global de Autorizaciones. No se agregaron dependencias, frameworks, tablas ni servicios externos.

## SQL

Fuente de verdad: `ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql`.

- Se elimino `compras.articulo` y las funciones `listar_articulos`, `get_articulo_cursor`, `guardar_articulo`, `borrar_articulo` y `listar_articulos_cursor`.
- `validar_requerimiento_detalle_fila()` ya no referencia columnas eliminadas. Resuelve el sector persistido del requerimiento y aplica exactamente:
  - `FARMACIA -> MEDICAMENTO`;
  - `PRESTACIONES MEDICAS -> NOMENCLADOR`;
  - `LEGALES -> NOMENCLADOR`;
  - cualquier otro sector se rechaza.
- La normalizacion SQL contempla vocales acentuadas mayusculas y minusculas. Esto es necesario en PostgreSQL LATIN1, donde `upper('é')` no necesariamente produce `É` con la locale del cluster.
- El trigger limpia los campos del tipo opuesto, valida requeridos, bloquea estructura fuera de PENDIENTE, calcula totales de cotizacion y valida el prestador ENVIADO.
- `guardar_requerimiento_detalle` conserva la firma de 13 parametros y el orden JDBC. Inserta/actualiza solo los campos tecnicos, limpia cruzados y reinicia la cotizacion al actualizar estructura.
- `buscar_requerimientos` busca por `tipo_item`, codigo/descripcion de nomenclador, nombre/troquel de medicamento y observaciones, sin join a articulos.
- El schema queda en una sola transaccion. Se retiraron DDL externos de `public`/`autorizaciones`, SELECT de diagnostico durante instalacion y caracteres `U+FFFD` incompatibles con LATIN1.

## Java y trust boundary

`EditarRequerimientoCompraServiceImpl.guardarDetalle` ahora:

1. recarga el requerimiento persistido;
2. exige estado PENDIENTE y sector persistido valido;
3. deriva el tipo por sector, sin confiar en `tipo_item` del navegador;
4. rechaza campos cruzados;
5. recupera el medicamento por `BusquedaMedicamentoServiceUtil.getMedicamento(id)` o la prestacion por `NomencladorServiceUtil.buscarNomencladorPorId(id)`;
6. valida existencia, baja, tipo, codigo, descripcion, nombre/presentacion y troquel;
7. reconstruye los textos tecnicos desde el registro canonico antes del JDBC;
8. ejecuta la funcion SQL de 13 parametros y exige exactamente un ID positivo.

`RequerimientoCompraDetalleHelper` replica la regla exacta de sectores para la serializacion de alta/edicion. Se eliminaron del runtime `CompraArticulo`, sus metodos utilitarios, ramas del Action, atributos de request y cargas desde presupuestos.

## Buscador tecnico especifico

Nuevos componentes:

- `BuscarItemTecnicoComprasAction.java`;
- `/compras/buscar_item_tecnico` en Struts;
- `portlet.compras.buscar_item_tecnico` en Tiles;
- `buscar_item_tecnico_result.jsp`.

Medicamentos reutiliza `BusquedaMedicamentoServiceUtil.getBusquedaMedicamentos` y permite nombre, presentacion y troquel. El resultado activo devuelve `id_medicamento`, troquel, nombre y presentacion.

Nomenclador reutiliza `getListaNomencladorPrestacionesMedicas` para Prestaciones Medicas y `getListaNomenclador` para Legales. El resultado activo devuelve `id_prestacion`, `id_tipo_nomenclador`, codigo y descripcion.

Ambos callbacks estan namespaced. Los IDs internos son hidden, hay botones Buscar/Limpiar y cualquier edicion manual de un criterio invalida el ID anterior. Los otros sectores ocultan ambos paneles y deshabilitan el alta de detalle.

No se modificaron los buscadores globales de medicamentos/nomenclador porque tienen numerosos consumidores y bugs legacy documentados en el diagnostico.

## Jasper/PDF y mail

- `requerimiento_compra.jrxml` declara todos los fields tecnicos.
- La descripcion de cada fila muestra tipo, codigo, descripcion y observaciones.
- El `.jasper` fue regenerado con JasperReports 3.7.4 y `JRJavacCompiler` sobre Java 8.
- El mail ya consumia `getTipoItemNormalizado`, `getCodigoItemVisible` y `getDescripcionItemVisible`; se conservaron esos contratos.
- El adjunto PDF se genera antes de reservar prestadores. Un PDF nulo/vacio o una excepcion de Jasper abortan el envio.
- Los tests de notificacion se alinearon con las APIs atomicas actuales de reserva/finalizacion y no abren una conexion externa.

## Archivos legacy eliminados

- `ext-impl/src/ar/com/ospim/compras/beans/CompraArticulo.java`.
- `ext-web/docroot/html/portlet/compras/requerimientos/articulos_sector.jsp`.
- `ext-web/docroot/html/portlet/compras/requerimientos/editar_articulo_popup.jsp`.
- Rutas `/compras/alta_articulo_popup` y `/compras/listar_articulos_sector`.

La busqueda final no encontro consumidores runtime de esos contratos.

## Codificacion

Los Java/JSP/XML legacy afectados se mantuvieron en su codificacion original CP1252/ISO-8859-1 y CRLF. El JRXML se mantuvo en ISO-8859-1. Para aplicar parches byte-safe se hizo una conversion temporal a UTF-8 y se restauro inmediatamente la codificacion original. `git diff` no muestra reescrituras masivas por encoding.
