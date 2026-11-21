# Gestión de academia – JPA + ObjectDB

Aplicación de escritorio para gestionar una academia de cursos monográficos:
alumnos, profesores, cursos, asignaturas y matrículas con notas. Proyecto
desarrollado durante el ciclo de Desarrollo de Aplicaciones Web (DAW).

## Funcionalidades

- Alta, consulta, edición y borrado de alumnos, profesores y cursos
- Matriculación de alumnos en cursos y calificación
- Cálculo automático de la nota media de cada alumno
- Un profesor solo puede impartir cursos de su ámbito (ciencias o humanidades)
- Al borrar un curso o un alumno se eliminan sus matrículas (borrado en cascada)

## Tecnologías

Java · JPA · JPQL · ObjectDB · Swing · UML · NetBeans

## Modelo de datos

- Alumno 1 ─ N Matrícula N ─ 1 Curso
- Curso N ─ 1 Asignatura
- Curso N ─ 1 Profesor (opcional)

La matrícula actúa como nexo entre alumno y curso y almacena la nota.

**Cascade:** `CascadeType.REMOVE` con `orphanRemoval = true` en Alumno → Matrícula
y en Curso → Matrícula. La relación Profesor ↔ Curso es bidireccional sin cascada.

**Consultas JPQL:** más de 10, entre ellas `JOIN FETCH` con `DISTINCT`, la media de
notas con `AVG` y actualizaciones con `UPDATE`.

<!-- Añade aquí el diagrama UML: ![UML](docs/uml.png) -->

## Documentación

La memoria completa (diagrama UML, explicación de entidades, relaciones, consultas
JPQL y capturas de la aplicación) está en [`Memoria.pdf`](Memoria.pdf).

## Cómo ejecutarlo

### Requisitos
- JDK 17 o superior (o el que uses en NetBeans)
- NetBeans
- [ObjectDB](https://www.objectdb.com/download) (`objectdb.jar`). No se incluye
  en el repositorio por ser software propietario.

### Pasos
1. Descarga ObjectDB y añade `objectdb.jar` a las librerías del proyecto.
2. Abre el proyecto en NetBeans y haz *Clean and Build*.
3. Ejecuta `app.MainProyecto`. El fichero de base de datos
   (`db/proyectoSanchezDB.odb`) se crea automáticamente al arrancar.

## Aprendizajes

- Funcionamiento de JPA y JPQL, y de los distintos tipos de cascada
- Cuándo encaja una base de datos orientada a objetos (aplicaciones contenidas con
  base de datos local) frente a una relacional (más concurrencia y escala)
- Importancia de acotar el alcance del proyecto al tiempo disponible

## Autor

Juan Sánchez Santiago
