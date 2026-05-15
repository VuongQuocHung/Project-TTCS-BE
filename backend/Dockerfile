# Giai đoạn 1: Build file JAR bằng Maven
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
# Copy pom.xml và tải các dependency trước (giúp build các lần sau nhanh hơn)
COPY pom.xml .
RUN mvn dependency:go-offline
# Copy source code và tiến hành build (bỏ qua test để tránh lỗi kết nối DB lúc build)
COPY src ./src
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy ứng dụng với môi trường JRE siêu nhẹ
FROM openjdk:17-jdk-slim
WORKDIR /app
# Lấy file .jar đã build từ giai đoạn 1 sang
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
# Lệnh khởi chạy ứng dụng, nhận Port động từ Koyeb
ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT:8080}", "app.jar"]
