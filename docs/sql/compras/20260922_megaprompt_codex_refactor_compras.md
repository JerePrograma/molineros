# MEGAPROMPT CODEX - ANALISIS DEFINITIVO PARA NORMALIZAR COMPRAS

Quiero una nueva investigacion profunda del modulo `/compras` de MOLINEROS para preparar un **refactor masivo en cobertura, pero minimo, preciso y quirurgico en implementacion**.

Esta iteracion es de **ANALISIS Y PROPUESTA**. No implementes todavia.

---

# 1. REPOSITORIO OBLIGATORIO

Repositorio:

`https://github.com/JerePrograma/molineros.git`

Ruta local obligatoria:

`C:\wsmolineros\ext`

Rama de trabajo y referencia:

`main / origin/main`

Antes de cualquier inspeccion o comando Git ejecuta obligatoriamente:

```powershell
Set-Location -LiteralPath 'C:\wsmolineros\ext' -ErrorAction Stop

$root = (git rev-parse --show-toplevel).Trim()
$origin = (git remote get-url origin).Trim()
$branch = (git branch --show-current).Trim()

if ($LASTEXITCODE -ne 0 -or
    $root -ne 'C:/wsmolineros/ext' -or
    $origin -ne 'https://github.com/JerePrograma/molineros.git' -or
    $branch -ne 'main') {

    throw "Repositorio, ruta o rama incorrectos. No inspeccionar ni modificar archivos."
}
```

Luego:

```powershell
git fetch origin

git status --short --branch

git rev-parse HEAD
git rev-parse origin/main
```

Toma **origin/main HEAD** como referencia funcional del codigo.

NO cambies de rama.

---

# 2. PRIMERA ITERACION: PROHIBIDO IMPLEMENTAR

En esta iteracion:

- NO modificar archivos.
- NO ejecutar migraciones.
- NO ejecutar `ALTER`, `DROP`, `UPDATE`, `INSERT` o `DELETE` contra ninguna DB.
- NO corregir datos.
- NO crear nuevos SQL definitivos.
- NO hacer commit.
- NO hacer push.
- NO reformatear archivos.
- NO reescribir arquitectura.

Solo quiero investigacion exhaustiva, contraste de evidencias y una propuesta unica de refactor.

---

# 3. NUEVA FUENTE DE VERDAD SQL - PRIORIDAD ABSOLUTA

Para la parte SQL, deja de considerar `compras_schema.sql` como representacion automatica de produccion.

Debes leer primero y usar como documentos de autoridad:

`docs/sql/compras/20260922_compras_fuente_verdad_produccion.sql`

`docs/sql/compras/20260922_compras_fuente_verdad_produccion.md`

Regla de precedencia:

1. Estado real de DB documentado por esos archivos / snapshot.
2. Scripts incrementales cuya aplicacion real este demostrada.
3. SQL canonico/versionado del repositorio.
4. SQL historico.

Si el repositorio contradice el snapshot real, **no presentes el repositorio como produccion**.

Si para una afirmacion exacta falta evidencia del cuerpo instalado de una funcion, marca la incertidumbre. No rellenes el hueco suponiendo que el cuerpo versionado es identico.

---

# 4. HECHOS DE PRODUCCION QUE DEBES TOMAR COMO PUNTO DE PARTIDA

La fotografia real recibida el 22/09/2026 mostro:

- PostgreSQL `9.6.5`.
- Schema `compras` owner `postgres`.
- 8 tablas.
- 81 funciones en `compras`.
- 4 secuencias.
- 15 constraints.
- **0 CHECK constraints**.
- **2 triggers de usuario**.
- 0 views.
- 0 materialized views.

Triggers de usuario realmente instalados:

1. `trg_compras_requerimiento_completar_baja`
   - `compras.requerimiento`
   - `compras.completar_baja_requerimiento_fila()`

2. `trg_compras_detalle_calcular_total`
   - `compras.requerimiento_detalle`
   - `compras.calcular_total_detalle_fila()`

Existen funciones `RETURNS trigger` como:

- `validar_requerimiento_fila()`
- `validar_requerimiento_detalle_fila()`
- `validar_tipo_prestacion_detalle_fila()`
- `validar_tipo_prestacion_detalle_nuevo()`
- `validar_prestador_cotizacion_fila()`

pero NO aparecieron asociadas a triggers de usuario en el catalogo real relevado.

No confundas:

> funcion existente

con:

> validacion realmente ejecutada automaticamente por DB.

Tambien debes partir de que no aparecieron FKs para varias relaciones que el codigo trata como funcionales, entre ellas:

- `requerimiento.id_sector -> sector_requerimiento`;
- `requerimiento_detalle.id_requerimiento -> requerimiento`.

NO deduzcas de esto que haya que agregar FKs. Solo registra el estado real.

---

# 5. NUEVA DEFINICION FUNCIONAL DE NEGOCIO

Esta definicion invalida una parte de las conclusiones del analisis anterior.

QA confirmo:

> Para datos maestros se quiere SIEMPRE el dato actual y se quiere eliminar la mayor cantidad razonable de informacion redundante.

Por lo tanto:

- NO uses "se pierde el snapshot historico" como argumento automatico para conservar una copia de un dato maestro.
- Si existe una identidad estable y el dato se puede recuperar de forma simple del maestro actual, esa copia es candidata real a eliminar.
- El objetivo principal no es ahorrar bytes. Es eliminar duplicacion logica, fuentes multiples de verdad e inconsistencias.

Pero esta regla NO autoriza a borrar datos que describen un evento, una transaccion, una auditoria o un documento concreto.

Debes distinguir estrictamente:

1. identidad del maestro;
2. atributo actual del maestro;
3. dato propio de la transaccion;
4. dato derivable matematicamente;
5. dato operacional/auditable;
6. compatibilidad legacy historica.

---

# 6. OBJETIVO CENTRAL

Responder con evidencia:

> Con la base realmente instalada y el codigo actual, que columnas de Compras podemos dejar de persistir y reemplazar por referencias, JOINs o calculos simples, mostrando siempre la informacion maestra actual, sin cambiar innecesariamente los contratos de UI/Jasper/Excel/Services y sin introducir nueva arquitectura?

Quiero la **maxima reduccion razonable de redundancia**, pero mediante el **menor mecanismo posible**.

Refactor masivo en alcance NO significa rediseño masivo.

---

# 7. REFERENCIA DE IMPLEMENTACION: LIQUIDACIONES

El modulo `/liquidaciones` es la referencia principal de estilo legacy para resolver funcionalidades analogas.

Haz un deep search especifico en Liquidaciones antes de proponer cualquier patron nuevo.

Busca en especial:

- `ServiceImpl`;
- `ServiceUtil`;
- uso de `CallableStatement` / `prepareCall`;
- funciones PostgreSQL existentes;
- lectura de maestros mediante JOIN o funcion;
- mapeo de `ResultSet`;
- formas de devolver descripcion/nombre actual sin duplicarlo en tablas transaccionales;
- filtros y busquedas;
- manejo de afiliados;
- prestadores;
- nomencladores/prestaciones si existe un equivalente;
- auditoria `alta_fecha`, `alta_usr`, `modi_fecha`, `modi_usr`, `baja_fecha`, `baja_usr`.

En el informe identifica **paths y metodos exactos de Liquidaciones** que sirven de precedente.

No uses la palabra "inspirado" de forma abstracta. Demuestra exactamente que patron existente propones copiar.

Prioridad de implementacion:

1. funcion SQL existente;
2. JOIN simple en funcion SQL existente;
3. Service legacy existente;
4. ampliacion minima de funcion/Service ya existente;
5. solo al final crear algo nuevo si no existe alternativa razonable.

Evitar SQL nativo nuevo disperso en Java si Liquidaciones resuelve el caso mediante funciones/CallableStatement.

---

# 8. ANALISIS OBLIGATORIO DEL AFILIADO

Hoy `compras.requerimiento` persiste:

- `afiliado_cuil_titular`
- `afiliado_int`
- `afiliado_id_ospim`
- `afiliado_nombre`
- `afiliado_apellido`
- `afiliado_documento_tipo`
- `afiliado_documento_nro`
- `afiliado_direccion`
- `afiliado_localidad`
- `afiliado_provincia`
- `afiliado_celular`
- `afiliado_telefono`
- `afiliado_email`

En los datos relevados:

- 39 requerimientos.
- 35 con CUIL + integrante.
- Esos 35 tienen cargadas sistematicamente las copias principales de identidad/documento/domicilio.

Ya existe ademas un ejemplo dentro de Compras:

`get_requerimiento_compra_pdf()` obtiene `afiliado_seccional` mediante JOIN con `public.afiliado` y `public.seccional`.

Quiero que determines, con codigo y SQL real:

### Identidad minima

No asumas sin probar que `CUIL + integrante` es necesariamente la mejor referencia futura.

Investiga:

- PK/unique real de `public.afiliado`;
- usos de `cuil_titular + inte`;
- `afiliado_id_ospim` y si aporta una identidad mas estable;
- cambios de CUIL (`afi_cambio_cuil` / funciones relacionadas);
- afiliados dados de baja;
- funciones legacy que buscan afiliados incluso dados de baja;
- como Liquidaciones recupera afiliado vigente/actual;
- que ocurre con un requerimiento viejo si el CUIL cambia.

El objetivo es encontrar la **minima identidad estable que permita obtener el dato actual**.

Si requiere conservar dos identificadores por estabilidad, dilo. No elimines una clave util solo para ganar una columna.

### Copias descriptivas

Para cada una de las 11 copias descriptivas/identificatorias restantes responde:

1. quien la escribe;
2. de que fuente la obtiene;
3. quien la lee;
4. donde se usa para filtro/busqueda;
5. JSP que la consume;
6. Jasper que la consume;
7. Excel/exportacion que la consume;
8. funcion SQL que la devuelve;
9. Service/bean que la mapea;
10. como reemplazar esa lectura por el maestro actual manteniendo el mismo alias/contrato si es posible.

Prioriza una solucion donde `requerimiento_base()` siga devolviendo los campos esperados, pero los resuelva desde el maestro actual.

No obligues a JSP/Jasper a conocer la normalizacion si SQL puede mantener su contrato de salida de forma simple.

---

# 9. PRESTADORES

Identidad principal observada:

`id_prestador`

Compras ya usa el patron correcto en lugares como `get_requerimiento_detalle()`:

- persiste `id_prestador`;
- obtiene `cuit` y `descripcion` actuales mediante `LEFT JOIN public.prestador`.

Usa esto como candidato a patron base.

Investiga todas las apariciones de:

- `id_prestador`;
- `descripcion_prestador`;
- CUIT/descripcion devueltos por funciones;
- `email_destino`;
- contactos actuales;
- `requerimiento_cotizacion_prestador`;
- `requerimiento_pedido_cotizacion`;
- `requerimiento_presupuesto`.

Distingue:

### Maestro actual

Ejemplo potencialmente derivable:

- descripcion del prestador cuando existe `id_prestador`.

### Evento operacional

Ejemplo que NO debe eliminarse automaticamente:

- `email_destino` de una reserva/intento de envio.

Ese valor puede representar el destinatario asociado a una operacion concreta aunque el maestro cambie despues.

No confundas ambos tipos.

---

# 10. EMPRESAS / AFIP

La DB real usa:

`informacion_afip.empresa`

Y Compras identifica cotizaciones de empresa mediante:

- `empresa_cuit`
- `empresa_sucursal`
- `descripcion_empresa`

Determina si `CUIT + sucursal` es identidad suficiente para recuperar siempre `razon_soc` actual.

Si lo es y no existe una necesidad operacional independiente, `descripcion_empresa` es candidata real a dejar de persistirse.

Rastrea todos sus escritores y lectores antes de decidir.

---

# 11. NOMENCLADOR / PRESTACIONES

DB real usada por Compras:

`autorizaciones.nomenclador`

Hoy el detalle puede almacenar:

- `id_prestacion`
- `id_tipo_nomenclador`
- `codigo_nomenclador`
- `descripcion_nomenclador`
- `id_tipo_prestacion`
- `tipo_item`

No asumas que todos son equivalentes.

Investiga exactamente:

- que identifica tecnicamente la prestacion;
- que clasifica el flujo de Compras;
- que se usa para rubros de prestadores;
- que se puede recuperar del nomenclador actual;
- que se usa en filtros;
- que se usa en busqueda historica;
- que usa Jasper;
- que usa RP;
- que validaciones Java vuelven a consultar el nomenclador;
- que hace Liquidaciones para informacion equivalente.

Nueva premisa:

Si `codigo`, `descripcion` o `id_tipo_nomenclador` son atributos actuales derivables de `id_prestacion`, la perdida de su snapshot historico NO es un impedimento por si sola.

Pero no elimines `id_tipo_prestacion` si representa una clasificacion propia de Compras que no se puede reconstruir inequivocamente desde `id_prestacion`.

### Historicos de items

Revisa con especial cuidado:

- `buscar_items_historicos_afiliado()`;
- `buscar_items_historicos_afiliado_clasificado()`.

Actualmente usan codigo/descripcion guardados.

Determina si pueden mantener el mismo contrato devolviendo codigo/descripcion **actuales** del nomenclador mediante `id_prestacion`.

---

# 12. MEDICAMENTOS LEGACY

En la muestra real actual:

- 73 detalles;
- 69 NOMENCLADOR;
- 4 OBSERVACION;
- 0 MEDICAMENTO.

Sin embargo el codigo conserva compatibilidad con:

- `id_medicamento`;
- `troquel`;
- `nombre_medicamento`;
- `tipo_item = MEDICAMENTO`.

No elimines este soporte solo porque la muestra actual sea cero.

Investiga:

- historial de Git;
- SQL historico;
- consumidores;
- posibilidad de datos en otros ambientes;
- si el modelo legacy exige conservar lectura/edicion restringida de esos registros.

Clasificalo como compatibilidad legacy si corresponde.

---

# 13. REDUNDANCIAS MATEMATICAS

## 13.1 `recupero`

Dato real:

- 39 filas comparadas.
- **1 inconsistencia** entre `recupero` y `(cargo_tercerizadora > 0)`.

Antes de proponer eliminarlo:

- localiza por codigo todos los caminos que pueden escribir ambos valores;
- explica como pudo aparecer esa inconsistencia dado que no hay CHECK real;
- determina cual de los dos valores representa la regla funcional correcta;
- revisa Action, Helper, Service y SQL;
- revisa filtros `p_recupero`;
- revisa Jasper/Excel/RP;
- define como migrar conceptualmente esa fila sin ejecutar nada.

No digas "es matematicamente redundante" y cierres el tema: existe evidencia real de drift.

## 13.2 `cargo_ospim` / `cargo_tercerizadora`

Dato real:

- 39 filas.
- 0 inconsistencias para suma 100.

Determina cual es el porcentaje minimo que conviene persistir segun el flujo real y Liquidaciones.

La otra salida puede conservarse calculada para no romper contratos.

## 13.3 `precio_total_estimado`

Dato real:

- 15 filas comparables.
- 0 inconsistencias.

Existe realmente el trigger:

`trg_compras_detalle_calcular_total`

con `calcular_total_detalle_fila()`.

Pero el trigger actual admite ciertos totales explicitamente informados.

Determina si esa posibilidad se usa legitimamente en codigo, SQL manual, RP o algun flujo.

Si no se usa, estudia eliminar el almacenamiento y conservar el alias calculado.

---

# 14. DOCUMENT LIBRARY Y DOCUMENTOS

No trates automaticamente como redundantes:

- `dl_group_id`;
- `dl_folder_id`;
- `dl_file_entry_id`;
- `dl_file_uuid`;
- `nombre_original`;
- `nombre_persistido`;
- `titulo`;
- `intento`.

Rastrea sus validaciones y consumo real.

Pueden existir duplicaciones tecnicas deliberadas para comprobar identidad del documento, descargar, compensar errores o asociar un intento de notificacion.

El objetivo de este refactor NO es debilitar Document Library.

---

# 15. AUDITORIA Y ESTADOS

En principio mantener:

- `alta_fecha`;
- `alta_usr`;
- `modi_fecha`;
- `modi_usr`;
- `baja_fecha`;
- `baja_usr`;
- estados operacionales;
- intentos;
- errores;
- reservas/tokens de RP;
- `motivo_baja` si existe en la version actual de codigo/DB analizada.

No los clasifiques como "duplicacion" solo porque otro objeto tenga fechas o usuarios.

---

# 16. REQUERIMIENTO_BASE Y CONTRATOS DE SALIDA

Analiza `compras.requerimiento_base()` como punto central de normalizacion.

Preferencia fuerte:

> eliminar redundancia fisica sin cambiar innecesariamente el contrato logico que Java y la UI consumen.

Ejemplo de estrategia a validar:

- retirar columnas fisicas del requerimiento;
- `requerimiento_base()` hace JOIN a maestros;
- mantiene nombres/aliases actuales;
- `buscar_requerimientos()` sigue consumiendo `requerimiento_base()`;
- `get_requerimiento()` sigue igual;
- `get_requerimiento_compra_pdf()` sigue recibiendo los campos;
- mappers Java pueden permanecer estables inicialmente;
- JSP/Jasper/Excel requieren pocos o ningun cambio.

No adoptes esta estrategia por dogma: comparala con Liquidaciones y confirma que es la opcion mas pequena.

---

# 17. SERVICES - FOCO EXTREMO

Haz inventario exacto de todos los `*ServiceImpl.java` afectados.

Especialmente:

- escrituras de requerimiento;
- lecturas/busquedas;
- detalle;
- cotizacion;
- documentos;
- notificacion de prestadores;
- RP;
- afiliados;
- nomenclador;
- prestadores;
- tercerizadoras/empresas cuando corresponda.

Para cada Service indica:

- metodo;
- funcion SQL invocada;
- parametros;
- columnas mapeadas;
- si utiliza nombre de columna o posicion;
- que deberia cambiar;
- que podria quedar intacto si SQL conserva contratos.

Quiero minimizar el diff Java.

---

# 18. CONSUMIDORES OCULTOS

Buscar globalmente cada columna candidata en:

- Java;
- ServiceImpl;
- Helper;
- Action;
- Bean;
- JSP;
- JSPF;
- JRXML;
- `.jasper` cuando sea posible identificar el contrato fuente;
- exportaciones Excel/CSV;
- PDFs;
- mails;
- Document Library;
- RP;
- SQL;
- funciones PostgreSQL;
- triggers;
- scripts historicos;
- tests;
- Maps/atributos de request/session;
- reflexion/BeanUtils si existe;
- serializacion.

No recomendar eliminar una columna sin listar todos sus consumidores relevantes encontrados.

---

# 19. DRIFT REPOSITORIO VS PRODUCCION

Quiero una seccion especifica comparando:

- `compras_schema.sql`;
- scripts incrementales;
- snapshot real documentado.

Lista exactamente:

- CHECKs que estan en repo pero no en DB;
- triggers definidos en repo pero no instalados;
- funciones con firma/cuerpo distinto cuando exista evidencia;
- FKs que se asumian y no existen;
- columnas reales que no coincidan;
- indexes reales;
- funciones reales adicionales;
- cualquier objeto versionado obsoleto.

NO propongas sincronizar todo indiscriminadamente.

El objetivo es saber que codigo/SQL debe tocar el refactor, no arreglar todo el drift historico.

---

# 20. POSTGRESQL 9.6

Toda propuesta debe ser compatible con PostgreSQL 9.6.5.

No uses como solucion principal:

- generated columns de versiones posteriores;
- procedures modernas;
- sintaxis no disponible en 9.6;
- features modernas solo por elegancia.

Preferir:

- funciones SQL/plpgsql existentes;
- SELECT/JOIN simples;
- expresiones calculadas;
- tipos/aliases compatibles;
- `CallableStatement` legacy desde Java.

---

# 21. CLASIFICACION OBLIGATORIA COLUMNA POR COLUMNA

Para toda columna relevante de las 8 tablas de Compras crea una matriz:

| Tabla | Columna | Quien escribe | Fuente real | Quien lee | Categoria | Puede eliminarse | Sustitucion minima | Riesgo |
|---|---|---|---|---|---|---|---|---|

Categorias permitidas:

- `MANTENER_TRANSACCIONAL`
- `MANTENER_IDENTIDAD`
- `MANTENER_OPERACIONAL_AUDITORIA`
- `MANTENER_COMPATIBILIDAD_LEGACY`
- `DERIVAR`
- `CONSULTAR_MAESTRO_ACTUAL`
- `CANDIDATO_DUDOSO`

No uses "snapshot historico" como categoria protectora para atributos maestros, porque negocio quiere el dato actual.

---

# 22. TEST DE RIGOR POR CADA CANDIDATO A ELIMINAR

Antes de recomendar una eliminacion responde:

1. ¿Quien escribe la columna?
2. ¿De donde sale el valor?
3. ¿Quien la lee?
4. ¿Existe lectura directa sin funcion central?
5. ¿Se usa para filtro/busqueda?
6. ¿JSP la espera?
7. ¿Jasper la espera?
8. ¿Excel/exportacion la espera?
9. ¿RP la usa?
10. ¿Mail/notificacion la usa?
11. ¿Document Library la usa?
12. ¿Se usa para concurrencia/idempotencia?
13. ¿Es identidad o descripcion?
14. ¿Es dato actual de un maestro?
15. ¿Es evento/auditoria?
16. ¿Se puede reconstruir exactamente?
17. ¿Cual es la clave minima para reconstruirlo?
18. ¿La clave puede cambiar?
19. ¿Como maneja Liquidaciones el mismo problema?
20. ¿Puede mantenerse el alias de salida sin conservar la columna fisica?
21. ¿Hay `SELECT *` o tipos compuestos afectados?
22. ¿Hay `SETOF tabla` afectado?
23. ¿Hay `ResultSet` posicional afectado?
24. ¿Que ocurre con filas dadas de baja?
25. ¿Que ocurre si el maestro ya no resuelve?
26. ¿Que comportamiento observable cambia?

Si alguna respuesta no puede demostrarse, marca incertidumbre.

---

# 23. CRITERIO DE DISENO

Busco un refactor con estas propiedades simultaneas:

- maximo ahorro de duplicacion logica razonable;
- minimo numero de conceptos nuevos;
- minimo diff Java;
- minimo cambio JSP/Jasper;
- SQL simple;
- reutilizacion de maestros;
- reutilizacion de funciones legacy;
- estilo Liquidaciones;
- PostgreSQL 9.6;
- sin ORM;
- sin nueva arquitectura;
- sin sobreingenieria.

Ante dos soluciones funcionalmente equivalentes, elige la que toque menos capas.

---

# 24. UNA SOLA PROPUESTA PRINCIPAL

No quiero diez alternativas.

Despues del analisis entrega **UNA arquitectura de refactor principal**.

Puede estar implementada por etapas tecnicas, pero todas deben formar parte de la misma estrategia.

Ejemplo de forma, no de conclusion obligatoria:

1. Resolver datos maestros actuales en funciones centrales de lectura.
2. Conservar aliases/contratos.
3. Dejar de persistir copias.
4. Migrar/limpiar datos necesarios.
5. Eliminar columnas fisicas redundantes.
6. Simplificar firmas Java/SQL solo donde aporte valor real.

Decide el orden segun evidencia.

---

# 25. RESULTADO ESPERADO

El informe final debe tener estas secciones exactas:

## A. Estado SQL real comprobado

Resumen de la DB instalada usando los nuevos archivos de verdad.

## B. Drift DB vs repositorio

Solo diferencias relevantes para este refactor.

## C. Precedentes exactos de Liquidaciones

Paths, clases, metodos y patron reutilizable.

## D. Modelo minimo objetivo

Como deberian quedar las 8 tablas conceptualmente, sin escribir aun la migracion.

## E. Matriz completa de columnas

`MANTENER / DERIVAR / CONSULTAR MAESTRO / COMPATIBILIDAD`.

## F. Afiliado

Identidad minima estable y columnas que pueden desaparecer.

## G. Prestadores y empresas

Identidad, datos maestros actuales y datos operacionales que deben permanecer.

## H. Nomenclador / prestaciones

Que conservar y que derivar del maestro actual.

## I. Redundancias matematicas

Especialmente la fila inconsistente de `recupero`.

## J. Services y consumidores afectados

Listado exacto de archivos/metodos.

## K. SQL afectado

Listado exacto de funciones/tipos/triggers/tablas/indexes que requeririan cambio.

## L. Propuesta unica de refactor

Explicacion concreta del mecanismo minimo.

## M. Plan de implementacion por etapas

Orden exacto y pequeno.

## N. Pruebas de regresion

UI, busqueda, filtros, PDF, Jasper, Excel, mail, cotizacion, RP, documentos.

## O. Riesgos e incertidumbres

Solo riesgos concretos.

## P. Que NO tocar

Datos operacionales/auditables y compatibilidad que no formen parte de la redundancia.

## Q. Archivos exactos que se modificarian en la siguiente iteracion

Separar:

- Java;
- JSP/Jasper;
- SQL canonico;
- SQL incremental;
- otros.

---

# 26. NIVEL DE DETALLE

Quiero un deep search real.

No basta con `grep` de nombres de columna.

Debes seguir flujo:

`Action/JSP -> Helper -> Service -> CallableStatement -> funcion SQL -> tabla -> funcion de lectura -> mapper -> bean -> JSP/Jasper/Excel/RP/mail`

para cada grupo importante.

Prioriza especialmente los `*ServiceImpl.java`, como en el analisis anterior.

---

# 27. REGLA FINAL

No quiero redisenar Compras.

Quiero responder y preparar la implementacion de esta pregunta:

> Si negocio quiere siempre los datos maestros actuales, ¿cual es la forma mas simple, legacy y segura de dejar en Compras solo las identidades y los datos propios de la operacion, eliminando el mayor volumen razonable de copias y valores derivables sin reescribir el modulo?

La solucion debe parecer una evolucion natural de MOLINEROS y de `/liquidaciones`, no un sistema nuevo insertado dentro del legacy.

Cuando termines, NO implementes. Entrega el informe y espera la segunda iteracion.
