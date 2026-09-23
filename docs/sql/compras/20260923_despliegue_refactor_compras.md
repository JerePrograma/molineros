# Compras - Despliegue preservador en dos fases

Esta guia reemplaza la estrategia de despliegue del incremental monolitico
`20260922_refactor_normalizacion_compras.sql`. Ese archivo queda inhabilitado con
un error explicito: no aplica ni redirige automaticamente ninguna fase.

La implementacion Java/JSP existente se conserva. La separacion afecta SQL,
documentacion de despliegue y pruebas SQL. No modifica maestros externos,
Document Library, correo ni configuracion global.

## A. Fase 1: expand y compatibilidad

Archivo: `docs/sql/compras/20260923_refactor_compras_fase1_expand.sql`.

Parte de Compras existente; no incluye ni ejecuta el canonico. Usa una transaccion
BEGIN/COMMIT. Ante cualquier error debe detenerse el cliente con ON_ERROR_STOP;
la transaccion abortada se revierte al cerrar la conexion. No continuar ni intentar
confirmar parcialmente un archivo fallido.

Orden de ejecucion:

1. Comprobar version, columnas originales, ausencia de CHECKs no relevados y los
   dos triggers esperados. Registrar DB/version/transaccion sin credenciales.
2. Bloquear escrituras en las seis tablas transaccionales; comprobar huerfanos y
   capturar contadores, extremos de cada componente de PK y huellas de contenido.
3. Diagnosticar discrepancias de recupero sin modificar las filas ni los cargos.
4. Crear las dos tablas de configuracion si faltan, ampliar las dos existentes y
   sembrar estados, capacidades, rubros y la relacion N:M.
5. Comprobar referencias, agregar FKs internas con NOT VALID donde falten y
   validarlas. Extender el tipo compuesto de salida y adaptar funciones.
6. Conservar propietario/EXECUTE de retornos recreados. Los objetos nuevos
   heredan propietario y permisos de sus equivalentes legacy de Compras.
7. Verificar catalogos, referencias y conservacion exacta de estadisticas y
   estructura original de las seis tablas. Confirmar solo si todo paso.

NOT VALID separa la instalacion de una FK de su validacion. Esta migracion no se
presenta como online: las comprobaciones pre/post necesitan una ventana sin
escrituras de Compras. Los bloqueos se mantienen hasta COMMIT/ROLLBACK. El
lock_timeout es 5 segundos y statement_timeout es 120 segundos; medir el ensayo
con el volumen de produccion antes de fijar la ventana. Un timeout aborta.

## B. Datos conservados

No hay INSERT, UPDATE, DELETE ni TRUNCATE ejecutados sobre estas tablas durante
la migracion. Los INSERT/UPDATE contenidos en definiciones de funciones solo se
ejecutan cuando posteriormente los invoca la aplicacion.

| Tabla | Controles adicionales al total y huellas |
|---|---|
| requerimiento | PK/id minimo y maximo; activos/baja |
| requerimiento_detalle | PK/id minimo y maximo; detalles activos/baja |
| requerimiento_presupuesto | PK/id minimo y maximo; activos/baja; tipos de documento; archivos DL |
| requerimiento_cotizacion_prestador | Ambos componentes de PK; relaciones, intentos y emails reservados |
| requerimiento_pedido_cotizacion | Tres componentes de PK; documentos por intento y archivos DL |
| requerimiento_reclamo_prestacional | PK/id minimo y maximo; RP vinculados y tokens reservados |

Tambien se comparan las PKs, columnas/defaults, indices y triggers de usuario
originales. Dos sumas de mitades del MD5 de cada fila permiten detectar cambios
de contenido sin copiar tablas ni concatenar toda la informacion en memoria.
Son una comprobacion adicional; la garantia de no modificar historia se fundamenta
en que el script no ejecuta DML sobre esas seis tablas. Solo quedan estadisticas
temporales en variables de sesion, que terminan con la transaccion.

Permanecen fisicamente las 18 columnas redundantes y sus valores originales:

| Tabla | Columnas que FASE 1 conserva y FASE 2 podra retirar |
|---|---|
| requerimiento (13) | afiliado_id_ospim, afiliado_nombre, afiliado_apellido, afiliado_documento_tipo, afiliado_documento_nro, afiliado_direccion, afiliado_localidad, afiliado_provincia, afiliado_celular, afiliado_telefono, afiliado_email, cargo_ospim, recupero |
| requerimiento_detalle (3) | id_tipo_nomenclador, codigo_nomenclador, descripcion_nomenclador |
| requerimiento_presupuesto (2) | descripcion_prestador, descripcion_empresa |

El nuevo runtime conserva aliases/getters y lee maestros actuales. Las columnas
viejas no se sincronizan como una segunda fuente de verdad. No usar sus valores
para informes nuevos ni asumir que permiten reinstalar Java viejo sin evaluar las
escrituras posteriores al despliegue.

RECUPERO se deriva de cargo_tercerizadora > 0; CARGO OSPIM de 100 menos ese cargo.
SURGE permanece fisico e independiente. La discrepancia informada del requerimiento
1186 solo se diagnostica: no se corrige el cargo ni el booleano historico.
precio_total_estimado permanece fisico y editable; no se recalcula historicamente.

## C. DDL y DML de fase 1

- CREATE TABLE IF NOT EXISTS para estado_requerimiento y
  tipo_prestacion_tipo_nomenclador. No se recrea ninguna tabla existente.
- ALTER TABLE sector_requerimiento: siete atributos de configuracion (tipo_item,
  seleccionable_alta, permite_cotizacion_empresa, permite_orden_compra_directa,
  busqueda_nomenclador_medica, permite_medicamento_legacy y sector_reclamo_prestacional).
- ALTER TABLE tipo_prestacion: rubro_prestador. Se establecen NOT NULL de los
  atributos obligatorios de configuracion; ninguna columna historica se elimina.
- INSERT de seis estados y 23 pares N:M con ON CONFLICT DO NOTHING. UPDATE
  limitado a campos nuevos de configuracion que aun estan sin inicializar.
- Cinco FKs internas comprobadas y validadas: requerimiento->sector,
  requerimiento->estado, detalle->requerimiento, detalle->tipo_prestacion y
  relacion N:M->tipo_prestacion. Se reconoce una FK equivalente aunque tenga
  otro nombre; no se crean FKs cruzadas a maestros.
- ALTER TYPE requerimiento_base_row agrega diez atributos logicos sin borrar
  los anteriores.
- 50 definiciones: 47 firmas existentes adaptadas y tres consultas auxiliares
  nuevas. Se conserva CREATE OR REPLACE cuando no cambia el retorno.
- ALTER OWNER, GRANT/REVOKE y COMMENT limitados a los objetos de Compras cuya
  creacion/recreacion requiere conservar permisos y comentario.

La unica excepcion dirigida de DROP en FASE 1 es el cambio de retorno de estas
nueve firmas. PostgreSQL 9.6 no admite ese cambio mediante CREATE OR REPLACE.
Cada DROP es RESTRICT, ocurre solamente si el retorno difiere y va seguido de
recreacion inmediata dentro de la misma transaccion. No existe CASCADE.

```text
get_documento_requerimiento(integer, integer, integer)
get_estado_requerimiento(integer)
get_sector_requerimiento(integer)
listar_documentos_requerimiento(integer, integer)
listar_estados_requerimiento()
listar_ordenes_medicas_requerimiento(integer)
listar_presupuestos_prestador(integer, integer)
listar_sectores_requerimiento()
listar_tipos_prestacion()
```

Una segunda ejecucion reconoce los retornos ya adaptados y conserva sus OIDs.
Si aparece SECURITY DEFINER, configuracion especial o una dependencia que impide
el retiro dirigido, aborta para revisar ese contrato concreto.

## D. Fase 2: limpieza posterior

Archivo: `docs/sql/compras/20260923_refactor_compras_fase2_cleanup.sql`.
**NO EJECUTAR AHORA. No forma parte del runner de ensayo.**

Solo se habilita despues de fase 1 aplicada, codigo desplegado, smoke UI, PDF,
Excel, mail, Document Library, RP y validacion QA. La variable de sesion que
habilita el archivo es una declaracion del operador, no prueba automatica de QA.
Sin esa declaracion el primer bloque aborta antes de cualquier eliminacion.

Objetos previstos para retirar:

- Las 18 columnas enumeradas arriba.
- ix_compras_requerimiento_afiliado_id_ospim.
- trg_compras_requerimiento_completar_baja, una vez comprobados su cuerpo y los
  caminos atomicos anular_requerimiento/cambiar_estado_requerimiento.
- validar_requerimiento_fila(), validar_requerimiento_detalle_fila(),
  validar_tipo_prestacion_detalle_fila(), validar_tipo_prestacion_detalle_nuevo(),
  validar_prestador_cotizacion_fila(), completar_baja_requerimiento_fila(),
  normalizar_rubro(character varying), estado_requerimiento_descripcion(integer).

Antes de cada funcion se verifican triggers, callers y dependencias. Antes del
indice se verifica su tabla, columna y que no respalde una constraint. Antes de
cada columna se verifican consumidores no revisados y dependencias catalogadas,
incluyendo objetos que DROP COLUMN retiraria automaticamente. Se permiten solo
los defaults propios de las columnas eliminadas. Todo retiro usa RESTRICT.

Se verifican las huellas de los contratos conocidos; las 50 funciones de fase 1
son obligatorias. Los contratos adicionales conocidos solo se comprueban si
existen, sin confundir las 83 funciones de desarrollo con las 81 informadas en
produccion. Las llamadas externas o SQL construido dinamicamente requieren
revision operativa: pg_depend no puede demostrar por si solo su ausencia.

Se conserva trg_compras_detalle_calcular_total, SURGE, precio_total_estimado,
identidades de maestros, documentos y todos los datos operacionales.

## E. Precondiciones para produccion

1. Ensayar sobre una copia representativa de la DB productiva 9.6.5 y conservar
   la salida completa de psql. El ensayo local 9.6.15 no reemplaza esa prueba.
2. Verificar el destino de conexion y usar un rol con permisos para los ALTER y
   para conservar los propietarios/grants existentes. No registrar credenciales.
3. Coordinar la ventana sin escrituras y el despliegue del Java ya implementado.
   Confirmar backup/restauracion operativos antes de la aplicacion definitiva.
4. Resolver mediante revision, sin borrado automatico, cualquier precondicion
   fallida: PK, columnas, CHECKs inesperados, triggers, huerfanos, maestro faltante,
   identidad afiliado ambigua, cargos fuera de rango o clasificacion incompatible.
5. Confirmar que la funcion externa de busqueda medica ya existe. No se instala
   ni modifica como parte de Compras.
6. Revisar los avisos PRE/POST y CONSERVACION OK. Ante error, detener la ejecucion
   y efectuar ROLLBACK; no saltar controles ni ejecutar partes sueltas.
7. Mantener cleanup cerrado hasta completar las regresiones reales de salida.

## F. Comandos de ensayo con ROLLBACK

Ensayo local reproducible, sin secuencias consumidas ni envios externos:

```powershell
Set-Location -LiteralPath 'C:\wsmolineros\ext' -ErrorAction Stop
python -u docs/sql/compras/pruebas_refactor/probar_migracion_rollback.py
if ($LASTEXITCODE -ne 0) { throw 'Fallo el ensayo; revisar el resultado y ROLLBACK.' }
```

El runner solo acepta devmolineros, obtiene credenciales de la configuracion
desplegada de Tomcat sin imprimirlas, interrumpe ante el primer error y siempre
revierte en finally. Verifica despues del rollback el contenido, secuencias,
permisos, objetos externos y numero original de tablas. Requiere psycopg2/pglast
solo en el entorno de pruebas, sin dependencias nuevas de la aplicacion.

Para ensayar con psql sobre una copia productiva, configurar previamente PGHOST,
PGPORT, PGDATABASE y PGUSER para ese destino y usar el mecanismo habitual de
autenticacion. Este bloque no incluye ni persiste contrasenas:

```powershell
$ErrorActionPreference = 'Stop'
if (-not $env:PGHOST -or -not $env:PGDATABASE -or -not $env:PGUSER) {
    throw 'Configurar primero el destino de ensayo PGHOST/PGDATABASE/PGUSER.'
}
$comprasOrigen = 'C:\wsmolineros\ext\docs\sql\compras\20260923_refactor_compras_fase1_expand.sql'
$comprasEnsayo = Join-Path $env:TEMP '20260923_compras_fase1_ensayo_rollback.sql'
$comprasEncoding = [System.Text.Encoding]::GetEncoding('ISO-8859-1')
$comprasSql = [System.IO.File]::ReadAllText($comprasOrigen, $comprasEncoding)
if ([regex]::Matches($comprasSql, '(?m)^COMMIT;\r?$').Count -ne 1 -or
    $comprasSql.TrimEnd() -notmatch 'COMMIT;$') {
    throw 'El archivo no tiene el unico COMMIT final esperado.'
}
$comprasSql = [regex]::Replace($comprasSql, '(?m)^COMMIT;\r?$', 'ROLLBACK;')
[System.IO.File]::WriteAllText($comprasEnsayo, $comprasSql, $comprasEncoding)
$env:PGCLIENTENCODING = 'LATIN1'
psql -X -v ON_ERROR_STOP=1 -f $comprasEnsayo
if ($LASTEXITCODE -ne 0) { throw 'Ensayo abortado; psql cerro la transaccion sin COMMIT.' }
```

En el ensayo se conserva toda FASE 1 y solo cambia su cierre por ROLLBACK. No
usar --single-transaction para envolver un archivo que ya maneja su transaccion.
No ejecutar fase 2 ni compras_schema.sql para preparar ese ensayo.

## G. Diferencias con el incremental anterior

El archivo anterior retiraba indice, 18 columnas, trigger de baja y ocho funciones
en el mismo despliegue. Esas operaciones pasan exclusivamente a fase 2. Fase 1
agrega auditoria de conservacion, valida referencias antes de FKs y solo recrea
contratos cuando cambia efectivamente su retorno. Ambos triggers originales
permanecen instalados durante compatibilidad.

compras_schema.sql representa exclusivamente una instalacion nueva: CREATE SCHEMA
compras aborta si ya existe. Se retiro del archivo la definicion externa
autorizaciones.busca_nomenclador_prest_med_compras y el INSERT post-COMMIT de
public.system_config. No se ejecutaron esos objetos ni se cambio configuracion
de mail. El canonico nunca es la via de actualizacion de produccion.

## H. Validacion y Git

Los resultados de esta correccion se registran en
`pruebas_refactor/20260923_resultado_despliegue.txt`. Los controles estaticos no
equivalen a UI, PDF renderizado, Excel, SMTP, DL o RP end-to-end. Fase 2 solo se
analiza estaticamente; no se ejecuta, ni siquiera dentro de ROLLBACK.

Baseline: main, HEAD y origin/main
308990e77a909ff49a88f47b33d0b36db2f7519d. Se preservan los cambios locales de la
iteracion anterior. No hay commit ni push. Java/JSP se verifican por SHA-256
contra el inicio de esta correccion. No se recompila Java al no cambiarlo.

Los archivos editados se escriben explicitamente en ISO-8859-1 sin BOM. Las
evidencias crudas anteriores no se modifican.
