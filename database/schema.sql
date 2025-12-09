-- PostgreSQL Schema for REST API Benchmark
-- Database: benchmark

-- Drop existing tables if they exist
DROP TABLE IF EXISTS item CASCADE;
DROP TABLE IF EXISTS category CASCADE;

-- Category table
CREATE TABLE category (
    id            BIGSERIAL PRIMARY KEY,
    code          VARCHAR(32) UNIQUE NOT NULL,
    name          VARCHAR(128) NOT NULL,
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Item table
CREATE TABLE item (
    id            BIGSERIAL PRIMARY KEY,
    sku           VARCHAR(64) UNIQUE NOT NULL,
    name          VARCHAR(128) NOT NULL,
    price         NUMERIC(10,2) NOT NULL,
    stock         INT NOT NULL,
    category_id   BIGINT NOT NULL REFERENCES category(id),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX idx_item_category ON item(category_id);
CREATE INDEX idx_item_updated_at ON item(updated_at);
CREATE INDEX idx_category_code ON category(code);
CREATE INDEX idx_item_sku ON item(sku);

-- Add comments for documentation
COMMENT ON TABLE category IS 'Product categories (2000 rows expected)';
COMMENT ON TABLE item IS 'Product items (100000 rows expected, ~50 per category)';
COMMENT ON INDEX idx_item_category IS 'Index for JOIN queries and filtering by category';
COMMENT ON INDEX idx_item_updated_at IS 'Index for temporal queries';

-- Grant permissions (adjust as needed)
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO benchmark_user;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO benchmark_user;
