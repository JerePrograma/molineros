# Checklist de continuidad — Reclamos Prestacionales

Este checklist permite retomar el trabajo directamente sobre `main` sin perder
el orden de dependencias ni confundir mitigaciones con resoluciones definitivas.

## Completado en `main`

- [x] restauración de precarga técnica;
- [x] revisión fail-closed;
- [x] cierre rechazado con códigos `3/5`;
- [x] rollback visual ante error de revisión;
- [x] parser de evaluación sin `String ==`;
- [x] construcción del reclamo fail-closed;
- [x] doble submit bloqueado;
- [x] estabilizador AJAX del editor;
- [x] carga de letras sin AJAX síncrono en el parche nuevo;
- [x] guard de pestañas con detección de duplicación;
- [x] propagación de `reclamoDraftId`;
- [x] helper backend `ReclamoPrestacionalDraftScope`;
- [x] snapshot externo anterior a la baja;
- [x] baja local + outbox en una transacción PostgreSQL;
- [x] confirmación HTTP limitada a `200/204`;
- [x] outbox durable con lease y backoff;
- [x] dispatcher daemon de reintentos;
- [x] autenticación configurable para el flujo reparado;
- [x] timeouts HTTP de conexión y lectura;
- [x] logs sin cuerpos externos;
- [x] contratos textuales y gate de release;
- [x] health check operativo de outbox;
- [x] documentación de despliegue, rollback y reconciliación.

## Bloqueos antes de producción

- [ ] ejecutar migración de outbox en la base objetivo;
- [ ] configurar las cuatro claves AppMobile;
- [ ] rotar credenciales históricas;
- [ ] retirar físicamente secretos de `ClienteAppMobile.java`;
- [ ] compilar proyecto completo en entorno Liferay;
- [ ] inspeccionar WAR y assets `p0-4`;
- [ ] ejecutar smoke tests completos;
- [ ] confirmar idempotencia de `AN` en AppMobile;
- [ ] monitorear outbox y logs durante 24 horas.

## Próximos commits recomendados

### Etapa 1 — eliminar duplicaciones legacy

- [ ] retirar segunda llamada AppMobile de `EditarReclamosEntryAction`;
- [ ] retirar relectura del reclamo después de la baja;
- [ ] eliminar `BAJAS_RECIENTES` una vez corregida la Action;
- [ ] actualizar contrato de sincronización.

### Etapa 2 — aislamiento backend por borrador

Migrar de una en una:

- [ ] prestación en edición;
- [ ] lista de prestaciones;
- [ ] lista de revisiones;
- [ ] lista de contactos;
- [ ] prestación en proceso;
- [ ] reclamo actual;
- [ ] lecturas de JSP correspondientes.

Cada commit debe incluir:

- resolución de `reclamoDraftId`;
- clave namespaced;
- fallback legacy temporal;
- contrato textual;
- prueba manual con dos pestañas.

### Etapa 3 — cierre estructural del editor

- [ ] corregir físicamente `datos_edicion_prestacion.jsp`;
- [ ] retirar saneamiento textual del interceptor;
- [ ] centralizar inicialización del fragmento;
- [ ] eliminar scripts anidados;
- [ ] verificar edición/autorización/rechazo.

### Etapa 4 — concurrencia real

- [ ] definir versión del agregado;
- [ ] persistirla o exponer timestamp confiable;
- [ ] enviarla en formulario;
- [ ] rechazar actualizaciones obsoletas;
- [ ] probar dos usuarios concurrentes.

### Etapa 5 — HTTP y semántica de mutación

- [ ] inventariar mutaciones GET/renderURL;
- [ ] migrar a ActionURL/POST;
- [ ] revisar CSRF;
- [ ] aplicar Post/Redirect/Get;
- [ ] definir idempotencia;
- [ ] codificar parámetros correctamente.

### Etapa 6 — operación

- [ ] integrar scheduler al ciclo de vida Liferay;
- [ ] agregar shutdown en redespliegue;
- [ ] crear panel de outbox;
- [ ] exportar métricas;
- [ ] registrar usuario originante y reconciliador.

## Regla de commits

Trabajar sobre `main` con commits pequeños y descriptivos. Después de cada
cambio:

1. revisar diff exacto;
2. actualizar o agregar contrato;
3. ejecutar contratos disponibles;
4. documentar limitaciones;
5. no declarar producción lista sin build y smoke test reales.
