CREATE TABLE users
(
    id       UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    email    VARCHAR(50) NOT NULL
);

SELECT *
FROM users;

CREATE TABLE problems
(
    id            UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    title         VARCHAR(50) NOT NULL,
    statement     TEXT        NOT NULL,
    timeLimitMs   LONG        NOT NULL,
    memoryLimitKb LONG        NOT NULL

);

CREATE TABLE test_cases
(
    id             UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    problemId      UUID NOT NULL,
    input          TEXT NOT NULL,
    expectedOutput TEXT,
    CONSTRAINT fk_test_case_problem FOREIGN KEY (problemId)
        REFERENCES problems (id) ON DELETE CASCADE
);

CREATE TABLE categories
(
    id         UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name       VARCHAR(20) NOT NULL,
    difficulty VARCHAR(20) NOT NULL
);

SELECT * FROM  PROBLEM_CATEGORIES;

CREATE TABLE problem_categories
(
    problemId  UUID NOT NULL,
    categoryId UUID NOT NULL,
    PRIMARY KEY (problemId, categoryId),
    CONSTRAINT fk_pc_problem FOREIGN KEY (problemId)
        REFERENCES problems (id) ON DELETE CASCADE,
    CONSTRAINT fk_pc_category FOREIGN KEY (categoryId)
        REFERENCES categories (id) ON DELETE CASCADE

);


CREATE TABLE contests
(
    id         UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    startTIme TIMESTAMP NOT NULL,
    endTime TIMESTAMP NOT NULL
);

CREATE TABLE contest_problems
(
    contestId  UUID NOT NULL,
    problemId UUID NOT NULL,
    PRIMARY KEY (contestId, problemId),
    CONSTRAINT fk_cp_contest FOREIGN KEY (contestId)
        REFERENCES contests (id) ON DELETE CASCADE,
    CONSTRAINT fk_cp_problem FOREIGN KEY (problemId)
        REFERENCES problems (id) ON DELETE CASCADE



);

CREATE TABLE submissions
(
    id         UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    userId UUID NOT NULL,
    problemID UUID NOT NULL,
    sourceCode Text NOT NULL,
    status VARCHAR(20) NOT NULL,
    submittedTime TIMESTAMP NOT NULL,
    errorMessage Text,
    CONSTRAINT fk_submission_user FOREIGN KEY (userId)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_submission_problem FOREIGN KEY (problemId)
        REFERENCES problems(id) ON DELETE CASCADE

);

INSERT INTO CATEGORIES (name, difficulty)
values ('Math', 'easy');
