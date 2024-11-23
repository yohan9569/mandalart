------------------ 계정 생성 및 권한 부여(DCL)
CREATE USER mandalart IDENTIFIED BY mandalart;
GRANT RESOURCE, CREATE SESSION, CREATE TABLE, CREATE VIEW TO mandalart;




------------------ 테이블 생성(create table)
-- 1. mandalarts 
CREATE TABLE mandalarts (
    mandalart_id         INTEGER NOT NULL,
    mandalart_title      VARCHAR2(50),   
    total_achieved       INTEGER NOT NULL,
    created_date         DATE NOT NULL,
    last_modified_date   DATE NOT NULL,
    completed_date       DATE
);

ALTER TABLE mandalarts ADD CONSTRAINT mandalart_id_pk PRIMARY KEY (mandalart_id);

-- total_achieved 컬럼에 대해 디폴트 값을 설정
ALTER TABLE mandalarts MODIFY total_achieved DEFAULT 0;

-- created_date, last_modified_date 컬럼에 대해 디폴트 값을 설정
ALTER TABLE mandalarts MODIFY created_date DEFAULT SYSDATE;
ALTER TABLE mandalarts MODIFY last_modified_date DEFAULT SYSDATE;


-- 2. mandalart_goals
CREATE TABLE mandalart_goals (
    serial_num      INTEGER NOT NULL,
    mandalart_id    INTEGER NOT NULL,
    goal_id         INTEGER NOT NULL,
    goal_name       VARCHAR2(48),     
    achieved        INTEGER NOT NULL
);

ALTER TABLE mandalart_goals 
    ADD CONSTRAINT serial_num_pk PRIMARY KEY (serial_num);

ALTER TABLE mandalart_goals
    ADD CONSTRAINT mandalart_id_fk FOREIGN KEY (mandalart_id)
        REFERENCES mandalarts (mandalart_id);

ALTER TABLE mandalart_goals
    ADD CONSTRAINT goal_id_fk FOREIGN KEY (goal_id)
        REFERENCES mandalart_goal_positions (goal_id);

-- achieved 컬럼에 0과 1만 입력
ALTER TABLE mandalart_goals 
    ADD CONSTRAINT chk_achieved CHECK (achieved IN (0, 1));

ALTER TABLE mandalart_goals MODIFY achieved DEFAULT 0;


-- 3. mandalart_goal_positions
CREATE TABLE mandalart_goal_positions (
    goal_id      INTEGER NOT NULL,
    row_idx      INTEGER NOT NULL,
    col_idx      INTEGER NOT NULL,
    parent_node  INTEGER
);

ALTER TABLE mandalart_goal_positions 
	ADD CONSTRAINT mandalart_goal_positions_pk PRIMARY KEY (goal_id);

ALTER TABLE mandalart_goal_positions
    ADD CONSTRAINT position_parent_node_fk FOREIGN KEY (parent_node)
        REFERENCES mandalart_goal_positions (goal_id);




------------------ 뷰 생성 코드 : goals and positions
CREATE OR REPLACE VIEW v_mandalart_goals AS
SELECT 
    g.serial_num,
    g.mandalart_id,
    g.goal_id,
    g.goal_name,
    g.achieved,
    p.row_idx,
    p.col_idx,
    p.parent_node
FROM 
    mandalart_goals g
JOIN 
    mandalart_goal_positions p
ON 
    g.goal_id = p.goal_id;




------------------ 시퀀스 생성
CREATE SEQUENCE mandalart_id_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE SEQUENCE goals_serial_num_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE;




------------------ 인덱스 생성
CREATE INDEX idx_goals_mandalart_id ON mandalart_goals(mandalart_id);
