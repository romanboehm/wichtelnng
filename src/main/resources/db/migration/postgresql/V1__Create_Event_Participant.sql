CREATE sequence hibernate_sequence start with 1 increment by 1;

CREATE TABLE event (
    id uuid NOT NULL,
    description varchar(1000) NOT NULL,
    email varchar(255) NOT NULL,
    name varchar(100) NOT NULL,
    currency varchar(3) NOT NULL,
    number decimal(10,2) NOT NULL,
    title varchar(100) NOT NULL,
    local_date_time timestamp NOT NULL,
    zone_id varchar(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE participant (
    id bigint NOT NULL,
    email varchar(255) NOT NULL,
    name varchar(100) NOT NULL,
    event_id uuid NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (event_id) REFERENCES event (id)  ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE (name, email)
);