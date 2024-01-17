ARG PROJECT=sok-dev-svc
FROM gcr.io/${PROJECT}/temurin-20
ARG _NAME
ENV NAME=$_NAME
RUN echo ${NAME}

COPY build/libs/*SNAPSHOT.jar /app.jar
COPY src/main/resources/static/info.json /static/info.json

#COPY src/main/resources/templates /templates
