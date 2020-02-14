DELETE FROM users;
DELETE FROM user_roles;
DELETE FROM restaurants;
DELETE FROM menus;
DELETE FROM dishes;
DELETE FROM votes;

ALTER SEQUENCE global_seq RESTART WITH 1000;

INSERT INTO users (name, email, password)
VALUES  ('User1', 'user1@gmail.com', '{noop}password'),
        ('User2', 'user2@gmail.com', '{noop}password'),
        ('Admin', 'admin@gmail.com', '{noop}admin');

INSERT INTO user_roles (role, user_id)
VALUES  ('ROLE_USER', 1000),
        ('ROLE_USER', 1001),
        ('ROLE_ADMIN', 1002),
        ('ROLE_USER', 1002);

INSERT INTO restaurants (name)
VALUES ('French Restaurant'),  --1003
       ('Chinese Restaurant'), --1004
       ('Italian Restaurant'); --1005


INSERT INTO menus (restaurant_id, date)
VALUES  (1003, current_date),                    --1006
        (1003, current_date + INTERVAL '1' DAY), --1007
        (1003, current_date - INTERVAL '1' DAY), --1008
        (1004, current_date),                    --1009
        (1004, current_date + INTERVAL '1' DAY), --1010
        (1004, current_date - INTERVAL '1' DAY), --1011
        (1005, current_date - INTERVAL '2' DAY), --1012
        (1005, current_date + INTERVAL '1' DAY), --1013
        (1005, current_date - INTERVAL '1' DAY); --1014

INSERT INTO dishes (menu_id, name, price)
VALUES  (1006, 'House salad', 350),
        (1006, 'Steak-Frites', 2000),
        (1007, 'Wild mushroom risotto', 1200),
        (1007, 'Roasted haddock fillet', 1300),
        (1008, 'Moroccan chicken', 1500),
        (1008, 'House burger', 1500),
        (1006, 'Ice cream & sorbet', 450),
        (1007, 'Eton mess', 450),
        (1008, 'Chocolate brownie', 650),
        (1009, 'Shanghai lamb', 1325),
        (1009, 'Oyster beef', 950),
        (1010, 'Crispy ginger beef', 1095),
        (1010, 'Lamb Beiging', 950),
        (1011, 'Sweet & sour chicken', 760),
        (1011, 'Pork momos', 950),
        (1009, 'Fried banana with ice cream', 475),
        (1010, 'Fresh fruit salad with mango sauce', 650),
        (1011, 'Toffee apples with ice cream', 650),
        (1012, 'Mozzarella buffala salad with grilled vegetables', 1100),
        (1012, 'Salmon tartar with avocado and strawberry salad', 1200),
        (1013, 'Parma ham with melon', 1100),
        (1013, 'Beef carpaccio with marinated champignons and rucola', 1300),
        (1014, 'Vitello Tonnato with rucket salad and caper leaves', 1300),
        (1014, 'Grilled octopus with potatoes, green olives and cherry tomatoes', 1600),
        (1012, 'Tiramisu', 800),
        (1013, 'Creme brulee', 700),
        (1014, 'Panna cotta della cassa', 700); --1041

INSERT INTO votes (user_id, restaurant_id, date)
VALUES  (1000, 1003, current_date),                    -- 1042
        (1000, 1003, current_date - INTERVAL '1' DAY), -- 1043
        (1001, 1003, current_date - INTERVAL '1' DAY), -- 1044
        (1002, 1003, current_date - INTERVAL '1' DAY); -- 1045

INSERT INTO restaurants (name)
VALUES ('Estonian Restaurant'); --1046

