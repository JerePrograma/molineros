# Backlog verificable — Reclamos Prestacionales

Este archivo resume los ítems que deben existir también como seguimiento de
trabajo en GitHub o en el sistema de tickets del equipo.

## Seguridad — retirar y rotar secretos AppMobile legacy

**Prioridad:** bloqueante de producción.

Alcance:

- rotar API key, email y contraseña del usuario técnico;
- retirar literales de `ClienteAppMobile.java`;
- migrar consumidores de `ClienteAppMobile.obtenerToken()`;
- verificar que el gate estricto pasa;
- evaluar limpieza del historial Git.

Evidencia automática:

```bash
bash scripts/reclamos/verificar_secretos_appmobile.sh
bash scripts/reclamos/validar_release_reclamos.sh
```

## Sesión — migrar claves globales a DraftScope

**Prioridad:** alta.

Alcance:

- ejecutar el inventario CSV;
- migrar una clave y endpoint por commit;
- mantener fallback legacy temporal;
- agregar contratos por cada lectura/escritura;
- probar dos pestañas y dos navegadores.

Evidencia automática:

```bash
bash scripts/reclamos/inventariar_sesion_reclamos.sh \
  /tmp/reclamos-session-inventory.csv
```

## Legacy — eliminar segunda llamada AppMobile

**Prioridad:** alta.

Alcance:

- eliminar relectura posterior a la baja;
- eliminar llamada directa `AN` de la Action;
- retirar guard `BAJAS_RECIENTES`;
- verificar una única ruta de sincronización: outbox.

## Editor — eliminar JavaScript inválido del JSP

**Prioridad:** media/alta.

Alcance:

- corregir el fragmento original;
- retirar saneamiento textual;
- conservar carga asíncrona y errores visibles;
- ejecutar smoke tests de edición, autorización y rechazo.

## Operación — formalizar ciclo de vida del scheduler

**Prioridad:** media.

Alcance:

- inicializar y detener mediante ciclo de vida Liferay/Tomcat;
- evitar classloader leaks en redespliegue;
- mantener protección multinodo mediante lease de base;
- agregar métrica de ejecución y pendientes.

## Concurrencia — optimistic locking del agregado

**Prioridad:** alta.

Alcance:

- versión persistida del reclamo;
- versión enviada en formulario;
- rechazo explícito de actualización obsoleta;
- prueba de dos usuarios sobre el mismo reclamo.
