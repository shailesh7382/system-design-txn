#!/bin/sh

echo "[INFO] Starting Liquibase Java runner script"

# Usage:
#   ./run-liquibase-java.sh [command] [dbType] [jdbcUrl] [username] [password] [changelog] [rollbackTagOrCount]
# Commands:
#   update                - Apply all pending migrations (default)
#   rollback <tag|count>  - Rollback to a tag or by count (see below)
#   status                - Show pending changesets
#
# Examples:
#   ./run-liquibase-java.sh update
#   ./run-liquibase-java.sh rollback h2 "" "" "" "" v1.0
#   ./run-liquibase-java.sh rollback h2 "" "" "" "" 1
#   ./run-liquibase-java.sh status clickhouse "jdbc:clickhouse://localhost:8123/default" default "" "db/changelog/db.changelog-master.sql"

JAR_PATH="target/liquibase-check-1.0-SNAPSHOT-shaded.jar"

echo "[INFO] Checking for fat JAR at $JAR_PATH"
if [ ! -f "$JAR_PATH" ]; then
  echo "[ERROR] Fat JAR not found at $JAR_PATH. Please run: mvn package"
  exit 1
fi

COMMAND="${1:-update}"
shift

echo "[INFO] Command: $COMMAND"
echo "[INFO] Arguments: $*"

case "$COMMAND" in
  update)
    echo "[INFO] Executing: java -jar $JAR_PATH update $*"
    java -jar "$JAR_PATH" update "$@"
    STATUS=$?
    ;;
  rollback)
    echo "[INFO] Executing: java -jar $JAR_PATH rollback $*"
    java -jar "$JAR_PATH" rollback "$@"
    STATUS=$?
    ;;
  status)
    echo "[INFO] Executing: java -jar $JAR_PATH status $*"
    java -jar "$JAR_PATH" status "$@"
    STATUS=$?
    ;;
  *)
    echo "[ERROR] Unknown command: $COMMAND"
    echo "[INFO] Usage: $0 [update|rollback|status] [dbType] [jdbcUrl] [username] [password] [changelog] [rollbackTagOrCount]"
    exit 1
    ;;
esac

if [ $STATUS -eq 0 ]; then
  echo "[INFO] Liquibase command '$COMMAND' completed successfully."
else
  echo "[ERROR] Liquibase command '$COMMAND' failed with exit code $STATUS."
fi

exit $STATUS
