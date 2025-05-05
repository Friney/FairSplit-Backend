alter table registered_users
    add column password varchar(255);

update registered_users
set password = 'password'
where password is null;

alter table registered_users
    alter column password set not null;