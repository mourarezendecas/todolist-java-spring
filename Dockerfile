# Estágio 1: Compilar o aplicativo
FROM maven:3.8.4-openjdk-17 AS build

# Copia o código-fonte para o contêiner
COPY . /app

# Define o diretório de trabalho
WORKDIR /app

# Compila o projeto
RUN mvn clean install

# Estágio 2: Configurar o PostgreSQL em um contêiner separado
FROM postgres:latest

# Variáveis de ambiente para configurar o PostgreSQL
ENV POSTGRES_DB todolist
ENV POSTGRES_USER usuario
ENV POSTGRES_PASSWORD senha

# Exponha a porta padrão do PostgreSQL (5432)
EXPOSE 5432

# Estágio 3: Copiar o artefato JAR do estágio de compilação para a imagem final
FROM openjdk:17-jre-slim

# Copia o artefato JAR do estágio de compilação para a imagem final
COPY --from=build /app/target/todolist-1.0.0.jar /app.jar

# Exponha a porta do aplicativo (8080)
EXPOSE 8080

# Comando de entrada para executar o aplicativo quando o contêiner for iniciado
CMD ["java", "-jar", "/app.jar"]
