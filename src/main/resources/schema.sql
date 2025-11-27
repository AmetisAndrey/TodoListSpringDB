CREATE TABLE IF NOT EXISTS tasks (
    id   bigserial primary key,
    title varchar(255) not null,
    description text,
    status varchar(50) not null,
    priority varchar(50) not null,
    due_date date,
    created_at timestamp not null,
    updated_at timestamp not null
    );


CREATE TABLE IF NOT EXISTS authorities(
    username varchar(50) not null,
    password varchar(50) not null,
    constraint fk_authorities_users
        foreign KEY (username) references users(username)
);

CREATE UNIQUE INDEX IF NOT EXISTS ix_auth_username
    on authorities (username, authority);


INSERT INTO users (username, password, enabled)
VALUES ('admin', '{noop}admin', TRUE)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO authorities (username, authority)
VALUES ('admin', 'ROLE_ADMIN')
    ON CONFLICT (username, authority) DO NOTHING;
