# Compras: instalacion parcial por indicacion del usuario

Ejecutar: 20261021_instalar_compras_sin_paso66_y_sin_sobrecarga.sql.
Esta es una variante separada. Los instaladores completos publicados permanecen intactos.

El 21/10/2026 el usuario solicito omitir el paso 66 y la sobrecarga de detalle.
Se ejecutan 62 de 66 sentencias del consolidado, en el orden original, con estas omisiones:

| Paso original | Omision |
|---|---|
|32|CREATE OR REPLACE de guardar_requerimiento_detalle de 14 argumentos.|
|33|Reemplazo de guardar_requerimiento_detalle de 13 argumentos; se conserva el instalado.|
|34|Reemplazo de guardar_requerimiento_detalle_clasificado; se conserva el instalado.|
|66|DO de validacion final completa.|

Se conservan las versiones instaladas de las funciones de los pasos 33 y 34,
porque sus nuevas versiones llaman a la funcion de 14 argumentos
que el usuario decidio postergar. Instalar solo esos reemplazos dejaria una dependencia
ausente. Se mantiene el circuito anterior de guardado; la nueva validacion de seleccion
por sector/rubro durante el alta queda pendiente. La configuracion de nomencladores,
busqueda y correccion individual si se instalan segun los otros bloques originales.

No se relajan los controles internos de los restantes bloques ni se desactivan triggers.
Una funcion de 14 argumentos que ya existiera no se borra ni se modifica por esta variante.
La restriccion original sobre una autorizacion corporativa ya instalada se conserva.
Despues de una ejecucion parcial confirmada no se debe reejecutar todo el historial;
la sobrecarga y sus llamadores necesitaran una migracion incremental especifica.

## Ejecucion y resultado

1. Confirmar el destino, respaldo y aplicacion detenida. Abrir una conexion nueva.
2. Volver a cargar este archivo completo. Es ASCII sin BOM, compatible con UTF8/LATIN1.
3. Ejecutar TODO el archivo y abrir Data Output. No ejecutar ambos instaladores.
4. INSTALACION_PARCIAL_CONFIRMADA significa que se ejecutaron las 62 sentencias elegidas
   y se confirmo la transaccion. NO significa que aprobaron las 26 funciones finales.
5. ERROR_MIGRACION_REVERTIDA indica el primer error y revierte todos los cambios de
   negocio de esta ejecucion antes de guardar el reporte temporal. Copiar bloque,
   paso, sqlstate, mensaje y detalle. El codigo 0 del cliente no reemplaza resultado.

El reporte incluye pasos_omitidos y pendientes. El ultimo SELECT corre despues del COMMIT.
La tabla del reporte es temporal y desaparece al cerrar esa conexion.
El verificador completo seguira rechazando las tres funciones diferidas: no interpretar
ese rechazo esperado como que toda la instalacion parcial se haya revertido.
Usar el inventario de solo lectura para ver lo aplicado y lo pendiente.

## Representacion del texto

Los 62 payloads preservan el SQL original. Las tildes se representan con escapes Unicode
SQL U& de PostgreSQL 9.6, y se normalizan CRLF a LF antes de ejecutar cada payload.
Esto conserva los textos fuente y evita que la lectura UTF8 de un archivo Latin1 altere
las tildes. standard_conforming_strings se fija localmente y se restaura al COMMIT.
El defecto de codificacion previo se reprodujo con PostgreSQL 9.6.5: fallaba solo el
hash de la sobrecarga 14 en el paso 66. Ese trabajo queda separado de esta variante.

Fuente exclusiva de negocio: docs/sql/compras/20261021_megascript_produccion_desde_20260928.sql.
Referencia legacy: las funciones 13 y clasificada existentes, preservadas; el contrato
de NomencladorServiceImpl de Autorizaciones y ReintegroServiceImpl.cambiarEstadoCuota
de Liquidaciones ya documentados en la guia del instalador completo.
No cambian firmas publicas, Java/JSP, permisos ni fuentes SQL historicas.

Validacion local: PostgreSQL 9.6.5 temporal en 127.0.0.1:54395, sin base real ni portal.
Evidencia: .codex/artifacts/megascript-produccion-20261021/instalacion-parcial/.
Se aprobaron 6 casos: instalacion parcial desde 5/8/13 con comparativas existentes;
conservacion exacta de los dos cuerpos de guardado y ausencia de la sobrecarga;
inventario desde conexion nueva con 23 coincidentes, 2 diferentes y 1 ausente;
36 comprobaciones funcionales existentes de los otros circuitos; rechazo de
reejecucion sin cambios; error tardio del bloque 14 con dump anterior/posterior identico.
La prueba incluyo lectura UTF8, finales CRLF y standard_conforming_strings inicialmente off.
El arnes, la comparacion de 62 payloads y los controles de bytes terminaron con codigo 0.
PRUEBA UI MANUAL PENDIENTE. La migracion completa sigue pendiente por decision del usuario.

SHA256 del instalador: 8723d0a2679fa9134ce0fceb63e2a564d1c41f4a2f369c8028d0a8222443fa72
