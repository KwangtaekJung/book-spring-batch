USE spring_batch;

DROP TABLE IF EXISTS customer;
CREATE TABLE customer  (
  id BIGINT  NOT NULL PRIMARY KEY ,
  firstName VARCHAR(20) NOT NULL ,
  middleInitial VARCHAR(1),
  lastName VARCHAR(20) NOT NULL,
  address VARCHAR(45) NOT NULL,
  city VARCHAR(16) NOT NULL,
  state CHAR(2) NOT NULL,
  zipCode CHAR(5)
);