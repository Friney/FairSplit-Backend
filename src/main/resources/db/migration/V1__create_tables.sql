-- Зарегистрированные пользователи
create table if not exists users
(
    id       serial primary key,
    username varchar(255) not null,
    email    varchar(255) not null unique
);

-- Незарегистрированные пользователи (по сути просто ники людей, которые не зарегистрированы)
create table if not exists guests
(
    id       serial primary key,
    username varchar(255) not null
);

-- Ивент
create table if not exists events
(
    id          serial primary key,
    name        varchar(255)  not null,
    description varchar(2055) not null
);

-- Участники ивента (пользователь или гость, другой null)
create table if not exists event_members
(
    id       serial primary key,
    user_id  bigint default null,
    guest_id bigint default null,
    event_id bigint not null,
    foreign key (user_id) references users (id),
    foreign key (guest_id) references guests (id),
    foreign key (event_id) references events (id),
    constraint user_or_guest_not_null check (
        (user_id is not null and guest_id is null) or
        (user_id is null and guest_id is not null)
        )
);

-- Чеки (оплатил либо пользователь, либо гость, другой null)
create table if not exists receipts
(
    id               serial primary key,
    name             varchar(255) not null,
    event_id         bigint       not null,
    paid_by_user_id  bigint default null,
    paid_by_guest_id bigint default null,
    foreign key (event_id) references events (id),
    foreign key (paid_by_user_id) references users (id),
    foreign key (paid_by_guest_id) references guests (id),
    constraint paid_by_user_or_guest_not_null check (
        (paid_by_user_id is not null and paid_by_guest_id is null) or
        (paid_by_user_id is null and paid_by_guest_id is not null)
        )
);

-- Участники чека (пользователь или гость, другой null)
create table if not exists receipt_members
(
    id         serial primary key,
    user_id    bigint default null,
    guest_id   bigint default null,
    receipt_id bigint not null,
    foreign key (user_id) references users (id),
    foreign key (guest_id) references guests (id),
    foreign key (receipt_id) references receipts (id),
    constraint user_or_guest_not_null check (
        (user_id is not null and guest_id is null) or
        (user_id is null and guest_id is not null)
        )
);

-- Расходы в чеке
create table if not exists expenses
(
    id         serial primary key,
    name       varchar(255)   not null,
    amount     decimal(19, 2) not null,
    receipt_id bigint         not null,
    foreign key (receipt_id) references receipts (id)
);

-- Участники расхода (пользователь или гость, другой null)
create table if not exists expense_members
(
    id         serial primary key,
    user_id    bigint default null,
    guest_id   bigint default null,
    expense_id bigint not null,
    foreign key (user_id) references users (id),
    foreign key (guest_id) references guests (id),
    foreign key (expense_id) references expenses (id),
    constraint user_or_guest_not_null check (
        (user_id is not null and guest_id is null) or
        (user_id is null and guest_id is not null)
        )
);
