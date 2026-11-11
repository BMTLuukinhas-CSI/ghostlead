-- Existing records intentionally remain unowned until an administrator verifies ownership.
ALTER TABLE users ALTER COLUMN company_id DROP NOT NULL;
ALTER TABLE companies ADD COLUMN owner_id UUID;
DO $$ DECLARE c RECORD; BEGIN
    FOR c IN SELECT conname FROM pg_constraint WHERE conrelid='companies'::regclass AND contype='u' AND conkey = ARRAY[(SELECT attnum FROM pg_attribute WHERE attrelid='companies'::regclass AND attname='email')]::smallint[]
    LOOP EXECUTE format('ALTER TABLE companies DROP CONSTRAINT %I', c.conname); END LOOP;
END $$;
ALTER TABLE companies ADD CONSTRAINT companies_owner_email_unique UNIQUE(owner_id,email);
CREATE INDEX companies_owner_idx ON companies(owner_id);
ALTER TABLE leads ADD COLUMN status VARCHAR(24) NOT NULL DEFAULT 'NEW';
ALTER TABLE leads ADD COLUMN deal_value NUMERIC(14,2) NOT NULL DEFAULT 0;
ALTER TABLE leads ADD COLUMN notes VARCHAR(4000);
ALTER TABLE leads ADD COLUMN created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE leads ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE leads ADD CONSTRAINT leads_status_check CHECK(status IN ('NEW','CONTACTED','PROPOSAL','WON','LOST'));
ALTER TABLE leads ADD CONSTRAINT leads_value_check CHECK(deal_value >= 0);
CREATE INDEX leads_company_created_idx ON leads(company_id,created_at DESC);
CREATE INDEX leads_company_status_idx ON leads(company_id,status);
