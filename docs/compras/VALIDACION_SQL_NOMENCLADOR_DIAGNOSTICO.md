# Validación SQL/Nomenclador - Diagnóstico previo a parches

Fecha: 2026-07-12

Este diagnóstico se cerró antes de modificar fuentes. Se inspeccionaron contratos SQL, Java/JDBC, Actions Struts, JSP/JavaScript, buscadores de Autorizaciones/Farmacia, Jasper/PDF, correo, notificaciones, reclamo prestacional y tests. Las búsquedas completas están en `docs/compras/evidencias/`.

## Resumen

El commit actual compila, pero no es ejecutable para el nuevo detalle técnico. El schema falla al instalarse, el trigger y la búsqueda referencian una columna eliminada, el editor obliga a escribir IDs internos, Java confía en datos manipulables y el Jasper omite la identidad técnica del ítem.

## Hallazgos P0

### P0-1 - Instalación SQL no termina

- Archivo: `ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql:4590`.
- Evidencia: PostgreSQL 17 descartable, `psql -X -v ON_ERROR_STOP=1 -f ...`.
- Resultado: exit `3`, `column "solicitar_cotizacion" of relation "prestador" already exists`.
- Causa: el schema canónico de Compras contiene al final DDL de `public` y `autorizaciones`, no idempotente y ajeno al módulo.
- Impacto: incumple el criterio obligatorio de instalación completa.

### P0-2 - Trigger de detalle roto

- Función: `compras.validar_requerimiento_detalle_fila()` (`828-953`).
- La tabla nueva (`365-475`) no tiene `id_articulo`.
- El trigger consulta `compras.articulo` y usa `NEW.id_articulo`/`OLD.id_articulo` (`852-871`, `884-899`).
- Evidencia ejecutable: `guardar_requerimiento_detalle(...)` falla con `record "new" has no field "id_articulo"`.
- Impacto: ningún medicamento ni nomenclador puede insertarse.

### P0-3 - Búsqueda general rota

- Función: `compras.buscar_requerimientos()` (`1470-1625`).
- Mantiene `JOIN compras.articulo` y `d.id_articulo` (`1596-1619`).
- Evidencia ejecutable: búsqueda por texto falla con `column d.id_articulo does not exist`.

### P0-4 - Funciones de artículos rotas

- `guardar_articulo` (`1208-1296`) y `borrar_articulo` (`1299-1333`) consultan `requerimiento_detalle.id_articulo`.
- `listar_articulos`, `get_articulo_cursor` y `listar_articulos_cursor` sostienen un catálogo que el flujo nuevo prohíbe.
- Impacto: schema incoherente y superficie legacy todavía invocable.

### P0-5 - No existen buscadores en Compras

- `_detalle_editor.jsp:36-137` presenta `id_prestacion`, `id_tipo_nomenclador` e `id_medicamento` como `<input type="text">`.
- No hay botón Buscar/Limpiar ni popup técnico.
- El usuario debe inventar/copiar IDs internos.
- `_detalle_scripts_editable.jsp` sólo valida enteros positivos; no resuelve registros.

### P0-6 - Jasper/PDF omite el ítem técnico

- `requerimiento_compra.jrxml` consulta `get_requerimiento_compra_pdf`, pero sólo declara 31 fields y no declara `tipo_item`, `codigo_item`, `descripcion_item`, medicamento ni nomenclador.
- La columna “Descripción del pedido” imprime únicamente `$F{detalle_observaciones}` (`384-391`).
- El dataset SQL sí expone los campos nuevos; el reporte no los consume.

## Hallazgos P1

### P1-1 - Java no valida contra DB canónica

- `EditarRequerimientoCompraServiceImpl.validarDetalleParaGuardar` (`970-1116`) comprueba sólo formato/IDs positivos.
- No llama `BusquedaMedicamentoServiceUtil.getMedicamento` ni `NomencladorServiceUtil.buscarNomencladorPorId`.
- No valida activo/baja, tipo, código, descripción, nombre, presentación o troquel.
- Persiste textos recibidos desde el navegador. Un ID real puede combinarse con texto falso.

### P1-2 - Campos cruzados no se rechazan en Java

- Para `NOMENCLADOR`, Java no rechaza campos de medicamento.
- Para `MEDICAMENTO`, Java no rechaza campos de nomenclador.
- SQL intenta limpiar valores cruzados, pero Java incumple el contrato obligatorio y no detecta requests manipulados.

### P1-3 - Sector resuelto de forma demasiado amplia

- `RequerimientoCompraDetalleHelper.resolverTipoItemEsperadoSegunSector` (`867-889`) devuelve MEDICAMENTO si la descripción contiene `FARMAC`; para cualquier otro sector devuelve NOMENCLADOR.
- SQL repite “Farmacia vs cualquier otro” (`2248-2263`).
- Monotributo, Sistemas, RRHH y Otros quedan habilitados incorrectamente como nomenclador.
- El requerimiento se recarga desde DB, lo cual es correcto, pero la regla no se limita a Farmacia/Prestaciones Médicas/Legales.

### P1-4 - Superficie de artículos todavía expuesta

- Struts: `/compras/alta_articulo_popup` y `/compras/listar_articulos_sector` (`struts-config.xml:7051-7103`).
- Tiles: popup y resultado de artículos (`tiles-defs.xml:4563-4573`).
- Action: ramas `saveArticuloPopup`, `saveArticulo`, `deleteArticulo`.
- Java: `CompraArticulo`, constantes JDBC y métodos CRUD.
- JSP: `editar_articulo_popup.jsp`, `articulos_sector.jsp`.
- Otros callers: `UploadPresupuestosComprasAction` y `EditarRequerimientoCompraAction` todavía cargan `ARTICULOS_COMPRA`.

## Hallazgos P2

### P2-1 - Transacción y contenido del schema no coinciden con su encabezado

- El archivo abre `BEGIN` en línea 37, hace `COMMIT` en 3222, vuelve a abrir en 3228 y hace `COMMIT` en 3582.
- Funciones de notificación se crean fuera de transacción.
- Ejecuta `SELECT` de diagnóstico con ID `1` durante instalación (`4200-4209`).
- Contiene DDL sobre `public.buscar_prestadores`, historial de prestadores y tipos externos (`4271-4590`).

### P2-2 - Buscador global de medicamentos tiene bugs legacy

- `med_seleccionado` se crea, pero `pierdeFocoMd` lee `med_seleccionada`.
- Llama `cerrarDivPd` en lugar de `cerrarDivMd`.
- `divMedicamento` y `popupMD` son globales/no namespaced.
- Hay callbacks globales `pasarParametrosAParentMd` y variantes.
- Decisión: no modificar el componente compartido; Compras tendrá callback y resultado específicos reutilizando service/SQL.

### P2-3 - Buscador global de nomenclador pierde `id_prestacion`

- `Nomenclador` ya mapea `id_prestacion` e `id_tipo_nomenclador`.
- `nomenclador_search_result.jsp` sólo llama `pasarParametrosAParentNm(idTipo, codigo, descripcion)`.
- El Action `BuscarNomencladorAction` usa `NomencladorServiceUtil`, cuya consulta canónica devuelve ambos IDs.
- Decisión: alternativa B, callback/resultados específicos de Compras. No se cambia el contrato global de todos los consumidores.

### P2-4 - Test de contrato obsoleto

- `ComprasRequerimientosUiContractTest` exige que sigan presentes los refcursors de artículos (`273-283`).
- Además falla en baseline por una contradicción de mensajes ajena al nuevo cambio.

## Hallazgos P3

- Comentarios y nombres siguen hablando de “artículo/item”.
- Hay texto con mojibake visible en fuentes ISO-8859-1 cuando se lee con la codificación incorrecta; cualquier parche debe preservar bytes/codificación legacy.
- El comentario superior del schema declara 22 argumentos de cabecera; debe mantenerse alineado con Java y SQL reales.

## Flujo real de medicamentos existente

1. JSP compartido: `html/portlet/utils/medicamentos/busqueda_medicamentos.jsp`.
2. Parámetros: `search_url`, `esEditable`, `id_medicamento`, `troquel`, `nombre_medicamento`, `popup`, `mostrar_con_presentacion`.
3. Ruta usada: `/autorizaciones/buscar_medicamentos` o variante edit.
4. Struts: forward directo, sin Action Java.
5. Tiles: `portlet.utils.medicamento.view` -> `medicamentos_search_result.jsp`.
6. Service: `BusquedaMedicamentoServiceUtil.getBusquedaMedicamentos`.
7. SQL: función `buscar_medicamentos(?,?,?,?,?,?)`.
8. Resultado: `Medicamento` con `id_medicamento`, troquel, nombre, presentación, código de barras y precio.
9. Callback: `pasarParametrosAParentMd(troquel,nombre,id,presentacion)`.
10. Limpiar: sólo invalida selección en ciertas teclas; contiene los bugs legacy P2-2.

## Flujo real de nomenclador existente

1. Origen principal inspeccionado: Reclamos Prestacionales/Autorizaciones (`view_reclamo.jspf` + `view_reclamo.js`).
2. Inputs: código, descripción y tipo de nomenclador.
3. JS: `buscarNomencladorAutocompletar`, popup `popupMD`.
4. Ruta: `/autorizaciones/buscarNomenclador`.
5. Action: `BuscarNomencladorAction`.
6. Forward/Tiles: `portlet.autorizaciones.buscar_nomenclador` -> `nomenclador_search_result.jsp`.
7. Service: `NomencladorServiceUtil.getListaNomenclador` o `getListaNomencladorPrestacionesMedicas`.
8. SQL: `autorizaciones.busca_nomenclador(...)` / `autorizaciones.busca_nomenclador_prest_med(...)`.
9. Bean: `Nomenclador.getMapping` obtiene `id_prestacion`, `id_tipo_nomenclador`, código, descripción, tipo y baja.
10. Defecto: el JSP descarta `id_prestacion` y el callback global recibe sólo tres argumentos.

## Contratos ya correctos antes de parchear

- `requerimiento_detalle` contiene columnas técnicas, constraints de tipo, requeridos, nulls cruzados, cantidad y precios.
- `guardar_requerimiento_detalle` tiene 13 parámetros y Java usa el mismo orden.
- Java obtiene el retorno escalar desde un único row y rechaza NULL/ID no positivo/múltiples rows.
- `get_requerimiento_detalle` y `get_requerimiento_compra_pdf` exponen los campos técnicos nuevos.
- Mail construye ítems con `getTipoItemNormalizado`, `getCodigoItemVisible` y `getDescripcionItemVisible`; no usa `CompraArticulo`.
- El adjunto PDF vacío o fallido provoca excepción y no se envía silenciosamente.

## Alternativas para nomenclador

| Alternativa | Evaluación |
| --- | --- |
| A - extender callback global | Riesgo alto: hay numerosos consumidores y firmas de 3/6 argumentos. |
| B - callback específico de Compras | Elegida: reutiliza service/SQL, devuelve ambos IDs y no cambia consumidores globales. |
| C - resolver sólo server-side por código/tipo | No elegida: aunque Java validará siempre, el criterio exige cargar un `id_prestacion` real en UI y la unicidad por código/tipo no está demostrada en el repo. |

## Baseline PostgreSQL ejecutable

- Motor: PostgreSQL 17 descartable, puerto 55452, autenticación trust local, base `compras_nomenclador_audit`.
- Producción y el servicio local 5432 no fueron usados.
- Instalación: exit `3` en línea 4590.
- Guardado Farmacia: exit `3`, `record "new" has no field "id_articulo"`.
- Búsqueda de texto: exit `3`, `column d.id_articulo does not exist`.

## Estado del diagnóstico

No apto para commit antes de los parches. Los P0 y P1 son reproducibles y están dentro del alcance solicitado.
