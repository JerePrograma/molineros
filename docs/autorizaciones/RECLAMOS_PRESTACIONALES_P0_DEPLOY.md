# Reclamos Prestacionales — despliegue P0 y reconciliación

## Objetivo

Este cambio estabiliza el flujo legacy de Reclamos Prestacionales sin volver al
JSP monolítico y sin mezclar una migración estructural de base de datos.

Cubre los fallos P0 que podían producir:

- pérdida de datos precargados al inicializar la pantalla;
- revisión rechazada sin estado `CERRADO` ni gestión `RECHAZADO` reales;
- guardado del reclamo antes de confirmar que la revisión fue registrada;
- degradación de la evaluación a `SINEVALUACION`;
- continuación del flujo después de una reconstrucción inválida;
- repetición de submit por doble clic;
- pérdida del `idReintegroApp` al borrar antes de sincronizar la aplicación
  móvil;
- ejecución de JavaScript viejo contra un JSP nuevo por caché del navegador.

## Alcance técnico

### Navegador

`view_reclamo_p0_patch.js` se carga después del JavaScript legacy y reemplaza
exclusivamente los puntos críticos:

- inicialización no destructiva por sector;
- restauración del snapshot de nomenclador/medicamento;
- validación estricta de fecha de revisión;
- registro de revisión con `POST` y comportamiento fail-closed;
- códigos canónicos `estado=3` y `tipoGestion=5` para el rechazo;
- guardado posterior al éxito de la revisión;
- prevención de doble submit;
- reinicialización del editor después del fragmento AJAX.

Los assets se sirven con versión `20260716-p0-2` para invalidar caché.

### Servidor

`ReclamosBaseAction`:

- interpreta de forma explícita `AUTORIZADO/AUTORIZADA` y
  `RECHAZADO/RECHAZADA`;
- elimina comparaciones de `String` por identidad;
- usa el valor visible de gestión como fallback del hidden legacy;
- valida fechas sin normalización permisiva;
- cancela la operación ante un reclamo incompleto o inválido;
- valida cuenta y seccional antes de construir el agregado.

`ReclamosPrestacionesServiceUtil.borrar`:

1. recupera el reclamo antes de borrarlo;
2. conserva `idReintegroApp`;
3. cancela la baja si no puede obtener el snapshot previo;
4. ejecuta la baja local;
5. solicita la anulación `AN` en AppMobile con el ID preservado.

## Limitación conocida: no existe transacción distribuida

PostgreSQL y AppMobile no participan de una misma transacción. Por lo tanto,
el cambio corrige el orden determinista de la operación, pero no garantiza que
una caída de red posterior a la baja local sea compensada automáticamente.

Cuando la sincronización externa no puede iniciarse se registra:

```text
RECLAMO_APP_SYNC_PENDING reclamo=<id> reintegroApp=<id> estado=AN motivo=<motivo>
```

Esta marca debe tratarse como una alerta operativa y reconciliarse. La solución
definitiva es una tabla outbox persistente con worker idempotente; no debe
improvisarse dentro de este hotfix.

## Validación previa al despliegue

### Contrato ejecutable

Desde la raíz del proyecto:

```bash
mkdir -p /tmp/rp-p0-contract
javac -d /tmp/rp-p0-contract \
  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java
java -cp /tmp/rp-p0-contract \
  ar.com.ospim.test.ReclamoPrestacionalP0ContractTest
```

Resultado esperado:

```text
CONTRATO_RECLAMO_PRESTACIONAL_P0_OK
```

### Compilación legacy

Ejecutar el procedimiento habitual completo del proyecto. No aceptar como
validación únicamente el contrato textual.

Verificar especialmente:

- compilación de `ReclamosBaseAction`;
- compilación de `ReclamosPrestacionesServiceUtil`;
- empaquetado del nuevo `view_reclamo_p0_patch.js`;
- inclusión de los scripts versionados en el WAR desplegado.

## Smoke test funcional obligatorio

Usar datos de prueba trazables. No probar inicialmente con un reclamo real que
posea orden de pago.

### 1. Alta normal

1. abrir un alta vacía;
2. seleccionar afiliado, sector y tipo de pedido;
3. agregar una prestación;
4. guardar;
5. recargar desde buscador;
6. comparar cabecera, prestación, montos, flags y evaluación.

Esperado: ningún dato cambia al recargar.

### 2. Precarga desde Compras

1. iniciar desde un requerimiento COTIZADO;
2. anotar sector, código, descripción, nomenclador, prestador y montos antes de
   que termine el render;
3. esperar la carga completa;
4. confirmar que esos valores no fueron limpiados;
5. completar fecha/comprobante requeridos;
6. guardar y volver a consultar.

Esperado: la prestación precargada no se duplica ni pierde referencia técnica.

### 3. Edición de prestación

1. abrir una prestación existente;
2. comprobar ID, código, descripción y valores;
3. cancelar y volver a abrir;
4. modificar y confirmar;
5. repetir con autorización y rechazo.

Esperado: el editor siempre corresponde a la fila elegida y no conserva datos
de otra prestación.

### 4. Revisión autorizada

1. cargar fecha válida y resolución autorizada;
2. registrar revisión;
3. comprobar que el fragmento responde antes de guardar;
4. volver a consultar el reclamo.

Esperado: una sola revisión activa y evaluación autorizada.

### 5. Revisión rechazada y cierre

1. cargar una revisión rechazada;
2. aceptar el cierre;
3. comprobar antes del submit:
   - `estado=3`;
   - `tipo_gestion_cierre_reclamo=5`;
   - `tipogestion=5`;
4. volver a consultar desde base/buscador.

Esperado: revisión persistida, cabecera cerrada y todas las reglas de rechazo
aplicadas en la misma operación funcional.

### 6. Fallo de revisión

Bloquear temporalmente el endpoint o forzar un error controlado.

Esperado:

- mensaje `El reclamo no fue guardado ni cerrado`;
- botón nuevamente utilizable;
- estado original conservado;
- ningún submit posterior.

### 7. Doble clic

Hacer doble clic en Guardar/Editar.

Esperado: una sola escritura y un solo mensaje de éxito.

### 8. Baja vinculada a AppMobile

1. elegir un reclamo de prueba con `idReintegroApp`;
2. registrar ambos IDs;
3. ejecutar baja;
4. verificar baja local;
5. verificar estado `AN` en AppMobile;
6. buscar en logs `RECLAMO_APP_SYNC_PENDING`.

Esperado: baja local y estado externo `AN`. Si existe la marca pendiente, el
caso no está sincronizado y debe reconciliarse manualmente.

## Consultas operativas posteriores

Durante las primeras 24 horas del despliegue buscar:

```bash
grep -R "RECLAMO_APP_SYNC_PENDING" <directorio-logs>
grep -R "Los datos del Reclamo Prestacional son inválidos" <directorio-logs>
grep -R "No se pudo registrar la revisión" <directorio-logs>
```

También controlar:

- cantidad de altas y actualizaciones contra el promedio habitual;
- reclamos cerrados con gestión `0`;
- reclamos rechazados cuyo estado no sea `3`;
- revisiones activas duplicadas;
- bajas locales con reintegro externo distinto de `AN`.

## Despliegue recomendado

1. generar backup de WAR desplegado y configuración;
2. registrar commit y checksum del artefacto;
3. desplegar en nodo de validación o ventana controlada;
4. invalidar caché/CDN si existe;
5. ejecutar smoke test completo;
6. habilitar tráfico normal;
7. monitorear logs y datos durante 24 horas.

No mezclar este despliegue con cambios de esquema, Compras o nomenclador que no
formen parte del mismo PR.

## Rollback

El rollback de este bloque no requiere SQL.

1. retirar el WAR nuevo;
2. restaurar el WAR previo;
3. reiniciar el contenedor según procedimiento habitual;
4. verificar que el asset `view_reclamo_p0_patch.js` ya no se sirva;
5. revisar operaciones efectuadas durante la ventana.

El rollback de código **no revierte datos ya persistidos**. Auditar:

- revisiones/cierres realizados;
- bajas de reclamos;
- estados externos `AN`;
- marcas `RECLAMO_APP_SYNC_PENDING`.

## Trabajo posterior obligatorio

Este hotfix no cierra la auditoría completa. Los siguientes bloques requieren
PRs separados:

1. outbox persistente e idempotente para AppMobile;
2. versión de agregado para concurrencia optimista real;
3. `draftId` para aislar pestañas y flujos de sesión;
4. mutaciones restantes de `renderURL/GET` a `POST`;
5. eliminación física del JavaScript inválido del include de edición;
6. retiro de hidden y contadores como autoridad de negocio;
7. pruebas E2E por sector y tipo de pedido.
