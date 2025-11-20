-------------------------------
-- META COST DATA
-------------------------------
-- Smart Home Bundle (4 rows)
INSERT INTO meta_cost VALUES ('M1','Smart Home Bundle','ACTIVE',120.00,8000,'2025-11-13 09:00:00','2025-11-13 17:00:00');
INSERT INTO meta_cost VALUES ('M1','smart home bundle','ACTIVE',135.50,8500,'2025-11-13 14:30:00','2025-11-13 22:30:00');
INSERT INTO meta_cost VALUES ('M1','SMART HOME BUNDLE',NULL,0.00,7000,'2025-11-13 20:10:00','2025-11-14 04:10:00');
INSERT INTO meta_cost VALUES ('M1','Smart  Home   Bundle','ACTIVE',150.75,9000,'2025-11-14 03:15:00','2025-11-14 11:15:00');

-- Winter Jacket Promo (3 rows)
INSERT INTO meta_cost VALUES ('M2','Winter Jacket Promo','ACTIVE',90.00,6000,'2025-11-13 08:45:00','2025-11-13 16:45:00');
INSERT INTO meta_cost VALUES ('M2','winter jacket promo','ACTIVE',95.00,6500,'2025-11-13 18:20:00','2025-11-14 02:20:00');
INSERT INTO meta_cost VALUES ('M2','WINTER JACKET PROMO','PAUSED',0.00,5000,'2025-11-14 06:00:00','2025-11-14 14:00:00');

-- Organic Coffee Launch (5 rows)
INSERT INTO meta_cost VALUES ('M3','Organic Coffee Launch','ACTIVE',200.00,15000,'2025-11-13 11:20:00','2025-11-13 19:20:00');
INSERT INTO meta_cost VALUES ('M3','organic coffee launch','ACTIVE',220.50,16000,'2025-11-13 16:40:00','2025-11-14 00:40:00');
INSERT INTO meta_cost VALUES ('M3','Organic coffee  launch','ACTIVE',240.00,17000,'2025-11-13 21:10:00','2025-11-14 05:10:00');
INSERT INTO meta_cost VALUES ('M3','ORGANIC COFFEE LAUNCH',NULL,0.00,12000,'2025-11-14 03:00:00','2025-11-14 11:00:00');
INSERT INTO meta_cost VALUES ('M3','Organic-Coffee Launch','ACTIVE',260.00,17500,'2025-11-14 07:30:00','2025-11-14 15:30:00');

-- Fitness Tracker Ads (2 rows)
INSERT INTO meta_cost VALUES ('M4','Fitness Tracker Ads','ACTIVE',310.00,20000,'2025-11-13 12:55:00','2025-11-13 20:55:00');
INSERT INTO meta_cost VALUES ('M4','fitness tracker ads','ACTIVE',330.25,21000,'2025-11-13 17:50:00','2025-11-14 01:50:00');

-- Kids Learning App (3 rows)
INSERT INTO meta_cost VALUES ('M5','Kids Learning App','ACTIVE',45.00,3000,'2025-11-13 10:10:00','2025-11-13 18:10:00');
INSERT INTO meta_cost VALUES ('M5','kids learning app',NULL,0.00,2500,'2025-11-13 21:40:00','2025-11-14 05:40:00');
INSERT INTO meta_cost VALUES ('M5','KIDS LEARNING APP','ACTIVE',55.00,3500,'2025-11-14 06:35:00','2025-11-14 14:35:00');


-------------------------------
-- SNAPCHAT COST DATA
-------------------------------
-- Smart Home Bundle (3 rows)
INSERT INTO snapchat_cost VALUES ('S1','Smart Home Bundle','ACTIVE',100.00,7000,'2025-11-13 09:30:00','2025-11-13 17:30:00');
INSERT INTO snapchat_cost VALUES ('S1','smart home bundle','ACTIVE',110.00,7500,'2025-11-13 19:20:00','2025-11-14 03:20:00');
INSERT INTO snapchat_cost VALUES ('S1','SMART HOME BUNDLE',NULL,0.00,6000,'2025-11-14 04:00:00','2025-11-14 12:00:00');

-- Winter Jacket Promo (2 rows)
INSERT INTO snapchat_cost VALUES ('S2','Winter Jacket Promo','ACTIVE',35.00,4000,'2025-11-13 10:00:00','2025-11-13 18:00:00');
INSERT INTO snapchat_cost VALUES ('S2','winter jacket promo','ACTIVE',40.00,4500,'2025-11-13 22:15:00','2025-11-14 06:15:00');

-- Organic Coffee Launch (4 rows)
INSERT INTO snapchat_cost VALUES ('S3','Organic Coffee Launch','ACTIVE',60.00,5000,'2025-11-13 11:30:00','2025-11-13 19:30:00');
INSERT INTO snapchat_cost VALUES ('S3','organic coffee launch','ACTIVE',65.00,5200,'2025-11-13 20:30:00','2025-11-14 04:30:00');
INSERT INTO snapchat_cost VALUES ('S3','ORGANIC COFFEE LAUNCH',NULL,0.00,4500,'2025-11-14 05:10:00','2025-11-14 13:10:00');
INSERT INTO snapchat_cost VALUES ('S3','Organic-Coffee Launch','ACTIVE',70.00,5400,'2025-11-14 07:45:00','2025-11-14 15:45:00');

-- Fitness Tracker Ads (3 rows)
INSERT INTO snapchat_cost VALUES ('S4','Fitness Tracker Ads','ACTIVE',150.00,14000,'2025-11-13 12:20:00','2025-11-13 20:20:00');
INSERT INTO snapchat_cost VALUES ('S4','fitness tracker ads','ACTIVE',160.00,15000,'2025-11-13 21:55:00','2025-11-14 05:55:00');
INSERT INTO snapchat_cost VALUES ('S4','FITNESS TRACKER ADS',NULL,0.00,12000,'2025-11-14 06:40:00','2025-11-14 14:40:00');

-- Kids Learning App (2 rows)
INSERT INTO snapchat_cost VALUES ('S5','Kids Learning App','ACTIVE',20.00,2500,'2025-11-13 11:00:00','2025-11-13 19:00:00');
INSERT INTO snapchat_cost VALUES ('S5','kids learning app','ACTIVE',22.00,2600,'2025-11-13 23:45:00','2025-11-14 07:45:00');


-------------------------------
-- REVENUE TABLE DATA
-------------------------------
-- Smart Home Bundle
INSERT INTO revenue_table VALUES ('Smart Home Bundle',400,350,30,2500,1200,500.00,'2025-11-13 18:00:00');
INSERT INTO revenue_table VALUES ('smart home bundle',420,360,32,2600,1300,520.00,'2025-11-14 01:00:00');
INSERT INTO revenue_table VALUES ('SMART HOME BUNDLE',410,355,31,2550,1250,510.00,'2025-11-14 07:20:00');

-- Winter Jacket Promo
INSERT INTO revenue_table VALUES ('Winter Jacket Promo',180,160,20,1400,600,250.00,'2025-11-13 17:20:00');
INSERT INTO revenue_table VALUES ('winter jacket promo',190,170,22,1500,650,270.00,'2025-11-14 04:30:00');

-- Organic Coffee Launch
INSERT INTO revenue_table VALUES ('Organic Coffee Launch',700,650,50,4000,2000,900.00,'2025-11-13 20:10:00');
INSERT INTO revenue_table VALUES ('organic coffee launch',720,660,52,4200,2050,930.00,'2025-11-14 02:20:00');
INSERT INTO revenue_table VALUES ('Organic-Coffee Launch',750,680,55,4300,2100,950.00,'2025-11-14 06:40:00');

-- Fitness Tracker Ads
INSERT INTO revenue_table VALUES ('Fitness Tracker Ads',1200,1150,75,6000,3000,1800.00,'2025-11-13 23:30:00');
INSERT INTO revenue_table VALUES ('fitness tracker ads',1250,1180,80,6200,3100,1850.00,'2025-11-14 07:15:00');

-- Kids Learning App
INSERT INTO revenue_table VALUES ('Kids Learning App',90,80,10,700,300,120.00,'2025-11-13 15:30:00');
INSERT INTO revenue_table VALUES ('kids learning app',95,85,12,750,320,130.00,'2025-11-14 05:30:00');
