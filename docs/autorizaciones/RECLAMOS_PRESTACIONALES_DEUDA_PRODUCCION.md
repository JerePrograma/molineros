# Reclamos Prestacionales — deuda técnica de producción

## Propósito

Este documento registra problemas todavía presentes en `main` que no deben
quedar ocultos por las reparaciones P0, la outbox o el guard de pestañas.

## P0-S1 — secretos AppMobile en código legacy

### Estado

Abierto.

`ClienteAppMobile.java` todavía declara credenciales históricas como literales.
El flujo reparado de baja y outbox ya utiliza `ReclamoAppMobileAuthClient`, pero
otros consumidores legacy pueden seguir accediendo a las declaraciones
antiguas.

### Riesgo

- exposición por acceso al repositorio o artefactos compilados;
- imposibilidad de rotación sin recompilar;
- reutilización accidental por código nuevo;
- persistencia del secreto en historial Git y caches.

### Criterios de aceptación

1. rotar API key, email técnico y contraseña en AppMobile;
2. reemplazar declaraciones literales por configuración externa;
3. buscar todas las referencias a `ClienteAppMobile.obtenerToken()`;
4. migrar cada consumidor al autenticador configurable;
5. eliminar los valores del archivo actual;
6. evaluar limpieza del historial Git con coordinación del equipo;
7. ejecutar:

   ```bash
   bash scripts/reclamos/validar_release_reclamos.sh
   ```

8. confirmar que el gate no requiere
   `ALLOW_LEGACY_APPMOBILE_SECRETS=1`.

## P0-S2 — segunda llamada AppMobile en Action legacy

### Estado

Mitigado, no eliminado.

La Action todavía relee el reclamo después de la baja e intenta actualizar
AppMobile nuevamente. `ReclamosPrestacionesServiceUtil` mantiene un registro
de bajas recientes durante 60 segundos y devuelve `null` ante esa lectura,
neutralizando la segunda llamada.

### Riesgo residual

- dependencia de una convención temporal en memoria;
- comportamiento distinto entre nodos si la Action y el servicio se separan;
- complejidad innecesaria para mantenimiento;
- posible reaparición si la Action cambia su orden.

### Criterios de aceptación

1. eliminar físicamente la relectura posterior a la baja;
2. eliminar la llamada directa a AppMobile de la Action;
3. conservar únicamente `ReclamosPrestacionesServiceUtil.borrar`;
4. verificar una sola transición externa por baja;
5. retirar `BAJAS_RECIENTES` y su TTL;
6. actualizar contratos y smoke tests.

## P0-S3 — aislamiento backend por borrador

### Estado

Infraestructura creada, migración pendiente.

Existen:

- `view_reclamo_tab_guard.js`;
- parámetro `reclamoDraftId` en formulario y AJAX;
- `ReclamoPrestacionalDraftScope` para claves namespaced.

Las Actions todavía utilizan claves globales de sesión para reclamo, listas,
prestación en edición, revisiones y contactos.

### Riesgo residual

El guard reduce ediciones simultáneas en un navegador, pero no impide todas las
colisiones causadas por:

- solicitudes concurrentes ya iniciadas;
- otros navegadores o dispositivos;
- endpoints que no respetan el draft;
- navegación atrás/adelante;
- sesiones compartidas en flujos legacy.

### Criterios de aceptación

Migrar, endpoint por endpoint:

1. `RECLAMO_PRESTACION_EN_EDICION`;
2. `LISTA_RECLAMO_PRESTACIONES`;
3. `LISTA_RECLAMO_REVISIONES`;
4. `LISTA_RECLAMO_CONTACTOS`;
5. `PRESTACION_RECLAMO_EN_PROCESO`;
6. cualquier clave equivalente encontrada durante la búsqueda;
7. lecturas JSP correspondientes;
8. alta, edición, autorización, rechazo, revisión y cierre.

Cada endpoint debe:

- resolver `reclamoDraftId` con `ReclamoPrestacionalDraftScope`;
- leer y escribir la misma clave namespaced;
- mantener fallback legacy sólo durante la transición;
- incluir contrato textual y prueba con dos borradores distintos.

## P1-S1 — JavaScript inválido en fragmento de edición

### Estado

Mitigado por interceptor AJAX.

El fragmento legacy todavía contiene bloques JavaScript inválidos. El
estabilizador los retira antes de insertarlos en el DOM.

### Criterios de aceptación

1. corregir físicamente el JSP;
2. mover toda inicialización a un único módulo JS;
3. retirar el saneamiento por texto;
4. probar edición desde todas las rutas;
5. conservar error visible ante fallo de carga.

## P1-S2 — ciclo de vida del scheduler

### Estado

Funcional con hilo daemon, integración formal pendiente.

El dispatcher se inicia desde el classloader al cargar el utilitario. El hilo es
daemon, pero no existe todavía un listener de `contextDestroyed` que invoque
`shutdownNow` durante redespliegues.

### Criterios de aceptación

1. integrar inicio/parada al ciclo de vida de Liferay/Tomcat;
2. asegurar una instancia por classloader;
3. detener executor al redesplegar;
4. verificar ausencia de classloader leaks;
5. conservar lease de base para despliegues multinodo.

## P1-S3 — optimistic locking del agregado

### Estado

Pendiente.

La comparación de estado no protege cambios concurrentes cuando el estado no
varía.

### Criterios de aceptación

1. agregar versión o timestamp confiable al agregado;
2. enviarlo en formulario;
3. comparar en la actualización;
4. rechazar una versión obsoleta con mensaje explícito;
5. no sobrescribir silenciosamente prestaciones, revisiones o cierre;
6. probar dos usuarios editando el mismo reclamo.

## P1-S4 — mutaciones mediante GET/renderURL

### Estado

Pendiente.

Persisten endpoints mutantes llamados mediante URLs de render y cargas AJAX.

### Criterios de aceptación

1. inventariar mutaciones;
2. migrarlas a ActionURL/POST;
3. mantener token CSRF del portal;
4. aplicar patrón Post/Redirect/Get cuando corresponda;
5. definir idempotencia;
6. eliminar parámetros concatenados manualmente.

## Regla de cierre

Ningún ítem debe marcarse resuelto sólo por existir una capa de compatibilidad.
La resolución exige eliminación del origen, contratos actualizados y prueba en
el entorno Liferay real.
