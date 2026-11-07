# Día a día · Examen Serie II

App Android en **Kotlin** para organizar pendientes de trabajo sin internet ni cuentas.

## Requisitos del proyecto

- Lista principal con RecyclerView y un adaptador.
- Pantallas independientes para lista, formulario y detalle, conectadas mediante Intents explícitos.
- Agregar una tarea, consultar su descripción y fecha, cambiar su estado y eliminarla.
- Persistencia local con SharedPreferences.
- Un commit por avance funcional.

## Abrir y ejecutar

1. En Android Studio, seleccionar **Open** y abrir la carpeta del proyecto.
2. Esperar a que finalice la sincronización de Gradle. La primera vez requiere internet para descargar las herramientas; la app funciona sin conexión.
3. Instalar Android SDK Platform 37 si Android Studio lo solicita. Usar el JDK 25 incluido con Android Studio.
4. Elegir un emulador o teléfono con Android 7.0 o posterior y pulsar **Run ▶** sobre el módulo `app`.

Herramientas del proyecto inicial: Android Gradle Plugin 9.4.1, Gradle 9.6.0 y JDK 25. SDK de compilación y destino: 37. Mínimo: API 24. Kotlin viene integrado en Android Gradle Plugin 9.

El SDK local se configura en `local.properties`, que no se publica. No se necesitan contraseñas, servicios externos ni permisos de internet.

## Uso

- **Nueva tarea:** título de hasta 80 caracteres y descripción de hasta 500. Ambos son obligatorios; se rechazan campos vacíos o con solo espacios.
- **Detalle:** tocar la tarjeta para leer la descripción completa, el estado y la fecha de creación.
- **Completar/reabrir:** usar el checkbox de la lista o el botón del detalle. Una tarea completada se muestra tachada y con una etiqueta verde.
- **Eliminar:** usar la papelera de la lista o el botón del detalle y confirmar la eliminación.
- **Filtros:** Todas, Pendientes y Completadas. Al guardar una tarea se activa Todas para mostrarla inmediatamente.
- Los cambios se guardan en el dispositivo. Cerrar la app conserva las tareas. Desinstalarla o borrar sus datos las elimina.

## Organización

| Archivo | Responsabilidad |
| --- | --- |
| `MainActivity.kt` | Lista principal, filtros y resumen del progreso. |
| `AddTaskActivity.kt` | Formulario, validación y devolución del identificador de la nueva tarea. |
| `TaskDetailActivity.kt` | Consulta, cambio de estado y eliminación de la tarea recibida. |
| `data/Task.kt` | Modelo con ID único, título, descripción, fecha y estado. |
| `data/TaskRepository.kt` | Persistencia de tareas en JSON mediante SharedPreferences. |
| `ui/TaskAdapter.kt` | ListAdapter, ViewHolder y DiffUtil para RecyclerView. |
| `ui/TaskContract.kt` | Clave compartida para pasar el ID mediante Intent.putExtra. |
| `ui/BaseActivity.kt` | Ajuste del contenido a las barras del sistema y al teclado. |

Las pantallas reciben el ID de la tarea y consultan sus datos actuales en el repositorio. La lista se actualiza al regresar del formulario o del detalle. El formulario conserva sus campos al recrearse la pantalla y confirma antes de descartar un borrador.

## Compilación y pruebas

Desde la terminal de Android Studio, con JDK 25:

```powershell
.\gradlew.bat :app:assembleDebug :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

El segundo comando necesita un emulador iniciado o un teléfono conectado. Las pruebas usan datos de prueba y limpian las tareas de la app instalada; ejecutarlas en un emulador de desarrollo.

APK: `app/build/outputs/apk/debug/app-debug.apk`.

Resultados de pruebas: `app/build/reports/androidTests/connected/debug/index.html`.

Revisión de Android: `app/build/reports/lint-results-debug.html`.

Las siete pruebas cubren persistencia, IDs independientes, validación, navegación, estados, eliminación con cancelación, recreación del formulario y alta desde un filtro vacío. Ver [validación](docs/VALIDACION.md).

## Entrega

- [Repositorio en GitHub](https://github.com/KazuMJR/Examen_SerieII_AndroidStudio).
- Historial de avances: `git log --oneline --reverse`.

Referencias: [RecyclerView](https://developer.android.com/develop/ui/views/layout/recyclerview), [SharedPreferences](https://developer.android.com/training/data-storage/shared-preferences) e [Intents con datos](https://developer.android.com/guide/components/activities/parcelables-and-bundles).
