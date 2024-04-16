# Backend de una aplicación de Lista de Tareas con Autenticación

## Introducción
Este proyecto consiste en el desarrollo de una aplicación de lista de tareas con funcionalidades de autenticación de usuarios. La aplicación permitirá a los usuarios registrarse, iniciar sesión, agregar tareas, marcarlas como completadas eliminarlas y podrán generar un reporte con el nombre de la tarea, la descripción, fecha de creación y el status.

### Tecnologías utilizadas:
- Se utiliza Java 17 como lenguaje de programación y Spring Boot 3.3.0 como framework 
- Se integra una base de datos MySQL para almacenar la información de usuarios y tareas
- Se utiliza Spring Security para la autenticación de usuarios, junto con JSON Web Token (JWT)
- Se utiliza el algoritmo de hash bcrypt para garantiza la seguridad de las credenciales de los usuarios almacenadas en la base de datos
- Se integra RabbitMQ como un intermediario de mensajes para facilitar la comunicación entre los distintos componentes de la aplicación
- Se utiliza Firebase Cloud Messaging para enviar notificaciones push en tiempo real a los usuarios.
- Se integra Redis como una base de datos en memoria para la caché de datos.

## Inicio Rápido
Estas instrucciones te ayudarán a obtener una copia del proyecto en tu máquina local para propósitos de desarrollo y prueba.

### Pre-requisitos
Docker Desktop
https://www.docker.com/products/docker-desktop/

RabbitMQ: 
Puedes utilizar la imagen de RabbitMQ descargándola desde Docker Hub:

  docker pull rabbitmq:3.13.1-management

Para crear y ejecutar un contenedor RabbitMQ a partir de la imagen descargada:

  `docker run --rm -it -p 15672:15672 -p 5672:5672 rabbitmq:3.13.1-management`

El puerto 15672 se utiliza para acceder a la interfaz de usuario de RabbitMQ, mientras que el puerto 5672 es el puerto de conexión AMQP que se utiliza para comunicarse con RabbitMQ.

Redis: 
Puedes utilizar la imagen de Redis descargándola desde Docker Hub:

  `docker pull redis`

Para crear y ejecutar un contenedor de Redis a partir de la imagen descargada:

  `docker run -p 6379:6379 redis`

## Para levantar el proyecto
- Clona este repositorio en tu máquina local
- Para la correcta ejecución del programa los contenedores de RabbitMQ y Redis deben de estar en ejecución
- En IntelliJ IDEA, haz clic en el botón "Run" (Ejecutar) en la barra de herramientas o presiona Shift + F10.

## Ejecutando las pruebas
Para comocer acerca de esta seccion te recomiendo visitar el archivo readme.pdf de este repositorio

[Ver documento PDF](readme.pdf)

## Construido con
JAVA 17
Spring boot 3.3.0
Maven

##Autores
Cintia Favila.
