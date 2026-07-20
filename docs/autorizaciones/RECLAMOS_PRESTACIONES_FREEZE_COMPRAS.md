# Reclamos Prestacionales: bloqueo al iniciar desde Compras

## Síntoma

Al presionar **Crear Reclamo Prestacional** desde un requerimiento COTIZADO, el navegador quedaba sin responder durante la apertura del editor.

## Causa raíz

La precarga de Compras publica la primera prestación en:

`WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION`

Por lo tanto, el primer render incluye `datos_edicion_prestacion.jsp`.

Ese JSP legacy ejecuta inmediatamente `filtrarLetraComprobanteEdicion()`. La función realiza una petición jQuery con `async:false` al endpoint `/autorizaciones/filtrarLetraComprobante`.

Una petición XHR síncrona bloquea el hilo principal del navegador hasta recibir respuesta. Si el endpoint demora por acceso a base de datos, carga del portlet o contención del servidor, toda la interfaz parece congelada.

La corrección anterior de renderizado no creó esa petición síncrona. Al permitir que la precarga llegara correctamente al editor, dejó expuesto el bloqueo que ya existía dentro del JSP legacy.

## Corrección

`view_reclamo.jsp` instala, antes de incluir el JSP legacy, un guard focalizado sobre `jQuery.ajax`.

El guard sólo interviene cuando se cumplen simultáneamente estas condiciones:

- la llamada usa la firma de opciones de jQuery;
- `async` está expresamente en `false`;
- la URL contiene `filtrarLetraComprobante`.

En ese caso clona las opciones y fuerza `async=true`. El resto de las llamadas AJAX conserva exactamente su comportamiento original.

Se registra en consola:

`RECLAMO_PRESTACIONAL_FILTRO_LETRA_ASYNC`

cuando el guard corrige una llamada bloqueante.

## Motivo de la implementación

`datos_edicion_prestacion.jsp` mantiene codificación ISO-8859-1 y contiene lógica legacy compartida con render inicial y cargas AJAX. Reescribir el archivo completo mediante una herramienta UTF-8 habría introducido riesgo de corrupción de caracteres.

La protección se ubica en `view_reclamo.jsp`, que es UTF-8, se ejecuta antes del include problemático y cubre también cualquier llamada posterior al mismo endpoint dentro de la pantalla.

## Alcance

Modificados:

- `view_reclamo.jsp`;
- `ReclamoPrestacionalEditorContractTest.java`;
- esta documentación.

No se modifican:

- la precarga de Compras;
- Actions;
- servicios o DAO;
- Struts o Tiles;
- el archivo ISO-8859-1 `datos_edicion_prestacion.jsp`;
- otros endpoints AJAX.

## Contrato automático

El contrato del editor exige que:

- el guard se instale antes de `view_reclamo.jspf`;
- sólo reconozca `filtrarLetraComprobante`;
- fuerce `opciones.async = true`;
- permanezca marcado para evitar dobles envolturas;
- el estabilizador AJAX del editor continúe sin llamadas síncronas.

## Smoke test en Liferay/Tomcat

1. Abrir un requerimiento COTIZADO en Compras.
2. Presionar **Crear Reclamo Prestacional**.
3. Confirmar que la navegación no congele el navegador.
4. Verificar que se abra el editor con la primera prestación precargada.
5. Confirmar que el combo de letra del comprobante se complete al responder el endpoint.
6. Revisar la consola: puede aparecer una vez `RECLAMO_PRESTACIONAL_FILTRO_LETRA_ASYNC`.
7. Simular demora en `/autorizaciones/filtrarLetraComprobante` y confirmar que el resto de la página siga respondiendo.
8. Editar una prestación desde la grilla y repetir la verificación.
9. Autorizar y rechazar una prestación para comprobar que el guard no afecta esos modos.
10. Revisar logs de servidor por demoras o errores del endpoint; el cliente deja de bloquearse, pero una demora de base de datos sigue siendo una incidencia independiente.

## Rollback

Revertir el commit completo. No requiere migración de base de datos ni limpieza de sesión.
