# Compras: autorizacion antes de la Orden de Compra de RRHH y Sistemas

El circuito de RRHH y Sistemas queda `PENDIENTE (1) -> PARA AUTORIZAR (6) ->
AUTORIZADO (7) -> ORDEN DE COMPRA (5)`. El circuito con Reclamo Prestacional
conserva `RECLAMO (RP) (4) -> PARA AUTORIZAR (6) -> AUTORIZADO (7)`.

La Orden de Compra solo puede crearse desde AUTORIZADO. Repetir la confirmacion
con el requerimiento ya en ORDEN DE COMPRA devuelve 5 y conserva su auditoria.
Enviar y autorizar repetidos se rechazan por el estado vigente.

## Permisos y pantalla

El envio requiere `COTIZAR_Compras`; aprobar requiere `AUTORIZAR_Compras`.
Crear y asignar en el portal el rol `AUTORIZAR_Compras` a los autorizadores.
Con el Java de esta entrega, ese rol permite consultar el requerimiento,
los detalles, las cotizaciones y la pantalla de autorizacion necesarios para
aprobar. No requiere asignar tambien `VIEW_Compras` y no habilita la edicion,
la cotizacion ni la creacion de la Orden de Compra.

En RRHH/Sistemas, la pantalla de consulta muestra el resumen, los detalles,
las cotizaciones y la Empresa adjudicada. En PARA AUTORIZAR aparece la accion
Autorizar para el rol correspondiente. Despues de aprobar, Crear Orden de Compra
queda disponible para el permiso de cotizacion existente.

## Datos y concurrencia

Enviar y autorizar en RRHH/Sistemas exigen requerimiento y sector activos,
al menos un detalle activo, una cotizacion de Empresa activa y exactamente
una Empresa adjudicada. Se conserva la validacion fiscal diferida de 20261006:
se admite nombre de Empresa sin CUIT; cuando hay identidad fiscal informada,
se conservan las reglas instaladas y la verificacion de sucursal contra el padron.
La autorizacion no requiere un Reclamo Prestacional para estos sectores.

La seleccion de Empresa adjudicada y las cotizaciones se preparan en PENDIENTE.
Las operaciones SQL de alta, baja, reactivacion y adjudicacion ya exigen ese
estado bajo bloqueo del requerimiento. El detalle estructural tambien exige
PENDIENTE. Las peticiones de una pantalla anterior se rechazan cuando el
requerimiento ya esta en PARA AUTORIZAR o AUTORIZADO.

Las funciones de envio, aprobacion y OC bloquean la misma fila del requerimiento.
La ruta generica de cambio de estado delega los destinos 6, 7 y 5 a las funciones
especificas y rechaza otras salidas desde 6 o 7. Por eso no puede usarse para
omitir la aprobacion aunque el ambiente no tenga instalado el trigger validador.
En el circuito medico se conserva el bloqueo RP antes del vinculo y la exigencia
de RP activo y VINCULADO.

## Instalacion

Revisar y guardar las definiciones del ambiente destino antes de instalar.
El contrato fiscal de `20261006_cotizaciones_identificacion_fiscal_diferida.sql`
y el de edicion de Empresa de `20261007_edicion_empresa_cotizacion.sql` deben
estar vigentes. Instalar primero `20261013_autorizacion_requerimiento.sql`
si falta la funcion de autorizacion; despues instalar
`20261015_autorizacion_orden_compra_empresas.sql`, antes del Java/JSP de esta entrega.

La migracion 20261015 es transaccional y exige los contratos de funciones,
columnas e indice de adjudicacion conocidos. Una variante no reconocida de los
fragmentos a adaptar aborta la transaccion. No modifica datos ni instala nuevos
triggers. Si existe `validar_requerimiento_fila()`, adapta sus bloques conocidos:
congela la fila en 6/7 salvo estado y auditoria, permite 7->5 solo corporativo
y conserva las demas validaciones. Conserva la asociacion de triggers instalada.
Puede volver a ejecutarse con sus propios bloques ya instalados.

No ejecutar automaticamente `20261014_baja_vinculo_reclamo_prestacional.sql`
como prerequisito de esta entrega: esa migracion incluye conciliacion de datos
historicos y pertenece a su despliegue especifico. El nuevo cuerpo de autorizacion
ya conserva el orden de bloqueo RP/vinculo que incorporo aquel cambio.

El archivo `compras_schema.sql` refleja las funciones nuevas y la columna e indice
de adjudicacion que el catalogo QA ya tenia instalados. No debe ejecutarse completo
sobre un ambiente existente; la instalacion incremental usa las migraciones.

## Evidencia y limites

El 15/10/2026 se leyo el catalogo configurado de QA, `devmolineros`, con PostgreSQL
9.6.15, dentro de una transaccion READ ONLY finalizada con ROLLBACK. No existia la
funcion de autorizacion de 20261013, no habia filas 6/7 ni CHECK de estado, y los
validadores de cabecera/detalle existian sin trigger asociado. La nueva migracion
no se aplico a QA y no se modificaron sus datos.

La validacion SQL de esta entrega usa un cluster temporal local PostgreSQL 18
con datos sinteticos, funciones instaladas capturadas y las migraciones reales.
La funcion de busqueda del padron tiene un doble local limitado a la identidad
fiscal sintetica. Esa prueba verifica transiciones, bloqueo, rollback y SQL real
en el entorno aislado; no demuestra compatibilidad ejecutada en PostgreSQL 9.6,
configuracion de roles del portal ni comportamiento autenticado de la pantalla.
Los resultados detallados quedan en
`.codex/artifacts/compras-autorizacion-rrhh-20261015/`.

PRUEBA UI MANUAL PENDIENTE hasta ejecutar la pantalla autenticada con usuario de
cotizacion y usuario que tenga solo AUTORIZAR_Compras, comprobar mensajes,
requests, persistencia, doble envio y log del backend en el ambiente desplegado.

Para revertir despues de usar 6/7 en requerimientos corporativos, resolver primero
esos requerimientos mediante una decision funcional autorizada. No renumerar
estados ni restaurar una funcion antigua que permita crear OC desde PENDIENTE
sin evaluar esos datos. Los documentos y las adjudicaciones se conservan.