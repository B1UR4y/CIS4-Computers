CREATE TABLE IF NOT EXISTS computers (
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    brand   VARCHAR(50)  NOT NULL,
    model   VARCHAR(100) NOT NULL,
    cpu     VARCHAR(100) NOT NULL,
    type    VARCHAR(30)  NOT NULL,

    ram_gb  INTEGER       NOT NULL CHECK (ram_gb > 0),
    price   NUMERIC(12,2) NOT NULL CHECK (price >= 0)
);
