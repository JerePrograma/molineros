# Auditoría de normalización de todas las tablas de Compras en QA

**Estado: APLICADO Y VERIFICADO EN QA LOCAL; PRUEBA UI MANUAL PENDIENTE.**

- Destino autorizado: QA `12.1.1.12:5432/devmolineros`. Producción queda excluida.
- Corte inicial: `3ab5d0c7a500b87cf22a443150d46a91dc9b0d65`; producto y forward de normalización: `8647edfd1550cae08fa60eb0d066b89712bc20e8`.
- El commit que incorpora este informe y los dos SQL CHECK cierra la auditoria. El SHA local/remoto y los comandos efectivos se registran en el recibo externo `publicacion_final_20261102.json`.
- Los dos forward quedaron confirmados en QA: once columnas y su indice retirados, FK de afiliado validada y CHECK secundario eliminado. El JAR compatible esta instalado; el arranque configurado termino con exit 0.

La revisión cubre las **14 tablas y 182/182 columnas iniciales**, con **558 filas contadas**. El resultado fisico verificado es **171 columnas**, despues de retirar las 11 copias de cabecera. Las huellas de los datos conservados de las 14 tablas son identicas antes y despues, con las mismas 558 filas. No se modificaron filas para hacer coincidir una propuesta.

Se reutilizaron el inventario instalado y los perfiles agregados de QA. El schema histórico del repositorio sirve como trazabilidad, no como autoridad de lo instalado. No se exportaron nombres, documentos, CUIT/CUIL, correos, archivos, tokens ni datos clínicos de filas. Las capturas contienen metadatos y contadores. Los revisores de fichas no abrieron conexiones propias ni cambiaron runtime; las lecturas fueron ejecutadas por el agente DB coordinado por raíz.

## Cobertura de las 14 tablas

| Tabla | Filas iniciales | Columnas iniciales | Columnas finales | Resultado de la auditoría |
| --- | ---: | ---: | ---: | --- |
| `compras.requerimiento` | 45 | 32 | 21 | Cabecera: 11 copias retiradas; dos claves al padrón; contratos financieros conservados. |
| `compras.requerimiento_afiliado_historial` | 0 | 8 | 8 | Evento anterior/nuevo y auditoría; conservar aunque tenga cero filas. |
| `compras.requerimiento_comparativa` | 35 | 14 | 14 | Oferta del proveedor: condiciones, importes y fechas propios. |
| `compras.requerimiento_comparativa_detalle` | 52 | 9 | 9 | Línea de oferta referida al canónico; no confundir con detalle del requerimiento. |
| `compras.requerimiento_cotizacion_prestador` | 98 | 12 | 12 | Estado por destinatario, intentos, fechas, destino y error; conservar. |
| `compras.requerimiento_detalle` | 71 | 22 | 22 | Ítem histórico, cantidades, precios y total manual; conservar. |
| `compras.requerimiento_orden_compra` | 3 | 16 | 16 | Documento emitido y versión; CHECK secundario duplicado retirado. |
| `compras.requerimiento_orden_compra_detalle` | 3 | 5 | 5 | Cantidad/precio de la orden y posición; no recalcular desde la oferta actual. |
| `compras.requerimiento_pedido_cotizacion` | 98 | 12 | 12 | Archivo por intento; identidad DL e idempotencia. |
| `compras.requerimiento_presupuesto` | 100 | 22 | 22 | Documentos 1/2/3; etiquetas derivadas y nombres de empresa libres. |
| `compras.requerimiento_reclamo_prestacional` | 17 | 12 | 12 | Vínculo, reserva, token, error y baja; conservar protocolo. |
| `compras.sector_nomenclador` | 22 | 5 | 5 | Asociación configurable: 17 específicas y 5 generales; conservar. |
| `compras.sector_requerimiento` | 6 | 10 | 10 | Catálogo propio y su ciclo de vida. |
| `compras.tipo_prestacion` | 8 | 3 | 3 | Rubros y pertenencia al sector; distinto de tipo_nomenclador. |
| **Total** | **558** | **182** | **171** | **Sin tablas ni columnas iniciales sin ficha.** |

Las 32 fichas de cabecera se combinan con las 93 de documentos/OC/RP/catálogos y las 57 de detalle/comparativa/cotización. Se conservaron los manifiestos anteriores y sus límites de evidencia. Las fichas registran tipo, nulabilidad, recuentos, clasificación, lectores/escritores directos y referencias instaladas.

## Normalizacion aplicada en QA

El forward de `8647edfd` mantiene `afiliado_cuil_titular` y `afiliado_int`. Sustituye la persistencia de los siguientes 11 atributos por datos obtenidos del padrón y de su domicilio:

1. `afiliado_id_ospim`.
2. `afiliado_nombre`.
3. `afiliado_apellido`.
4. `afiliado_documento_tipo`.
5. `afiliado_documento_nro`.
6. `afiliado_direccion`.
7. `afiliado_localidad`.
8. `afiliado_provincia`.
9. `afiliado_celular`.
10. `afiliado_telefono`.
11. `afiliado_email`.

Estado: **APLICADO_Y_VERIFICADO_QA**. La cabecera paso de 32 a 21 columnas y el verificador posterior aprobo sus postcondiciones. Se conservan las firmas legacy 23/24, el resultado de cabecera de 34 atributos y el contrato PDF de 43; los alias derivados permanecen disponibles a sus consumidores.

El mismo forward retiro `compras.ix_compras_requerimiento_afiliado_id_ospim`, dependiente de la columna retirada. Conserva `ix_compras_requerimiento_afiliado`, basado en CUIL e integrante, y exige que el índice anterior esté ausente al finalizar. No se usa `CASCADE`.

La FK instalada, validada y no diferible es `compras.requerimiento_afiliado_fkey` sobre `(afiliado_cuil_titular, afiliado_int)`, referida a `public.afiliado(cuil_titular, inte)`, con `MATCH FULL`, `ON UPDATE NO ACTION` y `ON DELETE NO ACTION`. Permite ambos valores NULL cuando el flujo lo admite y rechaza pares parciales. La exigencia funcional de afiliado y la posibilidad de cambiarlo según el estado siguen perteneciendo a la aplicación.

Fuentes: [forward](20261102_normalizar_afiliado_requerimiento.sql) y [reversión](20261102_normalizar_afiliado_requerimiento_reversion.sql). SHA256 del forward leído: `7f7c07a21c7fc59d938dba6ff7677447b027692d10228124a70f66fcf80ef614`.

Tambien se retiro unicamente `compras.requerimiento_orden_compra_version_edicion_check1`. Duplica semánticamente `requerimiento_orden_compra_version_edicion_check`: ambos verifican `version_edicion >= 0`, están validados/locales y tienen iguales propiedades de herencia, comentario y dependencia; `conbin` difiere solo en posiciones de fuente. La columna tiene attnum 29 en el corte; los scripts lo resuelven dinámicamente.

Se conserva el CHECK original, la columna `version_edicion`, su NOT NULL y la actualización por versión. El avance usa `DROP CONSTRAINT ... RESTRICT`; la reversión restaura la restricción nombrada y su significado, no el OID original. La revisión corrigió antes de ejecutar la revalidación de identidad después del LOCK y el acceso a OID de catálogo en PostgreSQL 9.6. Estado: **APLICADO_Y_VERIFICADO_QA**. La lectura posterior comprobo keeper original presente y copia ausente.

Fuentes: [avance CHECK](20261102_eliminar_check_version_oc_duplicado_qa.sql), SHA `ffb9ad138bbad48fbf4896e4121dbf18cad6d838e5b38271e487d19d31f9e464`; [reversión CHECK](20261102_eliminar_check_version_oc_duplicado_qa_reversion.sql), SHA `4891e7029967046c12ab4860e076c9a2e566134dbd69631f07a2014007d693b5`. La aplicacion operativa confirmada consta en el recibo final independiente de la fixture.

La fixture aislada del CHECK completó **538/538 verificaciones aprobadas**. Se usaron únicamente filas sintéticas y una base separada; todas sus conexiones quedaron cerradas y no hubo conexión a la base operativa ni a producción. La rama de dependientes no se ejercitó, según el propio recibo. La aplicacion sobre QA real se acredita aparte en los recibos operativos finales. Evidencia: [recibo de fixture CHECK](<C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/normalizacion_todas_tablas_20261102/check_oc_fixture/ejecucion_20261102T184542378000Z/resultado_fixture_check_oc_sin_PII.json>), SHA256 `f9bf8546baeb7c6a2cadc02388a9664929ed4d0eec67b532f16095d7f74a5813`.

## Normalización de OC que ya estaba presente

El inventario inicial confirma que estos 17 campos ya estaban ausentes. No se retiraron durante esta auditoría.

En `compras.requerimiento_orden_compra`, 14 campos:

- `fecha_orden_medica`, `fecha_recepcion`, `moneda`.
- `proveedor`, `proveedor_cuit`, `proveedor_domicilio`, `proveedor_contacto`, `proveedor_email`, `proveedor_telefono`.
- `confeccionado_por`, `telefono_compras`, `solicitante`, `plan`, `id_reclamo_prestacional`.

En `compras.requerimiento_orden_compra_detalle`, tres campos:

- `codigo_item`, `descripcion_item`, `precio_total`.

La [migración de OC del 26/10](20261026_normalizar_orden_compra.sql) contiene comprobaciones de conservación antes de retirar esos atributos. La identidad de proveedor y del ítem se resuelve por relaciones; el total de OC se calcula desde su cantidad y precio. Esto no autoriza a reemplazar los valores transaccionales de una orden por los vigentes de una oferta o de un maestro.

Las tres OC observadas tienen presupuesto existente, activo y del mismo requerimiento. Un origen es de tipo distinto de 3: es un hecho del perfil, no un error declarado. Sus tres líneas tienen origen válido y cantidades iguales al detalle actual; dos detalles actuales no tienen estimación y hay una diferencia de precio frente a la estimación actual. No se recalculó ni corrigió ningún importe.

## Copias futuras frente a datos que deben conservarse

`requerimiento_presupuesto.descripcion_prestador` guarda la etiqueta construida por el escritor Java: descripción en mayúsculas y CUIT, con los fallbacks de `PrestadorCotizacion.getEtiquetaVisible()`. Compararla solo con `public.prestador.descripcion` produjo 49 diferencias de formato y no demuestra historia diferente. El perfil corregido encontró **49/49 coincidencias exactas**, con 49 descripciones ASCII evaluables y cero no evaluables. No se generaliza la equivalencia de `SQL upper` y `Java Locale.ROOT` a Unicode.

Esa persistencia es candidata a normalización futura coordinada. Antes de retirarla deben sustituirse escritores, lectores, funciones, retornos tipados y compatibilidad. **No se promete ni ejecuta nueva DDL para esa columna en esta ventana.** Referencias directas: `PresupuestoCompraHelper.java:590`, `PrestadorCotizacion.java:37-58` y los lectores de `BusquedaRequerimientoCompraServiceImpl`.

`descripcion_empresa` tiene otra semántica: entre 13 cotizaciones de empresa, **12 conservan un nombre libre sin identidad CUIT+sucursal completa**. La única identificada tiene un maestro único y coincide tras `btrim`. Se debe conservar el modo libre; no corresponde eliminar globalmente la columna. Evitar la copia en el caso identificado requeriría un cambio coordinado posterior.

Los porcentajes `cargo_ospim`, `cargo_tercerizadora` y el flag `recupero` son posibles derivados, pero sus contratos siguen abiertos. Los 45 registros perfilados suman 100 y no tienen discrepancias entre recupero y cargo positivo. Sin embargo, `guardar_requerimiento` de 23 parámetros conserva las entradas financieras independientes, también en el forward. No se retiran columnas, se generan automáticamente valores ni se cambia esa semántica a partir de una regularidad del conjunto actual.

Los documentos DL conservan metadatos de identidad, carga, compensación e idempotencia. Se observaron **198 identidades técnicas distintas**, sin repeticiones entre presupuesto y pedido, y ninguna identidad no positiva o UUID ausente en ese corte. Esto no comprueba existencia física de archivos. `group/folder/file/name/uuid` participan en las comprobaciones Java y, para pedidos, en `mismoPedido`; su derivación técnica no habilita un DROP aislado.

## Integridad referencial futura y evidencia vacua

Estas cinco FK son posibilidades de estudio, no DDL aprobada para esta ventana:

| Relación candidata | Evidencia QA | Condición pendiente |
| --- | --- | --- |
| `requerimiento_detalle.id_prestacion` -> `autorizaciones.nomenclador.id_prestacion` | 60 referencias positivas; cero huérfanos observados, incluyendo activos y bajas. | Política de borrado del maestro y escritores externos. |
| `requerimiento_detalle.id_tipo_nomenclador` -> `public.tipo_nomenclador.id_tipo_nomenclador` | 60 valores no NULL. El perfil de huérfanos de este campo no se ejecutó en ese lote. | Perfil específico y preservación del tipo histórico; no imponer igualdad con el tipo actual del canónico. |
| `requerimiento_detalle.id_medicamento` -> `public.medicamentos.id_medicamento` | Maestro con PK integer; 71 referencias NULL y cero referencias positivas. | El cero huérfanos es **VACUO** para MED. Falta caso funcional positivo; no autoriza quitar ID, troquel o nombre. |
| `requerimiento_detalle.id_prestador` -> `public.prestador.id_prestador` | 35 referencias positivas válidas y 36 NULL; cero huérfanos observados. | Política de borrado, nulabilidad válida y escritores. |
| `requerimiento_cotizacion_prestador.id_prestador` -> `public.prestador.id_prestador` | 98 referencias positivas válidas, sin NULL ni huérfanos observados. | Política del maestro y compatibilidad de escritores externos. |

La comparativa tiene FK de prestador instalada: sus 35 referencias válidas no se presentan como una nueva FK pendiente. Las fichas documentales registran otros enlaces para estudio; no hay una propuesta operativa adicional ni cascadas.

## Datos transaccionales, históricos y efectos que se preservan

- Se conservan los históricos válidos y las rectificaciones externas autorizadas confirmadas posteriormente: el detalle 445 corresponde a 11503/tipo 3, incluido el gasto 11503; el detalle 446 corresponde a 2573/tipo 3. No se restauran los valores previos 10415 o 11452/tipo 10 por aplicar el mandato inicial. En el corte QA actual no existen el requerimiento 1388 ni los detalles 445/446; esta precisión se apoya en evidencia y confirmaciones humanas previas, sin reconsultar producción en esta auditoría. No se reclasifican globalmente los canónicos compartidos.
- `precio_total_estimado` admite un valor manual explícito. No se elimina, transforma en columna generada ni recalcula globalmente desde cantidad y unitario.
- Cantidad, precio, fecha, condiciones, envío, impuestos y validez de las comparativas son hechos de oferta, no copias de defaults o de la solicitud.
- Estado, intentos, destino efectivo del envío, fechas y error de notificación se conservan por concurrencia e idempotencia. `copia_cotizacion_enviada` es un control del flujo, no una copia del padrón.
- Las reservas RP conservan estado, token, fecha y error. El corte contiene 16 VINCULADO y una BAJA coherentes con su maestro. Que hoy no haya tokens o errores no los vuelve eliminables.
- El historial de reasignación conserva identidades anterior/nueva, fecha y actor. Sus cero filas actuales no justifican eliminarlo ni sustituir eventos por el estado actual del requerimiento.
- `sector_nomenclador` sigue siendo configuración funcional: 22 reglas, 17 específicas y cinco generales, sin cruces de sector ni huérfanos observados. Los criterios de marca y código no se eliminan por estar NULL hoy.
- Se preservan las **104 funciones de Compras y los cinco triggers de usuario**. El forward adapta únicamente los cuerpos declarados para dejar de persistir las 11 copias; conserva cantidades, firmas y contratos previstos. No se declara retirado un efecto SQL por existir un reemplazo Java.

Las 47 firmas de índices instalados no contienen duplicados exactos por claves, opclasses, collation, opciones, expresión, predicado y unicidad. El índice de pedidos con `intento DESC` solapa la PK para consultas con el par requerimiento/prestador fijo, pero no es un duplicado universal. No se propone retirarlo. La copia técnica concreta de CHECK es un objeto distinto de un índice duplicado.

## Evidencia y límites del cierre

| Evidencia externa | SHA256 |
| --- | --- |
| [Inventario instalado inicial](<C:/wsmolineros/ext/.codex/artifacts/normalizacion-afiliado-20261102/baseline-metadata.json>) | `879c320a0aa88831ec4cd38d98e547b81142a93be204803b276c56271b7142b3` |
| [Consolidación de 14 tablas y 182 fichas](<C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/normalizacion_todas_tablas_20261102/manifest_consolidacion_14_tablas_20261102.json>) | `9b5ae110921185cf1024b1dac23bf8b25fe95a753e4f902c07ade6ff6c6260de` |
| [Fichas 93 de documentos y catálogos](<C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/normalizacion_todas_tablas_20261102/documentos/manifest_documentos_catalogos_cierre_20261102.json>) | `65b21575e76f31432da0cc5e261a902e43b3fbd9a146a0a24aa1ef1cf07d4ca6` |
| [Fichas 57 de detalle, comparativa y cotización](<C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/normalizacion_todas_tablas_20261102/detalle/manifest_detalle_20261102.json>) | `1e84657e95f40b8823f9a0b823e2af38d68fce04d297001c6a73d7a0ada81f3e` |
| [Counts, nulos, tamaños y flags de índices](<C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/normalizacion_todas_tablas_20261102/db/captura_faltantes_20261102T181132753000Z/resultado_inventario_sin_PII.json>) | `dfde86a39ff3e6d6f9e7f494061641ac900b931520ce2962ef54da476759aa80` |
| [Perfiles financieros, maestros y descripciones simples](<C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/normalizacion_todas_tablas_20261102/db/captura_perfiles_focales_20261102T182114165000Z/resultado_perfiles_sin_PII.json>) | `fc0ecefc67ad43ffd6bb6d82125377defd3d6ecfc9045a973e783bb496525a59` |
| [Perfil OC/RP/documentos/asociaciones y etiqueta Java ASCII](<C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/normalizacion_todas_tablas_20261102/db/captura_perfiles_OC_RP_docs_etiqueta_20261102T183443350000Z/resultado_perfiles_OC_RP_docs_sin_PII.json>) | `7a304736e66f476241645bda347589f2c87c550437c4344dd2d47d4a420d810d` |
| [Propiedades y equivalencia de los CHECK](<C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/normalizacion_todas_tablas_20261102/db/checks_version_OC_metadata_sin_PII_20261102.json>) | `8d4cd37075f5775c297e5f385fc2a95735068677ee0468f27d2b1b62bd6df8fd` |
| [Revisión estática independiente de ambos SQL CHECK](<C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/normalizacion_todas_tablas_20261102/documentos/revision_independiente_SQL_check_OC_20261102.json>) | `78d30a7bd4f1174300e3311f41758208e239085b13e939cca0d54e554cdc9ef9` |

Referencias legacy focales: `Liquidaciones/services/PrestadorServiceImpl.java:184` resuelve el proveedor por identidad; `Autorizaciones/services/NomencladorServiceImpl.java:585` respeta la conexión transaccional prestada; `Autorizaciones/action/CuentaDocumentoHelper.java:225-275` consume los documentos de Compras desde un RP vinculado. Se adaptan sus convenciones, sin crear dependencias nuevas ni auditar esos módulos completos.

Esta version certifica la aplicacion y verificacion operativa descritas en QA. No certifica inexistencia de SQL dinamico o escritores externos, borrado de archivos DL, pruebas UI completas ni aptitud para produccion. Computer Use se detuvo por no poder verificar con suficiente confianza la URL actual de Brave en Windows; no se intento eludir ese control. No se retira una tabla por estar vacia ni una columna por contener NULL.

Compras conserva CUIL + inte como referencia, consulta el padrón actual para mostrar sus datos y permite cambiar el afiliado solo en borrador según las validaciones de la aplicación.


## Cierre operativo y resumen de lo retirado

La parada configurada del PID 95924 termino con exit 0 y se verifico ausencia de listener/Bootstrap. Se aplicaron los dos SQL exactos, se instalo un JAR nativo y se inicio un unico Tomcat, PID 24248, Java 8, listener 8080. Arranque exit 0. Los 39 archivos protegidos mantienen sus hashes.

- Estado SQL final: **14 tablas, 171 columnas visibles, 46 indices, 48 restricciones, 104 funciones y cinco triggers de usuario**. La FK agrega sus triggers internos propios; no confundirlos con los cinco triggers de usuario conservados.
- Se retiraron ahora las **11 columnas de cabecera** enumeradas, `ix_compras_requerimiento_afiliado_id_ospim` y `requerimiento_orden_compra_version_edicion_check1`. No se elimino ninguna tabla ni funcion.
- Las **17 columnas de OC** ya estaban ausentes. Acumulado entre ambas normalizaciones: **28 columnas**, sin atribuir los 17 campos anteriores a esta auditoria.
- El JAR nativo contiene 2.957 entradas y 2.930 clases major 49. Cambiaron solo tres clases principales y el recurso de esquema; las otras 2.927 clases coinciden con el artefacto anterior. SHA instalado: `72115b167216610d2be52d5555e5aba0859cda4b1c199b4e6aff1c275e55efa5`.
- El primer intento operativo termino con SQLSTATE 22021, cero forward confirmados y conexion cerrada: el ejecutor Python 2 enviaba bytes ISO-8859-1 a una conexion UTF-8. Una nueva captura anterior resulto identica a la primera salvo la hora. Se preservo el original; la V2 corrigio solo `execute(SQL.decode('latin-1'))`. Los cuatro SQL mantuvieron sus bytes y hashes. El manifiesto del incidente tiene SHA `c838b9ad24629ae982d590cb67aee0056fe73b7624a05d68c918535c9f62414a`.
- La reversion recompone las copias desde el padron actual. No recupera los textos duplicados descartados con autorizacion. El heap no se compacto: el ahorro observado de 16 KiB corresponde al indice retirado.

| Verificacion o comando | Resultado |
| --- | --- |
| Guardas root/origin, baseline, fetch, switch main y merge ff-only | Exit 0; trabajo local preservado y coordinado. |
| Build Java focalizado: ext-service, ext-impl y ext-web compile | Exit 0 para los tres targets; fuentes congeladas de producto 8647. |
| Ant ext-impl/build.xml jar | Exit 0; sin clean ni recompilacion adicional; un recurso copiado. |
| Suites Java de afiliado/reasignacion/snapshots/core/efectos | 282 comprobaciones aprobadas; alcance focal, no UI. |
| Fixture de normalizacion fisica | 310 comprobaciones aprobadas; avance/repeticion/reversion/negativos y SQL real. |
| Fixture CHECK duplicado | 538/538; 25 rechazos esperados, sin errores inesperados. |
| Preview instalador, parada, instalacion, arranque | Exit 0 en cada uno. |
| Primer aplicador V1 | Exit 2; envoltura de control exit 1; causa 22021 corregida, rollback certificado. |
| Aplicador V2 y verificacion posterior | Exit 0 en ambos; 14 huellas identicas, 34 referencias validas, nueve con baja logica. |
| Lecturas SQL postinstalacion | Exit 0; 24 contratos SELECT y 33 aserciones contra QA. Afiliado FOR SHARE solo planificado; selector empresa no rapido LIMIT 0. |
| Benchmark HTTP posterior | Exit 0; 16/16 HTTP 200; cero timeouts; tres jstacks validos y cero esperas de cache muestreadas. |
| UI autenticada posterior | PRUEBA UI MANUAL PENDIENTE: control detenido por URL Windows no verificable. Sin DML UI posterior. |

Las medianas HTTP fueron 3015,5 ms secuencial, 2828 ms con dos solicitudes simultaneas y 2679 ms con cuatro; maximo 4875 ms. Persisten segundos de latencia: el resultado acredita ausencia de timeouts en esa muestra y no una respuesta instantanea ni UI end-to-end. No se retiraron efectos SQL: los contratos antiguos siguen dependiendo de ellos; ver [certificacion de escritores](20261102_certificacion_escritores_QA.md).

| Recibo operativo | SHA256 |
| --- | --- |
| [Antes detenido](<C:/wsmolineros/ext/.codex/artifacts/normalizacion-afiliado-20261102/root/operativo-antes-20261102T190039759000Z.json>) | `fceb10104c556c11f69cbfa493152d84f1aa68993c84e5554e3e563d59d5edf8` |
| [Dos forward confirmados](<C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/normalizacion_todas_tablas_20261102/operativo/fase_aplicar_20261102T190226188000Z.json>) | `1c56e3b4ca8fd0007c6a93a6902648fafbd81ba4a9bea13d95d6f48d3aea8f63` |
| [Despues: datos y FK](<C:/wsmolineros/ext/.codex/artifacts/normalizacion-afiliado-20261102/root/operativo-despues-20261102T190340144000Z.json>) | `b71c18bf52e409f4ffbad44da4da2ee2f4784b35c32690257d2f2c543acc93ef` |
| [Metadatos finales](<C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/normalizacion_todas_tablas_20261102/operativo/fase_despues_20261102T190340144000Z.json>) | `6b7fa68c886f284fe69fda1769088d20ed05e0eb1ddb8cf700e02c878d29e4a8` |
| [Instalacion UNJAR](<C:/wsmolineros/ext/output/qa-cierre-20261102/naunjar/instalacion_20261102190326183/resultado.json>) | `3e037c53d4b21d92ab5f4700aec1588bfc351554aff8f61974aaa5e482a2fea5` |
| [HTTP16](<C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/normalizacion_todas_tablas_20261102/http_benchmark_Afiliado8647_24248_20261102.json>) | `127cac1e7516894ab3a96d0f3037fc3043e6b2bdcb4f8eb0af046334d7da8fb0` |
| [UI pendiente](<C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/normalizacion_todas_tablas_20261102/UI_pendiente_URL_control_20261102.json>) | `8ae10c1a3fc47691e9e0f56de23747d8bca66effc761e4db0b351cb831535ab6` |
| [Lecturas SQL postinstalacion](<C:/wsmolineros/ext/.codex/artifacts/normalizacion-afiliado-20261102/root/lecturas-post-instalacion.log>) | `10d6694e53ab9b7f86aba284ac0a44e42ca661ccf004e47c60fc93c5b8e7e57b` |
| [Revision independiente posterior](<C:/wsmolineros/ext/.codex/artifacts/normalizacion-afiliado-20261102/root/revision-independiente-post-qa.json>) | `b2251d01c510a1a46697f242470577707bb62b20aa7a9c22c883406dcf927f84` |
| [Runtime y logs finales](<C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/cierre_20261102/na_unjar/manifest_runtime_8647_CIERRE.json>) | `8c4b52f40a2ad2bfc1ed0d76b7167087ff3dd2e13c4a86ad40a593ff525e6ba5` |

El arranque actual no tiene GRAVE de Catalina. Los 40 GRAVE capturados pertenecen a la parada anterior. Desde el nuevo inicio, el portal registra cinco ERROR y dos WARN: dos errores tienen firmas de scheduler (ejecutarReportes lineas 81/131) observadas tambien antes; los grupos sin componente/topframe suficiente quedan NO CONCLUYENTES. No se afirma ausencia total de errores ni se los atribuye especulativamente a Compras. Los logs permanecieron sin bytes nuevos entre 19:07:53 y 19:10:14 UTC.
