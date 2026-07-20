# Reclamos Prestacionales: botones de prestación y Recuperable SUR

## Síntomas

1. **Editar Prestación** no completaba el guardado cuando la prestación tenía importes del área médica y `Recuperable SUR` permanecía en `SELECCIONE`.
2. **Cancelar Edición de la Prestación** podía no hacer nada y dejar la pantalla en un estado parcial.
3. El flujo todavía permitía seleccionar y persistir valores históricos de Recuperable SUR e importe Reconocido SSS.

## Causas

### Cancelación

La función legacy separaba `tipoaccionprestacion` usando `-` y asumía que siempre existía un identificador de prestación. En edición común y en la precarga desde Compras, el hidden podía contener solamente `0`.

El código intentaba entonces modificar:

`comboestadosreclamoundefined`

Sin verificar que el elemento existiera. El resultado era una excepción JavaScript al asignar `selectedIndex`.

### Edición y alta

Las funciones legacy exigían elegir Recuperable cuando existían importes del área médica. Además, las validaciones de montos interpretaban cualquier valor distinto de `NO Recuperable` como obligación de informar `Reconocido SSS`.

Esa regla es incompatible con el nuevo requerimiento: Recuperable SUR debe permanecer neutral en `SELECCIONE`.

## Regla funcional consolidada

Para todas las prestaciones de Reclamo Prestacional:

- `Recuperable SUR = 0 / SELECCIONE`;
- el selector permanece deshabilitado;
- `Reconocido SSS = 0`;
- el campo Reconocido SSS permanece de sólo lectura;
- las validaciones legacy no impiden agregar o editar por esta regla retirada;
- el request de alta o edición siempre envía ambos valores en cero;
- el Action de edición vuelve a normalizar ambos valores, aunque el request sea manipulado.

La regla no modifica el checkbox general `chk_recuperable` del encabezado/revisión del reclamo. Ese indicador pertenece a otro nivel funcional y no debe confundirse con el Recuperable de cada prestación.

## Implementación

### Cliente

`view_reclamo_prestacion_rules_patch.js`:

- envuelve las funciones namespaced de agregar y editar;
- atraviesa de forma controlada las validaciones antiguas;
- normaliza los parámetros antes de ejecutar el `.load()`;
- reemplaza la cancelación por una versión segura;
- sólo resetea el combo de estado cuando el elemento existe;
- reaplica la regla después de cargas AJAX del editor y del listado.

Se mantiene el JavaScript legacy intacto para evitar una reescritura masiva de un archivo con codificación histórica.

### Servidor

`EditarPrestacionReclamoAction`:

- ignora `recuperableSur` y `reconocidoSSS` enviados por el cliente;
- publica la prestación en edición ya normalizada;
- guarda siempre `recuperable=0` y `reconocidoSSS=0`.

## Contrato automático

`ReclamoPrestacionalPrestacionRulesContractTest` verifica:

- orden de carga del patch;
- envoltura de Agregar y Editar;
- cancelación sin acceso a elementos nulos;
- parámetros neutralizados;
- selects y campos bloqueados;
- normalización del Action.

El workflow ejecuta también `node --check` sobre el nuevo patch.

## Smoke test obligatorio

1. Abrir un reclamo con prestaciones existentes.
2. Presionar el ícono **Editar Prestación**.
3. Confirmar que Recuperable muestra `SELECCIONE` y está deshabilitado.
4. Confirmar que Reconocido SSS muestra `0` y está readonly.
5. Modificar un dato válido y presionar **Editar Prestación**.
6. Confirmar actualización de la fila y cierre del editor sin errores de consola.
7. Volver a editar y presionar **Cancelar Edición de la Prestación**.
8. Confirmar retorno al formulario de carga y habilitación de los combos de estado.
9. Repetir desde una precarga iniciada en Compras.
10. Agregar una prestación nueva con importes del área médica sin seleccionar Recuperable.
11. Recargar el reclamo desde base y verificar Recuperable vacío y Reconocido SSS en cero.
12. Verificar edición, autorización y rechazo para asegurar que la regla neutral no altera esos modos.

## Despliegue

No requiere migración de base de datos. Los registros históricos no se actualizan masivamente; se normalizan cuando participan del flujo de edición y en las nuevas altas. Para limpiar históricos en bloque se necesita una migración separada, con definición explícita de alcance y auditoría.

## Rollback

Revertir el commit completo. No debe retirarse sólo la capa de cliente o sólo la normalización del Action, porque volvería a existir divergencia entre pantalla, sesión y persistencia.