drop sequence if exists _user_seq;
drop sequence if exists social_account_seq;
drop table if exists _user,social_account,user_roles cascade;

create sequence _user_seq start with 1 increment by 50;

create sequence social_account_seq start with 1 increment by 50;

CREATE TABLE _user (
  id BIGINT NOT NULL,
   created_date TIMESTAMP,
   last_modified_date TIMESTAMP,
   email VARCHAR_IGNORECASE(255) NOT NULL,
   lemail VARCHAR_IGNORECASE(255) as lower(email),
   username VARCHAR_IGNORECASE(255),
   lusername VARCHAR_IGNORECASE(255) as lower(username),
   password VARCHAR(255),
   email_verified_date TIMESTAMP,
   profile_image_url VARCHAR(255),
   username_last_modified_date TIMESTAMP,
   CONSTRAINT pk__user PRIMARY KEY (id)
);

create unique index idx_lower_username ON _user (lusername);
create unique index idx_lower_email ON _user (lemail);

create table user_roles (user_id bigint not null, roles enum ('ADMIN','BASIC_USER','OWNER','STAFF'));

alter table user_roles add constraint FK_USERROLE_ON_USER foreign key (user_id) references _user;

CREATE TABLE social_account (
  id BIGINT NOT NULL,
   principal_name VARCHAR(255),
   provider_id VARCHAR(255),
   created_date TIMESTAMP,
   last_modified_date TIMESTAMP,
   email VARCHAR(255) NOT NULL,
   user_id BIGINT NOT NULL,
   CONSTRAINT pk_socialaccount PRIMARY KEY (id)
);

ALTER TABLE social_account ADD CONSTRAINT uc_3db3ad501afd46463c949cec3 UNIQUE (principal_name, provider_id);

ALTER TABLE social_account ADD CONSTRAINT uc_socialaccount_pridusid UNIQUE (provider_id, user_id);

ALTER TABLE social_account ADD CONSTRAINT FK_SOCIALACCOUNT_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);
