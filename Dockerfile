FROM eclipse-temurin:17-jre as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-jre
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
#ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]##for springboot 3.2 below
##for springboot 3.2 and above
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
##for springboot 3.2.0 and above The JarLauncher has been relocated to a new package since spring boot 3.2.0
