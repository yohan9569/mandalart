---------------- 트리거
CREATE OR REPLACE TRIGGER trg_after_insert_mandalarts
AFTER INSERT ON mandalarts
FOR EACH ROW
DECLARE
BEGIN
    FOR i IN 1..81 LOOP
        CONTINUE WHEN i IN (11, 14, 17, 38, 44, 65, 68, 71); -- 중간 계층 노드들 건너뛰기

        INSERT INTO mandalart_goals (
            serial_num,
            mandalart_id,
            goal_id,
            goal_name
        ) VALUES (
            goals_serial_num_seq.NEXTVAL, -- 시퀀스에서 serial_num 생성
            :NEW.mandalart_id,            -- 새로 추가된 만다라트의 ID 사용
            i,                            -- goal_id 순환
            NULL                         -- goal_name 초기값 NULL
        );
    END LOOP;
END;
/
