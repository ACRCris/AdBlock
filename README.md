# AdBlock (Android)

Aplicación Android tipo **firewall local + VPN** para control de conectividad por app, enfocada en bloquear o permitir tráfico de aplicaciones de usuario con una experiencia simple y clara.

> Estado: proyecto en desarrollo activo (UI, seguridad, robustez y cobertura de tests en evolución).

---

## Tabla de contenido

- [1. Objetivo del proyecto](#1-objetivo-del-proyecto)
- [2. Características principales](#2-características-principales)
- [3. Alcance funcional](#3-alcance-funcional)
- [4. Arquitectura y estructura](#4-arquitectura-y-estructura)
- [5. Stack tecnológico](#5-stack-tecnológico)
- [6. Testing](#9-testing)
- [7. Permisos y consideraciones Android 14/15/16](#10-permisos-y-consideraciones-android-141516)
---

## 1. Objetivo del proyecto

Construir una app Android que permita:

1. Visualizar apps instaladas del usuario.
2. Definir reglas de bloqueo/permitido por aplicación.
3. Aplicar reglas en ejecución mediante `VpnService` (firewall local sin root).
4. Mantener una UX simple para operación diaria.
5. Mantener una base robusta para pruebas automáticas y hardening de seguridad.

---

## 2. Características principales

- Listado de apps instaladas (priorizando apps de usuario).
- Activación/desactivación de bloqueo por app.
- Servicio VPN local para enforcement de reglas.
- Estado de servicio (iniciado, detenido, error).
- Pantalla informativa de “Cómo funciona”.
- Política de privacidad integrada en flujo de información.
- Diseño con navegación inferior consistente.

---

## 3. Alcance funcional

### Incluye
- Gestión de reglas por app.
- Inicio/parada de servicio VPN.
- UI principal para administración rápida.
- Mensajería/feedback de estado.
- Secciones de ayuda e información de uso.

### No incluye (por ahora)
- Bloqueo por dominio granular tipo DNS/proxy avanzado.
- Analíticas remotas.
- Sincronización cloud de reglas.
- Modo multi-perfil complejo.

---

## 4. Arquitectura y estructura

Estructura general del repositorio:

- `app/`
  - `src/main/`
    - `java/com/copiloto/addblock/`
      - `ui/`  
        - `screens/` (pantallas Compose)
        - `components/` (componentes reutilizables)
        - `viewmodel/` (estado y lógica de presentación)
      - `vpn/`  
        - `FirewallVpnService` (orquestación del túnel y reglas)
        - `VpnController` (control de start/stop/restart)
      - `data/` (repositorio/modelos de reglas, utilidades)
      - `utils/` (helpers del sistema, paquetes instalados, etc.)
    - `res/` (recursos XML/drawables/strings)
- `docs/`
  - documentos de diseño, seguridad y análisis técnico
- `build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`

### Capas (visión conceptual)

1. **UI (Compose)**
   - Renderiza estado.
   - Emite eventos de usuario.
2. **ViewModel**
   - Maneja estado de pantalla.
   - Orquesta acciones (cargar apps, toggle, iniciar/parar VPN).
3. **Dominio/Control**
   - Regla de negocio (qué app bloquear/permitir).
   - Coordinación con servicio VPN.
4. **Infraestructura Android**
   - `VpnService`, notificaciones FGS, PackageManager, permisos.

---

## 5. Stack tecnológico

- **Lenguaje:** Kotlin (y partes Java existentes en transición/mantenimiento).
- **UI:** Jetpack Compose + Material 3.
- **Arquitectura:** MVVM.
- **Build:** Gradle Kotlin DSL.
- **Pruebas:**
  - JUnit
  - Mockito
  - Robolectric
  - Instrumentation tests para Compose UI
- **Plataforma objetivo:** Android moderno (`targetSdk` alto, incluyendo escenarios API 34+ / 36).


## 6. Testing

La estrategia recomendada cubre tres niveles:

### 6.1 Unit tests (rápidos)
Valida lógica pura:
- transformación de estado
- reglas de filtrado de apps
- cálculo de acciones en ViewModel

### 6.2 Robolectric (framework Android sin dispositivo)
Valida:
- comportamiento de componentes Android en JVM
- interacciones con ciclo de vida básico
- edge cases de recursos/contexto

### 6.3 Instrumentation + Compose UI
Valida:
- navegación real
- render y semántica de componentes
- interacción de usuario (clicks/toggles)
- consistencia de estado visible

> Objetivo: prevenir regresiones como “el toggle no refleja cambio en primer click”, “scroll salta al inicio” o fallos de render por keys duplicadas.

---

## 7. Permisos y consideraciones Android 14/15/16

### VPN
La app usa `VpnService`, por lo que requiere consentimiento explícito del usuario (`VpnService.prepare`).

### Foreground Service (FGS)
En target SDK altos (34+ / 36), el servicio en foreground debe:
1. arrancar con `startForegroundService(...)`
2. llamar pronto a `startForeground(...)`
3. declarar **tipo de FGS** adecuado en manifest y/o llamada API, según versión

Si falta tipo, puede aparecer:
- `MissingForegroundServiceTypeException`
- `ForegroundServiceDidNotStartInTimeException`

### Nota importante
`FOREGROUND_SERVICE_SPECIAL_USE` **no** es automático por usar target 36.  
Solo aplica si realmente declaras tipo `specialUse`.
