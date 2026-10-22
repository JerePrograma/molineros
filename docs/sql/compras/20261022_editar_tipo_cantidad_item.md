# Editar tipo de cotizacion y cantidad antes del RP

El flujo `corregirItem` permite modificar el tipo de cotizacion y la cantidad
ademas de seleccionar la prestacion. Se conservan los estados 1/2/3 ya admitidos,
los roles existentes y el bloqueo ante cualquier relacion de RP iniciada.

El tipo debe pertenecer al sector del requerimiento y la prestacion debe
corresponder a la configuracion vigente de ese sector y tipo. Un tipo informado
no puede quitarse. La cantidad debe ser un entero mayor a cero.

El total estimado del detalle se recalcula con su precio unitario existente.
Las cantidades e importes ofertados en la comparativa se conservan; si cambia
la prestacion se mantiene la reasignacion atomica ya existente de su identidad.
Tambien se conservan observaciones, adjudicacion, documentos y estados.

## Instalacion

1. Con la aplicacion detenida y respaldo disponible, ejecutar completo
   `20261022_editar_tipo_cantidad_item.sql` en la base de desarrollo o destino
   correspondiente. Ante un error, ejecutar `ROLLBACK` y revisar la causa.
2. El parche agrega una firma de nueve argumentos y mantiene la firma de siete
   como wrapper compatible. No modifica filas de negocio ni cambia el estado
   de los triggers. Copia owner y EXECUTE existentes para la firma nueva.
3. Desplegar juntos los tres Java modificados y el JSP. Reiniciar Tomcat por
   las acciones configuradas, sin iniciar una segunda instancia.
4. Confirmar desde otra conexion los cuerpos instalados:

```sql
SELECT pronargs, md5(replace(prosrc, E'\r\n', E'\n')) AS huella
FROM pg_proc
WHERE pronamespace = 'compras'::regnamespace
  AND proname = 'corregir_item_requerimiento'
ORDER BY pronargs;
```

Huellas esperadas: firma 7 `4bef33915f3bd909be739524cec9ffa5`; firma 9 `c2cf811f57622a9e3105f5b99d98a2e1`.

El instalador solo admite la correccion anterior conocida o esta misma entrega.
Una variante desconocida del bloque de validacion se rechaza y revierte el DDL.
No instala ni activa validadores legacy que estuvieran huerfanos en el destino.

## Referencias y validacion

- Mismo modulo: validacion de tipo por sector y seleccion canonica de
  `EditarRequerimientoCompraHelper.guardarDetalle` y funcion atomica
  `compras.corregir_item_requerimiento` del 13/10/2026.
- Liquidaciones: `EditarLiquidacionServiceImpl.actualizaLiquidacionPrestacionEntry`
  y campo cantidad de `editar_liquidacion.jsp`, lineas 594-601.
- Permisos de sobrecargas: incremental de guardado del 21/10/2026.

El 22/10/2026 se ejecutaron 25 pruebas en una base aislada PostgreSQL 9.6.15 con
datos sinteticos: persistencia de ambos campos, total, firma anterior, limites,
sector, configuracion, duplicados, RP, estados, concurrencia, ACL, idempotencia,
rollback y guards legacy activos. La base de prueba se elimino al finalizar.
La validacion Java real de cantidad paso nueve casos de enteros, limites y
rechazos de vacio, cero, negativos, decimales, texto y desbordamiento.
Los tres builds Java focalizados finalizaron correctamente. La accion completa
informa un error preexistente de espacios en un cambio local de Prestadores,
que se preservo fuera de esta entrega.

No sustituye un build completo ni smoke tests de todos los estados y roles.
