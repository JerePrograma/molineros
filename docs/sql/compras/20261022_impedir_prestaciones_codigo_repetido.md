# Impedir prestaciones con codigo repetido en un requerimiento

La restriccion se aplica al alta, agregado y edicion en todas las instancias de
carga permitidas. Se compara el ID y el codigo canonico de la prestacion; el
codigo se normaliza con trim y sin distinguir mayusculas. Distintos IDs o tipos
de nomenclador no habilitan repetir el mismo codigo dentro del requerimiento.

El editor compartido valida antes de agregar o enviar. El Helper valida al
agregar y corregir. El trigger especifico en requerimiento_detalle es autoritativo
y cubre tambien las firmas legacy, guardados por lote y escrituras simultaneas.
El bloqueo de cabecera existente serializa las escrituras del requerimiento.
No se amplian los estados ni permisos que permiten modificar detalles.

La fila que se edita no se compara contra si misma. Las filas dadas de baja no
participan; reactivar una fila tampoco puede duplicar un codigo activo. El mismo
codigo puede utilizarse en otro requerimiento. Los detalles de observaciones sin
codigo mantienen su comportamiento actual.

## Instalacion

1. Con respaldo disponible, ejecutar completo
   `20261022_impedir_prestaciones_codigo_repetido.sql` en el destino autorizado.
   Se aplica en una transaccion con lock_timeout de cinco segundos.
2. El instalador crea solo el control y su trigger. Una reejecucion conocida es
   idempotente. Una funcion o trigger de revision desconocida se rechaza sin
   reemplazarlo. No habilita ni modifica otros validadores legacy.
3. Desplegar juntos el JSP compartido y EditarRequerimientoCompraHelper; compilar
   con Build Java focalizado y reiniciar Tomcat con las acciones configuradas.
4. Verificar funcion, trigger activo y pruebas de alta/edicion. La funcion es
   SECURITY INVOKER; no se concede ejecucion publica.

El parche no modifica ni elimina duplicados ya guardados. En el caso informado,
el requerimiento 46 conservaba dos filas con codigo 5555555. Deben corregirse
mediante el flujo habitual; no se realiza una depuracion automatica de datos.
Los guardados que vuelvan a informar una prestacion duplicada se rechazan.

## Referencias legacy

- Compras: EditarRequerimientoCompraHelper.corregirItem y mismoTexto;
  compras.validar_requerimiento_detalle_fila y su bloqueo de cabecera/EXISTS.
- Liquidaciones: EditarLiquidacionServiceUtil.validaPrestacionLiquidacion,
  lineas 578-600: comparar prestaciones existentes y rechazar desde servidor.
- Vista: se adapta validarDetalleEditor del editor compartido; se conserva su
  presentacion y el uso de alert/retorno false existente, sin nuevos componentes.

La validacion se realiza en PostgreSQL 9.6.15 con datos sinteticos y sobre las
funciones Java/JavaScript reales. Los detalles y limites de las pruebas UI y del
build se registran en el informe de entrega.
