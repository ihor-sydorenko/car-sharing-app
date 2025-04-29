insert into roles (id, name)
values (1, 'ROLE_MANAGER'),
       (2, 'ROLE_CUSTOMER');

insert into users (id, email, password, first_name, last_name, is_deleted)
values (1, 'customer@example.com', 'user12345', 'Customer', 'Userovski', false),
       (2, 'nelia@example.com', 'user12345', 'Nelia', 'Sydorenko', false),
       (3, 'ihor@example.com', 'user12345', 'Ihor', 'Sydorenko', false);

insert into users_roles (user_id, role_id)
values (1, 2),
       (2, 2);

insert into cars (id, model, brand, type, inventory, daily_fee, is_deleted)
values (1, 'Jetta GLI', 'Volkswagen', 'SEDAN', 5, 149.00, false),
       (2, 'Jetta2', 'Volkswagen', 'SEDAN', 10, 109.00, false);

insert into rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted, is_active)
values (1, '2025-04-10', '2025-04-15', '2025-04-15', 1, 1, false, false),
       (2, current_date, current_date + interval '1' day, null, 1, 1, false, true),
       (3, '2025-04-25', '2025-04-27', null, 2, 2, false, true);
