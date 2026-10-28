# 1. aşama: Debezium'un Postgres eklentisini indir
FROM alpine:3 AS plugin
ARG DEBEZIUM_VERSION=3.6.2.Final
RUN apk add --no-cache curl \
 && mkdir -p /plugins \
 && curl -fsSL https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/${DEBEZIUM_VERSION}/debezium-connector-postgres-${DEBEZIUM_VERSION}-plugin.tar.gz \
    | tar xz -C /plugins

# 2. aşama: Strimzi'nin Kafka image'ına eklentiyi koy
FROM quay.io/strimzi/kafka:1.2.0-kafka-4.2.0
USER root:root
COPY --from=plugin /plugins/ /opt/kafka/plugins/
USER 1001