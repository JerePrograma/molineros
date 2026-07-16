# Reclamos Prestacionales — outbox AppMobile

## Objetivo

La baja de un Reclamo Prestacional y la actualización del reintegro en AppMobile
ocurren en sistemas distintos. No existe una transacción distribuida entre la
base PostgreSQL de Molineros y el servicio HTTP externo.

La outbox evita que un fallo de token, red o HTTP quede registrado únicamente
en logs. El estado externo pendiente queda persistido y puede reintentarse sin
recrear el reclamo eliminado.

## Orden obligatorio de despliegue

1. detener o drenar operaciones de baja durante la ventana;
2. ejecutar el esquema:

   ```text
   sql/postgresql/autorizaciones/reclamo_appmobile_outbox.sql
   ```

3. verificar tabla, restricciones e índices;
4. desplegar el WAR que contiene los servicios de outbox;
5. reiniciar el contenedor según el procedimiento habitual;
6. comprobar el log `Despachador outbox AppMobile iniciado.`;
7. ejecutar una baja controlada vinculada a AppMobile;
8. verificar la fila de outbox y el estado externo `AN`.

No desplegar primero el código y después la tabla. El flujo de baja seguirá
intentando la sincronización directa, pero el scheduler registrará errores de
base una vez por minuto hasta que el esquema exista.

## Flujo implementado

1. Molineros recupera el reclamo y conserva `idReintegroApp`.
2. Ejecuta la baja local.
3. Registra el ID como baja reciente durante 60 segundos para neutralizar la
   relectura y segunda llamada de la Action legacy.
4. Inserta o reactiva el evento outbox `(idReintegroApp, AN)`.
5. Obtiene token y realiza el intento HTTP inmediato.
6. Sólo HTTP `200` o `204` se considera confirmado.
7. Si se confirma, la fila pasa a `PROCESADO`.
8. Si falla, permanece `PENDIENTE` y el scheduler la reintenta.

## Semántica de entrega

La entrega es **al menos una vez**.

Puede existir una repetición de `AN` cuando:

- AppMobile confirmó la operación, pero Molineros perdió conexión antes de
  marcar la outbox como procesada;
- el proceso cayó entre la respuesta HTTP y el commit de confirmación;
- un lease venció mientras la solicitud externa seguía en curso.

El endpoint de AppMobile debe tratar la transición a `AN` como idempotente. Si
no lo hace, debe corregirse antes de considerar este flujo transaccionalmente
seguro.

## Estados de la tabla

- `PENDIENTE`: disponible cuando `proximo_intento <= NOW()`.
- `PROCESANDO`: reservado mediante lease de cinco minutos.
- `PROCESADO`: AppMobile confirmó HTTP `200` o `204`.

Un registro `PROCESANDO` cuyo `bloqueado_hasta` venció vuelve a ser elegible.

## Reintentos

- lote máximo por ejecución: `20`;
- frecuencia del dispatcher: un minuto;
- máximo de una ejecución concurrente por JVM;
- backoff: 1, 2, 4, 8, 16, 32 y 60 minutos;
- máximo aceptado por llamada manual: `100` eventos.

El scheduler se inicia al cargar `ReclamosPrestacionesServiceUtil` y utiliza un
hilo daemon de prioridad mínima.

## Consultas de validación

### Existencia del esquema

```sql
SELECT to_regclass(
    'autorizaciones.reclamo_appmobile_outbox'
) AS tabla_outbox;
```

### Resumen por estado

```sql
SELECT
    estado_proceso,
    count(*) AS cantidad,
    min(creado_en) AS mas_antiguo,
    max(actualizado_en) AS ultima_actualizacion
FROM autorizaciones.reclamo_appmobile_outbox
GROUP BY estado_proceso
ORDER BY estado_proceso;
```

### Pendientes vencidos

```sql
SELECT
    id,
    id_reclamo,
    id_reintegro_app,
    estado_destino,
    intentos,
    proximo_intento,
    ultimo_error
FROM autorizaciones.reclamo_appmobile_outbox
WHERE procesado_en IS NULL
  AND proximo_intento <= NOW()
ORDER BY id;
```

### Leases vencidos

```sql
SELECT
    id,
    id_reintegro_app,
    intentos,
    bloqueado_hasta,
    ultimo_error
FROM autorizaciones.reclamo_appmobile_outbox
WHERE estado_proceso = 'PROCESANDO'
  AND bloqueado_hasta < NOW()
  AND procesado_en IS NULL
ORDER BY bloqueado_hasta;
```

### Eventos con muchos intentos

```sql
SELECT
    id,
    id_reclamo,
    id_reintegro_app,
    intentos,
    ultimo_error,
    actualizado_en
FROM autorizaciones.reclamo_appmobile_outbox
WHERE procesado_en IS NULL
  AND intentos >= 5
ORDER BY intentos DESC, actualizado_en ASC;
```

## Logs operativos

Buscar:

```text
RECLAMO_APP_SYNC_PENDING
RECLAMO_APP_OUTBOX_UNAVAILABLE
RECLAMO_APP_OUTBOX_CONFIRM_PENDING
AppMobile rechazó actualización de reintegro
No se pudo procesar outbox AppMobile
Outbox AppMobile procesada
```

Interpretación:

- `SYNC_PENDING`: el intento inmediato no fue confirmado;
- `OUTBOX_UNAVAILABLE`: no se pudo persistir o actualizar la cola;
- `OUTBOX_CONFIRM_PENDING`: AppMobile respondió exitosamente, pero falló la
  confirmación en base; puede producirse una repetición;
- `Outbox ... procesada`: cantidad confirmada en un lote automático.

## Reconciliación manual

1. consultar la fila pendiente;
2. verificar el estado actual del reintegro en AppMobile;
3. si ya está en `AN`, marcarla procesada:

   ```sql
   UPDATE autorizaciones.reclamo_appmobile_outbox
   SET estado_proceso = 'PROCESADO',
       procesado_en = NOW(),
       bloqueado_hasta = NULL,
       ultimo_error = NULL,
       actualizado_en = NOW()
   WHERE id = :id
     AND procesado_en IS NULL;
   ```

4. si no está en `AN`, reactivar el intento:

   ```sql
   UPDATE autorizaciones.reclamo_appmobile_outbox
   SET estado_proceso = 'PENDIENTE',
       proximo_intento = NOW(),
       bloqueado_hasta = NULL,
       actualizado_en = NOW()
   WHERE id = :id
     AND procesado_en IS NULL;
   ```

5. conservar evidencia del estado externo y del operador que realizó la
   reconciliación.

## Rollback de aplicación

El WAR anterior puede restaurarse, pero las filas de outbox deben conservarse.
No eliminar la tabla durante un rollback de código.

Después de volver al WAR anterior:

1. detener nuevas bajas vinculadas a AppMobile;
2. consultar pendientes;
3. reconciliarlos manualmente;
4. mantener la tabla para auditoría;
5. eliminarla sólo mediante una decisión posterior y con backup.

## Limitaciones pendientes

- La baja local y el insert de outbox todavía son dos transacciones de base
  separadas. Existe una ventana mínima entre ambas.
- El guard de baja reciente neutraliza la Action antigua, pero debe retirarse
  cuando se elimine físicamente la segunda llamada de esa Action.
- No hay panel administrativo de pendientes.
- No se persiste todavía el usuario que originó ni el operador que reconcilió.
- No existe una métrica exportada; el monitoreo actual depende de SQL y logs.
