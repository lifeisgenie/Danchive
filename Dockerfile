# syntax=docker/dockerfile:1

# -------- Build stage --------
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
RUN chmod +x gradlew
COPY settings.gradle build.gradle ./
COPY src src

# 디버그용(선택): 현재 JDK/Gradle 버전 출력
RUN java -version && ./gradlew -v

# Spring Boot fat-jar 빌드
RUN ./gradlew clean bootJar -x test --no-daemon

# -------- Run stage --------
FROM eclipse-temurin:17-jre-jammy
ENV TZ=Asia/Seoul \
    JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
WORKDIR /app

# 빌드 산출물 복사
COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8080/actuator/health || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar"]
