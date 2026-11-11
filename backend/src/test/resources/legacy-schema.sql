CREATE TABLE companies (id UUID PRIMARY KEY, name VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL UNIQUE);
CREATE TABLE users (id UUID PRIMARY KEY, name VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL UNIQUE, password_hash VARCHAR(255) NOT NULL, company_id UUID NOT NULL REFERENCES companies(id));
CREATE TABLE leads (id UUID PRIMARY KEY, name VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL, phone VARCHAR(255), company_id UUID NOT NULL REFERENCES companies(id));
INSERT INTO companies VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa','Empresa legada','legacy@example.com');
INSERT INTO leads VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb','Lead legado','lead@example.com','11999999999','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');
