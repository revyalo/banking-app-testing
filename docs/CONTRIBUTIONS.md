# Contribuciones del equipo

Este proyecto fue desarrollado en equipo para una práctica académica de calidad del software, pruebas automatizadas y CI/CD.

## David Arévalo Rey — [@revyalo](https://github.com/revyalo)

- Preparación inicial del repositorio y ajustes de Maven/Surefire.
- Incorporación de la versión de la aplicación en la interfaz.
- Creación de workflows de GitHub Actions para validar ramas y Pull Requests.
- Implementación de la restricción que impide operar a usuarios bloqueados.
- Pruebas unitarias asociadas a ingresos, retiradas y transferencias con usuarios bloqueados.
- Participación en detección y refactorización de problemas de calidad.
- Desarrollo de casos TDD y pruebas de sistema con Selenium.
- Documentación de la práctica y evidencias de ejecución.

Pull Request principal:

- [PR #5 — Impedir operaciones de usuarios bloqueados](https://github.com/revyalo/ais-2026-grupo-z/pull/5)

## Daniel Vela Quimbay — [@danivequi](https://github.com/danivequi)

- Implementación del límite de retiradas acumuladas durante 24 horas.
- Pruebas unitarias de la nueva regla de negocio.
- Desarrollo del workflow Nightly con pruebas Selenium en varios navegadores y sistemas operativos.
- Generación de una imagen Docker como artefacto de GitHub Actions.
- Participación en detección de problemas de calidad, refactorización y casos TDD.

Pull Requests principales:

- [PR #6 — Límite de retiradas en 24 horas](https://github.com/revyalo/ais-2026-grupo-z/pull/6)
- [PR #7 — Workflow Nightly](https://github.com/revyalo/ais-2026-grupo-z/pull/7)

## Ricardo Ullco Lagla — [@ullco4833-rgb](https://github.com/ullco4833-rgb)

- Configuración del workflow de despliegue.
- Construcción y publicación de la imagen Docker.
- Despliegue de la aplicación en Azure App Service.
- Implementación de un Smoke Test posterior al despliegue.
- Participación en pruebas unitarias, Selenium, TDD y refactorización.

## Metodología de trabajo

- Desarrollo mediante ramas independientes.
- Integración con Pull Requests.
- Validación automática con GitHub Actions.
- Uso de GitHubFlow para coordinar el trabajo del equipo.
- Ejecución local de pruebas con Maven antes de integrar cambios.

Los enlaces anteriores apuntan al nombre original del repositorio utilizado durante la práctica y pueden redirigir al repositorio actual.