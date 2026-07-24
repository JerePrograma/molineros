# Flujo de implementacion

1. Ejecutar verificaciones Git iniciales.
2. Preservar cambios locales.
3. Inspeccionar alcance directo.
4. Mantener contratos legacy y protecciones de seguridad.
5. Editar explicitamente en ISO-8859-1 sin BOM.
6. Ejecutar validaciones focalizadas.
7. Revisar `git diff --check`, estado, estadistica y diff por ruta.
8. Verificar BOM, mojibake, tildes y enes.
9. Agregar rutas exactas con `git add --`.
10. Crear commit detallado en espanol.
11. Hacer `git fetch origin --prune`.
12. Integrar avances remotos con merge, nunca rebase.
13. Repetir pruebas afectadas despues del merge.
14. Publicar `git push origin main:main`.
15. Verificar SHA y estado final.

No mezclar cleanup, modernizacion ni defectos ajenos.
