FROM postgres:15-alpine

# 環境変数設定
ENV POSTGRES_DB=scraper_db \
    POSTGRES_USER=scraper \
    POSTGRES_PASSWORD=password

# 初期化スクリプトをコンテナにコピー
COPY scripts/init.sql /docker-entrypoint-initdb.d/01-init.sql
COPY scripts/import_csv.sql /docker-entrypoint-initdb.d/02-import.sql

# DBファイルをボリュームマウント
VOLUME ["/var/lib/postgresql/data"]
