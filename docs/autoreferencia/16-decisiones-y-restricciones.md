# Decisiones y restricciones

| Decision | Razon | Verificacion |
|---|---|---|
| Trabajar en `main` | Flujo operativo definido por el proyecto. | `git branch --show-current`. |
| Push directo a `origin/main` | Evitar ramas auxiliares no solicitadas. | SHA local/remoto. |
| Cambio minimo | Reducir regresiones legacy. | Diff focalizado. |
| ISO-8859-1 sin BOM | Compatibilidad del repositorio. | Inspeccion de bytes y mojibake. |
| Java 8 y ES5 | Plataforma antigua. | Revisar APIs y sintaxis. |
| Preservar contratos | Struts/JSP dependen de nombres exactos. | Buscar productores y consumidores. |
| No auditoria general | Evitar ruido y cambios colaterales. | Alcance declarado. |
| Preservar seguridad | Nonce, permisos y validaciones protegen flujos. | Tests negativos y revision. |
| Sin refactor no solicitado | Riesgo desproporcionado. | Diff sin cleanup. |
