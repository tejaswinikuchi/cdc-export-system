DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM users LIMIT 1) THEN

        INSERT INTO users (name, email, created_at, updated_at, is_deleted)
        SELECT
            'User ' || gs,
            'user' || gs || '@example.com',
            NOW() - (random() * interval '30 days'),
            NOW() - (random() * interval '7 days'),
            CASE
                WHEN gs % 100 = 0 THEN TRUE
                ELSE FALSE
            END
        FROM generate_series(1, 100000) AS gs;

    END IF;
END $$;