# Banking App Testing & CI/CD

Aplicación bancaria web desarrollada con **Java 21** y **Spring Boot 4** para practicar calidad del software, pruebas automatizadas, desarrollo colaborativo y pipelines de CI/CD.

El proyecto incluye operaciones bancarias, reglas de negocio, pruebas unitarias y de sistema, workflows de GitHub Actions, contenerización con Docker y un flujo de despliegue en Azure utilizado durante la práctica académica.

> Proyecto educativo. Los datos y operaciones incluidos son ficticios y no representan un sistema bancario de producción.

## Funcionalidades principales

- Gestión de usuarios y cuentas bancarias.
- Ingresos y retiradas de dinero.
- Transferencias entre cuentas.
- Bloqueo de operaciones para usuarios marcados como bloqueados.
- Límite de retiradas acumuladas durante las últimas 24 horas.
- Interfaz web renderizada con Mustache.
- Persistencia con Spring Data JPA y base de datos H2 en memoria.
- Visualización de la versión actual de la aplicación.

## Calidad y pruebas

- Pruebas unitarias con **JUnit 5**, **Mockito** y **AssertJ**.
- Casos desarrollados mediante **TDD**.
- Pruebas de sistema end-to-end con **Selenium**.
- Cobertura de código con **JaCoCo**.
- Validación automática de ramas y Pull Requests con **GitHub Actions**.
- Workflow Nightly con pruebas Selenium en diferentes navegadores y sistemas operativos.
- Smoke Test posterior al despliegue.

## CI/CD

Durante el proyecto se configuraron varios flujos automatizados:

1. Ejecución de pruebas al trabajar en ramas de desarrollo.
2. Validación automática de Pull Requests antes de integrarlos.
3. Ejecución nocturna de pruebas de sistema con Selenium.
4. Construcción de una imagen Docker como artefacto del workflow Nightly.
5. Publicación de la imagen Docker y despliegue en Azure App Service.
6. Ejecución de un Smoke Test contra la aplicación desplegada.

La matriz Nightly cubre combinaciones de Chrome, Firefox y Edge sobre runners de Ubuntu, Windows y macOS.

## Tecnologías

- Java 21
- Spring Boot 4
- Spring MVC
- Spring Security
- Spring Data JPA
- Mustache
- H2 Database
- Maven
- JUnit 5
- Mockito
- AssertJ
- Selenium
- JaCoCo
- Docker
- GitHub Actions
- Azure App Service

## Ejecución local

### Requisitos

- Java 21
- Maven 3.9 o superior

### Ejecutar las pruebas

```bash
mvn clean test
```

### Iniciar la aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en:

```text
http://localhost:8080
```

La consola H2 está habilitada en:

```text
http://localhost:8080/h2-console
```

Configuración de conexión:

```text
JDBC URL: jdbc:h2:mem:bankingdb
Usuario: sa
Contraseña: dejar en blanco
```

## Docker

Construir la imagen:

```bash
docker build -t banking-app .
```

Ejecutar el contenedor:

```bash
docker run --rm -p 8080:8080 banking-app
```

## Contribución personal

Mi aportación principal al proyecto fue:

- Preparar el repositorio y corregir la configuración de Maven/Surefire.
- Añadir la versión de la aplicación a la interfaz.
- Crear workflows de GitHub Actions para validar ramas y Pull Requests.
- Implementar la funcionalidad que impide operar a usuarios bloqueados.
- Añadir pruebas unitarias para ingresos, retiradas y transferencias con usuarios bloqueados.
- Participar en la detección y refactorización de problemas de calidad.
- Desarrollar casos TDD y pruebas de sistema con Selenium.
- Documentar el trabajo y las evidencias de ejecución.

El reparto resumido de tareas del equipo está disponible en [`docs/CONTRIBUTIONS.md`](docs/CONTRIBUTIONS.md).

## Trabajo colaborativo

El proyecto se desarrolló mediante **GitHubFlow**:

- Una rama por funcionalidad o tarea.
- Pull Requests para revisión e integración.
- Checks automáticos antes de realizar el merge.
- Commits pequeños y trazables.

## Estructura principal

```text
.
├── .github/workflows/     # Pipelines de pruebas, Nightly y despliegue
├── docs/                  # Documentación del proyecto y contribuciones
├── images/                # Evidencias del despliegue
├── img/                   # Evidencias del workflow Nightly
├── src/main/              # Código de la aplicación
├── src/test/              # Pruebas unitarias y de sistema
├── Dockerfile
├── pom.xml
└── README.md
```

## Equipo

- **David Arévalo Rey** — [@revyalo](https://github.com/revyalo)
- **Daniel Vela Quimbay** — [@danivequi](https://github.com/danivequi)
- **Ricardo Ullco Lagla** — [@ullco4833-rgb](https://github.com/ullco4833-rgb)

Proyecto académico realizado en la Universidad Rey Juan Carlos.