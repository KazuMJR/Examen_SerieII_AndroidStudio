# Validación

Revisado el 7 de noviembre de 2026.

- Compilación de APK debug y APK de pruebas.
- Android Lint sin errores. Queda un aviso informativo sobre una versión más nueva de Android Gradle Plugin; se conserva la versión 9.4.1 del proyecto inicial.
- Pruebas instrumentadas en Medium Phone, Android API 37.
- Revisión visual de la lista vacía, formulario, lista con tarea, detalle, estado completado y confirmación de eliminación.
- API mínima declarada: 24. La revisión de compatibilidad se realiza con Lint; no se ejecutó un dispositivo API 24.

## Casos automatizados

1. Crear, completar, reabrir y eliminar tareas usando nuevas instancias del repositorio.
2. Mantener IDs y estados independientes para dos tareas con el mismo título.
3. Rechazar títulos compuestos solo por espacios.
4. Rechazar descripciones que superen 500 caracteres.
5. Recorrer las tres pantallas, validar el formulario vacío, consultar fecha y descripción, completar, filtrar, reabrir y cancelar/confirmar eliminación.
6. Conservar el borrador al recrear el formulario y permitir cancelar o confirmar su descarte.
7. Mostrar la nueva tarea al agregarla desde el filtro Completadas y eliminar desde el detalle.

Los datos se guardan mediante SharedPreferences privadas. No se solicitan permisos de red y las copias de seguridad de estas preferencias están excluidas.
