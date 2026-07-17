# Reclamos Prestacionales — limpieza P1

## Objetivo

Eliminar tres mecanismos legacy que ya no deben coexistir con la estabilización P0 y la outbox de AppMobile:

1. la segunda llamada directa de anulación desde `EditarReclamosEntryAction`;
2. el cache temporal `BAJAS_RECIENTES`, agregado únicamente para ocultar la relectura posterior a la baja;
3. el JavaScript inválido y los `<script>` anidados de `datos_edicion_prestacion.jsp`.

## Resultado esperado

- una sola ruta de baja y sincronización externa;
- ningún intento de recuperar el reclamo después de borrarlo;
- el editor de prestación produce JavaScript válido;
- código y descripción se escapan para contexto JavaScript;
- edición, autorización y rechazo conservan los datos de la fila seleccionada.

## Validación funcional pendiente de entorno

- editar una prestación con nomenclador;
- editar una prestación de farmacia;
- cancelar y volver a abrir filas diferentes;
- autorizar y rechazar una prestación;
- dar de baja un reclamo vinculado a AppMobile y verificar una sola transición `AN`;
- revisar outbox y logs de reconciliación.
