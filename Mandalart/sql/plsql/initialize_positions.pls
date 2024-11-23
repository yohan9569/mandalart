------------------ mandalart_goal_positions 미리 INSERT 하는 pl/sql 코드
BEGIN
    -- 먼저 goal_id, row_idx, col_idx를 삽입합니다.
    FOR i IN 1..81 LOOP
		    CONTINUE WHEN i IN (11, 14, 17, 38, 44, 65, 68, 71); -- 1부터 81까지 순환, 레벨 2의 노드들은 값이 중복되기 때문에 pass
        INSERT INTO mandalart_goal_positions (goal_id, row_idx, col_idx)
        VALUES (i, FLOOR((i-1) / 9), MOD(i-1, 9));
    END LOOP;

    -- 이제 parent_node 값을 업데이트합니다.
    FOR i IN 1..81 LOOP
		    CONTINUE WHEN i IN (11, 14, 17, 38, 44, 65, 68, 71); -- 1부터 81까지 순환, 레벨 2의 노드들은 값이 중복되기 때문에 pass
        UPDATE mandalart_goal_positions
        SET parent_node = 
            CASE
                WHEN goal_id = 41 THEN NULL
                WHEN row_idx < 3 THEN
                    CASE
                        WHEN col_idx < 3 THEN 31
                        WHEN col_idx < 6 THEN 32
                        ELSE 33
                    END
                WHEN row_idx < 6 THEN
                    CASE
                        WHEN col_idx < 3 THEN 40
                        WHEN col_idx < 6 THEN 41
                        ELSE 42
                    END
                ELSE 
                    CASE
                        WHEN col_idx < 3 THEN 49
                        WHEN col_idx < 6 THEN 50
                        ELSE 51
                    END
            END
        WHERE goal_id = i;
    END LOOP;
END;
/
