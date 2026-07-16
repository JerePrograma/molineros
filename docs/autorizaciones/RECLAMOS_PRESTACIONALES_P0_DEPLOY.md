# Reclamos Prestacionales — despliegue P0 y reconciliación

## Objetivo

Este PR estabiliza los puntos de mayor riesgo de Reclamos Prestacionales sin
restaurar el JSP monolítico, sin migraciones SQL y sin reescribir todavía el
módulo completo.

El alcance está limitado a evitar:

- pérdida de datos precargados durante la inicialización;
- revisión rechazada con estado o gestión incorrectos;
- guardado/cierre antes de confirmar que la revisión fue registrada;
- pantalla visualmente cerrada después de fallar la revisión;
- evaluación degradada a `SINEVALUACION` por comparación incorrecta de
  `String`;
- continuación del flujo con un reclamo que no pudo reconstruirse;
- doble submit accidental;
- pérdida de `idReintegroApp` por consultarlo después de la baja local;
- mezcla de versiones antiguas y nuevas del JavaScript por caché.

La auditoría completa contiene defectos adicionales. Este PR no debe tratarse
como cierre definitivo del módulo.

---

## Cambios implementados

### Navegador

`view_reclamo.jsp` captura un snapshot de la selección técnica antes de ejecutar
el JavaScript legacy y luego carga:

```text
view_reclamo.js?v=20260716-p0-2
view_reclamo_p0_patch.js?v=20260716-p0-2
```

La capa `view_reclamo_p0_patch.js`:

- restaura troquel, código, descripción y nomenclador precargados;
- evita que el render inicial del sector borre la selección técnica;
- valida la fecha de revisión antes de enviar;
- registra la revisión mediante `POST`;
- espera la respuesta exitosa antes de guardar o cerrar;
- utiliza los códigos canónicos:
  - estado cerrado: `3`;
  - gestión rechazada: `5`;
- envía los flags leídos por la pantalla, incluido `chk_entramite`;
- falla cerrado cuando el endpoint de revisión no responde;
- restaura el estado visual anterior si la revisión falla;
- diferencia el caso en que la revisión fue registrada pero el guardado del
  cierre no pudo completarse;
- impide doble submit durante una ventana corta;
- intenta reinicializar el editor después de cargar el fragmento AJAX;
- normaliza fechas opcionales completamente vacías antes del submit.

### Servidor

`ReclamosBaseAction` recibe cambios quirúrgicos:

- reemplaza comparaciones `String ==` por un parser explícito;
- acepta las representaciones legacy y enum:
  - `AUTORIZADO` / `AUTORIZADA`;
  - `RECHAZADO` / `RECHAZADA`;
  - `SINVALOR`;
  - `SINEVALUACION` / `SIN_EVALUACION`;
- usa `tipo_gestion_cierre_reclamo` como fallback cuando el hidden
  `tipogestion` llega vacío o en cero;
- reemplaza un `&` booleano por `&&`;
- registra y aborta cuando el constructor del reclamo falla;
- impide continuar con un objeto nulo después de una reconstrucción fallida.

El PR **no cambia** deliberadamente las reglas generales de cuenta bancaria,
afiliado, seccional ni parsing de fechas del formulario legacy. Esas áreas
requieren tickets separados y pruebas específicas.

### Baja local y AppMobile

`ReclamosPrestacionesServiceUtil.borrar` ahora:

1. consulta el reclamo antes de borrarlo;
2. conserva `idReintegroApp`;
3. cancela la baja si no puede obtener el snapshot previo;
4. ejecuta la baja local;
5. solicita el estado externo `AN` usando el identificador preservado;
6. registra `RECLAMO_APP_SYNC_PENDING` si no obtiene token o si la llamada
   arroja una excepción visible para el caller.

---

## Límites de sincronización externa

No existe una transacción distribuida entre PostgreSQL y AppMobile. La baja
local puede confirmarse y luego fallar la llamada externa.

Además, `ClienteAppMobile.actualizarEstadoReintegro` registra internamente las
respuestas HTTP no exitosas y no las propaga como excepción. Por eso deben
buscarse dos familias de logs:

```text
RECLAMO_APP_SYNC_PENDING
Error al actualizar estado del reintegro
Excepción al actualizar estado del reintegro
```

`RECLAMO_APP_SYNC_PENDING` no cubre necesariamente todos los HTTP no exitosos.
La solución definitiva es una outbox persistente, idempotente y reintentable.

La acción legacy todavía contiene una sincronización posterior a la baja. Si la
baja es lógica y el registro aún puede recuperarse, podría producirse una
segunda solicitud `AN`. Antes del merge se debe verificar en el entorno real
que AppMobile trata esa operación de forma idempotente o retirar ese bloque en
un PR controlado.

---

## Validación automatizada

El PR agrega el workflow:

```text
Reclamo Prestacional P0 Contract
```

Compila y ejecuta con Java 8:

```bash
mkdir -p /tmp/rp-p0-contract
javac -encoding UTF-8 \
  -d /tmp/rp-p0-contract \
  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java

java -cp /tmp/rp-p0-contract \
  ar.com.ospim.test.ReclamoPrestacionalP0ContractTest
```

Resultado esperado:

```text
CONTRATO_RECLAMO_PRESTACIONAL_P0_OK
```

Este contrato verifica estructura e invariantes críticas. No compila el
proyecto completo ni ejecuta Liferay.

---

## Por qué CI no ejecuta el build completo

El build Ant del repositorio depende de:

- una instalación completa de Liferay y del application server;
- rutas `app.server.*` locales;
- librerías del portal;
- configuración de base y propiedades de QA/producción;
- tareas de deploy que copian archivos directamente al servidor.

Agregar un workflow genérico de `ant deploy` sin reconstruir ese entorno sería
una validación falsa o podría exponer configuración sensible.

La compilación completa debe ejecutarse en el entorno de build legacy
controlado antes del merge.

---

## Smoke test obligatorio

No usar inicialmente un reclamo real con orden de pago. Trabajar con datos de
prueba trazables y registrar IDs antes y después.

### 1. Alta normal

1. Crear un reclamo nuevo.
2. Seleccionar afiliado, sector y tipo de pedido.
3. Agregar una prestación.
4. Guardar.
5. Volver a abrir desde el buscador.
6. Comparar cabecera, flags, evaluación, prestación y montos.

Esperado: los datos persistidos coinciden con la pantalla enviada.

### 2. Precarga desde Compras

1. Iniciar desde un requerimiento `COTIZADO`.
2. Registrar sector, código, descripción, tipo de nomenclador, prestador y
   montos.
3. Esperar la carga completa de la pantalla.
4. Confirmar que la inicialización no limpió esos datos.
5. Completar los campos obligatorios.
6. Guardar y volver a consultar.

Esperado: no se pierde ni duplica la prestación precargada.

### 3. Edición de prestación

1. Abrir una prestación existente.
2. Verificar ID, código, descripción y montos.
3. Cancelar.
4. Abrir otra prestación.
5. Confirmar que no conserva valores de la anterior.
6. Repetir con edición, autorización y rechazo.

Esperado: el editor siempre representa la fila elegida.

### 4. Revisión autorizada

1. Informar fecha válida y resolución autorizada.
2. Registrar la revisión.
3. Verificar que el fragmento responde antes del guardado.
4. Volver a consultar el reclamo.

Esperado: una sola revisión activa y evaluación autorizada.

### 5. Revisión rechazada

1. Informar una revisión rechazada.
2. Confirmar el cierre.
3. Antes del submit verificar:
   - `estado=3`;
   - `tipo_gestion_cierre_reclamo=5`;
   - `tipogestion=5`.
4. Volver a consultar en base y buscador.

Esperado: revisión persistida, estado cerrado y gestión rechazada.

### 6. Error al registrar revisión

Forzar un error controlado o bloquear temporalmente el endpoint.

Esperado:

- no se ejecuta el guardado del reclamo;
- el estado visual vuelve al valor anterior;
- la gestión visual vuelve al valor anterior;
- el botón queda nuevamente disponible;
- aparece `El reclamo no fue guardado ni cerrado`.

### 7. Revisión registrada pero cierre inválido

Forzar una validación del formulario después de registrar la revisión.

Esperado:

- la pantalla informa que la revisión ya fue registrada;
- no sugiere volver a crearla;
- permite corregir el formulario y guardar nuevamente;
- no se duplica la revisión activa.

### 8. Doble clic

Hacer doble clic rápido en Guardar/Editar.

Esperado: una sola escritura y un solo mensaje de éxito.

### 9. Baja vinculada a AppMobile

1. Seleccionar un reclamo de prueba con `idReintegroApp`.
2. Registrar ID local e ID externo.
3. Ejecutar la baja.
4. Verificar la baja local.
5. Verificar estado externo `AN`.
6. Revisar todas las marcas de sincronización.
7. Confirmar si hubo una o dos solicitudes externas.

Esperado: estado externo final `AN`. Una marca pendiente o un error HTTP implica
que el caso requiere reconciliación.

---

## Monitoreo posterior

Durante las primeras 24 horas buscar:

```bash
grep -R "RECLAMO_APP_SYNC_PENDING" <directorio-logs>
grep -R "Error al actualizar estado del reintegro" <directorio-logs>
grep -R "Excepción al actualizar estado del reintegro" <directorio-logs>
grep -R "Los datos del Reclamo Prestacional son inválidos" <directorio-logs>
```

Controlar también:

- reclamos cerrados con gestión `0`;
- reclamos rechazados cuyo estado no sea `3`;
- revisiones activas duplicadas;
- altas desde Compras con nomenclador vacío;
- bajas locales cuyo reintegro externo no esté en `AN`;
- repetición de solicitudes de anulación para el mismo ID.

---

## Despliegue

1. Compilar el proyecto completo en el entorno legacy.
2. Confirmar que el WAR contiene `view_reclamo_p0_patch.js`.
3. Registrar commit, checksum y WAR anterior.
4. Desplegar primero en QA o nodo controlado.
5. Invalidar caché del proxy/CDN si existe.
6. Ejecutar el smoke test completo.
7. Desplegar en producción durante una ventana controlada.
8. Monitorear datos y logs durante al menos 24 horas.

No mezclar este despliegue con cambios de esquema, nomenclador o Compras ajenos
al PR.

---

## Rollback

Este PR no incluye SQL.

1. Retirar el WAR nuevo.
2. Restaurar el WAR y checksum previos.
3. Reiniciar el contenedor según el procedimiento habitual.
4. Confirmar que `view_reclamo_p0_patch.js` ya no se sirve.
5. Invalidar caché si corresponde.
6. Auditar las operaciones realizadas durante la ventana.

El rollback de código no revierte datos. Revisar especialmente:

- revisiones y cierres ejecutados;
- bajas locales;
- estados externos `AN`;
- revisiones activas duplicadas;
- marcas de sincronización pendiente.

---

## Trabajo posterior obligatorio

1. Outbox persistente e idempotente para AppMobile.
2. Retirar la sincronización duplicada de la Action después de comprobar el
   comportamiento real de la baja.
3. Optimistic locking del agregado completo.
4. `draftId` para aislar pestañas y flujos de sesión.
5. Migrar mutaciones restantes de `renderURL/GET` a `POST`.
6. Eliminar físicamente el JavaScript inválido del include de edición.
7. Dejar de usar hidden y contadores como autoridad de negocio.
8. Pruebas E2E por sector y tipo de pedido.
