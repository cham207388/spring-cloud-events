
    create table order (
        id bigint not null,
        customer_name varchar(255),
        status varchar(255),
        total_amount numeric(38,2),
        primary key (id)
    );
