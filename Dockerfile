FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
ADD config config
ADD JNativeHook-1.0.0.x86_64.dll JNativeHook-1.0.0.x86_64.dll
COPY ./target/client-agent-1.0.0.jar qmsagent.jar
ENTRYPOINT ["java","-jar","qmsagent.jar"]