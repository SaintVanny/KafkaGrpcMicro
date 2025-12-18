#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL

    CREATE DATABASE orders_db;
    GRANT ALL PRIVILEGES ON DATABASE orders_db TO $POSTGRES_USER;

    CREATE DATABASE inventory_db;
    GRANT ALL PRIVILEGES ON DATABASE inventory_db TO $POSTGRES_USER;

    \l
EOSQL

echo "✅ Databases orders_db and inventory_db have been created successfully"