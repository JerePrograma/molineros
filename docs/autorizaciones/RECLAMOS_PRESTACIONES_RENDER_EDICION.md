# Reclamos Prestacionales: renderizado de carga y edición

## Objetivo

Documentar la causa, la decisión técnica y la validación de la corrección que unifica los PR #22, #23 y #24 sobre la sección **Datos de la Prestación**.

## Problema observado

La vista utilizaba `esBorradorCompras` para decidir si debía mostrar:

- el formulario de edición (`datos_edicion_prestacion`); o
- el formulario de carga manual (`datos_prestacion_ingreso`).

`esBorradorCompras` sólo describe el origen y la vigencia del contexto de Compras. No prueba que exista una prestación activa en edición.

El JSP `datos_edicion_prestacion.jsp` consume `WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION` y elimina el atributo de sesión durante el render. Por eso el contexto de Compras puede seguir vigente después de que el objeto de edición ya no exista.

La combinación anterior permitía este estado inválido:

1. editor visible;
2. editor sin contenido porque no existe prestación activa;
3. carga manual oculta porque `esBorradorCompras` continúa en `true`.

Además, la precarga iniciada directamente desde Compras puede no pasar por `EditarPrestacionReclamoAction`. En ese recorrido no siempre existe el atributo request `tipoEdicion`, aunque el editor lo utiliza para distinguir edición común, autorización y rechazo.

## Revisión de las alternativas

### PR #22

Acierta al reemplazar `esBorradorCompras` por la existencia real de `PRESTACION_EN_PROCESO_DE_EDICION`.

Limitación: no contempla el `tipoEdicion` ausente en la apertura directa desde Compras.

### PR #24

Aplica la misma decisión funcional que #22, con nombres más explícitos y lectura previa al include que consume el atributo.

Se toma como base de la solución final.

### PR #23

Detecta correctamente que la apertura desde Compras necesita un valor por defecto `tipoEdicion=0`.

No se adopta su estrategia de asignar temporalmente el estado de edición a `esBorradorCompras`, porque mezcla dos conceptos diferentes y obliga a guardar/restaurar una variable cuyo significado debe permanecer estable durante toda la vista.

## Solución final

### 1. Visibilidad gobernada por el estado real

`view_reclamo_prestaciones.jspf` consulta, antes de incluir el editor:

```java
session.getAttribute(
    WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION
) != null
```

Ese resultado controla exclusivamente los dos contenedores:

| Estado real | Editor | Carga manual |
|---|---:|---:|
| Existe prestación activa | visible | oculta |
| No existe prestación activa | oculto | visible |

La lectura debe realizarse antes del `liferay-util:include`, porque el JSP incluido elimina el atributo de sesión al consumirlo.

### 2. Contexto defensivo para edición común

`view_reclamo.jspf` aporta temporalmente `tipoEdicion=0` únicamente cuando:

- existe una prestación activa; y
- ningún Action colocó previamente `tipoEdicion`.

Si un Action informó `1` (autorización) o `2` (rechazo), el valor se conserva sin cambios.

El atributo temporal se elimina después del include. No se modifica `esBorradorCompras`.

## Invariantes preservadas

- `esBorradorCompras` conserva su significado de origen/contexto de Compras.
- La edición, autorización y rechazo abiertos por `EditarPrestacionReclamoAction` conservan su `tipoEdicion`.
- La carga manual vuelve a estar disponible cuando el objeto de edición ya fue consumido.
- No se modifican Actions, servicios, DAO, modelos, Struts, Tiles ni JavaScript externo.
- El JSPF legacy modificado en #24 conserva su contenido y codificación propios de la rama.

## Contrato automático

`ReclamoPrestacionalInitialViewContractTest` verifica que:

- la visibilidad dependa de `PRESTACION_EN_PROCESO_DE_EDICION`;
- `esBorradorCompras` no vuelva a controlar los contenedores;
- `tipoEdicion=0` se establezca antes del include;
- el valor temporal se retire después del include;
- el ensamblador no reasigne `esBorradorCompras`.

## Smoke test obligatorio en Liferay/Tomcat

1. Alta normal desde Autorizaciones: carga manual visible y editor oculto.
2. Alta desde Compras con prestación precargada: editor visible en modo edición común.
3. Guardar la prestación: listado actualizado y carga manual nuevamente visible.
4. Recargar después de consumir la precarga: no debe aparecer un editor vacío.
5. Editar una prestación existente: editor visible con datos correctos.
6. Autorizar: textos, botones y estilo correspondientes a `tipoEdicion=1`.
7. Rechazar: textos, observación y estilo correspondientes a `tipoEdicion=2`.
8. Cancelar edición: editor oculto y carga manual visible.
9. Verificar Código Presentado/nomenclador y Medicamento/Troquel según sector y tipo de pedido.
10. Revisar logs por errores Jasper, `NullPointerException` o forwards inexistentes.

## Despliegue y rollback

El cambio es de renderizado JSP y contrato textual. No requiere migración de base de datos.

Para rollback, revertir el commit de integración completo. No debe revertirse sólo uno de los dos ajustes funcionales, porque la visibilidad real y el contexto defensivo resuelven fallas distintas del mismo recorrido.
