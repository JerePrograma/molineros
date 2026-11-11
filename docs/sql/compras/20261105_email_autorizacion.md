# Compras: correo al enviar el RP a autorizar

El boton Para Autorizar conserva los permisos de COTIZAR_COMPRAS y la
transicion transaccional vigente. Primero confirma el cambio de estado y,
para Compras con RP vinculado, luego prepara un unico correo con los documentos:

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

Completar el destinatario en SystemConfig para habilitar el aviso por correo.
Admite una o varias direcciones separadas por punto y coma o coma.
Una clave vacia o con cualquier direccion invalida impide enviar el correo,
pero no bloquea la transicion de estado: la incidencia se registra en el log.
No se omiten destinatarios invalidos ni se afirma un envio que no se confirmo.

Se reutilizan SMTP, remitente y credenciales del correo de cotizaciones de
Compras. Se respeta REQUERIMIENTO_REDIRECCION_QA_HABILITADA; si esta habilitada,
REQUERIMIENTO_EMAIL_QA reemplaza los destinos y tambien se valida estrictamente.
No se agregan las copias de cotizacion a esta notificacion de autorizacion.
Si GMAIL_SIZE_VALIDO_ADJUNTOS tiene un valor, se valida la suma de los bytes de
los adjuntos antes de enviar el correo, segun la convencion de Autorizaciones.
El limite no incluye la sobrecarga MIME ni reemplaza las restricciones del SMTP.

## Errores y concurrencia

La identidad documental, la empresa, el sitio y el permiso VIEW se comprueban
antes de leer los bytes. La falta de un documento requerido, un contenido
invalido o un error de Jasper/DL impiden enviar el correo completo y generan
una incidencia en el log; el cambio de estado confirmado se conserva.
No se regeneran la comparativa ni el pedido al preparar este correo.

El cambio de estado conserva sus bloqueos, validaciones del RP activo y
rechazo de una segunda peticion. Toda la preparacion del correo y SMTP se ejecutan
despues del commit. Si fallan configuracion, documentos o SMTP, la pantalla
conserva el guardado exitoso y el helper registra el fallo con su causa en el log,
como las notificaciones posteriores al guardado de Ordenes de Pago de Liquidaciones.
La pantalla conserva el mensaje habitual de estado actualizado; no afirma que el
correo fue entregado. No se agregan excepciones de aviso, mensajes de sesion ni
nuevas navegaciones. Si falla el cambio de estado, el error original se propaga y
no se prepara ni envia correo. No se revierte ni se reenvia automaticamente, dado que
el servidor SMTP podria haber aceptado parte o todo el mensaje. La recuperacion
de ese aviso requiere verificar primero el resultado del envio.

RRHH y Sistemas conservan su circuito de autorizacion sin RP y sin este correo.
No se modifican la confirmacion de autorizacion ni la emision de Orden de Compra.

La conciliacion del 11/11/2026 elimina la dependencia del flujo de autorizacion
respecto del correo. Conserva los cambios del pull en configuracion operativa,
permisos de lectura de comparativa y Ordenes de Compra/suministro/seguimiento.

## Referencias legacy

- Liquidaciones: EditarOrdenesPagoOspimAction.processAction (guardado, correo posterior,
  destinatario faltante registrado en log y respuesta exitosa habitual), y
  SalvarUploadOrdenPagoFarmaciaOspimAction.enviaMailsTransferencias.
- Caller de esa referencia: OrdenPago.enviarMailTransferenciaBCC delega al envio
  desatendido; los problemas SMTP no modifican el resultado de la operacion.
- PreAutorizacionAction.enviarCorreoAutorizacion: solo referencia de adjuntos/SystemConfig.
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
local autenticada durante la revision del 11/11/2026. Quedan pendientes el build completo,
la instalacion de la clave, la prueba de la ficha Jasper con datos reales y
el envio SMTP con un destinatario configurado.
