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