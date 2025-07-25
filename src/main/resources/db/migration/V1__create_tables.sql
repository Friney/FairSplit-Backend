-- пользователи
create table if not exists users
(
    id   bigserial primary key,
    name varchar(255) not null
);

-- Зарегистрированные пользователи
create table if not exists registered_users
(
    user_id bigint primary key,
    email   varchar(255) not null unique,
    foreign key (user_id) references users (id)
);

-- Незарегистрированные пользователи (по сути просто ники людей, которые не зарегистрированы)
create table if not exists not_registered_users
(
            user_id bigint primary key,
    foreign key (user_id) references users (id)
);

-- Ивент
create table if not exists events
(
    id          bigserial primary key,
    name        varchar(255)  not null,
    description varchar(2055) not null
);

-- Чеки
create table if not exists receipts
(
    id              bigserial primary key,
    name            varchar(255) not null,
    event_id        bigint       not null,
    paid_by_user_id bigint default null,
    foreign key (event_id) references events (id),
    foreign key (paid_by_user_id) references users (id)
);

-- Расходы в чеке
create table if not exists expenses
(
    id         bigserial primary key,
    name       varchar(255)   not null,
    amount     decimal(19, 2) not null,
    receipt_id bigint         not null,
    foreign key (receipt_id) references receipts (id)
);

-- Участники расхода
create table if not exists expense_members
(
    id         bigserial primary key,
    user_id    bigint default null,
    expense_id bigint not null,
    foreign key (user_id) references users (id),
    foreign key (expense_id) references expenses (id)
);
