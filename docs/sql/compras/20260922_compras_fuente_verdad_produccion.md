# Compras - Fuente de verdad SQL de produccion

## Actualizacion funcional confirmada para la implementacion

Esta seccion reemplaza las inferencias anteriores de derivabilidad. No modifica
los resultados crudos del relevamiento SQL ni afirma que la migracion este aplicada.

- **Hecho DB:** el relevamiento de produccion informo PostgreSQL 9.6.5, ocho tablas,
  81 funciones, cuatro secuencias, 15 constraints, cero CHECK y dos triggers activos.
- **Regla funcional confirmada:** SURGE es el booleano independiente; RECUPERO es
  `cargo_tercerizadora > 0`; CARGO OSPIM es `100 - cargo_tercerizadora`.
- **Hecho DB suministrado:** el requerimiento 1186 con cargo tercerizadora 100 y
  recupero false es una discrepancia, no una excepcion funcional. Los cargos son
  la fuente de verdad; nunca se corrigen a partir del booleano redundante.
- **Regla funcional confirmada:** precio_total_estimado admite edicion explicita
  y puede diferir de cantidad por precio unitario. Se conserva fisicamente.
- **Decision de refactor:** mantener las 18 columnas fisicas durante FASE 1;
  retirarlas solo en FASE 2 despues de despliegue, regresiones y QA. Las lecturas
  usan aliases logicos y maestros actuales desde FASE 1.
- **Decision de refactor:** estados en tabla, relacion N:M entre tipo de prestacion
  y nomenclador, capacidades de sectores y rubro de prestador configurados.
- **Entorno de pruebas distinto:** la conexion local consultada durante esta
  implementacion respondio PostgreSQL 9.6.15 y 83 funciones. No reemplaza la foto
  de produccion anterior. Sus pruebas y limitaciones se documentan por separado.

Despliegue corregido el 2026-09-23: `20260923_refactor_compras_fase1_expand.sql`
conserva datos y columnas; `20260923_refactor_compras_fase2_cleanup.sql` queda
diferida hasta la validacion completa. El incremental monolitico anterior esta
inhabilitado. La fase 1 diagnostica recupero sin actualizar historia y verifica
contadores pre/post. Ver `20260923_despliegue_refactor_compras.md`. El canonico
solo sirve para una instalacion nueva y nunca actualiza una DB existente.

## 1. Objetivo

Este documento acompana a:

`docs/sql/compras/20260922_compras_fuente_verdad_produccion.sql`

Su objetivo es fijar una regla simple para cualquier analisis o refactor posterior del modulo `/compras`:

> La base instalada es la autoridad sobre el estado SQL real. El repositorio explica intencion e historia, pero no puede contradecir al catalogo real de PostgreSQL.

El `.sql` asociado es **solo lectura**. No instala, no migra y no corrige nada. Su funcion es volver a consultar el catalogo y los datos tecnicos minimos necesarios para saber que existe realmente en el ambiente objetivo.

Esto evita repetir el problema detectado en el analisis anterior: habia reglas y objetos en `compras_schema.sql` que no representan exactamente lo instalado hoy.

---

## 2. Orden obligatorio de autoridad

Para SQL de Compras, usar siempre este orden:

1. **Salida actual de `20260922_compras_fuente_verdad_produccion.sql` ejecutada contra la base objetivo.**
2. **Hechos del snapshot del 22/09/2026 documentados aqui.**
3. Scripts SQL incrementales que se sepa fueron ejecutados.
4. `ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql`.
5. Otros SQL historicos del repositorio.

Si hay una contradiccion, **gana la base instalada**.

No usar `compras_schema.sql` como prueba de que un CHECK, trigger, FK o funcion esta activo en produccion.

---

## 3. Ambiente relevado

Snapshot recibido el **22/09/2026 11:21:10 -03**.

- Base: `devmolineros`
- Usuario: `postgres`
- PostgreSQL: `9.6.5`
- Server encoding: `UTF8`
- Client encoding reportado: `UNICODE`
- TimeZone: `localtime`
- Search path: `"$user", public`
- Schema: `compras`
- Owner: `postgres`

### Compatibilidad obligatoria

Cualquier propuesta SQL debe ser valida para **PostgreSQL 9.6.5**.

No introducir sintaxis o funcionalidades que dependan de versiones modernas de PostgreSQL sin una necesidad real y sin comprobar compatibilidad.

---

## 4. Foto estructural comprobada

La ejecucion relevada mostro:

| Objeto | Cantidad |
|---|---:|
| Tablas | 8 |
| Funciones del schema `compras` | 81 |
| Secuencias | 4 |
| Constraints | 15 |
| CHECK constraints | **0** |
| Triggers de usuario | **2** |
| Views | 0 |
| Materialized views | 0 |

### Tablas

1. `compras.requerimiento`
2. `compras.requerimiento_cotizacion_prestador`
3. `compras.requerimiento_detalle`
4. `compras.requerimiento_pedido_cotizacion`
5. `compras.requerimiento_presupuesto`
6. `compras.requerimiento_reclamo_prestacional`
7. `compras.sector_requerimiento`
8. `compras.tipo_prestacion`

Tambien existe el tipo compuesto:

`compras.requerimiento_base_row`

---

## 5. Correcciones importantes respecto de cualquier analisis previo

### 5.1 Hoy no hay CHECK constraints en Compras

La consulta real de `pg_constraint` no devolvio ningun `CHECK` para el schema `compras`.

Por lo tanto, afirmaciones del tipo:

- `recupero` esta garantizado por un CHECK;
- `cargo_ospim + cargo_tercerizadora = 100` esta garantizado por un CHECK;
- `precio_total_estimado` esta garantizado por un CHECK;

**no son validas para la base relevada**.

Puede haber codigo o funciones que intenten mantener esas reglas, pero no existe hoy un CHECK instalado que las haga invariantes de DB.

### 5.2 Solo hay dos triggers de usuario instalados

Los triggers de usuario efectivamente relevados son:

- `trg_compras_requerimiento_completar_baja`
  - tabla: `compras.requerimiento`
  - funcion: `compras.completar_baja_requerimiento_fila()`

- `trg_compras_detalle_calcular_total`
  - tabla: `compras.requerimiento_detalle`
  - funcion: `compras.calcular_total_detalle_fila()`

Existen funciones `RETURNS trigger` como:

- `validar_requerimiento_fila()`
- `validar_requerimiento_detalle_fila()`
- `validar_tipo_prestacion_detalle_fila()`
- `validar_tipo_prestacion_detalle_nuevo()`
- `validar_prestador_cotizacion_fila()`

pero **no aparecieron asociadas a triggers de usuario en el catalogo relevado**.

Consecuencia:

> La existencia del cuerpo de una funcion `validar_*` no demuestra que esa validacion se ejecute hoy automaticamente.

El nuevo SQL tiene una seccion especifica para detectar funciones `RETURNS trigger` huerfanas.

### 5.3 No asumir FKs que no aparecen en el catalogo

Entre las constraints reales relevadas no aparecieron, entre otras:

- FK de `compras.requerimiento.id_sector` hacia `compras.sector_requerimiento`.
- FK de `compras.requerimiento_detalle.id_requerimiento` hacia `compras.requerimiento`.
- FKs cruzadas hacia afiliado, prestador o nomenclador.

Esto **no significa que haya que agregarlas**.

Solo significa que un analisis no puede afirmar que PostgreSQL garantiza esas relaciones actualmente.

El refactor pedido es de normalizacion y simplificacion legacy-first, no una campana para agregar constraints.

---

## 6. Datos reales relevantes para la normalizacion

### 6.1 Requerimiento

Se observaron **39 requerimientos** en las consultas de consistencia.

Afiliado:

- 35 con CUIL titular.
- 35 con integrante.
- 35 con clave completa `CUIL + integrante`.
- 4 sin clave completa.
- 35 con `afiliado_id_ospim` copiado.
- 35 con nombre copiado.
- 35 con apellido copiado.
- 35 con tipo y numero de documento copiados.
- 35 con direccion, localidad y provincia copiadas.
- 30 con celular copiado.
- 16 con telefono copiado.
- 13 con email copiado.

Esto confirma que el snapshot de afiliado no es marginal: para los requerimientos con afiliado se persiste sistematicamente una cantidad importante de informacion maestra duplicada.

### 6.2 Regla funcional definida por QA

Para el nuevo refactor se adopta una condicion funcional distinta de la investigacion anterior:

> Para informacion maestra se quiere el dato actual, no conservar una fotografia historica por comodidad.

Por lo tanto, la perdida del snapshot historico **no es por si sola un motivo para conservar** nombre, apellido, documento, domicilio, contacto, razon social, descripcion de nomenclador u otros atributos maestros.

La pregunta correcta pasa a ser:

> ¿Existe una identidad estable que permita obtener el dato actual de su maestro de manera simple, legacy y segura?

Datos de evento, auditoria, transaccion o documento siguen siendo otra categoria y no deben borrarse por confundirlos con datos maestros.

---

## 7. Redundancias matematicas verificadas contra datos reales

### `recupero`

Resultado:

- 39 filas comparadas.
- **1 inconsistencia** entre `recupero` y `(cargo_tercerizadora > 0)`.

Regla funcional corregida y confirmada: `recupero = (cargo_tercerizadora > 0)`.
La discrepancia no habilita una marca independiente. La lectura derivada elimina
el drift sin alterar los porcentajes. SURGE es el booleano independiente.

### `cargo_ospim`

Resultado:

- 39 filas comparadas.
- 0 inconsistencias para `cargo_ospim + cargo_tercerizadora = 100`.

Regla confirmada: se deriva como `100 - cargo_tercerizadora`; se valida rango y suma en Java.

### `precio_total_estimado`

Resultado:

- 73 detalles totales observados.
- 15 filas comparables con cantidad, unitario y total informados.
- 0 inconsistencias.

Actualmente existe ademas el trigger real `trg_compras_detalle_calcular_total`.

Regla confirmada: el total explicitamente informado puede ser diferente del producto. Se conserva la columna y el trigger de calculo; una actualizacion sin cambio de cantidad/unitario no debe sobrescribir el total manual.

---

## 8. Estado real de los detalles

Distribucion observada:

| `tipo_item` | `id_tipo_prestacion` | `id_tipo_nomenclador` | Cantidad |
|---|---:|---:|---:|
| NOMENCLADOR | 1 | 9 | 6 |
| NOMENCLADOR | 2 | 9 | 31 |
| NOMENCLADOR | 3 | 3 | 1 |
| NOMENCLADOR | 6 | 10 | 28 |
| NOMENCLADOR | 7 | 3 | 3 |
| OBSERVACION | NULL | NULL | 4 |

Totales:

- 73 detalles.
- 69 con `id_prestacion`.
- 69 con codigo de nomenclador copiado.
- 69 con descripcion de nomenclador copiada.
- 0 con `id_medicamento`.
- 0 con troquel.
- 0 con nombre de medicamento.

No se debe borrar compatibilidad `MEDICAMENTO` solo porque la muestra actual sea cero; primero hay que rastrear codigo y posibles historicos/otros ambientes.

---

## 9. Maestros que ya aparecen en el SQL real de Compras

Los cuerpos de funciones reales muestran dependencias directas con, al menos:

- `public.afiliado`
- `public.seccional`
- `public.prestador`
- `public.prestador_rubro`
- `public.prestad_contacto_e`
- `public.contacto_e`
- `public.afi_situ_medica`
- `autorizaciones.nomenclador`
- `informacion_afip.empresa`

El relevamiento general tambien detecto estructuras asociadas a tercerizadoras.

El `.sql` asociado vuelve a listar columnas reales de esos maestros y funciones legacy relacionadas por nombre.

No inventar una nueva tabla maestra de Compras si la entidad ya existe fuera de Compras.

---

## 10. Patron que ya existe y conviene extender

Compras ya tiene ejemplos del modelo que se busca:

### Prestador

`compras.get_requerimiento_detalle()` guarda `id_prestador` y obtiene al leer:

- `p.cuit`
- `p.descripcion`

mediante `LEFT JOIN public.prestador`.

Ese es precisamente el tipo de normalizacion legacy que debe evaluarse para otros datos maestros.

### Seccional de afiliado

`compras.get_requerimiento_compra_pdf()` ya obtiene `afiliado_seccional` desde:

- `public.afiliado`
- `public.seccional`

usando `CUIL + integrante`.

Sin embargo, nombre, documento, domicilio y contacto siguen viniendo del snapshot de `compras.requerimiento` a traves de `requerimiento_base()`.

### Nomenclador

`guardar_requerimiento_detalle()` valida contra `autorizaciones.nomenclador`, pero persiste tambien:

- `id_tipo_nomenclador`
- `codigo_nomenclador`
- `descripcion_nomenclador`

El nuevo analisis debe determinar que parte puede obtenerse siempre del nomenclador actual manteniendo los mismos contratos de salida.

---

## 11. Principio Liquidaciones / legacy-first

Para el refactor posterior, `/liquidaciones` es la referencia principal de estilo y estructura cuando exista una funcionalidad analoga.

Prioridad:

1. Reutilizar tablas maestras existentes.
2. Reutilizar funciones SQL existentes.
3. Usar funciones PostgreSQL simples para consultas/operaciones cuando ese sea el patron legacy.
4. Java mediante `CallableStatement` / Services legacy, evitando SQL nativo disperso.
5. Mantener aliases y contratos de salida para no tocar JSP/Jasper/Excel innecesariamente.
6. JOIN simple antes que nueva capa.
7. Cambio local antes que nueva abstraccion.

Evitar salvo necesidad demostrada:

- nueva arquitectura;
- repositories modernos agregados solo para este modulo;
- ORM;
- DTOs adicionales sin necesidad;
- nueva capa de dominio;
- duplicar Services existentes;
- copiar logica SQL al Java;
- agregar constraints/FKs como "limpieza" colateral;
- convertir el refactor en una migracion tecnologica.

---

## 12. Clasificacion que debe usar el nuevo analisis

Cada columna de Compras debe terminar exactamente en una de estas categorias:

### MANTENER - dato transaccional propio

El dato nace en la operacion y no puede reconstruirse desde otro maestro.

Ejemplos conceptuales:

- estado de la operacion;
- observacion ingresada;
- cantidad;
- precio cotizado/acordado original cuando corresponda;
- prestador adjudicado;
- documentos;
- intento de envio;
- error;
- usuario/fecha de auditoria;
- relacion con RP.

### MANTENER - identidad externa

Es la referencia minima necesaria para recuperar el maestro actual.

Ejemplos a comprobar:

- afiliado: `afiliado_cuil_titular + afiliado_int`;
- prestador: `id_prestador`;
- nomenclador: `id_prestacion`;
- empresa: `empresa_cuit + empresa_sucursal`.

### DERIVAR - dato calculable

Se obtiene exactamente de otros datos persistidos.

Derivaciones confirmadas:

- `recupero = cargo_tercerizadora > 0`;
- `cargo_ospim = 100 - cargo_tercerizadora`.

Se mantienen `surge` como booleano independiente y `precio_total_estimado` como importe editable.

### CONSULTAR MAESTRO ACTUAL

Es una copia descriptiva de una entidad identificable y negocio quiere el valor vigente.

Candidatos a investigar con prioridad:

- datos duplicados del afiliado;
- descripcion/CUIT de prestador donde exista `id_prestador`;
- codigo/descripcion/tipo de nomenclador donde alcance `id_prestacion`;
- descripcion de empresa donde exista `CUIT + sucursal`.

### MANTENER - dato operacional/auditable

Aunque se parezca a un dato maestro, representa lo ocurrido en una operacion concreta.

Ejemplo que requiere especial cuidado:

- `email_destino` de una reserva/intento de notificacion.

No eliminarlo solo porque el prestador tenga emails actuales.

---

## 13. Estrategia preferida para un refactor quirurgico

La primera opcion a estudiar para cada grupo es:

1. **Mantener el contrato externo.**
2. Cambiar la funcion SQL de lectura para resolver el dato actual mediante JOIN/funcion legacy.
3. Mantener temporalmente los nombres de campos que Java/JSP/Jasper ya esperan.
4. Dejar de escribir la columna redundante.
5. Solo despues retirar la columna fisica.
6. Limpiar parametros/beans Java unicamente cuando hacerlo reduzca complejidad sin ampliar el riesgo.

Ejemplo conceptual para afiliado:

`requerimiento_base()` puede seguir devolviendo `afiliado_nombre`, `afiliado_apellido`, etc., pero obtenerlos desde el maestro actual en lugar de `compras.requerimiento`.

Eso permite normalizar almacenamiento sin obligar a reescribir todas las capas al mismo tiempo.

Este patron debe compararse primero con `/liquidaciones` antes de implementarlo.

---

## 14. Como ejecutar el SQL de verdad

Ejecutar el archivo completo sobre la base que se quiera considerar fuente de verdad.

Es solo lectura y abre una transaccion `READ ONLY` que finaliza con `ROLLBACK`.

Guardar la salida completa con fecha y ambiente.

Ejemplo de nombre recomendado:

`docs/sql/compras/snapshots/20260922_devmolineros_compras_catalogo.txt`

Si se vuelve a ejecutar despues de una modificacion, guardar un snapshot nuevo. No sobrescribir silenciosamente la evidencia anterior.

---

## 15. Que no debe hacer Codex en la proxima iteracion

En la primera iteracion del nuevo analisis:

- no modificar archivos;
- no crear SQL de migracion definitivo;
- no hacer `ALTER`, `DROP`, `UPDATE` o correcciones de datos;
- no hacer commit;
- no hacer push;
- no asumir que un trigger existe por encontrar una funcion `RETURNS trigger`;
- no asumir CHECKs desde el repo;
- no defender snapshots historicos de datos maestros: QA confirmo que se quiere el dato actual;
- no eliminar datos de evento/auditoria por confundirlos con datos maestros;
- no agregar arquitectura.

El objetivo es entregar **un unico refactor principal**, amplio en cobertura pero minimo en mecanismo.

---

## 16. Archivos que debe usar Codex

SQL de autoridad:

`docs/sql/compras/20260922_compras_fuente_verdad_produccion.sql`

Explicacion y reglas de interpretacion:

`docs/sql/compras/20260922_compras_fuente_verdad_produccion.md`

Para el codigo:

- `origin/main` actual.
- modulo `/compras` completo.
- Services y ServiceImpl relacionados.
- `/liquidaciones` como referencia legacy principal.
- Afiliados, Prestadores, Autorizaciones/Nomenclador, Reclamo Prestacional, Document Library, Jasper/JSP/Excel solo cuando sean consumidores o fuentes reales.

El megaprompt preparado para esa iteracion se entrega en:

`docs/sql/compras/20260922_megaprompt_codex_refactor_compras.md`
