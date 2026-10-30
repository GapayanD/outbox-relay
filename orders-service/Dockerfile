FROM maven:3.9-eclipse-temurin-25
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests package
CMD ["sh", "-c", "java -jar target/*.jar"]
