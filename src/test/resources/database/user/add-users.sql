insert into roles (id, name)
values (1, 'ROLE_MANAGER'),
       (2, 'ROLE_CUSTOMER');

insert into users (id, email, password, first_name, last_name, is_deleted)
values (1, 'customer@example.com', '$2a$10$.tc/w4TOXUJS9OZLyGhp/eRDuCnlBCuVuhE0FW4MaET2CHpyQHJ9a', 'Customer', 'Userovski', false),
       (2, 'manager@exemple.com', '$2a$10$cB0N5IINQoQh1SJfskt9pO7jMeD.yIHbmoZTaS..3UGVZkzT26OgO', 'Manager', 'Adminovski', false),
       (3, 'ihor.sydorenko@gmail.com', '$2a$10$.tc/w4TOXUJS9OZLyGhp/eRDuCnlBCuVuhE0FW4MaET2CHpyQHJ9a', 'Ihor', 'Sydorenko', false);

insert into users_roles (user_id, role_id)
values (1, 2),
       (2, 1),
       (2, 2),
       (3, 2);
