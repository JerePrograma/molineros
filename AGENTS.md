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

1. `ESTADO_PENDIENTE`
2. `ESTADO_A_COTIZAR`
3. `ESTADO_COTIZADO`
4. `ESTADO_RECLAMO_RP`, recognized as `RECLAMO (RP)` and read-only.
5. `ESTADO_ORDEN_COMPRA`, recognized as `ORDEN DE COMPRA` and read-only.
99. `ESTADO_ANULADO`

Visible state descriptions are centralized and must be exactly:

- `PENDIENTE`
- `A COTIZAR`
- `COTIZADO`
- `RECLAMO (RP)`
- `ORDEN DE COMPRA`
- `ANULADO`

The only active forward transitions are:

- Pendiente -> A cotizar
- A cotizar -> Cotizado

Anulado is a terminal lateral state allowed only from Pendiente or A cotizar.

A transition from a state to the same state is not valid.

`validarTransicionEstado(actual, nuevo)` must return `false` when
`actual == nuevo`.

There are no active transitions to or from Reclamo (RP) or Orden de compra.

Retrying provider notifications is not a state transition. It is a separate
operation allowed only while the persisted request is in `ESTADO_A_COTIZAR`.

Do not restore Borrador, Requerimiento as an intermediate state, the old
Cotizaciones state, authorization, or purchase-order generation.

## Compras work queues

The tabs are work queues, not alternative views of the same unrestricted
search.

Expected mapping:

- Pendientes:
  - state 1, Pendiente.
- A cotizar:
  - state 2, A cotizar.
- Cotizados:
  - state 3, Cotizado.

The state catalog and unrestricted state filter must still include all six
recognized states, including the read-only historical states 4, 5, and 99.

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

- create requests in Pendiente;
- edit and save request structure only in Pendiente;
- manage articles and provider-type configuration when those features use
  this role.

### `COTIZAR_Compras`

For state Pendiente, may execute:

- `Enviar a cotizar`

This operation notifies eligible providers and performs transition 1 -> 2.

For state A cotizar, may:

- retry pending or failed provider notifications without changing state;
- edit quotation values independently from the request structure;
- select one successfully notified provider per detail;
- save quotation progress; the service automatically performs transition
  2 -> 3 when every active detail is complete and valid.

Budgets and quotation data are distinct capabilities:

- budget documents may be modified only in A cotizar;
- quotation prices and awarded providers may be modified only in A cotizar;
- structural fields and details remain blocked in A cotizar.

### `ANULAR_Compras`

May annul only Pendiente or A cotizar requests.

Do not grant edit or quotation permissions merely because the user may annul.

### Multiple roles

A user with multiple roles receives the union of valid actions, but only
when the persisted state permits each action.

Never show an action from an earlier or later workflow stage merely because
the user owns its role.

## Compras button matrix

Expected detailed-view actions:

### State 1 — Pendiente

With `ABM_Compras`:

- Save while editing.
- Edit while viewing.
- Manage the request structure.
- Imprimir PDF.
- Volver.

With `COTIZAR_Compras`:

- Enviar a cotizar.

With `ANULAR_Compras`:

- Anular.

### State 2 — A cotizar

With `COTIZAR_Compras`:

- Notificar prestadores pendientes.
- Guardar cotización.
- Modify budget documents.

With `ANULAR_Compras`:

- Anular.

The request structure and structural details are read-only.

### State 3 — Cotizado

Allow only read-only operations such as:

- View quotation results.
- View budget documents.
- Imprimir PDF.
- Volver.

### State 4 — Reclamo (RP), read-only

Allow search, view, existing-document download, PDF printing, and return only.
Do not expose mutation or transition actions.

### State 5 — Orden de compra, read-only

Allow search, view, existing-document download, PDF printing, and return only.
Do not expose purchase-order or transition actions.

### State 99 — Anulado

- Imprimir PDF.
- Volver.

## Compras quotation and provider rules

- Request structure is editable only in Pendiente.
- Quotation prices and awarded providers are editable only in A cotizar.
- Budgets are modifiable only in A cotizar.
- Saving a quotation automatically changes it to Cotizado only when every
  active detail has a valid amount and an awarded provider whose notification
  for the same request is persisted as ENVIADO.
- Each detail has its own awarded provider.
- The awarded provider must belong to the request notification set.
- The provider notification must be in ENVIADO state before adjudication.
- Cotizado, Reclamo (RP), Orden de compra, and Anulado are read-only.
- Reclamo and purchase-order transition functionality are inactive.

## Compras notification behavior

Moving from Pendiente to A cotizar triggers provider notification.

Pending-provider notification retry:

- is allowed only in state A cotizar;
- does not change the state;
- must not resend providers already recorded as ENVIADO;
- must not reserve the same provider simultaneously in two processes;
- remains protected by `COTIZAR_Compras`.

Notification reservation and finalization are separate:

1. reserve atomically as PROCESANDO;
2. send email;
3. finalize as ENVIADO, ERROR, or EMAIL_INVALIDO.

Do not add authorization, purchase-order, delivery-status, or unrelated
infrastructure without an explicit requirement.

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
- backward transitions are rejected;
- transitions to states 4 and 5 are rejected;
- states 3, 4, 5, and 99 have no outgoing transitions;
- notification retry works separately in state 2;
- structure is editable only in state 1;
- quotation and budgets are editable only in state 2;
- each awarded provider is ENVIADO for the same request;
- each role sees only state-compatible actions;
- direct requests without permission are rejected by Java;
- list tabs contain only states 1, 2, and 3 in their assigned queues.

Do not report a command as successful unless it actually completed
successfully.

Report:

- exact commands executed;
- exact result of each command;
- files modified;
- root cause;
- any pre-existing failures;
- any validation that could not be run and why.

<!-- BEGIN MANAGED: MOLINEROS CODEX POLICY -->

## Additional Codex policy

### Repository safety

* Inspect `git status --short --branch` before modifying files.
* Preserve all unrelated and pre-existing user changes.
* Do not restore, reset, delete, move, rename, overwrite, or stash unrelated
  work.
* If `git apply --check` fails, do not force the patch. Use it only as a
  reference and compare its intended changes against the current code.
* Do not commit, push, merge, tag, open a pull request, or perform remote
  changes unless explicitly requested.

### Ponytail

* Ponytail may be activated automatically when its use is appropriate for the
  task.
* Select the level according to scope, risk, reversibility, and certainty about
  the existing implementation.
* Use `lite` for normal repository work where minimalism is useful but the task
  still requires broad inspection or validation.
* `full` may be activated automatically for localized changes with a clear root
  cause, bounded scope, established repository patterns, and well-defined
  validation.
* `ultra` may be activated automatically for very small, mechanical,
  reversible, and low-risk changes where the complete correct solution is
  unambiguous.
* Do not select a stronger Ponytail level merely to minimize the number of files,
  lines, tests, or validation commands.
* Downgrade from `ultra` to `full` or `lite` when repository inspection reveals
  hidden callers, cross-layer behavior, ambiguous ownership, legacy build
  constraints, or broader regression risk.
* Ponytail may remain active for security-sensitive or integrity-sensitive
  tasks, but it must never simplify, omit, or weaken:

  * authentication or authorization;
  * persisted ownership checks;
  * state-transition validation;
  * trust-boundary validation;
  * data integrity or persistence;
  * migrations or SQL correctness;
  * document or attachment ownership;
  * auditability;
  * financial calculations;
  * accessibility;
  * error handling that prevents data loss;
  * necessary regression coverage;
  * final supported validation.
* In those areas, Ponytail controls implementation economy only. Security,
  correctness, evidence, and completeness always take priority over diff size.
* `full` and `ultra` do not authorize skipping repository inspection,
  supported builds, relevant tests, or the final requested report.
* The user may explicitly request any Ponytail level, but Codex may choose a
  less aggressive level when the discovered risk makes the requested level
  incompatible with a complete and correct implementation.

### Legacy validation environment

* Use an installed Java 8 JDK for repository validation.
* Set `JAVA_HOME` only for the validation process or command.
* Do not persist machine-specific JDK paths in tracked files.
* Preserve the existing encoding and line endings of legacy source files.
* Review the diff for mojibake, mass encoding changes, and line-ending churn.

When backend and web validation apply, use this order:

1. `ant -f ext-service/build.xml compile`
2. `ant -f ext-impl/build.xml compile`
3. `ant -f ext-web/build.xml compile`
4. `ant -f ext-web/build.xml merge`

* Investigate a failed step before continuing or retrying.
* Report the initial failure separately from the definitive result.
* `ext-web compile` does not prove that all JSPs compiled unless JSPC actually
  ran.
* `ext-web merge` validates webapp assembly but does not replace JSPC or a
  runtime Tomcat check.

### JSP and Tomcat validation

* Do not report `compile-tomcat` as successful by forcing
  `app.server.tomcat.version=6.0` against Tomcat 8.5.
* Such an override is diagnostic only.
* Do not claim JSP compilation succeeded unless Jasper/JspC completed
  successfully.
* If JSPC is blocked by the legacy build or classpath, report:

  * the exact command;
  * the root exception;
  * the classpath or build-target limitation;
  * which validations completed successfully;
  * which runtime or manual validation remains pending.
* Do not change shared build files or the Tomcat classpath outside task scope
  merely to make validation pass.

### Security regression evidence

For changes involving permissions, states, details, documents, attachments,
or persisted data:

* Reload persisted entities before authorization, ownership, editability, or
  transition checks.
* Do not trust browser-supplied IDs, folders, names, titles, states, or entity
  relationships.
* Validate authorization before loading sensitive data, issuing save tokens,
  or performing mutations.
* Keep service-level protection when the service has callers other than the
  current Action.
* Cover the actual bypass or defect with a reproducible regression check.

Relevant cases include:

* a valid detail ID belonging to another requirement;
* an attachment associated with another folder or requirement;
* render and mutation attempts without the required role;
* same-state and backward transitions;
* attempted transitions from terminal states;
* token issuance before authorization and editability checks.

When automated coverage is not practical in the legacy architecture:

1. explain the concrete limitation;
2. document the exact manual scenario;
3. record the observed result;
4. identify the remaining coverage gap.

Do not present an indirect utility test as equivalent security coverage.

### Diagnostic checks

* Temporary stubs, substitute sources, improvised mocks, partial compilation,
  and incomplete classpaths are diagnostic only.
* Run them outside the repository or in a temporary directory.
* They must not modify tracked files.
* They do not replace the supported repository build or final validation.

### Final validation and reporting

After modifying files:

* Run `git diff --check`.
* Review `git diff -- <changed files>`.
* Run relevant searches for alternate mutation paths or bypasses.
* Run the applicable repository validation commands.
* Record exact commands and final results.
* Distinguish:

  * successful validation;
  * introduced failure;
  * pre-existing failure;
  * environment limitation;
  * diagnostic validation;
  * validation not run.

Group progress updates around material findings, design decisions, blockers,
and validation results. Do not narrate every mechanical command or retry.

<!-- END MANAGED: MOLINEROS CODEX POLICY -->
