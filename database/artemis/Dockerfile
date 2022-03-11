FROM openjdk:11.0.14.1-jre

ENV ARTEMIS_VERSION 2.12.0
ENV ARTEMIS apache-artemis-$ARTEMIS_VERSION
ENV ARTEMIS_STOMP=61613 ARTEMIS_MQTT=1883 ARTEMIS_AMQP=5672 ARTEMIS_WS=5445 ARTEMIS_TCP=61616 ARTEMIS_JMX=9404 ARTEMIS_UI=8161
ENV SHA512_VAL=af429525300ec7a8e995edd6ff62f65c7d805d52957947abfdb5f9393183b2ec78c201e952ba9e789081cc487767042e8ab207d46a6780617bc8211cab08caaf

ENV ARTEMIS_USER artemis
ENV ARTEMIS_PASSWORD artemis
ENV ANONYMOUS_LOGIN false
ENV EXTRA_ARGS --http-host 0.0.0.0 --relax-jolokia

ENV ARTEMIS_HOME /opt/activemq-artemis

RUN curl "https://archive.apache.org/dist/activemq/activemq-artemis/$ARTEMIS_VERSION/$ARTEMIS-bin.tar.gz" -o $ARTEMIS-bin.tar.gz

# Validate checksum
RUN if [ "$SHA512_VAL" != "$(sha512sum $ARTEMIS-bin.tar.gz | awk '{print($1)}')" ];\
    then \
        echo "sha512 values doesn't match! exiting."  && \
        exit 1; \
    fi;

COPY ./docker-run.sh /

RUN tar xzf $ARTEMIS-bin.tar.gz -C  /opt && \
    ln -s /opt/$ARTEMIS $ARTEMIS_HOME && \
    useradd -r -M -d $ARTEMIS_HOME artemis && \
    chown -R artemis:artemis /opt/$ARTEMIS && \
    chown -h artemis:artemis $ARTEMIS_HOME && \
    mkdir /var/lib/artemis-instance && \
    chown -R artemis:artemis /var/lib/artemis-instance

USER artemis

EXPOSE $ARTEMIS_STOMP $ARTEMIS_MQTT $ARTEMIS_AMQP $ARTEMIS_WS $ARTEMIS_TCP $ARTEMIS_JMX $ARTEMIS_UI

# Expose some outstanding folders
VOLUME ["/var/lib/artemis-instance"]
WORKDIR /var/lib/artemis-instance

ENTRYPOINT ["/docker-run.sh"]
CMD ["run"]