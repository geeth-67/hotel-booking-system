FROM docker.io/keycloak/keycloak:25.0.0

USER root

COPY realm-export.json /opt/keycloak/data/import/realm-export.json
COPY --chmod=755 healthcheck.sh /opt/keycloak/healthcheck.sh

RUN mkdir -p /opt/keycloak/data/import

USER 1000

HEALTHCHECK --interval=10s --timeout=5s --retries=20 --start-period=90s \
  CMD ["/bin/bash", "/opt/keycloak/healthcheck.sh"]