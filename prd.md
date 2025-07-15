# 📝 Product Requirements Document (PRD) — App de Rutinas y Progreso de Entrenamiento

## 🧠 Objetivo

Desarrollar una aplicación Android nativa para registrar y planificar mis entrenamientos personales. La app debe permitirme crear y registrar ejercicios con imágenes, videos o links de guía; definir series, repeticiones, peso, tiempos, kilómetros, etc.; agrupar ejercicios en rutinas, reproducir esas rutinas paso a paso y registrar métricas de progreso a lo largo del tiempo. También quiero anotar observaciones después de cada rutina o ejercicio.

---

## 👤 Usuarios

- Yo mismo (uso personal, sin login ni multiusuario)

---

## 🔧 Funcionalidades principales

### 🏋️‍♂️ Ejercicios
- CRUD de ejercicios
- Campos por ejercicio:
  - Nombre
  - Descripción
  - Grupo(s) muscular(es)
  - Tipo de serie: repeticiones / tiempo / distancia (km)
  - Peso (si aplica)
  - Intensidad (baja / media / alta)
  - Fotos, videos o links de guía
- Registro histórico de progresos (peso, repeticiones, tiempo, km)

### 🔄 Rutinas
- CRUD de rutinas
- Una rutina agrupa múltiples ejercicios en orden
- Posibilidad de definir:
  - Series por ejercicio
  - Descanso entre ejercicios
  - Descanso entre series
  - Tiempo de preparación inicial
  - Notas personalizadas
- Indicación automática de intensidad total de la rutina (promedio o ponderada)
- Registro del tiempo total al completar una rutina

### ▶️ Play de rutina (ejecución guiada)
- Pantalla paso a paso con:
  - Ejercicio actual, con foto/video
  - Cronómetro para tiempo de serie o descanso
  - Señales auditivas (ej: inicio, fin de serie, fin de descanso)
  - Funciona en segundo plano (ej: mientras escucho música o veo YouTube)
- Posibilidad de pausar / saltar / repetir ejercicios
- Registro automático del tiempo total al finalizar

### 📈 Métricas
- Progreso histórico por ejercicio (peso, repeticiones, etc.)
- Tiempo total de entrenamiento por día / semana / mes
- Evolución por grupo muscular
- Diario personal con notas después de rutinas
- Estadísticas de intensidad y duración de rutinas

---

## 🚫 Lo que **no** debe incluir por ahora

- Sistema de login/autenticación
- Integración con APIs externas
- Sin funcionalidades colaborativas

---

## 📐 Pantallas esperadas

1. **Inicio**: métricas generales + botón rápido para iniciar rutina
2. **Rutinas**: lista de rutinas, play, crear/editar
3. **Ejercicios**: CRUD de ejercicios, búsqueda por grupo muscular
4. **Play de rutina**: pantalla cronómetro + media del ejercicio actual
5. **Métricas**: progresos, evolución, tiempos, intensidad
6. **Notas**: listado de anotaciones post-rutina o ejercicio
7. **Configuración**: sonidos, tiempo entre series, unidad de medida, etc.

---

## 📊 Métrica de éxito

- Si puedo registrar mi progreso con facilidad
- Si la guía de rutina es usable y funcional en segundo plano
- Si tengo datos útiles para revisar mi evolución

---

## 💻 Requisitos técnicos

- Aplicación Android nativa
- Backend en Kotlin o Java (opcional)
- Base de datos local (Room, SQLite o similar)
- Jetpack Compose para la UI
- Sonidos custom para alertas
- Ejecución en segundo plano
- Acceso a cámara/galería para subir fotos/videos opcionalmente

---

## 🧩 Integraciones

- Ninguna por ahora
