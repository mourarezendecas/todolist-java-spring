# Estágio 1: Compilar o aplicativo
FROM adoptopenjdk/openjdk17:latest AS build

# Atualiza o sistema e instala o Maven
RUN apt-get update && apt-get install -y maven

# Copia o código-fonte para o contêiner
COPY . /app

# Define o diretório de trabalho
WORKDIR /app

# Compila o projeto
RUN mvn clean install

# Estágio 2: Configurar o PostgreSQL e criar a base de dados
FROM postgres:latest

# Variáveis de ambiente para configurar o PostgreSQL
ENV POSTGRES_DB todolist
ENV POSTGRES_USER usuario
ENV POSTGRES_PASSWORD senha

# Exponha a porta padrão do PostgreSQL (5432)
EXPOSE 5432

# Estágio 3: Copiar o artefato JAR do estágio de compilação para a imagem final
FROM adoptopenjdk/openjdk17:latest

# Copia o artefato JAR do estágio de compilação para a imagem final
COPY --from=build /app/target/todolist-1.0.0.jar /app.jar

# Exponha a porta do aplicativo (8080)
EXPOSE 8080

# Comando de entrada para executar o aplicativo quando o contêiner for iniciado
CMD ["java", "-jar", "/app.jar"]
