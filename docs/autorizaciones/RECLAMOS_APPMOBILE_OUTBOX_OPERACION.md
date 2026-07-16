# Reclamos Prestacionales — outbox AppMobile

## Objetivo

La baja de un Reclamo Prestacional y la actualización del reintegro en AppMobile
ocurren en sistemas distintos. No existe una transacción distribuida entre la
base PostgreSQL de Molineros y el servicio HTTP externo.

La baja local y el registro de la outbox sí se ejecutan actualmente dentro de
la misma transacción PostgreSQL. Para reclamos vinculados a AppMobile, si no se
puede registrar el evento pendiente, también se revierte la baja local.

La outbox evita que un fallo de autenticación, red o HTTP quede registrado
únicamente en logs. El estado externo pendiente queda persistido y puede
reintentarse sin recrear el reclamo eliminado.

## Configuración obligatoria

El flujo reparado no utiliza credenciales literales del cliente AppMobile
legacy. Antes de desplegar deben existir estas claves en la configuración leída
por `TraeListasServiceUtil.getSystemConfig`:

```text
APP_HOST_WEBSERVICE
APP_BACKOFFICE_API_KEY
APP_BACKOFFICE_EMAIL
APP_BACKOFFICE_PASSWORD
```

Requisitos:

- `APP_HOST_WEBSERVICE`: esquema, host y puerto, sin necesidad de `/` final;
- `APP_BACKOFFICE_API_KEY`: valor de cabecera `api-key`;
- `APP_BACKOFFICE_EMAIL`: usuario técnico del backoffice;
- `APP_BACKOFFICE_PASSWORD`: contraseña del usuario técnico.

No registrar estos valores en Git, scripts SQL, logs o documentación operativa.
La aplicación sólo informa qué clave falta; nunca imprime sus contenidos.

## Orden obligatorio de despliegue

1. detener o drenar operaciones de baja durante la ventana;
2. confirmar las cuatro claves de configuración anteriores;
3. ejecutar el esquema:

   ```text
   sql/postgresql/autorizaciones/reclamo_appmobile_outbox.sql
   ```

4. verificar tabla, restricciones e índices;
5. desplegar el WAR que contiene los servicios de outbox;
6. reiniciar el contenedor según el procedimiento habitual;
7. comprobar el log `Despachador outbox AppMobile iniciado.`;
8. ejecutar una baja controlada vinculada a AppMobile;
9. verificar la fila de outbox y el estado externo `AN`.

No desplegar primero el código y después la tabla. En un reclamo vinculado a
AppMobile, la baja transaccional necesita insertar o reactivar la outbox antes
del commit. Si la tabla no existe, la baja se revierte y se registra un error.

## Flujo implementado

1. Molineros recupera el reclamo y conserva `idReintegroApp`.
2. Abre una transacción PostgreSQL.
3. Ejecuta `autorizaciones.borra_reclamo_prestacional`.
4. Dentro de la misma conexión inserta o reactiva el evento
   `(idReintegroApp, AN)`.
5. Confirma conjuntamente baja local y outbox.
6. Registra el ID como baja reciente durante 60 segundos para neutralizar la
   relectura y segunda llamada de la Action legacy.
7. Obtiene el token mediante configuración y realiza el intento HTTP inmediato.
8. Sólo HTTP `200` o `204` se considera confirmado.
9. Si se confirma, la fila pasa a `PROCESADO`.
10. Si falla, permanece `PENDIENTE` y el scheduler la reintenta.

## Semántica de entrega

La entrega hacia AppMobile es **al menos una vez**.

Puede existir una repetición de `AN` cuando:

- AppMobile confirmó la operación, pero Molineros perdió conexión antes de
  marcar la outbox como procesada;
- el proceso cayó entre la respuesta HTTP y el commit de confirmación;
- un lease venció mientras la solicitud externa seguía en curso.

El endpoint de AppMobile debe tratar la transición a `AN` como idempotente. Si
no lo hace, debe corregirse antes de considerar este flujo operacionalmente
seguro.

La atomicidad garantizada se limita a baja local + alta/reactivación de outbox.
No incluye la llamada HTTP externa.

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

En instalaciones con varios nodos, cada JVM puede iniciar su dispatcher. El
lease en base evita que dos nodos procesen normalmente la misma fila, aunque la
semántica sigue siendo al menos una vez ante caídas y expiración de leases.

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
AppMobile rechazó autenticación
AppMobile rechazó actualización de reintegro
No se pudo completar baja transaccional
No se pudo procesar outbox AppMobile
Outbox AppMobile procesada
```

Interpretación:

- `SYNC_PENDING`: el intento inmediato no fue confirmado;
- `OUTBOX_UNAVAILABLE`: falló una actualización posterior de la cola; la baja
  inicial vinculada ya creó el evento dentro de su transacción;
- `OUTBOX_CONFIRM_PENDING`: AppMobile respondió exitosamente, pero falló la
  confirmación en base; puede producirse una repetición;
- `No se pudo completar baja transaccional`: baja y outbox fueron revertidas;
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

Un WAR anterior no conoce la baja transaccional ni el scheduler de outbox. Por
eso no debe reanudarse el flujo normal de bajas vinculadas hasta definir quién
procesará los pendientes existentes.

## Limitaciones pendientes

- El guard de baja reciente neutraliza la Action antigua, pero debe retirarse
  cuando se elimine físicamente la segunda llamada de esa Action.
- El scheduler daemon no está integrado todavía al ciclo de vida formal de
  Liferay; un redespliegue debe verificar que el classloader anterior se libere.
- No hay panel administrativo de pendientes.
- No se persiste todavía el usuario que originó ni el operador que reconcilió.
- No existe una métrica exportada; el monitoreo actual depende de SQL y logs.
- Otros flujos legacy de `ClienteAppMobile` todavía deben migrarse para retirar
  definitivamente credenciales históricas del archivo heredado.
