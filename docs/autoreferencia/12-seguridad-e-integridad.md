# Seguridad e integridad

## Patrones comprobados

- Escape HTML con `HtmlUtil.escape`.
- Validacion con `Validator`.
- Handoff Compras -> Reclamo validado por nonce, usuario y vigencia.
- Cast defensivo mediante `instanceof`.
- Comportamiento fail-closed: sin contexto valido no se fuerza `ADD`.
- Timeout y cancelacion de XHR en busqueda de afiliados.
- Manejo explicito de errores AJAX.
- Acciones de recuperacion de archivos y Document Library.

## Reglas

- Preservar permisos, nonce, sesion, validaciones y transacciones.
- Validar tipo, tamano, nombre y autorizacion en subidas/descargas.
- No exponer secretos ni datos personales o medicos.
- No documentar valores de tokens de build, contrasenas o conexiones.
- No convertir una llamada asincrona en sincronica ni eliminar timeouts sin evidencia.
