FROM eclipse-temurin:21-jre-jammy

EXPOSE 8080

COPY target/P2SignalNotification-*.jar P2SignalNotification.jar

HEALTHCHECK --interval=15s --timeout=15s --retries=3 \
    CMD wget -q -O /dev/null http://localhost:8080/health || exit 1

ENTRYPOINT java -jar P2SignalNotification.jar