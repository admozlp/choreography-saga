ALTER TABLE stocks
    ADD CONSTRAINT fk_stocks_product FOREIGN KEY (product_id) REFERENCES products (id);
