# Plan integral de correcciones QA  -  Flujo de Compras

## 1. Identificaci&#243;n del documento

- **Repositorio:** `JerePrograma/molineros`
- **Ruta local obligatoria:** `C:\devmolineros\ext`
- **Rama &#250;nica:** `main`
- **HEAD remoto verificado al elaborar este documento:** `d684d25afe20722d8fd8e7903931d89385f58e46`
- **Fecha de evidencia:** 30 de julio de 2026
- **Fuente funcional:** observaciones de QA comunicadas por Analia Comas.
- **Fuente t&#233;cnica:** `qa-compras-evidencia.zip`, generado desde el working tree posterior a la actualizaci&#243;n SVN r7312.
- **Estado del &#237;ndice al generar la evidencia:** limpio; `git diff --cached --quiet` devolvi&#243; `0`.
- **Pruebas focalizadas ejecutadas:**
  - `ComprasRequerimientosUiContractTest`: aprobada.
  - `ComprasSurgeSelectContractTest`: aprobada.
- **Advertencia:** las pruebas actuales no cubren todav&#237;a todos los nuevos requerimientos descritos aqu&#237;.

## 2. Objetivo

Implementar las correcciones solicitadas por QA en todo el flujo de Compras, pero hacerlo mediante cambios m&#237;nimos, verificables e independientes.

Cada requerimiento debe:

1. analizarse de forma aislada;
2. modificar &#250;nicamente sus archivos directos;
3. conservar los contratos funcionales existentes;
4. incorporar una prueba focalizada razonable;
5. validarse antes de continuar con el siguiente;
6. generar un commit at&#243;mico propio directamente sobre `main`;
7. publicarse en `origin/main` sin ramas auxiliares, pull requests, rebase ni force-push.

Este documento no autoriza a mezclar las correcciones QA con:

- Reclamo Prestacional;
- Interbanking;
- Liquidaciones;
- producci&#243;n r7311 o r7312;
- limpieza general del repositorio;
- cambios globales de estilos;
- reestructuraciones arquitect&#243;nicas;
- cambios del modelo de estados de Compras.

## 3. Restricciones cr&#237;ticas del entorno actual

### 3.1 Working tree local

El working tree local contiene numerosos cambios versionados y archivos sin seguimiento. Algunos pertenecen a Compras y se superponen con los archivos candidatos.

Por lo tanto:

- no ejecutar `git add .`;
- no ejecutar `git add -A`;
- no ejecutar `git stash`;
- no ejecutar `git reset --hard`;
- no ejecutar `git clean -fd`;
- no ejecutar `git restore .`;
- no ejecutar `git checkout -- .`;
- no cambiar de rama;
- no descartar ni sobrescribir los cambios existentes;
- no hacer un commit general del working tree.

Antes de cada requerimiento hay que volver a comparar su archivo objetivo contra:

- `HEAD`;
- el working tree;
- el respaldo de evidencia;
- cualquier commit remoto nuevo.

### 3.2 Codificaci&#243;n

Todo archivo textual creado o modificado debe quedar en:

```text
ISO-8859-1 sin BOM
```

La API textual de GitHub escribe UTF-8. No debe utilizarse para reemplazar JSP, JSPF, Java, JavaScript, SQL o properties existentes que est&#233;n en ISO-8859-1.

La implementaci&#243;n remota s&#243;lo es segura cuando:

- el archivo nuevo contiene exclusivamente bytes ASCII; o
- el conector permite crear blobs Base64 de bytes ISO-8859-1 y construir un tree sobre el tree real de `main`.

Si no se dispone del tree base real, los cambios de c&#243;digo deben prepararse localmente mediante los bloques PowerShell de este documento y publicarse desde el clon validado.

### 3.3 Actualizaci&#243;n SVN r7312

Despu&#233;s de la primera evidencia se ejecut&#243; una actualizaci&#243;n SVN a r7312 que:

- fusion&#243; `tiles-defs.xml`;
- fusion&#243; `struts-config.xml`;
- actualiz&#243; `c3p0-config.xml`;
- actualiz&#243; dos JSP de Liquidaciones.

Estos archivos no forman parte de las correcciones QA de Compras. No deben incluirse en sus commits.

## 4. Normalizaci&#243;n de las observaciones de QA

Las observaciones recibidas se convierten en los siguientes requerimientos:

| ID | Requerimiento | Prioridad |
|---|---|---|
| `CQA-001` | Uniformar dimensiones, separaci&#243;n y alineaci&#243;n de labels y controles en alta, edici&#243;n y vista | Cr&#237;tica |
| `CQA-002` | Mostrar de forma persistente a qu&#233; correos se intent&#243; o realiz&#243; el env&#237;o; eliminar la ambig&#252;edad de &#8220;Revisar email&#8221; | Cr&#237;tica |
| `CQA-003` | Reordenar el formulario completo de alta y edici&#243;n | Alta |
| `CQA-004` | Ubicar Adjudicaci&#243;n debajo de Pedidos de presupuestos | Alta |
| `CQA-005` | No mostrar Recupero en el alta, conservando su c&#225;lculo y persistencia | Alta |
| `CQA-006` | Unificar t&#237;tulos de secci&#243;n y corregir la ubicaci&#243;n visual de Detalle del requerimiento | Media |
| `CQA-007` | Ejecutar una regresi&#243;n focalizada de todo el flujo de Compras | Cr&#237;tica |

## 5. Estado t&#233;cnico demostrado

### 5.1 Orden actual de la pantalla

`_layout_edicion.jsp` y `_layout_vista.jsp` renderizan actualmente:

1. datos b&#225;sicos;
2. afiliado;
3. observaciones;
4. detalle del requerimiento;
5. pedidos de presupuestos;
6. botonera.

La Adjudicaci&#243;n no es una secci&#243;n independiente del layout. Est&#225; incluida dentro de `_detalle_tabla.jsp`, antes de la tabla de detalles.

### 5.2 Estilos actuales

`_estilos.jsp` define estilos puntuales para:

- controles de solo lectura;
- tabla resumen;
- observaciones de vista;
- afiliado readonly.

No define un contrato com&#250;n para los controles editables:

- altura;
- `line-height`;
- padding;
- `box-sizing`;
- separaci&#243;n vertical de filas;
- distancia uniforme label/control;
- anchuras sem&#225;nticas.

Los JSP usan atributos `size`, `cols`, estilos inline y defaults del tema legacy. Esta mezcla explica la percepci&#243;n de textboxes altos, finos, pegados o desalineados.

### 5.3 Email

La cadena &#8220;Revisar email&#8221; no identifica un bot&#243;n. Es un texto de estado mostrado dentro de una etiqueta visual.

El control realmente interactivo se llama `Ver detalle (N)` y s&#243;lo aparece cuando el resultado inmediato de la notificaci&#243;n contiene incidencias.

La tabla desplegada contiene:

- Prestador;
- Estado;
- Motivo.

No contiene:

- email registrado;
- email destino efectivo;
- fecha del intento;
- resultado persistido del env&#237;o.

El modelo `PrestadorCotizacion` ya contiene un campo `email`. La consulta de prestadores enviados ya recupera `p.contacto AS email` y `estado_envio`, pero la vista de Pedidos de presupuestos s&#243;lo muestra raz&#243;n social, CUIT y estado.

La base tambi&#233;n dispone de `email_destino` en la relaci&#243;n de cotizaci&#243;n. Ese dato es necesario para distinguir:

- email real del prestador;
- destinatario efectivo;
- redirecci&#243;n temporal de QA.

### 5.4 Recupero

`_datos_basicos.jsp` muestra Recupero en la pantalla.

La sem&#225;ntica no depende exclusivamente del checkbox visible:

- existe un hidden sincronizado;
- JavaScript deriva Recupero desde Cargo tercerizadora;
- el backend vuelve a calcularlo;
- el dato se usa en otros flujos.

Por lo tanto, el requerimiento es ocultar el control visual en alta, no eliminar el contrato ni la l&#243;gica.

## 6. Secuencia obligatoria de implementaci&#243;n

Orden recomendado:

1. `CQA-001`  -  estilos y espaciado;
2. `CQA-005`  -  ocultar Recupero en alta;
3. `CQA-003`  -  reordenar el formulario;
4. `CQA-004`  -  extraer y mover Adjudicaci&#243;n;
5. `CQA-002`  -  trazabilidad de emails;
6. `CQA-006`  -  t&#237;tulos de secciones;
7. `CQA-007`  -  regresi&#243;n integral.

No combinar dos IDs en un mismo commit salvo dependencia t&#233;cnica inseparable y documentada.

---

# CQA-001  -  Uniformidad visual de controles

## Problema

QA observa:

- textboxes pegados;
- alturas diferentes;
- anchuras visualmente inconsistentes;
- ausencia de margen entre l&#237;neas;
- distancia irregular entre label y control;
- lectura confusa del formulario.

## Causa demostrable

La pantalla mezcla:

- inputs gobernados por `size`;
- selects sin clase com&#250;n;
- textarea con `cols="100"`;
- estilos inline;
- componentes legacy incluidos;
- estilos &#250;nicamente para readonly.

No existe una regla CSS exclusiva de Compras que normalice todos los controles editables.

## Archivos principales

```text
ext-web/docroot/html/portlet/compras/requerimientos/partials/_estilos.jsp
ext-web/docroot/html/portlet/compras/requerimientos/partials/_datos_basicos.jsp
ext-web/docroot/html/portlet/compras/requerimientos/partials/_observaciones.jsp
ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_editor.jsp
```

Dependencias directas permitidas:

```text
ext-web/docroot/html/portlet/compras/requerimientos/partials/_afiliado_editable.jsp
ext-web/docroot/html/portlet/compras/requerimientos/requerimiento_adjuntos.jsp
```

No modificar CSS global ni otros portlets.

## Implementaci&#243;n m&#237;nima propuesta

Agregar clases exclusivas, por ejemplo:

```text
compras-formulario-requerimiento
compras-campo
compras-campo-corto
compras-campo-medio
compras-campo-largo
compras-label
compras-fila
```

Usar CSS compatible con navegadores legacy:

- `display`;
- `height`;
- `line-height`;
- `padding`;
- `margin`;
- `vertical-align`;
- `box-sizing`;
- `width` o `max-width`.

No usar:

- flexbox;
- grid;
- variables CSS;
- selectores modernos innecesarios;
- estilos globales sobre `input`, `select`, `textarea`, `.portlet` o `body`.

Contrato visual recomendado:

```css
.compras-formulario-requerimiento input[type="text"],
.compras-formulario-requerimiento select {
    height: 26px;
    line-height: 20px;
    padding: 2px 5px;
    box-sizing: border-box;
    vertical-align: middle;
}

.compras-formulario-requerimiento textarea {
    padding: 5px;
    box-sizing: border-box;
    vertical-align: top;
}

.compras-formulario-requerimiento td {
    padding-top: 5px;
    padding-bottom: 5px;
    vertical-align: middle;
}

.compras-formulario-requerimiento td.compras-label {
    padding-right: 8px;
    white-space: nowrap;
}
```

Los valores deben ajustarse despu&#233;s de la prueba visual; no deben aplicarse a controles de fecha generados por Liferay si deforman sus selects.

## Criterios de aceptaci&#243;n

- Ning&#250;n label toca el control asociado.
- Existe separaci&#243;n visible entre filas consecutivas.
- Inputs de texto y selects de una misma fila tienen altura equivalente.
- Los controles readonly y editables no saltan verticalmente al alternar modo.
- Los campos cortos conservan anchura corta; no se fuerza `width:100%` a todos.
- El textarea no desborda el fieldset.
- No aparece scroll horizontal general a 1366&#215;768.
- Alta, edici&#243;n y vista mantienen el mismo ritmo visual.
- El cambio no altera formularios fuera de Compras.

## Prueba contractual requerida

Crear o actualizar un test focalizado que compruebe:

- existencia del contenedor exclusivo;
- clases aplicadas a controles clave;
- ausencia de selectores CSS globales;
- ausencia de flexbox y grid;
- ausencia de `overflow-x:hidden`;
- conservaci&#243;n de IDs y handlers.

## Commit propuesto

```text
fix(compras): uniforma controles del requerimiento
```

## Validaci&#243;n local

```powershell
$files = @(
    'ext-web/docroot/html/portlet/compras/requerimientos/partials/_estilos.jsp',
    'ext-web/docroot/html/portlet/compras/requerimientos/partials/_datos_basicos.jsp',
    'ext-web/docroot/html/portlet/compras/requerimientos/partials/_observaciones.jsp',
    'ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_editor.jsp'
)

git diff --check -- $files
git diff --stat -- $files
git diff --no-ext-diff --unified=30 -- $files
```

Validaci&#243;n visual manual:

- alta nueva con sector que requiere afiliado;
- alta nueva con sector que no requiere afiliado;
- edici&#243;n;
- vista;
- 1366&#215;768;
- 1600&#215;900;
- zoom 100 %.

---

# CQA-002  -  Trazabilidad y revisi&#243;n de emails

## Problema

QA necesita saber:

- a qu&#233; email estaba asociado cada prestador;
- a qu&#233; email se envi&#243; efectivamente;
- qu&#233; pas&#243; con cada env&#237;o.

La etiqueta &#8220;Revisar email&#8221; parece una acci&#243;n, pero no ejecuta nada porque es un estado visual.

## Decisi&#243;n funcional

Implementar una acci&#243;n real y persistente denominada:

```text
Ver destinatarios
```

Debe estar disponible despu&#233;s de recargar la pantalla, no s&#243;lo inmediatamente despu&#233;s del env&#237;o.

No convertir &#8220;Revisar email&#8221; en enlace. Debe cambiarse por un estado no ambiguo, por ejemplo:

```text
Email a revisar
```

## Datos m&#237;nimos a mostrar

Por prestador:

- raz&#243;n social;
- CUIT;
- email registrado;
- email destino efectivo;
- estado de env&#237;o;
- fecha del &#250;ltimo intento, cuando est&#233; disponible;
- motivo o &#250;ltimo error, cuando est&#233; disponible;
- indicador de redirecci&#243;n QA.

## Seguridad

- Mostrar &#250;nicamente a usuarios con rol autorizado de Compras.
- Escapar toda salida HTML.
- No incluir credenciales SMTP.
- No registrar destinatarios en logs nuevos.
- No exponer informaci&#243;n de otros requerimientos.
- Consultar siempre por `id_requerimiento`.
- Mantener fail-closed ante errores de lectura.

## Archivos principales probables

```text
ext-impl/src/ar/com/ospim/compras/requerimientos/beans/PrestadorCotizacion.java
ext-impl/src/ar/com/ospim/compras/requerimientos/service/BusquedaRequerimientoCompraServiceImpl.java
ext-impl/src/ar/com/ospim/compras/requerimientos/service/BusquedaRequerimientoCompraServiceUtil.java
ext-impl/src/ar/com/ospim/compras/requerimientos/action/VerRequerimientoCompraAction.java
ext-web/docroot/html/portlet/compras/requerimientos/requerimiento_adjuntos.jsp
ext-web/docroot/html/portlet/compras/requerimientos/partials/_mensajes.jsp
```

S&#243;lo agregar SQL de esquema si la columna necesaria realmente no existe. La evidencia indica que `email_destino` ya est&#225; disponible.

## Implementaci&#243;n propuesta

Opci&#243;n m&#237;nima:

1. ampliar la consulta `listarPrestadoresEnviados`;
2. recuperar email registrado y destino efectivo;
3. mapearlos a propiedades distintas;
4. renderizar columnas persistentes en Pedidos de presupuestos;
5. agregar un bot&#243;n o enlace real para expandir/contraer las columnas o el detalle;
6. renombrar el estado &#8220;Revisar email&#8221; a &#8220;Email a revisar&#8221;.

No reutilizar un &#250;nico campo para email real y destino efectivo.

## Criterios de aceptaci&#243;n

- Despu&#233;s de enviar y recargar, la pantalla sigue mostrando destinatarios.
- Si no hubo redirecci&#243;n, email registrado y destino efectivo coinciden.
- Si hubo redirecci&#243;n QA, ambos valores se muestran diferenciados.
- &#8220;Email a revisar&#8221; no parece un bot&#243;n.
- &#8220;Ver destinatarios&#8221; responde al click y actualiza `aria-expanded`.
- El detalle funciona con JavaScript ES5 y jQuery legacy.
- Un error de consulta muestra un mensaje controlado y no datos parciales.
- No se cambia el algoritmo de env&#237;o.

## Pruebas requeridas

- contrato Java del bean;
- contrato SQL textual de la consulta;
- contrato JSP de columnas y bot&#243;n;
- prueba de roles;
- prueba de ausencia de credenciales;
- prueba de escaping.

## Consulta local de verificaci&#243;n de DB

Usar variables de entorno; no escribir credenciales en el script:

```powershell
if (!(Get-Command psql -ErrorAction SilentlyContinue)) {
    throw 'psql no est&#225; disponible.'
}

if ([string]::IsNullOrEmpty($env:MOLINEROS_DB_URL)) {
    throw 'Defina MOLINEROS_DB_URL.'
}

$idRequerimiento = 12345

$query = @"
SELECT
    rcp.id_requerimiento,
    rcp.id_prestador,
    p.descripcion,
    p.cuit,
    p.contacto AS email_registrado,
    rcp.email_destino,
    rcp.estado_envio,
    rcp.fecha_envio
FROM compras.requerimiento_cotizacion_prestador rcp
JOIN public.prestador p
  ON p.id_prestador = rcp.id_prestador
WHERE rcp.id_requerimiento = $idRequerimiento
ORDER BY p.descripcion, p.cuit;
"@

$query | psql $env:MOLINEROS_DB_URL -v ON_ERROR_STOP=1
```

Ajustar nombres de columnas s&#243;lo despu&#233;s de verificarlos en `compras_schema.sql`.

## Commit propuesto

```text
feat(compras): muestra destinatarios de cotizacion
```

---

# CQA-003  -  Reordenamiento del formulario

## Orden propuesto

### Alta

1. Datos generales.
2. Afiliado, cuando corresponda.
3. Detalle del requerimiento.
4. Observaciones / Descripci&#243;n.
5. Botonera.

### Edici&#243;n

1. Datos generales.
2. Afiliado, cuando corresponda.
3. Detalle del requerimiento.
4. Observaciones / Descripci&#243;n.
5. Pedidos de presupuestos, cuando corresponda.
6. Adjudicaci&#243;n, cuando corresponda.
7. Botonera.

### Vista

Mismo orden que edici&#243;n, en readonly.

## Archivos principales

```text
ext-web/docroot/html/portlet/compras/requerimientos/partials/_layout_edicion.jsp
ext-web/docroot/html/portlet/compras/requerimientos/partials/_layout_vista.jsp
```

No utilizar `_layout_requerimiento.jsp`: la evidencia confirma que no existe en `HEAD`.

## Criterios de aceptaci&#243;n

- Alta, edici&#243;n y vista comparten el mismo orden conceptual.
- No se duplica ning&#250;n include.
- No se pierde ning&#250;n script necesario.
- Los campos hidden permanecen dentro del formulario correcto.
- La botonera queda al final.
- El detalle aparece antes de observaciones.
- Pedidos de presupuestos y Adjudicaci&#243;n no aparecen en alta nueva.
- Los permisos y estados siguen determinando la visibilidad.

## Prueba contractual requerida

Comprobar el orden literal de includes en ambos layouts.

## Commit propuesto

```text
fix(compras): reordena formulario de requerimiento
```

---

# CQA-004  -  Adjudicaci&#243;n debajo de Pedidos de presupuestos

## Problema

Adjudicaci&#243;n est&#225; dentro de `_detalle_tabla.jsp`. Esa ubicaci&#243;n la acopla a Detalle del requerimiento y hace imposible ubicarla limpiamente debajo de Pedidos de presupuestos.

## Implementaci&#243;n propuesta

Extraer el bloque a un partial exclusivo:

```text
ext-web/docroot/html/portlet/compras/requerimientos/partials/_adjudicacion.jsp
```

Separar, cuando sea necesario:

```text
_adjudicacion_modelo.jsp
_adjudicacion.jsp
```

El nuevo bloque debe renderizarse:

```text
_adjuntos.jsp
_adjudicacion.jsp
_botonera.jsp
```

## Riesgo t&#233;cnico

`_detalle.jsp` utiliza `liferay-util:include`. Las variables locales de `requerimiento_detalle_embebido.jsp` no est&#225;n garantizadas fuera de ese include.

No mover HTML peg&#225;ndolo fuera sin resolver el modelo.

Soluciones v&#225;lidas:

1. calcular el modelo de Adjudicaci&#243;n en el layout padre;
2. crear un partial de modelo autocontenido;
3. publicar atributos request expl&#237;citos desde el action.

Evitar duplicar consultas costosas.

## Contratos que deben conservarse

- s&#243;lo prestadores con presupuesto cargado pueden seleccionarse;
- selecci&#243;n &#250;nica;
- estado readonly;
- mensajes de error;
- IDs:
  - `id_prestador_adjudicado`;
- handler:
  - `capturarPrestadorAdjudicado`;
- hidden o par&#225;metro utilizado por guardado;
- permisos de cotizaci&#243;n;
- estados permitidos.

## Criterios de aceptaci&#243;n

- &#8220;Pedidos de presupuestos&#8221; aparece primero.
- &#8220;Adjudicaci&#243;n&#8221; aparece inmediatamente despu&#233;s.
- El selector sigue habilitando &#250;nicamente prestadores con presupuesto.
- La tabla de detalle ya no contiene el fieldset Adjudicaci&#243;n.
- No se duplica la consulta de presupuestos sin justificaci&#243;n.
- Guardar cotizaci&#243;n conserva el prestador elegido.

## Commit propuesto

```text
fix(compras): mueve adjudicacion bajo presupuestos
```

---

# CQA-005  -  Ocultar Recupero en alta

## Problema

Recupero se muestra en el alta y genera confusi&#243;n.

## Decisi&#243;n

No renderizar el control visual Recupero cuando:

```text
esNuevo == true
```

Conservar:

- hidden de formulario;
- derivaci&#243;n desde Cargo tercerizadora;
- sincronizaci&#243;n JavaScript;
- validaci&#243;n backend;
- persistencia;
- visualizaci&#243;n posterior en edici&#243;n o vista, salvo nueva decisi&#243;n funcional.

## Archivo principal

```text
ext-web/docroot/html/portlet/compras/requerimientos/partials/_datos_basicos.jsp
```

Dependencias directas a verificar:

```text
ext-web/docroot/html/portlet/compras/requerimientos/partials/_form_hidden.jsp
ext-web/docroot/html/portlet/compras/requerimientos/partials/_scripts_comunes.jsp
ext-impl/src/ar/com/ospim/compras/requerimientos/action/EditarRequerimientoCompraAction.java
```

## Implementaci&#243;n m&#237;nima

Condicionar &#250;nicamente las celdas visuales.

No comentar l&#243;gica Java ni JavaScript.

No dejar celdas vac&#237;as que rompan el layout.

No eliminar el ID hidden ni renombrar el par&#225;metro.

## Criterios de aceptaci&#243;n

- Alta nueva: Recupero no aparece.
- Cambiar Cargo tercerizadora sigue actualizando el hidden.
- Guardar alta persiste el valor derivado correcto.
- Edici&#243;n y vista muestran el valor vigente, si ese comportamiento se conserva.
- El layout no deja huecos.
- Surge permanece visible y obligatorio.

## Prueba contractual requerida

- ausencia del label/control visible en rama `esNuevo`;
- presencia del hidden;
- presencia de sincronizaci&#243;n;
- conservaci&#243;n del parser backend;
- conservaci&#243;n de Surge.

## Commit propuesto

```text
fix(compras): oculta recupero en alta
```

---

# CQA-006  -  T&#237;tulos y jerarqu&#237;a de secciones

## Problema

QA identifica &#8220;Detalle del requerimiento&#8221; como un t&#237;tulo desplazado o visualmente inconsistente.

## Implementaci&#243;n propuesta

Crear clases de secci&#243;n exclusivas de Compras:

```text
compras-seccion
compras-seccion-titulo
```

Aplicarlas a:

- Datos generales;
- Afiliado;
- Detalle del requerimiento;
- Observaciones / Descripci&#243;n;
- Pedidos de presupuestos;
- Adjudicaci&#243;n.

No cambiar el color global de `legend`.

## Criterios de aceptaci&#243;n

- Todos los t&#237;tulos tienen tipograf&#237;a, espaciado y color consistentes.
- El t&#237;tulo queda unido visualmente a su contenido.
- No se modifica otro portlet.
- No se oculta contenido.
- No se sustituye fieldset/legend si eso rompe accesibilidad o CSS legacy.

## Commit propuesto

```text
style(compras): unifica titulos de seccion
```

---

# CQA-007  -  Regresi&#243;n focalizada del flujo completo

## Matriz funcional

### Alta

- crear requerimiento;
- seleccionar sector;
- verificar afiliado condicional;
- verificar Surge obligatorio;
- verificar Recupero oculto;
- agregar detalle;
- guardar.

### Edici&#243;n

- cargar requerimiento;
- verificar orden de secciones;
- modificar datos permitidos;
- modificar detalle;
- guardar;
- no alterar campos readonly.

### Env&#237;o a cotizar

- enviar a prestadores compatibles;
- verificar resumen;
- verificar destinatarios;
- verificar estados de env&#237;o;
- probar incidencia de email;
- recargar y confirmar persistencia.

### Presupuestos

- mostrar Pedidos de presupuestos;
- subir archivo;
- impedir prestador sin env&#237;o v&#225;lido;
- eliminar archivo;
- impedir duplicados incompatibles.

### Adjudicaci&#243;n

- aparecer despu&#233;s de Pedidos;
- habilitar s&#243;lo prestadores con presupuesto;
- seleccionar uno;
- guardar cotizaci&#243;n;
- recargar y conservar selecci&#243;n.

### Vista

- mantener orden;
- bloquear edici&#243;n;
- permitir revisar destinatarios seg&#250;n rol;
- permitir PDF;
- permitir acciones vigentes.

### Reclamo Prestacional

S&#243;lo smoke test del handoff existente. No modificarlo dentro de estas tareas.

## Pruebas automatizadas m&#237;nimas

```text
ComprasRequerimientosUiContractTest
ComprasSurgeSelectContractTest
ComprasDatosBasicosLayoutContractTest
nuevo contrato de layout QA
nuevo contrato de destinatarios
nuevo contrato de adjudicaci&#243;n
```

## Build local focalizado

```powershell
Set-Location -LiteralPath 'C:\devmolineros\ext'

$tests = @(
    'ext-impl/src/ar/com/ospim/test/ComprasRequerimientosUiContractTest.java',
    'ext-impl/src/ar/com/ospim/test/ComprasSurgeSelectContractTest.java'
)

$out = 'C:\devmolineros\tests-compras-bin'

Remove-Item -LiteralPath $out -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path $out | Out-Null

javac -encoding ISO-8859-1 -d $out $tests

if ($LASTEXITCODE -ne 0) {
    throw 'Fall&#243; javac.'
}

java -cp $out ar.com.ospim.test.ComprasRequerimientosUiContractTest
if ($LASTEXITCODE -ne 0) {
    throw 'Fall&#243; ComprasRequerimientosUiContractTest.'
}

java -cp $out ar.com.ospim.test.ComprasSurgeSelectContractTest
if ($LASTEXITCODE -ne 0) {
    throw 'Fall&#243; ComprasSurgeSelectContractTest.'
}
```

A&#241;adir los tests nuevos al bloque cuando existan.

---

# 7. Protocolo Git por requerimiento

## Preflight

```powershell
Set-Location -LiteralPath 'C:\devmolineros\ext'

$root = (git rev-parse --show-toplevel).Trim()
$origin = (git remote get-url origin).Trim()

if ([IO.Path]::GetFullPath($root).TrimEnd('\') -ne 'C:\devmolineros\ext') {
    throw "Ra&#237;z incorrecta: $root"
}

if ($origin -ne 'https://github.com/JerePrograma/molineros.git') {
    throw "Origin incorrecto: $origin"
}

git status --short
git branch --show-current
git rev-parse HEAD
git log -1 --oneline
git fetch origin --prune

if ((git branch --show-current).Trim() -ne 'main') {
    throw 'La rama debe ser main.'
}

if ((git rev-parse HEAD).Trim() -ne (git rev-parse origin/main).Trim()) {
    throw 'main local y origin/main no coinciden.'
}

git diff --cached --quiet

if ($LASTEXITCODE -ne 0) {
    throw 'Existen cambios staged.'
}
```

## Staging focalizado

Si un archivo contiene cambios ajenos, no usar `git add -- archivo` completo.

Usar:

```powershell
git add -p -- 'ruta/exacta/archivo'
```

Revisar el &#237;ndice:

```powershell
git diff --cached --check
git diff --cached --stat
git diff --cached --no-ext-diff --unified=30
```

No aceptar hunks de:

- Reclamo Prestacional;
- r7311/r7312;
- Liquidaciones;
- Interbanking;
- estados no solicitados;
- cambios de mensajes ajenos.

## Commit

```powershell
git commit -m "MENSAJE DEL REQUERIMIENTO"
```

## Sincronizaci&#243;n y push

```powershell
$commitCreado = (git rev-parse HEAD).Trim()

git fetch origin --prune

if (!(git merge-base --is-ancestor origin/main main)) {
    throw 'main local no contiene origin/main. No usar rebase ni force.'
}

if ((git rev-parse origin/main).Trim() -ne (git rev-parse HEAD^).Trim()) {
    git merge --no-edit origin/main

    if ($LASTEXITCODE -ne 0) {
        throw 'Conflicto al integrar origin/main.'
    }
}

git push origin main:main

if ($LASTEXITCODE -ne 0) {
    throw 'Push rechazado. No usar force.'
}

git fetch origin --prune

$local = (git rev-parse main).Trim()
$remote = (git rev-parse origin/main).Trim()

if ($local -ne $remote) {
    throw "main local y origin/main no coinciden. Local=$local Remoto=$remote"
}

git merge-base --is-ancestor $commitCreado origin/main

if ($LASTEXITCODE -ne 0) {
    throw 'El commit creado no est&#225; contenido en origin/main.'
}

git status --short --branch
```

Este bloque presupone que se aisl&#243; correctamente el commit. Con el working tree actual, no debe ejecutarse hasta revisar los hunks.

# 8. Verificaci&#243;n ISO-8859-1 por archivo

```powershell
$files = @(
    'RUTA_1',
    'RUTA_2'
)

$latin1 = [Text.Encoding]::GetEncoding(28591)
$utf8Strict = New-Object Text.UTF8Encoding($false, $true)

foreach ($file in $files) {
    $bytes = [IO.File]::ReadAllBytes(
        (Join-Path 'C:\devmolineros\ext' $file)
    )

    $bom = (
        $bytes.Length -ge 3 -and
        $bytes[0] -eq 0xEF -and
        $bytes[1] -eq 0xBB -and
        $bytes[2] -eq 0xBF
    )

    $text = $latin1.GetString($bytes)

    $mojibake = (
        $text.Contains([string][char]0x00C3) -or
        $text.Contains([string][char]0x00C2) -or
        $text.Contains([string][char]0xFFFD)
    )

    $validUtf8 = $false

    try {
        [void]$utf8Strict.GetString($bytes)
        $validUtf8 = $true
    }
    catch {
        $validUtf8 = $false
    }

    $nonAscii = @($bytes | Where-Object { $_ -gt 127 }).Count -gt 0

    [PSCustomObject]@{
        Ruta = $file
        BOM_UTF8 = $bom
        Mojibake = $mojibake
        UTF8_Con_NoASCII = ($validUtf8 -and $nonAscii)
        BlobFiltrado = (
            git hash-object --filters --path="$file" -- "$file"
        ).Trim()
    }
}
```

Condici&#243;n de aprobaci&#243;n:

```text
BOM_UTF8 = False
Mojibake = False
UTF8_Con_NoASCII = False
```

Los archivos ASCII son compatibles con ISO-8859-1 y UTF-8; registrar esa condici&#243;n expl&#237;citamente.

# 9. Qu&#233; puede hacerse remotamente y qu&#233; requiere local

## Remotamente verificable

- lectura de `main`;
- lectura de JSP/Java/tests;
- comparaci&#243;n de commits;
- identificaci&#243;n de callers;
- inspecci&#243;n de contratos;
- publicaci&#243;n de archivos nuevos exclusivamente ASCII;
- verificaci&#243;n del commit remoto;
- consulta de CI disponible.

## No verificable desde el conector

- working tree local;
- cambios SVN no publicados;
- EOL real local;
- bytes ISO-8859-1 de archivos modificados localmente;
- Ant;
- JspC/Jasper;
- Tomcat;
- navegador autenticado;
- DB local;
- env&#237;o SMTP real;
- medidas visuales;
- E2E.

Para esos puntos deben utilizarse los bloques PowerShell y pruebas manuales incluidos aqu&#237;.

# 10. Plantilla de informe por requerimiento

```text
Requerimiento:
Repositorio:
Rama:
HEAD inicial:
origin/main inicial:

Archivos inspeccionados:
Archivos modificados:
Motivo por archivo:

Defecto demostrado:
Correcci&#243;n aplicada:
Contratos preservados:

Codificaci&#243;n por archivo:
EOL por archivo:
M&#233;todo de verificaci&#243;n:

Pruebas ejecutadas:
C&#243;digo de salida:
Resultados:

Validaci&#243;n visual:
Resoluci&#243;n:
Resultado:

Commit:
Push:
SHA final main:
SHA final origin/main:

Pendientes:
Riesgos residuales:
Veredicto:
```

# 11. Veredicto de este documento

```text
PLAN DEFINIDO  -  IMPLEMENTACI&#211;N PENDIENTE POR REQUERIMIENTO
```

No corresponde afirmar &#8220;listo para producci&#243;n&#8221; hasta completar:

- todos los commits;
- build;
- despliegue;
- navegaci&#243;n autenticada;
- regresi&#243;n QA;
- verificaci&#243;n de emails y DB.

---

# 12. Megaprompt de reanudaci&#243;n

Copiar el bloque completo:

```text
Actu&#225; como arquitecto de software y desarrollador senior especializado en
Java 8, Liferay 5.2, Struts, JSP/JSPF legacy, JavaScript ES5, jQuery legacy,
PostgreSQL 9.6, accesibilidad b&#225;sica y pruebas contractuales.

Trabaj&#225; sobre el proyecto Molineros.

Repositorio obligatorio:
https://github.com/JerePrograma/molineros

Ruta local obligatoria:
C:\devmolineros\ext

Rama &#250;nica:
main

Objetivo:
implementar de forma individual y verificable los requerimientos QA del flujo
de Compras documentados en:

docs/qa/compras/2026-07-30-plan-correcciones-qa-compras.md

Modalidad obligatoria:

1. Antes de cualquier inspecci&#243;n o comando Git, ejecutar:

Set-Location -LiteralPath 'C:\devmolineros\ext'
git rev-parse --show-toplevel
git remote get-url origin

La ra&#237;z debe ser C:\devmolineros\ext y origin debe ser:
https://github.com/JerePrograma/molineros.git

2. Trabajar directamente sobre main y origin/main.
3. No crear ramas.
4. No abrir pull requests.
5. No modificar ni fusionar el PR #46.
6. No usar stash, rebase, reset destructivo, clean, restore general ni
   force-push.
7. Preservar todos los cambios locales existentes.
8. Detenerse si un archivo objetivo contiene cambios locales que no pueden
   aislarse de forma segura.
9. Analizar &#250;nicamente los archivos directos del requerimiento activo.
10. No inspeccionar Reclamo Prestacional, Liquidaciones, Interbanking,
    Struts, Tiles o SQL ajeno salvo dependencia directa demostrada.
11. Implementar un &#250;nico requerimiento por vez.
12. Crear un commit at&#243;mico por requerimiento.
13. No mezclar cambios de r7311 o r7312.
14. Mantener Java 8, JSP legacy, Liferay 5.2, JavaScript ES5 y jQuery legacy.
15. Guardar todos los archivos textuales modificados en ISO-8859-1 sin BOM.
16. Verificar por archivo:
    - BOM ausente;
    - mojibake ausente;
    - tildes y e&#241;es legibles;
    - EOL preservado;
    - blob filtrado revisado.
17. Ejecutar:
    - git diff --check;
    - diff focalizado;
    - tests contractuales directos;
    - build o compilaci&#243;n focalizada disponible.
18. No afirmar validaci&#243;n visual si no se ejecut&#243; navegador autenticado.
19. Cuando algo no pueda verificarse remotamente, entregar un bloque
    PowerShell exacto para ejecutarlo localmente.
20. Antes de publicar:
    - git fetch origin --prune;
    - integrar origin/main mediante merge si avanz&#243;;
    - nunca usar rebase;
    - repetir pruebas afectadas.
21. Publicar con:
    git push origin main:main
22. Confirmar al final que main local y origin/main tienen el mismo SHA.

Requerimientos, en orden:

CQA-001:
uniformar alturas, anchuras sem&#225;nticas, separaci&#243;n entre filas y distancia
label/control en alta, edici&#243;n y vista, sin CSS global, flexbox ni grid.

CQA-005:
ocultar Recupero &#250;nicamente en alta; conservar hidden, c&#225;lculo,
sincronizaci&#243;n, backend y persistencia.

CQA-003:
reordenar alta, edici&#243;n y vista de acuerdo con el documento.

CQA-004:
extraer Adjudicaci&#243;n de la tabla de detalle y ubicarla inmediatamente despu&#233;s
de Pedidos de presupuestos, conservando permisos, selecci&#243;n &#250;nica,
prestadores habilitados y guardado.

CQA-002:
mostrar de forma persistente email registrado, destino efectivo, estado y
motivo por prestador; implementar una acci&#243;n real Ver destinatarios; renombrar
el estado ambiguo Revisar email; no alterar el algoritmo SMTP.

CQA-006:
unificar t&#237;tulos de secci&#243;n mediante clases exclusivas de Compras.

CQA-007:
ejecutar la regresi&#243;n focalizada completa.

No implementar todos en un &#250;nico commit.

Comenz&#225; por:
1. verificar el repositorio;
2. leer el documento;
3. comprobar el estado exacto de main y del working tree;
4. seleccionar el primer requerimiento no completado;
5. implementar realmente;
6. probar;
7. commitear y publicar;
8. informar evidencia exacta.

Informe final obligatorio por requerimiento:

- repositorio y ruta;
- rama;
- HEAD inicial;
- archivos inspeccionados;
- archivos modificados;
- motivo preciso;
- codificaci&#243;n y EOL;
- pruebas y c&#243;digos de salida;
- commit;
- resultado del push;
- SHA main local;
- SHA origin/main;
- validaciones no ejecutadas;
- riesgos residuales;
- veredicto.
```
