CREATE TABLE users (
    user_id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    designation     VARCHAR(255),
    year_of_exp     INTEGER DEFAULT NULL,
    bio             VARCHAR(255),
    created_at      TIMESTAMP DEFAULT NOW()
);


CREATE  TABLE projects (
    project_id      BIGINT GENERATED ALWAYS AS IDENTITY
                    PRIMARY KEY NOT NULL,
    name            VARCHAR(100) NOT NULL ,
    description     VARCHAR(255),
    owner_id        BIGINT NOT NULL
                    REFERENCES users(user_id),
    created_at      TIMESTAMP default NOW()
);

CREATE TABLE project_memberships (
    project_id      BIGINT NOT NULL
                    REFERENCES projects(project_id),
    user_id         BIGINT NOT NULL
                    REFERENCES users(user_id),
    status          VARCHAR(10) NOT NULL DEFAULT 'ACTIVE'
                    CHECK(status in ('ACTIVE', 'INACTIVE')),
    joined_at       TIMESTAMP NOT NULL DEFAULT now(),
    updatedAt       TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (project_id, user_id)
);

CREATE TABLE tasks (
    task_id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    title           VARCHAR(100) NOT NULL,
    description     TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT('NEW')
                    CHECK (status IN ('NEW', 'OPEN', 'COMPLETED', 'CANCELLED')),
    project_id      BIGINT NOT NULL REFERENCES projects(project_id),
    reporter_id     BIGINT NOT NULL REFERENCES users(user_id),
    assignee_id     BIGINT DEFAULT NULL REFERENCES users(user_id),
    due_date        DATE DEFAULT NOW(),
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);
