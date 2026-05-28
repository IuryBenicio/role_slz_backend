CREATE TABLE user_relationships (
    follower_id BIGINT NOT NULL,
    followed_id BIGINT NOT NULL,

    PRIMARY KEY (follower_id, followed_id),

    CONSTRAINT fk_follower
        FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT fk_followed
        FOREIGN KEY (followed_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_follower_id ON user_relationships(follower_id);
CREATE INDEX idx_followed_id ON user_relationships(followed_id);