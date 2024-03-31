FROM dph5199278/tomcat-native-lib:2.0-jre21

ARG DEBIAN_FRONTEND=noninteractive
ARG VCS_REF
ARG APPLICATION_CUSTOMER_ENTITY
ARG APPLICATION_BUILD_DATESTR

LABEL author="Dely" \
   email="dph5199278@163.com" \
   org.label-schema.vcs-ref=$VCS_REF \
   org.label-schema.vcs-url="https://github.com/dph5199278/wit"

ENV APPLICATION_CUSTOMER_ENTITY=$APPLICATION_CUSTOMER_ENTITY \
   APPLICATION_BUILD_DATESTR=$APPLICATION_BUILD_DATESTR \
   # Set the locale
   LANG=C.UTF-8 \
   LANGUAGE=en_US:en \
   LC_ALL=C.UTF-8

# create dirs
RUN set -eux; \
   mkdir -p /{data,logs} && \
   mkdir -p /{opt/wit,opt/wit/upgrade} \
   # install other lib and configure timezone
   apt-get update && \
   apt-get install --no-install-recommends -y tzdata iputils-ping default-mysql-client && \
   ln -sf /usr/share/zoneinfo/Asia/Shanghai  /etc/localtime && \
   DEBIAN_FRONTEND=noninteractive dpkg-reconfigure --frontend noninteractive tzdata && \
   rm -rf /var/lib/apt/lists/*

# copy shell
COPY ./assets/ /opt/wit/
RUN chmod +x /opt/wit/*.sh

# build JAR
COPY ./app/target/contact-center.jar /opt/wit/contact-center.jar

# copy sql script
COPY ./app/target/sql/ /tmp/ROOT/

WORKDIR /opt/wit
EXPOSE 8000-10000
ENTRYPOINT ["/opt/wit/docker-entrypoint.sh"]
