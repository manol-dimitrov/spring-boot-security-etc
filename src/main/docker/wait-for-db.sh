#!/bin/bash
# wait-for-db.sh

set -e

host="$1"
shift
cmd="$@"

until mysql -h "$host" -u "root" -c '\l'; do
  >&2 echo "MySql is unavailable - sleeping"
  sleep 1
done

>&2 echo "MySql is up - executing command..."
exec $cmd