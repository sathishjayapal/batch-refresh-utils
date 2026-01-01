CREATE SEQUENCE  IF NOT EXISTS primary_sequence START WITH 10000 INCREMENT BY 1;

CREATE TABLE batch_job (
    id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    schedule_cron VARCHAR(100),
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    date_created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT batch_job_pkey PRIMARY KEY (id)
);

CREATE TABLE batch_job_run (
    id BIGINT NOT NULL,
    started_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    finished_at TIMESTAMP WITHOUT TIME ZONE,
    status VARCHAR(30) NOT NULL,
    trigger_type VARCHAR(30) NOT NULL,
    input_params TEXT,
    result_summary TEXT,
    error_message TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    job_id BIGINT NOT NULL,
    date_created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT batch_job_run_pkey PRIMARY KEY (id)
);

CREATE TABLE email_template (
    id BIGINT NOT NULL,
    code VARCHAR(100) NOT NULL,
    subject_template TEXT NOT NULL,
    body_template TEXT NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    date_created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT email_template_pkey PRIMARY KEY (id)
);

CREATE TABLE job_run_email (
    id BIGINT NOT NULL,
    recipient_email VARCHAR(320) NOT NULL,
    recipient_name VARCHAR(200),
    subject TEXT NOT NULL,
    body TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    sent_at TIMESTAMP WITHOUT TIME ZONE,
    error_message TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    job_run_id BIGINT NOT NULL,
    template_id BIGINT,
    date_created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT job_run_email_pkey PRIMARY KEY (id)
);

CREATE TABLE job_notification_subscription (
    id BIGINT NOT NULL,
    recipient_email VARCHAR(320) NOT NULL,
    notify_on VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    job_id BIGINT NOT NULL,
    template_id BIGINT NOT NULL,
    date_created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT job_notification_subscription_pkey PRIMARY KEY (id)
);

ALTER TABLE batch_job_run ADD CONSTRAINT fk_batch_job_run_job_id FOREIGN KEY (job_id) REFERENCES batch_job (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE job_run_email ADD CONSTRAINT fk_job_run_email_job_run_id FOREIGN KEY (job_run_id) REFERENCES batch_job_run (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE job_run_email ADD CONSTRAINT fk_job_run_email_template_id FOREIGN KEY (template_id) REFERENCES email_template (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE job_notification_subscription ADD CONSTRAINT fk_job_notification_subscription_job_id FOREIGN KEY (job_id) REFERENCES batch_job (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE job_notification_subscription ADD CONSTRAINT fk_job_notification_subscription_template_id FOREIGN KEY (template_id) REFERENCES email_template (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
