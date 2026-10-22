# Reemplazo puntual de prestaciones del requerimiento 1297

SQL: 20261021_reemplazar_prestaciones_1297.sql.
Entrega local para ejecutar en la base objetivo. Codex no lo ejecuto en una
base real ni modifico el requerimiento de produccion.

## Resultado autorizado

El usuario solicito sustituir las quince filas del codigo 070204 por las doce
prestaciones encontradas, usar la unica fila de cantidad 2 para cemento y
eliminar tres sobrantes. Confirmo clasificar las nueve prestaciones del
nomenclador tipo 10 como INSUMOS.

| ID prestacion | Codigo | Prestacion | Tipo nomenclador | Tipo cotizacion | Cantidad |
|---|---|---|---|---|---|
| 11482 | 9100046 | Valvula aortica N21 | 14 | PROTESIS CARDIOLOGIA (4) | 1 |
| 11483 | 9100047 | Tubo vascular recto | 14 | PROTESIS CARDIOLOGIA (4) | 1 |
| 11484 | 9100048 | Protesis vascular recta | 14 | PROTESIS CARDIOLOGIA (4) | 1 |
| 11485 | 9100049 | Parche vascular de teflon | 10 | INSUMOS (6) | 1 |
| 11486 | 9100050 | Cemento biologico | 10 | INSUMOS (6) | 2 |
| 11488 | 9100051 | Oxigenador de membrana | 10 | INSUMOS (6) | 1 |
| 11489 | 9100052 | Set de cardioplejia | 10 | INSUMOS (6) | 1 |
| 11490 | 9100053 | Solucion cardioplejica HTK | 10 | INSUMOS (6) | 1 |
| 11491 | 9100054 | Canula de cardioplejia | 10 | INSUMOS (6) | 1 |
| 11492 | 9100055 | Canula de drenaje ventricular | 10 | INSUMOS (6) | 1 |
| 11493 | 9100056 | Canula arterial femoral | 10 | INSUMOS (6) | 1 |
| 11494 | 9100057 | Canula venosa doble canasta | 10 | INSUMOS (6) | 1 |

Son doce renglones y trece unidades, mas los detalles ajenos al codigo 070204.
El gasto de envio 9999009 permanece intacto.
Las descripciones exactas se toman del catalogo activo; no se corrigen sus
textos "DRACON" ni "CARDIOPLEJIS" como efecto de esta operacion.

## Identidad e historial

No se modifica el estado 2 del requerimiento ni se desactivan triggers.
La regla vigente solo permite cambiar el tipo de cotizacion de una fila en
estado PENDIENTE. Para respetarla:

- Tres protesis se corrigen con la funcion existente y conservan su ID.
- Nueve insumos se crean como filas nuevas de rubro 6. Sus fuentes quedan
  de baja logica, con auditoria del operador SQL.
- Las tres filas sobrantes quedan de baja logica adicional.

En total hay doce bajas historicas: nueve por sustitucion y tres por exceso.
La grilla de mapeo distingue ambas causas y muestra ID anterior y nuevo.
Ninguna fila se elimina fisicamente. Se conservan observaciones individuales,
cantidades y valores nulos/cero originales. Si las filas cantidad 1 tienen
notas distintas que impidan asignarlas por orden de ID, el script se detiene.

La cabecera completa, su observacion general e interna, los otros detalles,
las ordenes medicas, los pedidos PDF y las notificaciones se comparan antes
y despues. No se generan pedidos nuevos ni se reenvia correo. Los PDFs ya
enviados conservan su contenido historico; no reflejan automaticamente la
correccion. Un nuevo aviso a proveedores requiere una accion posterior.

## Ejecucion

1. Usar una copia restaurada para el ensayo previo y disponer de respaldo.
2. Detener la aplicacion durante esta correccion puntual.
3. Abrir una conexion NUEVA a la base objetivo con el operador autorizado.
   Se audita current_user, no se inventa un usuario del portal.
4. Abrir y ejecutar TODO el SQL. Es ASCII, valido como ISO-8859-1 y UTF8.
5. Leer la ULTIMA grilla, emitida despues de COMMIT:

| resultado | Significado |
|---|---|
| REEMPLAZO_CONFIRMADO | Validaciones y cambios completos confirmados. |
| YA_ESTABA_CORREGIDO | Los doce destinos ya coinciden; no se escribio nuevamente. |
| ERROR_SIN_CAMBIOS | Se revirtieron los cambios de negocio del bloque; revisar etapa/SQLSTATE/mensaje. |

El mensaje generico de ejecucion correcta o exit code 0 del cliente NO
equivale a REEMPLAZO_CONFIRMADO: el script muestra los errores en la grilla.
No continuar eliminando validaciones si se obtiene ERROR_SIN_CAMBIOS.
Una desconexion, cancelacion/statement_timeout o fallo del COMMIT puede
impedir obtener resultado; comprobar desde otra conexion antes de asumir exito.
WHEN OTHERS no captura query_canceled. La ausencia de resultado no acredita
que se haya aplicado la correccion.

Guardar las grillas de resultado y mapeo. Luego recargar el portal y comprobar
el requerimiento 1297. El script tambien muestra el detalle activo completo
antes de la ultima grilla de resultado.

Consulta de control desde otra conexion:

    SELECT d.id_tipo_prestacion, t.descripcion AS tipo_cotizacion,
           count(*) AS renglones, sum(d.cantidad) AS unidades
    FROM compras.requerimiento_detalle d
    JOIN compras.tipo_prestacion t
      ON t.id_tipo_prestacion = d.id_tipo_prestacion
    WHERE d.id_requerimiento = 1297
      AND d.baja_fecha IS NULL
      AND d.id_prestacion IN (
          11482,11483,11484,11485,11486,11488,
          11489,11490,11491,11492,11493,11494
      )
    GROUP BY d.id_tipo_prestacion, t.descripcion
    ORDER BY d.id_tipo_prestacion;

Esperado: rubro 4 con tres renglones/tres unidades; rubro 6 con nueve
renglones/diez unidades. Cemento 11486 debe estar activo una sola vez con
cantidad 2, y ninguna fila activa debe conservar el codigo 070204.

## Controles y alcance

El script exige quince filas activas originales, catorce cantidad 1 y una
cantidad 2, todas NOMENCLADOR/rubro 4, sin importes distintos de cero ni
prestador adjudicado. Verifica pares ID/codigo, tipos, marca 3 y actividad del
catalogo, y valida su configuracion efectiva para los rubros 4 y 6.
No amplia la configuracion global ni reclasifica otras filas.

Rechaza un RP o su reserva, cotizaciones activas tipo 1/3, comparativas,
notificaciones PROCESANDO, destinos parciales/duplicados, referencias externas
a IDs de detalle y el trigger general legacy incompatible con esta operacion.
Las ordenes medicas tipo 2 y notificaciones ya enviadas son datos protegidos,
no motivos para borrar historial.

Usa bloqueo advisory del RP, cabecera FOR UPDATE y detalles ordenados.
Las constraints diferidas se fuerzan dentro del bloque protegido antes de
registrar exito. Cualquier fallo de datos revierte la operacion completa.
Las secuencias SERIAL pueden avanzar incluso con rollback; puede haber
huecos de IDs sin cambios de negocio.

## Referencias y trazabilidad

Referencia de Liquidaciones:
ext-impl/src/ar/com/ospim/liquidaciones/services/EditarLiquidacionServiceImpl.java:482,
actualizaLiquidacionPrestacionEntry: correccion por ID y auditoria.
Compras reutiliza corregir_item_requerimiento y
borrar_requerimiento_detalle; el INSERT se adapta del canonico en
20261013_sector_nomenclador.sql:606-669. La forma de resultado/rollback
proviene del instalador pgAdmin y de la reparacion de guardado del 21/10.

Repositorio: https://github.com/JerePrograma/molineros.git.
Ruta: C:/wsmolineros/ext. Rama: main.
HEAD inicial: e2bd08c3292ed898382fc8d90c5e4c946da6979b.
Dos archivos nuevos: este documento y su SQL homonimo. No se editaron clases,
JSP, funciones instaladas, triggers, instaladores generales ni otros archivos.
Ambos se escriben explicitamente ISO-8859-1 sin BOM; son ASCII.

La evidencia de pruebas queda en tmp/corregir-1297-20261021, ignorado por Git.
PostgreSQL local de ensayo: 17.5, puerto 54395, datos sinteticos.
No hay binarios 9.6 completos disponibles; ensayo real en PostgreSQL 9.6
PENDIENTE. PRUEBA UI MANUAL PENDIENTE.
No corresponde build Java/JSP para esta entrega exclusivamente SQL.
No se accedio a produccion ni se hizo commit, push o despliegue.

## Validacion final del 21/10/2026

Inicio registrado: 17:06:01 -03. HEAD inicial/final y origin/main:
e2bd08c3292ed898382fc8d90c5e4c946da6979b.
Cluster propio de ensayo detenido correctamente; puerto 54395 libre.
No se encontraron cambios locales ajenos al iniciar ni al terminar.
La entrega queda sin commit ni push: los dos archivos son nuevos.

Veintitres grupos aprobados con PostgreSQL17.5 y funciones/triggers reales:

- Tres pruebas de los controles existentes: bloqueo de cambio de rubro en
  estado2, rechazo de tipo10/rubro4 y admision de INSERT tipo10/rubro6.
- Reemplazo exacto: tres IDs conservados, nueve nuevos, doce bajas logicas,
  doce destinos con trece unidades y gasto de envio intacto. Notas individuales,
  cabeceras de dos requerimientos, otra fila ajena, orden medica, nueve
  notificaciones y nueve PDFs permanecieron iguales.
- Reejecucion YA_ESTABA_CORREGIDO con datos y secuencia identicos.
- Dieciseis rechazos sin cambios de negocio: estado3, catorce fuentes,
  dos cantidades2, catalogo incorrecto/inactivo, configuracion faltante,
  RP, comparativa, cotizaciones1/3, envio PROCESANDO, notas distintas,
  trigger incompatible, fallo intermedio, final duplicado/faltante y
  fallo diferido antes de confirmar.
- Dos pruebas de notas equivalentes: espacios exteriores y NULL/vacio;
  las notas se conservaron literalmente en sus nuevos renglones.

La revision independiente detecto un conteo sin DISTINCT que podia aceptar
un destino duplicado y otro faltante como reejecucion. Se corrigio en ambos
controles y la matriz final se ejecuto contra el SQL corregido.
SHA256 del SQL probado:
1695ec3316b8f8bcf4855c8555c73392a3b63121ef325bf9e4f859bb2ae1b44b.

Resultados/comandos y exit codes:

- Baseline Git: validacion de ruta/remoto, status, rama, HEAD, log, fetch,
  switch main y merge --ff-only: 0.
- Initdb/pg_ctl y creacion de fixtures sinteticos: 0. El primer fixture
  tenia un documento mal clasificado y fue corregido solo en el arnes.
- Probes de triggers: rechazos psql3 ESPERADOS, aceptacion psql0;
  verificaciones del arnes0.
- Matriz final: arnes0; clientes psql0 en todos los casos. Los negativos
  verificaron ERROR_SIN_CAMBIOS y snapshot identico de todas las filas
  de negocio/config/catalogo. El exit0 del cliente por si solo no es exito.
- pg_ctl stop del cluster propio: 0; verificacion de puerto libre: 0.
- Verificacion de bytes, SHA del SQL probado, BOM, mojibake y CRLF: 0.
- git diff --check/status/stat/diff: 0 sobre rutas seguidas. Por tratarse
  de dos archivos nuevos, tambien se leyeron completos y se guardaron sus
  diffs contra NUL; exit1 esperado por diferencias, sin errores de espacios.
  Git avisa que normalizara CRLF a LF si luego se agregan los archivos.
- Un primer control auxiliar trato exit1 del diff nuevo como fallo;
  se reviso la salida y se ajusto el control al contrato de --no-index.
- Una primera llamada de creacion de la guia fallo por delimitacion del
  script (sin escribir archivos); la llamada corregida termino0.
- Lecturas exploratorias: 0 o busquedas sin coincidencias1. Dos nombres
  opcionales de logs no existian; se usaron los logs final_* reales.

No quedan pruebas funcionales fallidas en el fixture final. Lo reproducido
antes del cambio es la forma historica incorrecta y las restricciones de rubro.
Se verificaron ambos textos como ISO-8859-1 sin BOM; todos sus bytes son ASCII,
sin U+00C3/U+00C2/U+FFFD y con saltos CRLF. El diff no recodifica fuentes.

La matriz y las comparaciones no sustituyen el ensayo en una copia de la base
real9.6 ni el smoke test del portal. Riesgos residuales: contratos, permisos,
triggers o datos distintos del fixture. El preflight se detiene cuando no
coinciden sus condiciones; no autoriza desactivar controles para continuar.
