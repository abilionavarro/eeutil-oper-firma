# Docker

Se a�ade un fichero **Dockerfile** para construir el contenedor docker que permita la prueba y ejecuci�n de la aplicaci�n de forma aut�noma e independiente del entorno de explotaci�n de la CARM.

El �nico requisito que debe cumplir la m�quina donde se pretenda ejecutar el proyecto **eeutil-oper-firma** es que tenga instalada la aplicaci�n [Docker](https://www.docker.com/)

### Dockerfile

Para el montaje solo es necesario construir una imagen docker a partir del fichero **Dockerfile**. Para ello situese en la carpeta */docker* del proyecto y ejecute el siguiente comando:

```sh
$ sudo docker build -t imagen-eeutil .
```

El comando *build* crea una imagen Docker a partir de un Dockerfile y la opci�n -t se usa para poner nombre a la imagen. 

Tras su ejecuci�n se ir�n sucediendo una serie de pasos definidos en el Dockerfile que dar�n como resultado una imagen con un servidor tomcat y el war del proyecto, listo para ejecutarse.

El Dockerfile se divide en 3 etapas en la que cada una se utiliza la imagen de Docker necesaria para realizar la tarea:

* La primera utiliza una imagen de **git** para descargar el proyecto del repositorio
```docker 
FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/josefsabater/eeutil-oper-firma.git
```

* La segunda utiliza una imagen de **maven** para compilar el proyecto
```docker 
FROM maven:3.6.3-jdk-8
WORKDIR /app
COPY --from=0 /app/eeutil-oper-firma /app
RUN mvn install 
```

* La tercera y �ltima utiliza una imagen de **tomcat** para desplegar el war generado en la etapa anterior. Adem�s, se le pasa al servidor los argumentos de entrada mediante la variable de entorno JAVA_OPTS
```docker
FROM tomcat:8.5-jdk8
ENV JAVA_OPTS="-Djavax.net.ssl.trustStore=/usr/local/tomcat/conf/eeutil/truststore.jks \
-Djavax.net.ssl.trustStorePassword=changeit \
-Dlocal_home_app=/usr/local/tomcat/conf/eeutil \
-Deeutil-oper-firma.config.path=/usr/local/tomcat/conf/eeutil"
WORKDIR /app
COPY --from=1 /app/fuentes/eeutil-oper-firma/target/eeutil-oper-firma.war /usr/local/tomcat/webapps
EXPOSE 8080
CMD ["catalina.sh", "run"]
```

Tras finalizar el proceso, se habr� creado la imagen **eeutil-oper-firma** en el repositorio local de Docker. Se puede consultar dicho repositorio ejecutando el comando:

```sh
$ sudo docker images
```

Una vez tenemos la imagen con el servidor tomcat y el war, hay que ejecutar un contenedor que despliegue la aplicaci�n partiendo de dicha imagen. Para ello debemos ejecutar el siguiente comando, sustituyendo los siguientes par�metros:

* <path_config>: Ruta al directorio con los ficheros de configuraci�n
* <path_logs>: Ruta al directorio donde se generan los logs

```sh
$ sudo docker run --name eeutil-oper-firma-c -v <path_config>:/usr/local/tomcat/conf/eeutil -v <path_logs>:/usr/local/tomcat/logs -p 8080:8080 eeutil-oper-firma
```

### Fichero de configuraci�n
La ruta de donde se obtienen los ficheros de configuraci�n, se establece como argumento del servidor Tomcat al arrancarlo, mediante la variable de entorno **JAVA_OPTS**. Estos par�metros establecen rutas propias del contenedor donde se ejecuta el servidor y a las que **no** se tiene acceso desde el exterior. Por poder pasarle al contenedor los ficheros de configuraci�n desde el exterior utilizamos la opci�n -v, que permite mapear directorios entre un contenedor y el host donde se ejecuta.

### Logs
Notes� que entre los par�metros de entrada de despliegue del servidor, no se encuentra ninguno que indique la localizaci�n del log. Esto se debe a que dicha localizaci�n se encuentra definida en el fichero de configuraci�n **log4j.properties**. Es por eso que, antes de ejecutar el contenedor, deber� modificar esta ruta para establecer una propia del contenedor:

```
log4j.appender.GENERAL.File=/usr/local/tomcat/logs/eeutil-firma.log# Docker

Se a�ade un fichero **Dockerfile** para construir el contenedor docker que permita la prueba y ejecuci�n de la aplicaci�n de forma aut�noma e independiente del entorno de explotaci�n de la CARM.

El �nico requisito que debe cumplir la m�quina donde se pretenda ejecutar el proyecto **eeutil-oper-firma** es que tenga instalada la aplicaci�n [Docker](https://www.docker.com/)

## Generar imagen

Para el montaje solo es necesario construir una imagen docker a partir del fichero *Dockerfile*. Para ello sit�ese en la carpeta ``/docker`` del proyecto y ejecute el siguiente comando:

```sh
$ sudo docker build -t eeutil-oper-firma .
```

El comando ``build`` crea una imagen Docker a partir de un *Dockerfile*. La opci�n ``-t`` se usa para poner nombre a la imagen. 

Tras su ejecuci�n se ir�n sucediendo una serie de pasos definidos en el *Dockerfile* que dar�n como resultado una imagen con el war de la aplicaci�n y un servidor tomcat, listo para ejecutarse.

El Dockerfile se divide en 3 etapas:

* La primera utiliza una imagen git ``alpine/git`` para descargar ambos proyectos del repositorio
```docker 
FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/josefsabater/eeutil-shared.git
RUN git clone https://github.com/abilionavarro/eeutil-oper-firma.git
```

* La segunda utiliza una imagen maven ``maven:3.6.3-jdk-8`` para compilar los proyectos
```docker 
FROM maven:3.6.3-jdk-8
WORKDIR /app
COPY --from=0 /app /app
WORKDIR /app/eeutil-shared
RUN mvn install
WORKDIR /app/eeutil-oper-firma
RUN mvn install
```

* La tercera y �ltima utiliza una imagen tomcat de la versi�n 8.5 ``tomcat:8.5-jdk8`` para desplegar el war generado en la etapa anterior. Adem�s, se le pasa al servidor los argumentos de entrada mediante la variable de entorno ``JAVA_OPTS``
```docker
FROM tomcat:8.5-jdk8
ENV JAVA_OPTS="-Djavax.net.ssl.trustStore=/usr/local/tomcat/conf/eeutil/truststore.jks \
-Djavax.net.ssl.trustStorePassword=changeit \
-Dlocal_home_app=/usr/local/tomcat/conf/eeutil \
-Deeutil-oper-firma.config.path=/usr/local/tomcat/conf/eeutil"
WORKDIR /app
COPY --from=1 /app/eeutil-oper-firma/fuentes/eeutil-oper-firma/target/eeutil-oper-firma.war /usr/local/tomcat/webapps
EXPOSE 8080
CMD ["catalina.sh", "run"]
```

Tras finalizar el proceso, se habr� creado la imagen ``eeutil-oper-firma`` en el repositorio local de Docker. Se puede consultar dicho repositorio ejecutando el comando:

```sh
$ sudo docker images
```

## Ejecutar contenedor

Una vez hemos generado la imagen con todo lo necesario, hay que ejecutar un contenedor que despliegue la aplicaci�n partiendo de dicha imagen. Para ello debemos ejecutar el siguiente comando, sustituyendo los siguientes par�metros:

* ``<path_config>``: Ruta absoluta al directorio con los ficheros de configuraci�n y al **truststore.jks**. Puede ser la ruta donde se encuentran en el proyecto ``<path>/resources/config`` *(recuerda que la ruta debe ser absoluta)* o cualquier otra. Es importante recordar que los ficheros de configuraci�n que se encuentran en ``<path>/resources/config`` son en realidad una **plantilla** para la configuraci�n, no tienen valores reales de conexi�n ni configuraci�n. Adem�s, el fichero **truststore.jks** tendr� que ser a�adido expl�citamente, ya que no se encuentra entre los ficheros de configuraci�n
* ``<path_logs>``: Ruta absoluta al directorio donde se generar�n los logs de la aplicaci�n. Puede ser cualquiera de la m�quina donde se va a ejecutar el contenedor

```sh
$ sudo docker run --name eeutil-oper-firma-c -v <path_config>:/usr/local/tomcat/conf/eeutil -v <path_logs>:/usr/local/tomcat/logs -p 8080:8080 eeutil-oper-firma
```

Breve resumen sobre cada par�metro de la ejecuci�n:
* ``--name eeutil-oper-firma``: Nombre del contenedor 
* ``-v <path_config>:/usr/local/tomcat/conf/eeutil``: Mapeo del directorio de configuraci�n del host en el contenedor
* ``-v <path_logs>:/usr/local/tomcat/logs``: Mapeo del directorio de logs del host en el contenedor
* ``-p 8080:8080``: Mapeo del puerto 8080 del contenedor en el mismo puerto del host. Esto permite que la aplicaci�n desplegada en el puerto 8080 del contenedor sea accesible desde el puerto 8080 del host

### Fichero de configuraci�n
La ruta de donde se obtienen los ficheros de configuraci�n, se establece como argumento del servidor Tomcat al arrancarlo, mediante la variable de entorno **JAVA_OPTS**. Estos par�metros establecen rutas propias del contenedor donde se ejecuta el servidor y a las que **no** se tiene acceso desde el exterior. Por poder pas�rle al contenedor los ficheros de configuraci�n desde el exterior utilizamos la opci�n -v, que permite mapear directorios entre un contenedor y el host donde se ejecuta.

Adem�s, tamb�en se ha establecido el directorio de configuraci�n como ruta para obtener el fichero **truststore.jks**. Lo que significa que se debe a�adir al directorio de los ficheros de configuraci�n para que el servidor Tomcat lo pueda coger. 

### Logs
Notes� que entre los par�metros de entrada de despliegue del servidor, no se encuentra ninguno que indique la localizaci�n del log. Esto se debe a que dicha localizaci�n se encuentra definida en el fichero de configuraci�n **log4j.properties**. Es por eso que, antes de ejecutar el contenedor, deber� modificar esta ruta para establecer la propia del contenedor:

```
log4j.appender.GENERAL.File=/usr/local/tomcat/logs/eeutil-oper-firma.log
```

## Para terminar

Puede comprobar que el contenedor esta ejecut�ndose correctamente accediendo mediante un navegador a la ruta http://localhost:8080/eeutil-oper-firma/ws

Un �ltimo consejo para finalizar: Si desea utilizar el contenedor para realizar despliegues mientra desarrolla, tenga en cuenta que el comando ``docker build`` utiliza una cache propia para ahorrarse etapas. Esto significa que si sube alg�n cambio al repositorio y vuelve a ejecutar un ``docker build``, �ste cambio **no se reflejar� en el despliegue**, ya que no se descargar� de nuevo el c�digo. 

Para sortear este inconveniente, puede ejecutar el ``docker build`` con el par�metro ``--no-cache``, de esta forma se volver�n a ejecutar todas las etapas:

```sh
$ sudo docker build -t eeutil-oper-firma --no-cache .
```
```
