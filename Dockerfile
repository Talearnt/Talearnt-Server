# jdk-17 버전 설치 경량화 버전
FROM eclipse-temurin:17-jdk-alpine

# jar 파일, 컨테이너 최상위 경로로 복사
COPY build/libs/*SNAPSHOT.jar /app.jar

# 컨테이너 최초 실행 시에 app.jar 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]

# 이 프로젝트의 최상위 경로에서 ./gradlew clean build 실행
# 빌드를 한 파일을 기반으로
# Docker build -t 이름 .
# 으로 명령어 입력
# docker run -d -p 80:80 이름
# 이렇게 하면 포트 연결하고 서버 띄워줌