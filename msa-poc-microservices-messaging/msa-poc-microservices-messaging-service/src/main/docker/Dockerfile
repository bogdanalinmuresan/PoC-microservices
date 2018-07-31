FROM openjdk:alpine 
VOLUME /tmp 
COPY @project.artifactId@-@packageClassifier@.jar app.jar
RUN sh -c 'touch /app.jar' 
ENV JAVA_OPTS="-server -Xms256m -Xmx1024m -Xmn256m -Xss2m" 
EXPOSE @docker.exposePort@
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]