

# msa-poc-microservicios



# Microservicios

La architectura va a estar compuesta por tres microservicios los cuales se van a comunicar entre elllos. El microservicio user va a tener diferentes endpoints. A estos endpoints se va a llamar para crear, modificar, borrar y consultar usuarios. La información relativa a los usuarios se almacena en una base de datos.  A su vez, este microservicio llama al microservicio communication el cual recibe la informacion del usuario y la accion que ha realizado. El microservicio messaging es llamado desde el microservicio communication y se le envia mediante post un objeto que contiene al equivalente a un correo electronico: FROM, TO, BODY, SUBJECT. El microserivicio messaging solo imprime una traza con toda esta información. 

### Como usar

Tener arrancado docker Version 18.03.0-ce. Descargar el projecto del repositorio de bitbucket. Dentro del proyecto ejecutar `docker-compose up -d --build` y lo servicios se levantarán. A veces, el servicio user no se leventa, por lo que hay que levantarlo individualemente con el comando `docker-compose up -d --build user` 

### User 

Microservicio que expone los siguientes endpoints:

- GET  `/user/{id}` 
  - Si se especifica el identificador (`id`), busca en base de datos el usuario con identificador `id` que está dado de alta. Si existe, devuelve la información. Si no existe, devuelve un código HTTP de respuesta 404 (`NOT FOUND`).
  - Si no se especifica, recuperará de la base de datos todos los usuarios dados de alta. Si no se encuenta ninguno, devuelve un código HTTP de respuesta 404 (`NOT FOUND`).
- POST `/user`. Permite dar de alta un usuario (se graba en la base de datos). Para ello, se ha de especificar la información de usuario (nombre, apellido y dirección de correo electrónico). En caso de que la creación haya ido bien, se devuelve código HTTP de respuesta 201 (`CREATED`) con la información del usuario completa.
- PATCH `/user/{id}`. Permite modificar un usuario (es preciso enviar la información del usuario - nombre, apellido y dirección de correo - que se desea modificar). Se busca el usuario con identificador `id` en estado alta. Si no se encuentra, se devuelve el código HTTP de respuesta 404 (`NOT FOUND`). Si se encuentra, se actualiza el registro en la base de datos y se devuelve código HTTP de respuesta 200 (`OK`) con la información del usuario completa.
- DELETE `/user/{id}`. Se busca el usuario con identificador `id` en estado alta. Si no se encuentra, se devuelve código HTTP de respuesta 404 (`NOT FOUND`). Si se encuentra, se actualiza el campo de fecha de borrado en base de datos y se devuelve código HTTP de respuesta 204 (`NO CONTENT`) sin contenido en la respuesta. 

Si la petición no es de consulta (`GET`), se invocará al servicio de Comunicación vía REST utilizando `RestTemplate` con la acción y la información completa del usuario.

#### Dependencias

        <!--- Boot: Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    
        <!--- Data: JPA -->
         <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
    
        <!--- Database: Postgres -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
        
        <!--- Cloud: Stream -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream</artifactId>
        </dependency>
    
        <!--- Cloud: Stream: Kafka (Binder) -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream-binder-kafka</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j2</artifactId>
        </dependency>
        
        <!-- model mapper -->
        <dependency>
            <groupId>org.modelmapper</groupId>
            <artifactId>modelmapper</artifactId>
        </dependency>

### Communication

Este microservicio va a incluir comunicación asincrona ente el microservicio user y communication cuando se produce un borrado de usuario (DELETE) utilizando Apache Kafka. 

#### Dependencias 

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream</artifactId>
        </dependency>
    
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream-binder-kafka</artifactId>
        </dependency>
    
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j2</artifactId>
        </dependency>
        
    	<dependency>
            <groupId>cglib</groupId>
            <artifactId>cglib</artifactId>
    	</dependency>
    
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>

### Messaging

Microservicio que expone un único endpoint REST en ruta `/messaging` que recibe vía POST un objeto que contiene la información equivalente a un correo electrónico: FROM, TO, BODY, SUBJECT.

Con esta información, únicamente generará una traza con esta información.

#### Dependencias

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream</artifactId>
        </dependency>
    
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream-binder-kafka</artifactId>
        </dependency>
    
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j2</artifactId>
        </dependency>
    
        <dependency>
            <groupId>cglib</groupId>
            <artifactId>cglib</artifactId>
        </dependency>
    

# Mensajeria con Kafka
# Mensajeria con Kafka



Este es el diseño básico de la comunicación asincrona. En nuestro caso, el microservicio user va a ser productor, communication va a ser productor y consumidor y el messaging solo consumidor.

![](D:\99.Workspace\java\msa-poc-microservices\imagenes\kafka.jpg)

#### docker-compose

```
  #Kafka service
  kafka:
    image: fjahijado/kafka
    environment:
      - STORAGE_TYPE=mem
    ports:
      - 9092:9092
    networks:
      - services
    container_name: kafka
```

Para añadir comunicación asincrona entrer el microservicio user y communication primero hay que añadir las siguentes dependencias

        <!--- Cloud: Stream -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream</artifactId>
        </dependency>
    
        <!--- Cloud: Stream: Kafka (Binder) -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream-binder-kafka</artifactId>
        </dependency>
Se crea la clase `StreamConfiguration.java`  en todos  los microservicios 

```
package msa.poc.microservices.communication.service.configuration;

import msa.poc.microservices.communication.service.stream.NotificationStreamProcessor;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(NotificationStreamProcessor.class)
public class StreamConfiguration {
}

```

Y tambien la clase `NotificationStreamProcessor.java `la cual va a ser differente en función de si el microservicio es productor, consumidor o ambos. 

El microservicio user tiene  este método porque es productor: 

    @Output("msa-poc-output")
    MessageChannel sendNotification();
El microservicio communication tiene estos dos métodos porque es consumidor y productor:

    @Input("msa-poc-input")
    SubscribableChannel readNotification();
    
    @Output("msa-poc-output")
    MessageChannel sendNotification();
Y el microservicio messaging tiene este método porque es consumidor:

    @Input("msa-poc-input")
    SubscribableChannel readNotification();
En la clase controlador de cada microservicio es donde se llaman los métodos mencionados.

 

# Circuit Breaker

Para utilizar este patrón, añadimos la dependencia a nuestro pom padre y a los microservicios.

```
<!-- Cloud: Hystrix - Version -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    <version>${hystrix.version}</version>
</dependency> 
```

Añadimos la anotación `@EnableCircuitBreaker` a la clase Application. 

Ahora en la clase  de cada microservicio se implementa un método el cual se va a llamar cuando no se pueda conectar con el microservicio porque este caido. 

Por ejemplo,  en el método `sendNotification`  hemos puesto la anotación `@HystrixCommand(fallbackMethod = "sendNotificationFallback")` la cúal indica que en caso de fallo de la llamada al servicio con la url `serviceURL` se va a llamar al método `sendNotificationFallback`  y va a mostra un mensaje de error. Con los microservicios communication y messaging se sigue los mismos pasos.

```
    @Override
    @HystrixCommand(fallbackMethod = "sendNotificationFallback")
    public CommunicationResponse sendNotification(UserRequest userRequest) {
        final ResponseEntity<CommunicationResponse> response;
        HttpEntity<?> httpEntity = new HttpEntity<>(userRequest, buildRequestHeaders());
            response = restTemplate.exchange(serviceUrl, HttpMethod.POST, httpEntity,
                    CommunicationResponse.class);

        return response.getBody();

    }

    public CommunicationResponse sendNotificationFallback(UserRequest userRequest) {
        CommunicationResponse communicationResponse = new CommunicationResponse();

        communicationResponse.setResponse(InternalStatus.ERROR,"Communication service is not available");

        return communicationResponse;
    }

```





# Config-Server

![](D:\99.Workspace\java\msa-poc-microservices\imagenes\config-server-architecture.jpg)

#### docker-compose

```
  config:
    build:
      context: msa-poc-microservices-config/msa-poc-microservices-config-service/target/docker
    ports:
      - 8888:8888
    networks:
      - services
    container_name: config
```

#### Dependencias 

En el pom padre dentro de la etiqueta  `dependencyManagement` añadimos:

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dependencies</artifactId>
    <version>Finchley.RC1</version> 
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

#### Dependencias config-server 

```
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
</dependencies>
```

#### Dependencias clientes

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-client</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

##### application.yml del servidor

En el archivo properties del config-server se especifica el repositorio remoto git. 

`spring.cloud.config.server.git.force.pull= true`  va a actualizar los archivos cada vez que estos se modifican despues de realizarse un commit.

```
server:
  port: 8888
spring:
  application:
    name: config
  cloud:
    config:
      server:
        git:
          uri: https://github.com/bogdanalinmuresan/config-repo
          force-pull: true
management:
  endpoints:
    jmx:
      exposure:
        include: health,info
```

##### boostrap.yml del cliente

En este caso todos los clientes van a tener las mismas propiedades, excepto el valor name, que va a ser diferente para cada uno. El valor de name que le demos a cada microservicio, sera como se llame el fichero .properties que esta en el servidor de configuración. 

```
server:
  port: @docker.exposePort@
spring:
  application:
    name: user
  profiles:
    active: dev
  cloud:
    config:
      retry:
        initial-interval: 3000
        multiplier: 1.3
        max-interval: 5000
        max-attempts: 20
      uri: http://config:8888
      enabled: true
management:
  endpoints:
    web:
      exposure:
        include: refresh
```

La dependencia `spring-boot-starter-actuator` nos permite refrescar los  endpoints de los microservicios para recuperar las nuevas propiedades del repositorio remoto Git. 

 `spring.cloud.config.uri` especifica la ruta del servidor de configuracion que será desde donde se obtendrán las propiedades del microservicio. En este caso las propiedades se mapean por {name}-{profile}.properties. Por lo tanto en el servidor de configuracion debe existir el archivo user-dev.properties.

`management.endpoints.web.exposure.include=refresh` permite refrescar el endpoint para actualizar el archivo de propiedades cuando el repositorio git se ha modificado. Esto quiere decir, que cada vez que se realice un nuevo commit modificando las propiedades, hay que hacer un refresh en el endpoint del cliente. El comando para este microservicio es `curl localhost:8095/actuator/refresh -d {} -H "Content-Type: application/json"` 



# Base de datos postgres

Usamos la base de datos para realizar operaciones sobre los usuarios. 

### docker-compose

Destacar la propiedad `postgres.build-context= postgres/docker` la cual indicamos en que ruta esta el archivo Dockerfile. 

```
  # Postgres service
  postgres:
    build:
      context: postgres/docker
    ports:
      - 5432:5432
    networks:
      - services
    container_name: postgres
```

### Dockerfile

Al arrancar la base de datos queremos que se ejecute el archivo .sql, para ello hay que compiarlo y esta en la misma ruta que el dockerfile

```
FROM library/postgres
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=password
ENV POSTGRES_DB=prueba
COPY schema.sql docker-entrypoint-initdb.d/schema.sql
```

### Dependencias

```
        <!--- Data: JPA -->
         <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!--- Database: Postgres -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
```

### application.properties

```
spring.datasource.url= jdbc:postgresql://postgres:5432/prueba
spring.datasource.username= postgres
spring.datasource.password= password
spring.datasource.continue-on-error= true
spring.datasource.initialization-mode= always
spring.datasource.driver-class-name= org.postgresql.Driver
 
spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults= false # Si no, lanza una excepción ; Encontrado: http://vkuzel.blogspot.com.es/2016/03/spring-boot-jpa-hibernate-atomikos.html
spring.jpa.hibernate.ddl-auto= none
```



# Tests con Spock

## Estructuración de los Tests en bloques
Spock estructura los Test en bloques intuitivos; no hay única forma de realizarlos, puesto que el número de bloques es extenso. A continuación se muestran dos formas:
### 1. Con "given", "expect", ["where"]
    given:
      Aquí es donde inicializamos el "contexto" de nuestros Test; es decir, los objetos y otras variables que vamos a utilizar posteriormente.
    expect:
      Dados los objetos que hemos utilizado en el bloque "given", hacemos las comprobaciones lógicas necesarias en función de los parámetros que nosotros consideremos.

  EJEMPLO

```
  class Test extends Specification{
        given:
          int num = 1
          int num2 = 2
        expect:
          num + num2 == 3  /* La comprobación lógica. Si true, pasa el Test; si false, falla */
    }
```

No obstante, también es posible realizar una batería de pruebas. Es decir, probar una funcionalidad con varios casos de prueba.

Para el ejemplo, imaginemos que tenemos una clase "Operaciones" con un método "int suma(int n1, int n2)" que recibe por parámetro dos enteros y realiza la suma de ambos, devolviendo el resultado.

```
  class Test extends Specification {
      given:
        Operaciones operaciones = new Operaciones()
      expect:
        operaciones.suma(n1,n2) == resul
      where:
        n1 | n2 | resul
        1 | 3 | 4
        6 | 3 | 9
        0 | 1 | 1
  }
```


Lo que básicamente estamos haciendo en el bloque "where" es definir varios casos de prueba en función de las variables "n1", "n2" y "resul". La comprobación lógica para la operación se realizar en el bloque "expect", como en el ejemplo anterior.

##### Funcionamiento del bloque "where" con objetos
Cuando la batería de pruebas la queremos hacer objetos y no con tipos primitivos (o con objetos que emulan el comportamiento de tipos primitivos; ejemplo: String o Integer), haremos una función aparte que nos devuelve un "array" con los parámetros que necesitamos. 
EJEMPLO (Test de ComposeService)

    def "ComposeService"() {
       given:
            ComposeService composeService = new ComposeServiceImpl(messageSource: 	Mock(MessageSource))
       expect:
        def r = composeService.compose(action,userDto).getCommunicationDto()
        r.getFrom() == "info@ust-global.com"
        r.getTo() == userDto.getEmail()
        r.getAction() == action
    
        where:
        /* Declaramos los parámetros que utilizamos en el bloque "expect" y le pasamos la función  	que nos crea los objetos necesarios en función de los parámetros */
        [action, userDto] << ComposeServiceData()
    }
    
    def "ComposeServiceData"() {
        UserDto userDto1 = new UserDto()
        userDto1.setName("ComposeService - Name")
        userDto1.setSurname1("ComposeService - Surname1")
        userDto1.setSurname2("ComposeService - Surname2")
        userDto1.setEmail("ComposeService - Email")
        
        /* La primera dimensión de este array representa un caso de prueba; la segunda, los parámetros asignados */
        [[Action.ADD,userDto1]]
    }

### 2. Con "given", "when", "then" y ["where"]
En este caso, el bloque anterior "expect" es divido en dos bloques "when" y "then", con el propósito de separar la operación de la comprobación lógica del resultado. De esta forma, en el bloque "when" se debe realizar la invocación al método y finalmente será el bloque "then" es el que compruebe la validez y haga que el Test pase o falle.
El comportamiento del bloque "where" es análogo al mostrado en el apartado anterior.
### Mocks con Spock
Se pueden realizar de varias maneras. Dada una clase "User": 
  1. User user = Mock(User.class)
    Estamos "mockeando" la clase User. No obstante, si dentro de esta clase hubiese otras clases declarados (concretamente, con @Autowired), deberíamos mockear también estas clases.
    Por ejemplo, imaginemos la clase User:

    ```
    class User {
       @Autowired
       private Address address;
       @Autowired
       private Country country;
       public boolean connected () {
                [. . .]
       }
    }
    ```

    ​
  1. User user = new User(address: Mock(Address.class), country: Mock(Country.class))
  2. También podemos simular la respuesta de un método.

      ```
      User user = Mock{
          connected >> true /* La respuesta siempre va a ser true */
      }
      ```

      ​
##### "Mockear" endpoints de controladores con Spock
Se utilizará la clase "MockMvc" propia del framework Spring. Si tenemos un controlador:

```
  @Controller
  class UserController{
    @PostMapping(path="/user")
    public ResponseEntity<Response> addUser(@RequestBody User user) {
                            [. . .]   
    }
  }
```


Podemos realizar el siguiente Test:

```
class Test extends Specification{
      UserController userController = new UserController()
      User user = new User()
     /* Inicializamos el objeto con nuestro controlador */
     MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build()
     /*
     perfom --> Realizamos una operación POST sobre el endpoint que hemos definido ( "/user")
     contentType --> Añadimos el tipo de los datos que vamos a enviar (en este caso, JSON)
     content --> Añadimos propiamente el objeto que vamos a enviar
     andExpect --> comprobamos que el envío se ha realizado correctamente
    */
      def response = mockMvc.perform(MockMvcRequestBuilders.post("/messaging/").contentType(MediaType.APPLICATION_JSON).content(user)).andExpect(MockMvcResultMatchers.status().isOk())
}
```



#### Dependencias 


Añadimos las dependencias en el pom padre necesarias para los microservicios. Vamos a usar Spock y JUnit para los tests de la comunicación asincrona.

```
	 <!-- https://mvnrepository.com/artifact/org.springframework.kafka/spring-kafka-test -->
     <dependency>
     	<groupId>org.springframework.kafka</groupId>
     	<artifactId>spring-kafka-test</artifactId>
     </dependency>
	<dependency>
        <groupId>org.spockframework</groupId>
        <artifactId>spock-core</artifactId>
        <version>1.0-groovy-2.4</version>
        <scope>test</scope>
      </dependency>

      <!-- Spock: Groovy - Version -->
      <dependency>
        <groupId>org.codehaus.groovy</groupId>
        <artifactId>groovy-all</artifactId>
        <version>2.4.5</version>
        <scope>test</scope>
      </dependency>

      <!-- Spock: Spring - Version -->
      <dependency>
        <groupId>org.spockframework</groupId>
        <artifactId>spock-spring</artifactId>
        <version>1.0-groovy-2.4</version>
      </dependency>
      
      <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <scope>test</scope>
       </dependency>
```



### microservicio user

Los test para el microservicio se va a dividir en dos partes. Una parte para los test de los endpoints y la sengunda parte para los tests de la base de datos.

Empezamos creando la estructura de carpetas de nuestros tests. Para ello, creamos  src/test/resources,  src/test/groovy y la clase ApplicationTest.java dentro del paquete `msa.poc.microservices.user`  en src/test/java.  Dentro del directorio groovy creamos un paquete `msa.poc.microservices.user` y dentro creamos las classes `.groovy` para los test de la api y la base de datos. 

![](D:\99.Workspace\java\msa-poc-microservices\imagenes\estructura-user.JPG)

La clase ApplicationTest.java esta anotada con @SpringBootTest por lo tanto va a buscar la clase anotada con `@SpringBootConfiguration` como punto de partida para cargar la configuración de Spring y lo va a hacer en el siguiente orden: msa.poc.microservices.user - msa.poc.microservices - msa.poc - msa. 

Por esta razón, si al paquete de los test se le hubiera llamado de otra forma, no se encontraría la clase anotada con `@SprinBootApplication` 

Para los microservicios communication y messaging el procediemiento es el mismo. 

Ver:  https://www.sylvainlemoine.com/2017/11/08/spring-boot-test-howto/

#### Dependencias

Dependencias necesarias para realizar los test de la api y de la base de datos:

```
    	<!-- H2  -->
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>test</scope>
		</dependency>
				<dependency>
		    <groupId>org.springframework.kafka</groupId>
		    <artifactId>spring-kafka-test</artifactId>
		</dependency>
        
         <!-- Spock -->
	     <dependency>
		      <groupId>org.spockframework</groupId>
		      <artifactId>spock-core</artifactId>
		      <scope>test</scope>
		</dependency>
		<dependency>
		    <groupId>org.codehaus.groovy</groupId>
		    <artifactId>groovy-all</artifactId>
		    <scope>test</scope>
		</dependency> 
		
		<dependency>
           <groupId>org.spockframework</groupId>
           <artifactId>spock-spring</artifactId>
       </dependency>
       
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-test</artifactId>
           <scope>test</scope>
       </dependency>
```

#### Test de la base de datos

Para los test de base de datos se crea la clase DBSpec que extiende de Specification. Todos los test usando Spock van a extender de esta clase. 

Destacar las anotaciones de esta clase 

`@ActiveProfiles("test")` el perfil activo necesario para los tests. 
`@DataJpaTest `  carga una base de datos en memoria, hay que usar la anotación en combinacion con @RunWith que se encuentra en la clase ApplicationTest.java
`@ContextConfiguration` define como cargar y configurar el contexto para los test de integración. Carga las clases anotadas como componente(@Service, @Repository, etc.)
`@ComponentScan(basePackages="msa.poc.microservices.user")`  indica que paquete escanear, es importante si hacemos autowired y usamos clases de otro directorio



### Microservicio communication

Este microservicio se encarga de recibir las peticiones del microservicio user y mandarselas al microservicio messaging. Entre las funciones de este esta la composición del mensaje que se le envia a messaging. El mensaje se crea en base al idioma local. Es decir, el microservicio recibe un tipo de acción junto con los datos del usuario y entonces crea la composición del email que se enviará a messaging. Las variables para la composición del email se obtienen de los archivos .properties los cuales están definidas en ingles y español dependiendo de lo que devuelve el método `LocaleContextHolder.getLocale()` en la clase `ComposeServiceImpl.java `

### Microservicio messaging

Este microservicio solamente imprime los datos recibidos del microservicio communication, es decir la acción que el usuario esta realizado y la composción del email.



## Trazas distribuidas con Jaeger

Se ha usado Jaeger y OpenTracing para la intrumentación del código. Las dependencias en el pom padre necesarias son:

	<dependency>
	    <groupId>io.jaegertracing</groupId>
	    <artifactId>jaeger-client</artifactId>
	    <version>0.30.3</version>
	</dependency>
	
	<!-- https://mvnrepository.com/artifact/io.opentracing.contrib/opentracing-spring-web-autoconfigure -->
	<dependency>
	    <groupId>io.opentracing.contrib</groupId>
	    <artifactId>opentracing-spring-web-autoconfigure</artifactId>
	    <version>0.3.2</version>
	</dependency>
	
###  Docker-compose 

Para cada microservicio definimos las variables de entorno, por ejempolo para el microservicio communication.

```
 environment: 
      - JAEGER_SERVICE_NAME=communication
      - JAEGER_AGENT_HOST=jaeger
      - JAEGER_AGENT_PORT=6831     
      - JAEGER_SAMPLER_MANAGER_HOST_PORT=jaeger:5778
```

Por último, en el fichero Application.java de cada microservicio especificamos

```
    @Bean
    public io.opentracing.Tracer jaegerTracing(){
    	Configuration config = Configuration.fromEnv();
		return config.getTracer();
```