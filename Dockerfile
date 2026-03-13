# === Build Stage ===
FROM gradle:8-jdk21 AS build

WORKDIR /app

# Gradle 캐시 활용을 위해 빌드 설정 파일 먼저 복사
COPY build.gradle settings.gradle ./
COPY gradle/ gradle/

# 의존성 다운로드 (소스 변경 시에도 캐시 재활용)
RUN gradle dependencies --no-daemon || true

# 소스 코드 복사 후 빌드
COPY src/ src/
RUN gradle build -x test --no-daemon

# === Runtime Stage ===
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
