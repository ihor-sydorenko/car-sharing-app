insert into cars (id, model, brand, type, inventory, daily_fee, is_deleted)
values (2, 'Jetta2', 'Volkswagen', 'SEDAN', 10, 109.00, false);

insert into rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted, is_active)
values (4, '2025-04-25', '2025-04-27', null, 2, 2, false, true);