--users
insert into users(name, surname, phone_number, email, password, autentificated, role) values ('Pera', 'Peric', '+381234567', 'pera.peric@gmail.com', '$2a$12$7tKvSJjtxAajnmfeWIcj5OzS/h4p9XAQuPK6UWMSCiXiWX5oCXvxe', true, 'USER')
insert into users(name, surname, phone_number, email, password, autentificated, role) values ('Marko', 'Markovic', '+381278567', 'marko.markovic@gmail.com', '$2a$12$b0dwOABJwVkynC25bAgACec8oLse6K3yCVz2fPOYWZo5M5vMrVZRS', true, 'USER')
insert into users(name, surname, phone_number, email, password, autentificated, role) values ('Jovana', 'Jovanovic', '+386664567', 'jovana.jovanovic@gmail.com', '$2a$12$yl66nfVmBAs/BxioF.fqW.vjSEDEPFeb8g1p6zF4/5/BonhRJCiv6', true, 'ADMIN')

--certificates
insert into certificates(serial_number, valid_from, valid_to, status, certificate_type, email) values ('123', TIMESTAMP '2023-04-01 10:00:00', TIMESTAMP '2024-04-01 10:00:00', 'VALID', 'ROOT', 'pera.peric@gmail.com')
insert into certificates(serial_number, issuer_serial_number, valid_from, valid_to, status, certificate_type, email) values ('2135', '123', TIMESTAMP '2023-04-03 10:00:00', TIMESTAMP '2023-10-03 10:00:00', 'VALID', 'INTERMEDIATE', 'pera.peric@gmail.com')
insert into certificates(serial_number, issuer_serial_number, valid_from, valid_to, status, certificate_type, email) values ('548', '2135', TIMESTAMP '2023-04-04 10:00:00', TIMESTAMP '2023-07-04 10:00:00', 'VALID', 'END', 'marko.markovic@gmail.com')