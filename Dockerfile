FROM gcr.io/google_appengine/openjdk8

VOLUME /tmp
ADD target/universal/bijbaan-server-.zip bijbaan-server.zip
RUN unzip bijbaan-server.zip
ENTRYPOINT ./bijbaan-server-/bin/bijbaan-server -Dplay.http.secret.key=corgiswithhats -Dhttp.port=8080