-- Mikroservis başına bir DB. Her servisin Flyway'i kendi DB'sinde çalışır.
-- Postgres container ilk açılışta /docker-entrypoint-initdb.d/*.sql dosyalarını otomatik çalıştırır.

CREATE DATABASE saga1_order;
CREATE DATABASE saga1_stock;
CREATE DATABASE saga1_payment;
CREATE DATABASE saga1_bank;
