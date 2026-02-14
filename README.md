# Space Traveler 🚀

Pequeña aplicación Android para la gestión de misiones espaciales, diseñada con un enfoque en arquitectura limpia, persistencia local y sincronización offline.

## 🛠️ Arquitectura y Tecnologías
- **Arquitectura:** Clean Architecture + MVVM.
- **UI:** Jetpack Compose con Material 3.
- **DI:** Hilt (Dagger).
- **Persistencia:** Room Database (Single Source of Truth).
- **Red:** Retrofit + OkHttp.
- **Sincronización:** Sistema de cola de operaciones offline (`OfflineOperations`).

## 📡 Configuración del Servidor (Mockoon)
Para que la aplicación funcione correctamente con la API, se recomienda usar **Mockoon**:
1. Descarga e instala [Mockoon](https://mockoon.com/).
2. Importa o crea un entorno en el puerto `3000`.
3. Asegúrate de tener configurados los siguientes endpoints:
   - `GET /missions`: Retorna la lista de misiones.
   - `POST /missions`: Retorna status 201 (necesario para evitar el error 405).
   - `GET /missions/:id`: Retorna el detalle de una misión.

## 🚀 Pasos para ejecutar el proyecto
1. Abrir el proyecto en Android Studio Ladybug (2024.2.1) o superior.  
2. Asegurarse de que el servidor Mockoon esté corriendo en el puerto `3000`.  
3. Si usas un emulador, la `BASE_URL` ya está configurada como `http://10.0.2.2:3000/` en el `build.gradle.kts` del módulo app.  
4. Sincronizar Gradle y ejecutar la aplicación.
