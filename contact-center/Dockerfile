FROM adoptopenjdk:8u292-b10-jre-hotspot
MAINTAINER Dely <dph5199278@163.com>

ARG DEBIAN_FRONTEND=noninteractive
ARG VCS_REF
ARG APPLICATION_CUSTOMER_ENTITY
ARG APPLICATION_BUILD_DATESTR

ENV APPLICATION_CUSTOMER_ENTITY=$APPLICATION_CUSTOMER_ENTITY
ENV APPLICATION_BUILD_DATESTR=$APPLICATION_BUILD_DATESTR

LABEL org.label-schema.vcs-ref=$VCS_REF \
          org.label-schema.vcs-url="https://github.com/dph5199278/wit"

# install other lib and configure timezone
RUN apt-get update && \
   apt-get install --no-install-recommends -y tzdata iputils-ping mysql-client-8.0 zip unzip vim-tiny libfontconfig1 libfreetype6 && \ 
   ln -sf /usr/share/zoneinfo/Asia/Shanghai  /etc/localtime && \
   DEBIAN_FRONTEND=noninteractive dpkg-reconfigure --frontend noninteractive tzdata && \
   rm -rf /var/lib/apt/lists/*

# Set the locale
ENV LANG C.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL C.UTF-8

# create dirs
RUN /bin/bash -c "mkdir -p /{data,logs}"

# build WAR
RUN mkdir -p /opt/wit
COPY ./app/target/contact-center-*.war /opt/wit/contact-center.war
COPY ./assets/mysql.setup.db.sh /opt/wit
COPY ./assets/mysql.upgrade.db.sh /opt/wit
COPY ./assets/utils.sh /opt/wit
COPY ./assets/docker-entrypoint.sh /opt/wit
RUN chmod +x /opt/wit/*.sh

WORKDIR /opt/wit
EXPOSE 8000-10000
CMD ["./docker-entrypoint.sh"]