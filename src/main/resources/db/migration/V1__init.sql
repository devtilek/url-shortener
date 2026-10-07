-- =========================================
-- V1: initial schema
-- =========================================

CREATE TABLE users (
                       id           BIGSERIAL PRIMARY KEY,
                       username     VARCHAR(50)  NOT NULL UNIQUE,
                       email        VARCHAR(120) NOT NULL UNIQUE,
                       password     VARCHAR(255) NOT NULL,
                       role         VARCHAR(20)  NOT NULL DEFAULT 'USER',
                       created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE links (
                       id           BIGSERIAL PRIMARY KEY,
                       code         VARCHAR(20)  NOT NULL UNIQUE,
                       original_url TEXT         NOT NULL,
                       user_id      BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       clicks_count BIGINT       NOT NULL DEFAULT 0,
                       expires_at   TIMESTAMP,
                       created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_links_code     ON links(code);
CREATE INDEX idx_links_user_id  ON links(user_id);

CREATE TABLE clicks (
                        id            BIGSERIAL PRIMARY KEY,
                        link_id       BIGINT      NOT NULL REFERENCES links(id) ON DELETE CASCADE,
                        clicked_at    TIMESTAMP   NOT NULL DEFAULT NOW(),
                        ip_address    VARCHAR(45),
                        user_agent    VARCHAR(500),
                        referer       VARCHAR(500)
);

CREATE INDEX idx_clicks_link_id    ON clicks(link_id);
CREATE INDEX idx_clicks_clicked_at ON clicks(clicked_at);