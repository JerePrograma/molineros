# Compras: correo al enviar el RP a autorizar

El boton Para Autorizar conserva los permisos de COTIZAR_COMPRAS y la
transicion transaccional vigente. Para Compras con RP vinculado prepara
un unico correo con los documentos existentes:

- Pedido de presupuesto del prestador adjudicado y del intento vigente,
  conservado en Document Library: RequerimientoCompra_{idCompra}.pdf.
- Comparativa asociada al RP en Document Library, en su version actual:
  {idRP}-ComparativaCompra_{idCompra}.pdf.
- Ficha imprimible del RP generada con el reporte Jasper vigente: RP {idRP}.pdf.
- Todas las ordenes medicas activas de la compra, con sus nombres y formatos originales.

La ficha PDF corresponde a la aclaracion funcional del 05/11/2026.
No se adjuntan otros archivos de la pestana Imagenes del RP.
El asunto reproduce el formato del correo de referencia, con los datos
vigentes del afiliado y de la compra, sin prefijo de reenvio:

    RP:{idRP}-{APELLIDO NOMBRE}-DU:{documento}-{LOCALIDAD}-{PROVINCIA}-ID:{idCompra}-COMPARATIVA

Los campos faltantes se muestran como S/D. Los controles y saltos de linea
se retiran del asunto. El cuerpo informa el envio a autorizar y la lista
de documentos, sin copiar firmas, direcciones o destinatarios del ejemplo.

## Configuracion

La migracion 20261105_email_autorizacion.sql agrega COMPRAS_EMAIL_AUTORIZACION
con valor vacio, por pedido funcional. Es repetible y conserva cualquier valor
ya configurado; aborta ante claves duplicadas. Esta entrega prepara el script,
no lo aplica a una base de datos ni cambia direcciones reales.

Completar el destinatario en SystemConfig antes de utilizar el envio del RP.
Admite una o varias direcciones separadas por punto y coma o coma.
Una clave vacia o con cualquier direccion invalida detiene la operacion
antes de cambiar el estado. No se omiten destinatarios invalidos en silencio.

Se reutilizan SMTP, remitente y credenciales del correo de cotizaciones de
Compras. Se respeta REQUERIMIENTO_REDIRECCION_QA_HABILITADA; si esta habilitada,
REQUERIMIENTO_EMAIL_QA reemplaza los destinos y tambien se valida estrictamente.
No se agregan las copias de cotizacion a esta notificacion de autorizacion.
Si GMAIL_SIZE_VALIDO_ADJUNTOS tiene un valor, se valida la suma de los bytes de
los adjuntos antes de confirmar el estado, segun la convencion de Autorizaciones.
El limite no incluye la sobrecarga MIME ni reemplaza las restricciones del SMTP.

## Errores y concurrencia

La identidad documental, la empresa, el sitio y el permiso VIEW se comprueban
antes de leer los bytes. La falta de un documento requerido, un contenido
invalido o un error de Jasper/DL detienen la operacion antes de cambiar el estado.
No se regeneran la comparativa ni el pedido al preparar este correo.

El cambio de estado conserva sus bloqueos, validaciones del RP activo y
rechazo de una segunda peticion. SMTP se ejecuta despues del commit: si falla,
la pantalla informa que la compra quedo PARA AUTORIZAR y que no pudo confirmarse
el correo. No se revierte el estado ni se reenvia automaticamente, dado que
el servidor SMTP podria haber aceptado parte o todo el mensaje. La recuperacion
de ese aviso requiere verificar primero el resultado del envio.

RRHH y Sistemas conservan su circuito de autorizacion sin RP y sin este correo.
No se modifican la confirmacion de autorizacion ni la emision de Orden de Compra.

## Referencias legacy

- PreAutorizacionAction.enviarCorreoAutorizacion: SystemConfig y correo con adjuntos.
- DescargarDocumentoCompraReclamoAction.descargarComparativa y descargarPedidoCotizacion:
  identidad DL, company/site, permiso VIEW y lectura segura.
- PdfServlet.generaRptReclamoPrestacional: plantilla y parametros de la ficha RP.
- CotizacionPrestadorMailHelper: transporte SMTP y construccion MIME de Compras.

## Validacion

Ejecutar test/compras/autorizacion_email_test.ps1 con Java 8 source/target 1.5.
Comprueba MIME real, documentos multiples, asunto, configuracion/QA, contexto,
relacion, secuencia de operaciones, limites y fallos de datos/persistencia/SMTP.
Las fronteras de datos, DL y SMTP se simulan; no demuestra entrega real ni
persistencia en base de datos. El mismo script ejecuta tambien la clase existente
ComprasAutorizadorPermisosTest para la regresion de reglas y transiciones.

PRUEBA UI MANUAL PENDIENTE: no habia listener de Tomcat en 8080 ni una sesion
local autenticada durante esta entrega. Quedan pendientes el build completo,
la instalacion de la clave, la prueba de la ficha Jasper con datos reales y
el envio SMTP con un destinatario configurado.
