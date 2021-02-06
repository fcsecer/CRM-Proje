DROP DATABASE  IF EXISTS `spring_security`;

CREATE DATABASE  IF NOT EXISTS `spring_security`;
USE `spring_security`;

--
-- Tablo "kullanıcıları" için tablo yapısı oluşturuluyor
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` char(68) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Kullanıcı tablosundan veri alma
--
-- NOT: BCrypt kullanarak şifreleme yapıldı (öğrenmek amaçlı)
--

INSERT INTO `users` 
VALUES 
('musteri','{bcrypt}$2a$10$hSKRVsD2jI4o5YSUkOIKj.xVxJiQPXzPc6ae2Yry95kOnOmJFKrnK', 1),
('yonetici','{bcrypt}$2a$10$hSKRVsD2jI4o5YSUkOIKj.xVxJiQPXzPc6ae2Yry95kOnOmJFKrnK', 1),
('admin','{bcrypt}$2a$10$hSKRVsD2jI4o5YSUkOIKj.xVxJiQPXzPc6ae2Yry95kOnOmJFKrnK', 1),
('CanCan','{bcrypt}$2a$10$hSKRVsD2jI4o5YSUkOIKj.xVxJiQPXzPc6ae2Yry95kOnOmJFKrnK', 1);


--
-- "Kullanıcı bilgi" tablosu için tablo yapısı oluşturuluyor
--

DROP TABLE IF EXISTS `authorities`;
CREATE TABLE `authorities` (
  `username` varchar(50) NOT NULL,
  `authority` varchar(50) NOT NULL,
  UNIQUE KEY `authorities_idx_1` (`username`,`authority`),
  CONSTRAINT `authorities_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Kullanıcı bilgisinden veri alma
--

INSERT INTO `authorities` 
VALUES 
('musteri','ROLE_EMPLOYEE'),
('yonetici','ROLE_EMPLOYEE'),
('yonetici','ROLE_MANAGER'),
('admin','ROLE_EMPLOYEE'),
('admin','ROLE_ADMIN'),
('CanCan','ROLE_EMPLOYEE'),
('CanCan','ROLE_ADMIN');
