alter table expense_members
    drop constraint expense_members_expense_id_fkey,
    add constraint expense_members_expense_id_fkey
        foreign key (expense_id) references expenses (id) on delete cascade;

alter table expenses
    drop constraint expenses_receipt_id_fkey,
    add constraint expenses_receipt_id_fkey
        foreign key (receipt_id) references receipts (id) on delete cascade;

alter table receipts
    drop constraint receipts_event_id_fkey,
    add constraint receipts_event_id_fkey
        foreign key (event_id) references events (id) on delete cascade;

alter table events
    drop constraint events_owner_id_fkey,
    add constraint events_owner_id_fkey
        foreign key (owner_id) references registered_users (user_id) on delete cascade;