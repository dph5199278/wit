FROM openjdk:8u332-jre
LABEL author="Dely"
LABEL email="dph5199278@163.com"

ARG DEBIAN_FRONTEND=noninteractive
ARG VCS_REF
ARG APPLICATION_CUSTOMER_ENTITY
ARG APPLICATION_BUILD_DATESTR

ENV APPLICATION_CUSTOMER_ENTITY=$APPLICATION_CUSTOMER_ENTITY
ENV APPLICATION_BUILD_DATESTR=$APPLICATION_BUILD_DATESTR

LABEL org.label-schema.vcs-ref=$VCS_REF \
          org.label-schema.vcs-url="https://github.com/dph5199278/wit"

# Set the locale
ENV LANG C.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL C.UTF-8

# create dirs
RUN /bin/bash -c "mkdir -p /{data,logs}"
RUN /bin/bash -c "mkdir -p /{opt/wit,opt/wit/upgrade}"

# install other lib and configure timezone
RUN set -eux; \
   apt-get update && \
   apt-get install --no-install-recommends -y tzdata iputils-ping default-mysql-client && \
   ln -sf /usr/share/zoneinfo/Asia/Shanghai  /etc/localtime && \
   DEBIAN_FRONTEND=noninteractive dpkg-reconfigure --frontend noninteractive tzdata && \
   rm -rf /var/lib/apt/lists/*

# copy shell
COPY ./assets/mysql.setup.db.sh /opt/wit
COPY ./assets/mysql.upgrade.db.sh /opt/wit
COPY ./assets/utils.sh /opt/wit
COPY ./assets/docker-entrypoint.sh /opt/wit
RUN chmod +x /opt/wit/*.sh

# build JAR
COPY ./app/target/contact-center.jar /opt/wit/contact-center.jar

# copy sql script
COPY ./app/target/sql/cosinee-MySQL-slim.sql /tmp/ROOT/cosinee-MySQL-slim.sql
COPY ./app/target/sql/upgrade/* /tmp/ROOT/upgrade/

WORKDIR /opt/wit
EXPOSE 8000-10000
ENTRYPOINT ["/opt/wit/docker-entrypoint.sh"]
