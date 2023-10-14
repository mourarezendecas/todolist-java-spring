# Estágio 1: Compilar o aplicativo
FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y

# Copie o código-fonte para o contêiner
COPY . .

RUN apt-get install maven -y
RUN mvn clean install

# Estágio 2: Configurar o PostgreSQL
FROM postgres:latest

# Variáveis de ambiente para configurar o PostgreSQL
ENV POSTGRES_DB todolist
ENV POSTGRES_USER postgres
ENV POSTGRES_PASSWORD postgres

# Expor a porta padrão do PostgreSQL (5432)
EXPOSE 5432

# Estágio 3: Configurar o aplicativo
FROM openjdk:17-jdk-slim

EXPOSE 8080

# Copie o artefato JAR do estágio de compilação para a imagem final
COPY --from=build /target/todolist-1.0.0.jar app.jar

# Comando de entrada para executar o aplicativo quando o contêiner for iniciado
ENTRYPOINT [ "java", "-jar", "app.jar" ]
