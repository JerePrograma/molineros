# Reclamos Prestacionales — despliegue controlado

## Estado de este documento

Este procedimiento describe el estado actual de `main` después de las
reparaciones P0, la estabilización del editor, el guard de pestañas y la outbox
AppMobile.

No representa una reescritura completa del módulo. Continúan existiendo
componentes legacy que requieren migración gradual.

## Objetivos cubiertos

El conjunto actual reduce los riesgos de:

- pérdida de código, descripción, troquel o nomenclador precargado;
- revisión rechazada sin estado cerrado `3` y gestión rechazada `5`;
- cierre ejecutado antes de confirmar la revisión;
- pantalla visualmente cerrada después de fallar el endpoint de revisión;
- evaluación degradada por comparar `String` con `==`;
- continuación con un reclamo nulo o parcialmente construido;
- doble submit accidental;
- editor AJAX inicializado por JavaScript estructuralmente inválido;
- AJAX síncrono al cargar letras de comprobante;
- sobrescritura accidental entre dos pestañas del mismo navegador;
- pérdida de `idReintegroApp` después de la baja;
- confirmación falsa de sincronización ante HTTP no exitoso;
- baja local confirmada sin un evento durable de reconciliación;
- uso de credenciales literales por el flujo AppMobile reparado;
- registro de cuerpos de respuesta externos potencialmente sensibles.

## Activos del navegador

`view_reclamo.jsp` carga actualmente, en este orden:

```text
view_reclamo.js?v=20260716-p0-4
view_reclamo_tab_guard.js?v=20260716-p0-4
view_reclamo_editor_patch.js?v=20260716-p0-4
view_reclamo_p0_patch.js?v=20260716-p0-4
```

El orden es obligatorio:

1. el JavaScript legacy define las funciones originales;
2. el guard establece propiedad de pestaña y propaga `reclamoDraftId`;
3. el estabilizador del editor intercepta la carga del fragmento defectuoso;
4. la capa P0 reemplaza revisión, cierre, precarga y doble submit.

### Capa P0

`view_reclamo_p0_patch.js`:

- restaura la selección técnica precargada;
- separa render del sector de limpieza destructiva;
- valida la fecha de revisión;
- registra la revisión mediante `POST`;
- espera éxito antes de intentar el cierre;
- utiliza `estado=3` y `gestión=5` para rechazo;
- envía los flags de revisión, incluido `chk_entramite`;
- falla cerrado si el endpoint no responde;
- restaura estado y gestión anteriores ante error;
- distingue revisión persistida de cierre no completado;
- bloquea doble submit;
- normaliza fechas opcionales completamente vacías.

### Estabilizador del editor

`view_reclamo_editor_patch.js`:

- intercepta exclusivamente `editar_reclamosprestaciones`;
- retira de la respuesta los bloques JavaScript legacy conocidos como rotos;
- reinicializa el editor de manera controlada;
- carga letras de comprobante de forma asíncrona;
- corrige etiquetas de edición, autorización y rechazo;
- encapsula cálculos numéricos sin variables globales implícitas;
- presenta un error visible cuando el fragmento no puede cargarse.

El JavaScript inválido todavía existe físicamente en
`datos_edicion_prestacion.jsp`. La capa evita su ejecución en la ruta AJAX
interceptada, pero la eliminación física sigue pendiente.

### Guard de pestañas

`view_reclamo_tab_guard.js`:

- asigna una identidad única a cada carga de página;
- conserva un `draftId` por pestaña;
- detecta pestañas duplicadas que heredaron `sessionStorage`;
- mantiene un lease compartido mediante `localStorage`;
- bloquea los controles de pestañas no propietarias;
- permite tomar control explícitamente;
- expira el lease si la pestaña propietaria desaparece;
- agrega `reclamoDraftId` al formulario y a solicitudes AJAX relacionadas.

Este guard es una mitigación cliente. Las claves globales de sesión de las
Actions todavía no fueron migradas completamente al scope backend por borrador.

## Reconstrucción del reclamo

`ReclamosBaseAction` contiene cambios deliberadamente acotados:

- parser explícito de evaluación;
- soporte para `AUTORIZADO/AUTORIZADA` y `RECHAZADO/RECHAZADA`;
- soporte para `SINVALOR`, `SINEVALUACION` y `SIN_EVALUACION`;
- fallback del combo visible de gestión cuando el hidden llega vacío;
- reemplazo de un `&` booleano por `&&`;
- aborto explícito si el constructor falla;
- prohibición de continuar con un objeto nulo.

No se reinterpretaron globalmente las reglas de cuentas, afiliados,
seccionales ni todas las fechas del formulario legacy.

## Scope backend de borradores

`ReclamoPrestacionalDraftScope` define la convención para la migración gradual:

- parámetro: `reclamoDraftId`;
- formato permitido: letras, números, guion y guion bajo, entre 8 y 80 caracteres;
- ausencia del parámetro: compatibilidad `legacy`;
- clave aislada: `<claveBase>::DRAFT::<draftId>`;
- soporte para `HttpSession` y `PortletSession.APPLICATION_SCOPE`.

El helper está implementado y protegido por contrato, pero aún debe conectarse
a cada Action y cada fragmento que utiliza claves globales como listas,
prestación en edición, revisiones y contactos.

## Baja local y AppMobile

### Atomicidad local

Para reclamos vinculados a AppMobile,
`ReclamoPrestacionalBajaTransaccionalService` ejecuta en la misma conexión y el
mismo commit PostgreSQL:

1. `autorizaciones.borra_reclamo_prestacional`;
2. inserción o reactivación del evento outbox `(idReintegroApp, AN)`.

Si la outbox no puede registrarse, la baja local también se revierte.

La semántica del stored procedure se conserva: sólo un resultado `0` impide la
baja; una ejecución sin filas se trata como éxito, igual que el código legacy.

### Intento inmediato

Después del commit local:

1. se obtiene el token mediante configuración;
2. se ejecuta la solicitud de estado `AN`;
3. únicamente HTTP `200` o `204` se considera confirmado;
4. si se confirma, la outbox pasa a `PROCESADO`;
5. si falla, permanece `PENDIENTE`.

Los clientes nuevos no escriben cuerpos de respuesta en logs. Sólo registran
HTTP, longitud de respuesta y contexto técnico no secreto.

### Outbox durable

Es obligatorio ejecutar:

```text
sql/postgresql/autorizaciones/reclamo_appmobile_outbox.sql
```

La tabla utiliza:

- `PENDIENTE`;
- `PROCESANDO` con lease de cinco minutos;
- `PROCESADO`;
- índice parcial único por `id_reintegro_app + estado_destino` mientras el
  evento no esté procesado;
- backoff de 1, 2, 4, 8, 16, 32 y 60 minutos.

El dispatcher:

- usa un único hilo daemon por JVM;
- se inicia al cargar `ReclamosPrestacionesServiceUtil`;
- ejecuta un lote máximo de 20 eventos cada minuto;
- evita dos ejecuciones simultáneas dentro de la misma JVM;
- recupera leases vencidos.

La entrega HTTP es **al menos una vez**. AppMobile debe tratar `AN` como una
transición idempotente.

### Compatibilidad con la Action legacy

La Action antigua todavía contiene una lectura y una posible segunda llamada
después de la baja. El servicio registra el ID como baja reciente durante 60
segundos y devuelve `null` ante esa relectura, neutralizando actualmente la
segunda solicitud.

Este guard debe retirarse cuando se elimine físicamente el bloque duplicado de
la Action.

## Configuración obligatoria

El flujo reparado requiere:

```text
APP_HOST_WEBSERVICE
APP_BACKOFFICE_API_KEY
APP_BACKOFFICE_EMAIL
APP_BACKOFFICE_PASSWORD
```

Estas claves se leen mediante `TraeListasServiceUtil.getSystemConfig`.

No registrar los valores en Git, SQL, documentación ni logs.

El archivo legacy `ClienteAppMobile.java` todavía conserva credenciales
históricas embebidas para otros flujos. La baja reparada y el worker de outbox
ya no utilizan ese autenticador, pero la eliminación física de esos secretos
sigue siendo obligatoria.

## Validación automatizada

El workflow actual se denomina:

```text
Reclamo Prestacional Contracts
```

Compila con Java 8 y ejecuta contratos independientes de Liferay para:

- P0 de revisión/cierre y reconstrucción;
- estabilizador del editor;
- guard de pestañas;
- scope backend de borradores;
- autenticación y sincronización AppMobile;
- outbox, leases, scheduler y backoff.

Estos contratos verifican estructura e invariantes textuales. No sustituyen:

- compilación completa del proyecto;
- carga real del WAR;
- pruebas de navegador;
- pruebas de stored procedures;
- llamadas reales a AppMobile.

## Por qué CI no compila Liferay completo

El build Ant depende de:

- instalación completa de Liferay y application server;
- rutas locales `app.server.*`;
- librerías del portal no reconstruidas por el workflow;
- propiedades de QA/producción;
- tareas que copian directamente al servidor.

Un `ant deploy` genérico en GitHub Actions produciría una validación falsa o
podría requerir secretos de entorno. La compilación completa debe ejecutarse en
el entorno legacy controlado.

## Orden obligatorio de despliegue

1. detener o drenar bajas de Reclamos Prestacionales;
2. confirmar las cuatro claves AppMobile;
3. ejecutar `reclamo_appmobile_outbox.sql`;
4. verificar tabla, restricciones e índices;
5. compilar el proyecto completo en el entorno Liferay;
6. inspeccionar el WAR y confirmar los cuatro assets `p0-4`;
7. registrar SHA, checksum y copia del WAR anterior;
8. desplegar en QA o nodo controlado;
9. invalidar caché de navegador, proxy o CDN;
10. ejecutar el smoke test completo;
11. desplegar en producción durante una ventana controlada;
12. monitorear base y logs durante al menos 24 horas.

No desplegar el código antes del esquema. Una baja vinculada necesita registrar
la outbox dentro de su transacción.

## Smoke test obligatorio

### Alta normal

1. crear un reclamo;
2. seleccionar afiliado, sector y tipo de pedido;
3. agregar una prestación;
4. guardar;
5. reabrir desde el buscador;
6. comparar cabecera, flags, evaluación, prestación y montos.

### Precarga desde Compras

1. iniciar desde un requerimiento `COTIZADO`;
2. registrar sector, código, descripción, nomenclador, prestador y montos;
3. esperar la inicialización completa;
4. confirmar que la precarga no fue limpiada;
5. guardar y volver a consultar;
6. verificar que no existe duplicación.

### Editor de prestación

1. abrir una prestación existente;
2. verificar ID, código, descripción y montos;
3. cancelar;
4. abrir otra fila;
5. confirmar que no conserva valores de la anterior;
6. probar edición, autorización y rechazo;
7. simular fallo del fragmento AJAX y verificar mensaje visible.

### Revisión autorizada

1. informar fecha válida y resolución autorizada;
2. registrar revisión;
3. verificar que el fragmento responde antes del guardado;
4. volver a consultar;
5. confirmar una sola revisión activa.

### Revisión rechazada

1. informar resolución rechazada;
2. confirmar cierre;
3. verificar antes del submit:
   - `estado=3`;
   - `tipo_gestion_cierre_reclamo=5`;
   - `tipogestion=5`;
4. consultar base y buscador.

### Fallo de revisión

Forzar error de red o servidor.

Esperado:

- no se ejecuta el guardado;
- estado y gestión vuelven al valor anterior;
- el botón queda disponible;
- aparece `El reclamo no fue guardado ni cerrado`.

### Revisión persistida y cierre inválido

Forzar una validación del formulario después de registrar la revisión.

Esperado:

- la pantalla informa que la revisión ya existe;
- permite corregir y guardar;
- no crea una segunda revisión activa.

### Dos pestañas

1. abrir el mismo editor en dos pestañas;
2. confirmar que una queda bloqueada;
3. duplicar la pestaña propietaria;
4. confirmar que la copia obtiene otra identidad y queda bloqueada;
5. tomar control desde la segunda;
6. confirmar que la primera pierde propiedad.

Esto valida la mitigación cliente, no el aislamiento backend definitivo.

### Baja vinculada a AppMobile

1. registrar ID local e `idReintegroApp`;
2. ejecutar la baja;
3. verificar que baja local y outbox se confirman juntas;
4. comprobar estado externo `AN`;
5. comprobar que la fila queda `PROCESADO`;
6. forzar token inválido y verificar fila `PENDIENTE`;
7. restaurar configuración y verificar reintento automático;
8. comprobar que no existen dos solicitudes causadas por la Action legacy.

## Monitoreo

Buscar:

```text
RECLAMO_APP_SYNC_PENDING
RECLAMO_APP_OUTBOX_UNAVAILABLE
RECLAMO_APP_OUTBOX_CONFIRM_PENDING
No se pudo completar baja transaccional
No se pudo procesar outbox AppMobile
Outbox AppMobile procesada
Los datos del Reclamo Prestacional son inválidos
```

Controlar también:

- cerrados con gestión `0`;
- rechazados cuyo estado no sea `3`;
- revisiones activas duplicadas;
- precargas de Compras sin referencia técnica;
- outbox pendiente vencida;
- leases vencidos;
- eventos con cinco o más intentos;
- reintegros externos distintos de `AN` después de una baja.

Las consultas SQL y el procedimiento detallado de reconciliación están en:

```text
docs/autorizaciones/RECLAMOS_APPMOBILE_OUTBOX_OPERACION.md
```

## Rollback

El rollback del WAR no debe eliminar la tabla de outbox.

1. detener nuevas bajas vinculadas;
2. retirar el WAR nuevo;
3. restaurar WAR y checksum anteriores;
4. reiniciar el contenedor;
5. invalidar caché;
6. consultar eventos pendientes y procesando;
7. reconciliar manualmente AppMobile;
8. auditar operaciones ejecutadas durante la ventana.

Un WAR anterior no procesa la outbox ni garantiza la atomicidad nueva. No
reanudar bajas vinculadas hasta decidir quién procesará los eventos existentes.

## Trabajo pendiente

1. eliminar físicamente las credenciales heredadas de `ClienteAppMobile.java` y
   rotarlas en AppMobile;
2. eliminar físicamente la segunda sincronización de la Action legacy;
3. migrar cada clave global de sesión a `ReclamoPrestacionalDraftScope`;
4. retirar el guard de baja reciente cuando la Action deje de releer;
5. implementar optimistic locking del agregado completo;
6. migrar mutaciones restantes de `renderURL/GET` a `POST`;
7. eliminar físicamente el JavaScript inválido del JSP de edición;
8. integrar el scheduler al ciclo de vida formal del classloader de Liferay;
9. agregar panel administrativo y métricas de outbox;
10. ejecutar pruebas E2E por sector, tipo de pedido y origen Compras.
