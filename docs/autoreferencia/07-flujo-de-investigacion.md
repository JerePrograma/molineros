# Flujo de investigacion

1. Verificar raiz, remoto, rama, HEAD y cambios locales.
2. Sincronizar `main` con `origin/main` mediante fast-forward.
3. Tomar como alcance primario los archivos nombrados.
4. Identificar entradas, salidas y contratos del caso.
5. Inspeccionar solo includes, callers, actions, forwards y tests directos.
6. Reproducir o razonar el defecto con evidencia.
7. Separar causa raiz, sintomas y warnings no causales.
8. Formular el cambio minimo.
9. Registrar incertidumbres y archivos no inspeccionados.
10. No ampliar a Struts/Tiles si el defecto no cruza navegacion o renderizado.

Ejemplo: para un boton faltante en Reclamo Prestacional, revisar primero la JSP que decide el modo, el contexto Compras y el fragmento que renderiza la botonera. Solo revisar Struts si la action o el forward participan en la perdida del modo.
