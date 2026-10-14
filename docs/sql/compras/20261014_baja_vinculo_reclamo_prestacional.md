# Baja de RP y anulacion de requerimientos de Compras

## Alcance

La baja logica de `autorizaciones.reclamos_prestacionales` marca la relacion de
Compras como `BAJA`, con fecha y usuario. Conserva ambos IDs y su historial.
El requerimiento conserva su estado: no vuelve a COTIZADO ni a ENVIADO A COTIZAR.

La anulacion se habilita para PENDIENTE (1), ENVIADO A COTIZAR (2) y COTIZADO (3).
CON RP (4) exige una relacion de BAJA del mismo requerimiento y un RP existente
con `baja_fecha` informada. Sin relacion, con RP activo o con datos incompletos,
la operacion se rechaza. Estados 5, 6, 7 y 99 conservan sus restricciones.
Una relacion activa, reservada o con error bloquea tambien una anulacion de
estados 1/2/3 inconsistentes; no se elimina un RP activo como efecto lateral.

## Referencias legacy verificadas en codigo

- Liquidaciones: `EditarLiquidacionServiceUtil.borraLiquidacionPrestacionEntry`
  invoca `ComprobanteItemServiceUtil.deleteLiquidacionReclamo`, que ejecuta
  `borrar_liquidacion_reclamo_item`, antes de dar de baja la prestacion.
- Reintegros: `EditarReintegroEntryAction` invoca
  `ReintegroServiceUtil.borraReintegroReclamoPrestacion` antes de borrar la
  prestacion; su servicio ejecuta `borra_reintegro_reclamo`.
- Autorizaciones: `ReclamoPrestacionServiceImpl.borrar` ejecuta
  `autorizaciones.borra_reclamo_prestacional(id, usuario)`.

Los cuerpos SQL de esas funciones no estan versionados en el checkout.
Se reutiliza la convencion de dar de baja la asociacion junto con su origen,
con un trigger que participa en la misma transaccion del RP. No se reemplaza
la funcion legacy ni sus otras integraciones.

## Instalacion

Aplicar `20261014_baja_vinculo_reclamo_prestacional.sql` en la base objetivo
ANTES de desplegar las clases/JSP. El script requiere propiedad/permisos sobre
Compras y sobre la tabla de RP para instalar el trigger. Es transaccional y
puede repetirse. No fue aplicado a la base del portal por esta tarea.

La autorizacion 4 -> 6 -> 7 toma primero el bloqueo del RP y despues el del
vinculo, para compartir el orden con la baja concurrente. Mantiene las mismas
reglas de estado y vuelve a comprobar que el RP este activo.

El catalogo instalado es la autoridad. El script agrega los dos campos de baja,
adapta los CHECK conocidos solo si existen, conserva los otros triggers y
funciones, y adapta `validar_requerimiento_fila` solo cuando reconoce su cuerpo.
Una variante desconocida produce un error y revierte la migracion.
Tambien reconcilia las relaciones VINCULADO/ERROR de RP que ya estaban de baja.

`trg_compras_baja_vinculo_reclamo` observa la baja efectiva despues de cada UPDATE,
incluso cuando un BEFORE trigger completa la fecha de baja.
La ruta legacy de baja utiliza baja logica. No se agrega una eliminacion fisica
ni se infiere actividad del RP de su estado de gestion; se usa `baja_fecha`.

La funcion `anular_requerimiento` vuelve a verificar el estado y el RP bajo
bloqueo y registra motivo, usuario y fecha de baja aun cuando no este instalado
el trigger validador de Compras. La entrada DELETE legacy usa el mismo Helper.
La lista recupera VINCULADO/BAJA en un batch; exportacion y descarga desde RP
siguen exigiendo `isVinculado()` para presentar una relacion activa.

## Validacion y reversa

Las pruebas y sus resultados capturados estan en
`.codex/artifacts/compras-baja-rp-20261014`. PostgreSQL aislado 18 permite
comprobar las operaciones sin tocar datos reales; no acredita instalacion ni
compatibilidad ejecutada contra PostgreSQL 9.6 del ambiente objetivo.
Resultados del 14/10/2026:

- Matriz Java con fuentes reales y JDK 8: 1161 comprobaciones, cero fallos.
- Ant configurado: ext-service, ext-impl y ext-web aprobados.
- Jasper: acciones y resultados del listado compilados a dos clases reales.
- PostgreSQL aislado 18.6: 47 comprobaciones aprobadas con la migracion repetida
  y otras 47 con funciones del schema actualizado; siete cuerpos coincidentes.
- Tres escenarios concurrentes: autorizar antes de baja, baja antes de autorizar,
  y anulacion frente a reactivacion del RP. Sin deadlock; rechazo de autorizacion
  con RP de baja y bloqueo hasta confirmar la anulacion.
- Diff y doce archivos ISO-8859-1 sin BOM verificados.

La prueba autenticada en el portal y la ejecucion en PostgreSQL legacy quedan
pendientes. Ant no reemplaza la compilacion Jasper ni una prueba funcional.

Para revertir codigo, revertir el commit de esta tarea. La reversa SQL requiere
revisar el catalogo y los vinculos ya dados de baja antes de retirar el trigger
y las funciones; no convertir automaticamente BAJA a VINCULADO ni borrar la
auditoria. No hay publicacion del portal ni modificacion de sus datos en esta tarea.
