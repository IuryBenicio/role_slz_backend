# --- Etapa 1: Construção (Build) ---
FROM maven:3.9-eclipse-temurin-17 AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia o pom.xml e baixa as dependências (ajuda no cache do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código-fonte e compila o projeto ignorando testes para ser mais rápido
COPY src ./src
RUN mvn clean package -DskipTests

# --- Etapa 2: Execução (Run) ---
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia apenas o arquivo .jar gerado na etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Porta padrão que aplicações Java/Spring costumam usar
EXPOSE 8080

# Comando para iniciar a aplicação
CMD ["java", "-jar", "app.jar"]