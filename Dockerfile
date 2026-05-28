FROM eclipse-temurin:21-jdk

# 타임존 설정 (Asia/Seoul)
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

# 빌드 아티팩트 복사 (boot jar만. plain jar는 build.gradle에서 비활성)
COPY build/libs/*SNAPSHOT.jar app.jar

# 엔트리포인트 설정
ENTRYPOINT ["java", "-jar", "app.jar"]
