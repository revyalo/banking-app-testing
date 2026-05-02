# Práctica 2 - Implementación de pipelines de CI/CD y desarrollo colaborativo

**Grupo Z**

## Miembros del Equipo
| Nombre y Apellidos | Correo URJC | Usuario GitHub |
|:---                 |:---                            |:--- |
| David Arevalo Rey   | d.arevalo.2023@alumnos.urjc.es | revyalo |
| Daniel Vela Quimbay | d.vela.2023@alumnos.urjc.es    | danivequi |
| Ricardo Ullco Lagla | r.ullco.2023@alumnos.urjc.es   | ullco4833-rgb |

---

## Participación de Miembros en la Práctica 2

| Tarea | David Arévalo Rey | Daniel Vela Quimbay | Ricardo Ullco Lagla |
|---|---|---|---|
| **Tarea 1 - Preparación del repositorio** | Preparó la rama `p2-tarea1-preparacion-repositorio`, corrigió la configuración de Maven/Surefire eliminando la línea problemática de `argLine`, añadió la propiedad `app.version`, creó `AppVersionControllerAdvice` y modificó el login para mostrar la versión de la aplicación. <br><br>Commits: [9a06487](https://github.com/revyalo/ais-2026-grupo-z/commit/9a06487), [6bc10d6](https://github.com/revyalo/ais-2026-grupo-z/commit/6bc10d6). <br>Merge: [6eac54e](https://github.com/revyalo/ais-2026-grupo-z/commit/6eac54ebe8b6b4be4c05d2f6a5f00250d0371ed5). | Pendiente de completar por el miembro correspondiente. | Pendiente de completar por el miembro correspondiente. |
| **Tarea 2 - Definición de workflows** | Añadió workflows de GitHub Actions para automatizar la ejecución de pruebas en ramas y Pull Requests. Creó el workflow de pruebas unitarias en ramas y el workflow de pruebas automáticas para Pull Requests. <br><br>Commits: [d6586e8](https://github.com/revyalo/ais-2026-grupo-z/commit/d6586e8), [e4cc6ee](https://github.com/revyalo/ais-2026-grupo-z/commit/e4cc6ee). | Pendiente de completar por el miembro correspondiente. | Pendiente de completar por el miembro correspondiente. |
| **Tarea 3 - Desarrollo colaborativo con GitHubFlow** | Implementó la rama `feature-2`, correspondiente a impedir operaciones de usuarios bloqueados. Añadió el atributo `banned` en la entidad `User`, incorporó validaciones en `AccountService`, añadió pruebas unitarias para usuarios bloqueados y actualizó la versión de la aplicación a `1.1.0`. <br><br>Pull Request: [PR #5 - Tarea 3: impedir operaciones de usuarios bloqueados](https://github.com/revyalo/ais-2026-grupo-z/pull/5). <br>Commit principal: [0cb1931](https://github.com/revyalo/ais-2026-grupo-z/commit/0cb1931). | Pendiente de completar `feature-1`. | Pendiente de completar la parte asignada por el grupo. |
| **Tarea 4 - Realización de la memoria** | Actualizó la memoria en `README.md` documentando la parte realizada: preparación del repositorio, workflows iniciales, rama `feature-2`, Pull Requests, comandos Git utilizados y evidencias de ejecución. | Pendiente de completar por el miembro correspondiente. | Pendiente de completar por el miembro correspondiente. |

---

## Desarrollo con GitHubFlow

### Asignación de tareas

Durante la práctica se ha seguido un flujo de trabajo basado en GitHubFlow. Cada funcionalidad o tarea se ha desarrollado en una rama independiente creada a partir de `main`. Después, los cambios se han integrado mediante Pull Requests revisados y validados con workflows de GitHub Actions.

| Tarea | Responsable | Estado |
|---|---|---|
| Tarea 1 - Preparación del repositorio | David Arévalo Rey | Completada |
| Tarea 2 - Workflows de pruebas en ramas y Pull Requests | David Arévalo Rey | Completada |
| Tarea 3 - Feature 2: impedir operaciones de usuarios bloqueados | David Arévalo Rey | Completada |
| Tarea 3 - Feature 1 | Pendiente de otro integrante | Pendiente |
| Workflows restantes / despliegue / nightly | Pendiente de otros integrantes | Pendiente |
| Tarea 4 - Memoria | Grupo Z | En proceso |

### Pasos seguidos

#### 1. Actualización de la rama principal

Antes de comenzar cada tarea se actualizó la rama `main` para partir siempre de la última versión del proyecto:
```bash
git checkout main
git pull origin main
```

Git checkout main para cambiar a la rama `main` del repositorio
Git pull origin main descarga e integra los ultimos cambios de Github

#### 2. Ramas de trabajo

Para la tarea 1:
```bash
git checkout -b p2-tarea1-preparacion-repositorio
```

Para la tarea 2:
```bash
git checkout -b p2-tarea2-workflows
git checkout -b p2-tarea2-pr-workflow
```

Para la tarea 3 en la funcionalidad feature-2:
```bash
git checkout -b feature-2
```
El comando git checkout -b crea una nueva rama y cambia automaticamente a ella

#### 3. Comprobacion de cambios
Durante el desarrollo se uso la siguiete funcion para comprobar el estado del repo

```bash
git status
```
Permite ver la rama actual, ficheros actualizados, los nuevos y si hay cambios pendientes de commit.

#### 4. Ejecución de pruebas en local.
Antes de subir los cambios se ejecutaron las pruebas con Maven

```bash
mvn clean test
```

Limpia el directorio, recompila el proyecto y ejecuta las pruebas automaticas

En la rama feature-2, hubo un problema con las pruebas unitarias devido al formato de los mensajes, que se soluciono corredctamente.

#### 5. Preparacion de commits
Una vez realizado unos cambios se añadieron los ficheros modificados
```bash
git add pom.xml
git add src/main/java/es/codeurjc/model/User.java
git add src/main/java/es/codeurjc/service/AccountService.java
git add src/test/java/es/codeurjc/unit/AccountServiceTest.java```
```

El comando git add indica que ficheros se incluiran en el siguiente commit.
Despues se crearia el siguiente commit correspondiente:

```bash
git commit -m "Tarea 3: impedir operaciones de usuarios bloqueados"
```
El comando git commit guarda los cambios en el historial local del repositorio

#### 6. Subida de la rama al repositorio remoto
Despues de crear el commit, se subio la rama a Github
```bash
git push origin feature-2
```
Publicamos la rama local feature-2 en el repositorio remoto

#### 7. Creación y revision del Pull Request
Con la rama subida a GitHub, se crea su Pull Request correspondiente:

[PR #5 - Tarea 3: impedir operaciones de usuarios bloqueados](https://github.com/revyalo/ais-2026-grupo-z/pull/5)
En este Pull Request se ejecutaron los workflows configurados para validar automáticamente los cambios. Finalmente, los checks aparecieron en verde, por lo que la rama quedó lista para revisión e integración.

## Cambios realizados en `feature-2`

La rama `feature-2` implementa la funcionalidad para impedir operaciones bancarias a usuarios bloqueados.

### Cambios en `User`

Se añadió el atributo `banned` a la entidad `User`:

```Java
    private boolean banned = false;
```

También se añadieron sus métodos de acceso:


```Java
    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }
```
    
Este atributo permite marcar a un usuario como bloqueado.

### Cambios en `AccountService`

Se añadió una validación para comprobar si el usuario propietario de una cuenta está bloqueado antes de permitir operaciones bancarias.

La validación se aplica en operaciones como:

- Ingresos.
- Retiradas.
- Transferencias desde una cuenta origen.
- Transferencias hacia una cuenta destino.

Si el usuario está bloqueado, se lanza una excepción y no se realiza la operación.

### Cambios en pruebas unitarias

Se añadieron pruebas unitarias en `AccountServiceTest` para comprobar que no se permiten operaciones con usuarios bloqueados:

- No permite ingresar dinero si el usuario está bloqueado.
- No permite retirar dinero si el usuario está bloqueado.
- No permite transferir dinero si el usuario origen está bloqueado.
- No permite transferir dinero si el usuario destino está bloqueado.

Además, se corrigieron las comprobaciones de mensajes de notificación para adaptarlas al formato decimal utilizado por el servicio.

### Cambio de versión

Se actualizó la versión de la aplicación en `pom.xml`:

    <version>1.1.0</version>

## Workflows de GitHub Actions

### Workflow de pruebas en ramas

Se añadió un workflow para ejecutar pruebas automáticamente al trabajar con ramas de desarrollo.

Evidencia: [Commit workflow ramas](https://github.com/revyalo/ais-2026-grupo-z/commit/d6586e8)

Este workflow permite detectar errores antes de abrir o integrar cambios en `main`.

### Workflow de Pull Requests

Se añadió un workflow que se ejecuta al abrir o actualizar un Pull Request.

Evidencia: [Commit workflow Pull Requests](https://github.com/revyalo/ais-2026-grupo-z/commit/e4cc6ee)

En el caso de la rama `feature-2`, el Pull Request asociado ejecutó correctamente los checks:

[PR #5 - Tarea 3: impedir operaciones de usuarios bloqueados](https://github.com/revyalo/ais-2026-grupo-z/pull/5)

## Despliegue en Azure

Pendiente de completar por el miembro encargado del despliegue.

Cuando el despliegue esté realizado se añadirá:

- Captura de pantalla de la aplicación desplegada en Azure.
- Captura de pantalla del dashboard de Azure con la última versión desplegada.
- URL pública de la aplicación desplegada.

## Workflow de Nightly

Pendiente de completar por el miembro encargado del workflow de Nightly.

En esta sección se documentará:

- Cuándo se lanza el workflow.
- Qué tareas realiza.
- Enlace a la última ejecución.
- Enlaces a los artefactos generados.

### **Participación de Miembros en la Práctica 1**

#### **Alumno 1 - David Arévalo Rey**

He participado en las diferentes tareas de la practica, preparando el repositorio para evitar conflictos futuros, la apotacion de diferenctes detecciones de calidad con diferentes herramientas, su respectiva refactorizacion, la creacion de varias pruebas unitarias, dos casos TDD y dos pruebas de Selenium 

| Nº    | Commits      |
|:------------: |:------------:|
|1| [Issue detectada](https://github.com/revyalo/ais-2026-grupo-z/commit/a9dfde9ae07436ce59379877ae1740aa405bd84a)|
|2| [Prueba unitaria implementada](https://github.com/revyalo/ais-2026-grupo-z/commit/b69370af7430b8d80c3319b3a1897353b8833602)  |
|3| [Refactorización implementada](https://github.com/revyalo/ais-2026-grupo-z/commit/8c9e03e738bf6314286f3248499d2441e29b2a8b)  |
|4| [Caso de TDD implementado](https://github.com/revyalo/ais-2026-grupo-z/commit/ecb938d019027eef9430168b494cb128b0a9e877)  |
|5| [Prueba de sistema implementada](https://github.com/revyalo/ais-2026-grupo-z/commit/ed36d5a7029772356d88335501829f19382e7dc1)  |

---

#### **Alumno 2 - Daniel Vela Quimbay**

He participado en las diferentes tareas de la práctica aportando la detección de 3 issues de calidad, varias pruebas unitarias, al refactorización de los issues  encontrados, dos casos de TDD y dos prueba de sistema web con Selenium para comprobar la funcionalidad de transferencias.

| Nº    | Commits      |
|:------------: |:------------:|
|1| [Issue 1 detectada](https://github.com/revyalo/ais-2026-grupo-z/commit/03aeb214e948c135283c1b2f164ceddbe4cb2f9b)<br>[Issue 2 y 3 detectada](https://github.com/revyalo/ais-2026-grupo-z/commit/c4c74e97c3355a8e0ab95d366d184bc3c058c9ac) |  
|2| [Prueba unitaria implementada](https://github.com/revyalo/ais-2026-grupo-z/commit/54d910afc9fbf36f1e394275a2cfbce84885c9a4)  |
|3| [Refactorización implementada](https://github.com/revyalo/ais-2026-grupo-z/commit/144182f941ef3f621dc26fe9df38ac6fcdf45942)  |
|4| [Caso de TDD implementado](https://github.com/revyalo/ais-2026-grupo-z/commit/9147cafd0413f61686cdb2d98623f1be879c7ddf)  |
|5| [Prueba de sistema implementada](https://github.com/revyalo/ais-2026-grupo-z/commit/29c849ed0a901961ea47964b8dc11435f84f00d5)  |

---

#### **Alumno 3 - Ricardo Ullco Lagla**

He participado en las diferentes tareas de la práctica aportando la detección de 2 issues de calidad y su posterior refactorizacion, 3 pruebas unitarias, dos casos de 
TDD y dos prueba de sistema web con Selenium para comprobar la funcionalidad de transferencias

| Nº    | Commits      |
|:------------: |:------------:|
|1| [Issue detectada](https://github.com/revyalo/ais-2026-grupo-z/commit/bb101b1e52b9fe2ef62df78fbae8c30f748c0174)  |
|2| [Prueba unitaria implementada](https://github.com/revyalo/ais-2026-grupo-z/commit/a043ef8d1b291260fb53b224454e51db896bd9e4)  |
|3| [Refactorización implementada](https://github.com/revyalo/ais-2026-grupo-z/commit/fc9df951d3f8b0afda81bc40c5e4cc24f70e6c59)  |
|4| [Caso de TDD implementado](https://github.com/revyalo/ais-2026-grupo-z/commit/c0d6b9e8a9ae8257e57f432fc78e1ddfaa9bc30e)  |
|5| [Prueba de sistema implementada](https://github.com/revyalo/ais-2026-grupo-z/commit/6d82a00c96f438f4d2613b30553e5763642052d0)  |
