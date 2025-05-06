alter table events
    add column owner_id bigint,
    add foreign key (owner_id) references registered_users (user_id);

update events
set owner_id = 2
where owner_id is null;

alter table events
    alter column owner_id set not null;