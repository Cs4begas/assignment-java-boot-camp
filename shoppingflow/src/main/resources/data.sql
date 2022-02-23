CREATE TABLE product (
  id SERIAL PRIMARY KEY,
  name varchar,
  img varchar,
  price double,
  discount double,
  rating double,
  full_price double,
  warranty int,
  created_at datetime,
  product_type_id int
);

CREATE TABLE product_size (
  id SERIAL PRIMARY KEY,
  product_type_id int NOT NULL,
  description varchar
);

CREATE TABLE product_type (
  id SERIAL PRIMARY KEY,
  description varchar
);

CREATE TABLE customer (
  id SERIAL PRIMARY KEY,
  name varchar,
  last_name varchar,
  address varchar,
  postal_code int,
  district varchar,
  province varchar,
  phone_number varchar,
  created_at datetime
);

CREATE TABLE basket (
  id SERIAL PRIMARY KEY,
  customer_id int,
  created_at datetime
);

CREATE TABLE basket_item (
   id SERIAL PRIMARY KEY,
    basket_id int,
    product_id int,
    item_name varchar,
    item_price double,
    item_size varchar,
    item_discount double,
    item_net_price double,
    created_at datetime
);

CREATE TABLE payment_type (
  id SERIAL PRIMARY KEY,
  description varchar
);

CREATE TABLE basket_order (
  id SERIAL PRIMARY KEY,
  basket_id int,
  basket_payment_id int,
  payment_id int,
  coupon_id int,
  order_status_id int,
  invoice_number int,
  order_amount double,
  created_at datetime
);

CREATE TABLE basket_payment (
  id SERIAL PRIMARY KEY,
  payment_type_id int,
  card_number int,
  card_owner_name varchar,
  card_expired_month int,
  card_expired_year int,
  card_ccv_cvv int,
  payer varchar,
  created_at datetime
);

CREATE TABLE coupon (
  id SERIAL PRIMARY KEY,
  code varchar,
  discount double,
  is_deleted boolean
);

CREATE TABLE order_status (
  id SERIAL PRIMARY KEY,
  description varchar
);

ALTER TABLE product ADD FOREIGN KEY (product_type_id) REFERENCES product_type (id);

ALTER TABLE product_size ADD FOREIGN KEY (product_type_id) REFERENCES product_type (id);

ALTER TABLE basket ADD FOREIGN KEY (customer_id) REFERENCES customer (id);

ALTER TABLE basket_item ADD FOREIGN KEY (basket_id) REFERENCES basket (id);

ALTER TABLE basket_item ADD FOREIGN KEY (product_id) REFERENCES product (id);

ALTER TABLE basket_order ADD FOREIGN KEY (basket_id) REFERENCES basket (id);

ALTER TABLE basket_order ADD FOREIGN KEY (basket_payment_id) REFERENCES basket_payment (id);

ALTER TABLE basket_order ADD FOREIGN KEY (coupon_id) REFERENCES coupon (id);

ALTER TABLE basket_order ADD FOREIGN KEY (order_status_id) REFERENCES order_status (id);

ALTER TABLE basket_payment ADD FOREIGN KEY (payment_type_id) REFERENCES payment_type (id);

--migration product_type
insert into product_type (id,description) VALUES (1,'รองเท้า');
insert into product_type (id,description) VALUES (2,'เสื้อผ้า');

--migration product_size
insert into product_size (id,description,product_type_id) VALUES (1,'32',1);
insert into product_size (id,description,product_type_id) VALUES (2,'33',1);
insert into product_size (id,description,product_type_id) VALUES (3,'34',1);
insert into product_size (id,description,product_type_id) VALUES (4,'35',1);
insert into product_size (id,description,product_type_id) VALUES (5,'36',1);
insert into product_size (id,description,product_type_id) VALUES (6,'37',1);
insert into product_size (id,description,product_type_id) VALUES (7,'38',1);
insert into product_size (id,description,product_type_id) VALUES (8,'40',1);
insert into product_size (id,description,product_type_id) VALUES (9,'S',2);
insert into product_size (id,description,product_type_id) VALUES (10,'M',2);
insert into product_size (id,description,product_type_id) VALUES (11,'L',2);
insert into product_size (id,description,product_type_id) VALUES (12,'XL',2);

--migration payment_type
insert into payment_type (id,description) VALUES (1,'บัตรเครดิต');
insert into payment_type (id,description) VALUES (2,'เก็บเงินปลายทาง');
insert into payment_type (id,description) VALUES (3,'บัตรเดบิต');

--migration coupon
insert into coupon (id,code,discount,is_deleted) VALUES (1,'Lazadee',0.1,FALSE);
insert into coupon (id,code,discount,is_deleted) VALUES (2,'LazaDiscount',0.2,FALSE);

--migration products
insert into product (id,name,img,price,discount,rating,full_price,warranty,created_at,product_type_id) VALUES (1,'Adidas Yeezy Boost 350 V2','https://unsplash.com/photos/Cgb4gMKRcMA',28900,0.28,null,39900,2,now(),1);
insert into product (id,name,img,price,discount,rating,full_price,warranty,created_at,product_type_id) VALUES (2,'Adidas NMD R1 Primeknit CoreBlack/Core Black','https://unsplash.com/photos/zMaQFh-0ajA',9900,0.34,null,15000,2,now(),1);
insert into product (id,name,img,price,discount,rating,full_price,warranty,created_at,product_type_id) VALUES (3,'Adidas NMD R1 PK Japan Triple Black (BZ0220)','https://unsplash.com/photos/XwWGyrVidZE',12900,0.14,null,15000,2,now(),1);
insert into product (id,name,img,price,discount,rating,full_price,warranty,created_at,product_type_id) VALUES (4,'POCA SHOE NMD Sneakers Fashion รองเท้า ลำลองผ้าใบ ผู้หญิง-ผู้ชาย แฟชั่น ราคาถูกสวย ๆ Sport Unisex รุ่น PSN-Black/White','https://unsplash.com/photos/3Bn1F4BxumI',399,0.79,3.5,1900,2,now(),1);
insert into product (id,name,img,price,discount,rating,full_price,warranty,created_at,product_type_id) VALUES (5,'Adidas NMD R1 Color Black/Icey Blue (BY9951)','https://unsplash.com/photos/uc0oMfwLmLs',7990,0.33,4,12000,2,now(),1);

--migration customers
insert into customer (id,name,last_name,address,postal_code,district,province,phone_number,created_at) VALUES (1,'เมสซี่','ลีโอเนล','408/13 อาคารพหลโยธินเพลส' ,24521,'ลาดสวาย','ปทุมธานี','0890654212',now());
insert into customer (id,name,last_name,address,postal_code,district,province,phone_number,created_at) VALUES (2,'โรนัลโด้','คริสเตียนโน่','89/3 หมู่ 4 เพชรเกษม',10160,'บางแค','กทม','0832456421',now());

----migration order_status
insert into order_status (id,description) VALUES (1,'Checkout');
insert into order_status (id,description) VALUES (2,'Confirm-Shipping');
insert into order_status (id,description) VALUES (3,'Confirm-Order');