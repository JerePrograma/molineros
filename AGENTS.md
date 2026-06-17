# Repository instructions

## Project context

This repository is a legacy Liferay application.

Primary stack:

- Java 8.
- Liferay Portal with Struts `PortletAction`.
- JSP pages with scriptlets and legacy Liferay taglibs.
- Apache Tiles.
- Tomcat 8.5.x.
- PostgreSQL.
- Ant-based or repository-provided legacy build tooling.
- Main source modules:
  - `ext-impl/src`
  - `ext-web/docroot`

Preserve the existing architecture and compatibility requirements.

Do not introduce Spring Boot, Maven, Gradle, modern frontend frameworks,
new persistence layers, or new dependencies unless the task explicitly
requires them.

## Initial inspection

Before modifying files:

1. Inspect `git status --short --branch`.
2. Inspect the relevant source, JSP, Struts, Tiles, SQL, and service files.
3. Search for all usages of affected constants, methods, actions, forwards,
   request attributes, session attributes, and JSP partials.
4. Understand the complete request flow before applying a fix:
   - JSP or portlet URL;
   - Struts action;
   - Java validation;
   - service;
   - database function;
   - render action;
   - Tiles forward;
   - resulting JSP.
5. Preserve all unrelated user changes.

Do not infer bean methods or database columns. Inspect the actual class,
SQL function, or schema before using them.

## Source and generated files

Modify source files instead of compiled or generated outputs.

Prefer:

- Java sources under `ext-impl/src`.
- JSP and web configuration under `ext-web/docroot`.

Do not hand-edit `.class`, generated metadata, compiled reports, deployment
outputs, or copied classpath resources unless the task explicitly requires
regenerating them.

Do not modify dependency manifests, generated files, or deployment
configuration merely to bypass a local build problem.

## Java compatibility

Keep all Java code compatible with Java 8 and the libraries already present
in the project.

Follow the existing legacy style when practical.

Do not use APIs from newer Java versions.

Do not suppress compilation errors with unsafe casts, ignored rules,
placeholder methods, or invented compatibility helpers.

Use existing utilities such as:

- `ConnectionHelper`
- `PermissionUtil`
- existing service utility singleton patterns
- existing `WebKeys` classes
- existing Liferay and Struts conventions

Always close JDBC resources using the repository's established helpers.

## Liferay and Struts rules

Keep Struts action paths, forwards, Tiles definitions, and JSP URLs aligned.

When adding or changing a screen, verify all corresponding pieces:

- `struts-config.xml`
- Tiles definition
- Java `PortletAction`
- `WebKeys` forward constant
- physical JSP path
- render/action URL
- request attributes

Use `PortletURL` and existing Liferay URL conventions.

Do not trust browser parameters for authorization, current state, or
ownership checks.

Before a server-side mutation, reload the current entity from the database
and validate its persisted state.

UI visibility is not a security boundary. Every permission and state rule
must also be enforced in the Java action or service.

## JSP rules

The JSP implementation is legacy and uses static includes and scriptlets.

Take special care with:

- duplicate variable declarations across static includes;
- duplicate `<portlet:defineObjects />`;
- nested forms;
- namespaced and non-namespaced parameters;
- stale request or session attributes;
- malformed scriptlets inside JSP tag attributes;
- JavaScript function names that use the portlet namespace;
- URLs losing `struts_action` after GET submission.

Do not place Java expressions containing string literals inside a
double-quoted JSP tag attribute when Jasper can parse the inner quotes
incorrectly.

Instead of:

```jsp
<c:if test="<%= SessionMessages.contains(renderRequest, "message") %>">
```

calculate the boolean before rendering:

```jsp
<%
boolean showMessage =
        SessionMessages.contains(
                renderRequest,
                "message"
        );
%>

<c:if test="<%= showMessage %>">
```

When a JSP includes `compras/init.jsp`, verify whether portlet objects and
taglibs are already declared before adding them again.

Avoid falling back to stale `PortletSession` search results when a new
request or tab-specific search should be authoritative.

## Compras workflow

The purchase-request state flow is:

1. `ESTADO_BORRADOR`
2. `ESTADO_REQUERIMIENTO`
3. `ESTADO_AUTORIZADO`
4. `ESTADO_COTIZACIONES`
5. `ESTADO_ORDEN_COMPRA`
99. `ESTADO_ANULADO`

Valid forward transitions are only:

- Borrador -> Requerimiento
- Requerimiento -> Autorizado
- Autorizado -> Cotizaciones
- Cotizaciones -> Orden de compra

Anulado is a terminal lateral state from the explicitly allowed prior
states.

A transition from a state to the same state is not valid.

`validarTransicionEstado(actual, nuevo)` must return `false` when
`actual == nuevo`.

Retrying pending provider notifications is not a state transition. It is a
separate operation allowed only while the persisted request is already in
`ESTADO_COTIZACIONES`.

Do not implement notification retry as transition `4 -> 4`.

## Compras work queues

The tabs are work queues, not alternative views of the same unrestricted
search.

Expected mapping:

- Requerimientos:
  - state 1, Borrador.
- Autorizaciones:
  - state 2, Requerimiento.
- Cotizaciones:
  - state 3, Autorizado.
- Ordenes de compra:
  - state 4, Cotizaciones.

A request must not remain visible in a previous queue after its persisted
state advances.

Verify both the forced JSP state and the AJAX parameters sent to
`/compras/buscar_requerimientos`.

## Compras permissions and actions

All action visibility must depend on both:

- the current persisted state;
- the user's corresponding role.

The Java action must enforce the same rule independently of the JSP.

### `VIEW_Compras`

May:

- list requests;
- view requests;
- print requests.

Must not modify requests or change state.

### `ABM_Compras`

May:

- create requests;
- edit and save only Borrador requests;
- send Borrador to Requerimiento;
- access provider-type configuration when that feature uses this role.

Expected state-1 action label:

- `Enviar a autorizar`

### `AUTORIZAR_Compras`

May authorize only requests in state Requerimiento.

Expected state-2 action label:

- `Autorizar`

Must not see earlier or later workflow actions unless the user also owns
the corresponding role.

### `COTIZAR_Compras`

For state Autorizado, may execute the transition to Cotizaciones.

Expected state-3 action label:

- `Enviar a cotizar`

This action performs:

- state transition 3 -> 4;
- provider notification process.

For state Cotizaciones, may separately execute:

- `Notificar prestadores pendientes`

That action does not change state.

### `ORDEN_COMPRA_Compras`

For state Cotizaciones, may execute:

- `Generar orden de compra`

This performs transition 4 -> 5.

### `ANULAR_Compras`

May annul only the states permitted by the centralized business rule.

Do not grant edit, authorization, quotation, or purchase-order permissions
merely because the user may annul.

If the existing backend intentionally permits `ABM_Compras` to annul,
keep the UI and backend rules identical and do not broaden them further.

### Multiple roles

A user with multiple roles receives the union of valid actions, but only
when the persisted state permits each action.

Never show an action from an earlier or later workflow stage merely because
the user owns its role.

## Compras button matrix

Expected detailed-view actions:

### State 1 — Borrador

With `ABM_Compras`:

- Save while editing.
- Edit while viewing.
- Enviar a autorizar.
- Anular when allowed.
- Imprimir PDF.
- Volver.

### State 2 — Requerimiento

With `AUTORIZAR_Compras`:

- Autorizar.
- Anular when independently allowed.
- Imprimir PDF.
- Volver.

Do not show:

- Editar.
- Enviar a autorizar.
- Enviar a cotizar.
- Generar orden de compra.

### State 3 — Autorizado

With `COTIZAR_Compras`:

- Enviar a cotizar.
- Anular when independently allowed.
- Imprimir PDF.
- Volver.

Do not show:

- Editar.
- Enviar a autorizar.
- Autorizar.
- Notificar prestadores pendientes.
- Generar orden de compra.

### State 4 — Cotizaciones

With `COTIZAR_Compras`:

- Notificar prestadores pendientes.

With `ORDEN_COMPRA_Compras`:

- Generar orden de compra.

With both roles, show both actions.

Also allow:

- Anular when independently permitted.
- Imprimir PDF.
- Volver.

Do not show actions from states 1, 2, or 3.

### State 5 — Orden de compra

Allow only read-only operations such as:

- Imprimir PDF.
- Volver.

### State 99 — Anulado

Allow only read-only operations such as:

- Imprimir PDF.
- Volver.

## Compras notification behavior

Moving from Autorizado to Cotizaciones triggers provider notification.

Pending-provider notification retry:

- is allowed only in state Cotizaciones;
- does not change the state;
- must not resend providers already recorded as successfully notified;
- remains protected by `COTIZAR_Compras`.

The current simplified audit represents successful notifications.

Preserve the established ordering unless a task explicitly changes the
design:

1. send email;
2. insert successful-notification audit.

Do not add delivery-status infrastructure or schema columns without an
explicit requirement.

## Database changes

Use the existing `compras` schema and existing JDBC/service patterns.

Prefer existing PostgreSQL functions where available.

Do not add or alter tables, functions, constraints, or indexes unless the
task explicitly requires a database change.

When changing SQL behavior:

- inspect all callers;
- keep PostgreSQL-version compatibility;
- preserve transaction behavior;
- preserve audit semantics;
- validate expected result columns and JDBC types.

## Scope discipline

Prefer the smallest complete change that fixes the root cause.

Do not fix only a button label when the real issue is state validation,
stale data, incorrect filtering, or authorization.

Do not redesign the whole module for a localized task.

Do not modify unrelated formatting or legacy code.

Do not commit, push, merge, tag, or open a pull request unless explicitly
requested.

## Validation

After modifying code:

1. Inspect `git diff`.
2. Run `git status --short`.
3. Discover and use the validation commands already supported by the
   repository.
4. Compile affected Java sources or modules.
5. Compile or deploy affected JSP/web resources using the existing project
   workflow.
6. Check for Jasper JSP compilation errors.
7. Run available tests.
8. Search again for obsolete or contradictory implementations.
9. Verify that only task-related files changed.

For Compras workflow changes, verify at minimum:

- same-state transitions are rejected;
- 1 -> 2 is accepted;
- 2 -> 3 is accepted;
- 3 -> 4 is accepted;
- 4 -> 5 is accepted;
- backward transitions are rejected;
- states 5 and 99 have no outgoing transitions;
- notification retry works separately in state 4;
- each role sees only state-compatible actions;
- direct requests without permission are rejected by Java;
- list tabs contain only their assigned state.

Do not report a command as successful unless it actually completed
successfully.

Report:

- exact commands executed;
- exact result of each command;
- files modified;
- root cause;
- any pre-existing failures;
- any validation that could not be run and why.

## Ponytail

Ponytail is the default implementation policy for every coding task in this
repository.

- Apply Ponytail before designing or writing code.
- Use Ponytail in `ultra` mode by default.
- Prefer deletion, reuse, standard-library features, platform-native
  capabilities, and the smallest complete implementation.
- Do not disable or bypass Ponytail unless the user explicitly requests it.
- Ponytail must never be used to skip validation, error handling, security,
  authorization, accessibility, data-loss protection, or required tests.
- Before completing a coding task, apply the equivalent of
  `@ponytail-review` to the final diff.