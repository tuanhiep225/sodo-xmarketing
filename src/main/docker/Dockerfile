FROM openjdk:8u151-jdk-alpine3.7

# Install lots of fonts
ENV LANG en_GB.UTF-8
RUN apk add --update ttf-dejavu ttf-droid ttf-freefont ttf-liberation ttf-ubuntu-font-family && rm -rf /var/cache/apk/*

VOLUME /tmp
ADD sodo-xmarketing-1.0.0.jar app.jar
RUN apk add --no-cache bash
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xmx1024M","-jar","/app.jar"]