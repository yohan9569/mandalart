---------------- mandalart_pkg BODY
create or replace PACKAGE BODY mandalart_pkg IS

    PROCEDURE insert_mandalarts(p_mandalart_id OUT INTEGER) IS
    BEGIN
        -- 시퀀스를 사용해 새로운 mandalart_id 생성
        SELECT mandalart_id_seq.NEXTVAL INTO p_mandalart_id FROM dual;

        -- 새로운 mandalart를 테이블에 삽입
        INSERT INTO mandalarts (mandalart_id)
        VALUES (p_mandalart_id);

        -- 새로 추가된 만다라트의 목표들을 삽입
        -- insert_mandalart_goals(p_mandalart_id);
    END insert_mandalarts;

    FUNCTION get_latest_mandalart_id RETURN INTEGER IS
        v_result INTEGER;
    BEGIN
        BEGIN
            SELECT mandalart_id
            INTO v_result
            FROM (SELECT mandalart_id
                  FROM mandalarts
                  ORDER BY last_modified_date DESC) -- 수정일 기준으로 정렬
            WHERE ROWNUM = 1;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                v_result := NULL; -- 데이터가 없을 경우 NULL 반환
        END;
        RETURN v_result;
    END get_latest_mandalart_id;

    FUNCTION get_mandalart_grid(p_mandalart_id INTEGER) RETURN SYS_REFCURSOR IS
        v_cur SYS_REFCURSOR;
    BEGIN
        OPEN v_cur FOR
        SELECT 
            serial_num,
            mandalart_id,
            goal_id,
            goal_name,
            achieved,
            row_idx,
            col_idx,
            parent_node
        FROM 
            v_mandalart_goals
        WHERE
            mandalart_id = p_mandalart_id
        ORDER BY
            row_idx, col_idx;

        RETURN v_cur;
    END get_mandalart_grid;

    FUNCTION open_mandalart RETURN INTEGER IS
        p_mandalart_id INTEGER;
        v_count INTEGER;
    BEGIN
        -- mandalarts 테이블이 비어 있는지 확인
        SELECT COUNT(1) INTO v_count FROM mandalarts;

        IF v_count = 0 THEN
            -- 테이블이 비어 있으면 새로운 mandalart 생성
            insert_mandalarts(p_mandalart_id);
        ELSE
            -- 최신 수정 mandalart_id를 가져옴
            p_mandalart_id := get_latest_mandalart_id;
        END IF;

        RETURN p_mandalart_id;
    END open_mandalart;

    PROCEDURE update_mandalart_title(p_mandalart_id INTEGER, p_mandalart_title VARCHAR2) IS
    BEGIN
        UPDATE mandalarts
        SET mandalart_title = p_mandalart_title
        WHERE mandalart_id = p_mandalart_id;
    EXCEPTION
        WHEN OTHERS THEN
            -- 예외 메시지를 기록하고 예외를 다시 발생시킴
            DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
            RAISE;
    END update_mandalart_title;

		PROCEDURE get_mandalart_title (p_mandalart_id IN INTEGER, p_mandalart_title OUT VARCHAR2) 
		IS
		BEGIN
		    SELECT 
		        mandalart_title
		    INTO 
		        p_mandalart_title
		    FROM 
		        mandalarts
		    WHERE mandalart_id = p_mandalart_id;

		EXCEPTION
		    WHEN NO_DATA_FOUND THEN
		        p_mandalart_title := NULL;
		    WHEN OTHERS THEN
		        RAISE;
		END;


    PROCEDURE update_goal_name (
        p_mandalart_id IN INTEGER,
        p_row_idx IN INTEGER,
        p_col_idx IN INTEGER,
        p_goal_name IN VARCHAR2
    )
    IS
        v_goal_id INTEGER;
    BEGIN
        -- goal_id 찾기
        SELECT g.goal_id INTO v_goal_id
        FROM mandalart_goal_positions g
        WHERE g.row_idx = p_row_idx
          AND g.col_idx = p_col_idx;

        -- goal_name 업데이트
        UPDATE mandalart_goals g
        SET g.goal_name = p_goal_name
        WHERE g.mandalart_id = p_mandalart_id
          AND g.goal_id = v_goal_id;

        -- p_goal_name이 NULL인 경우 achieved = 0으로 업데이트
        IF p_goal_name IS NULL THEN
            UPDATE mandalart_goals g
            SET g.achieved = 0
            WHERE g.mandalart_id = p_mandalart_id
              AND g.goal_id = v_goal_id;
        END IF;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.PUT_LINE('Error: No goal_id found for the provided row_idx and col_idx.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
            RAISE;
    END update_goal_name;



    PROCEDURE toggle_goal_achieved(p_mandalart_id INTEGER, p_goal_id INTEGER) IS
    BEGIN
        UPDATE mandalart_goals
        SET achieved = CASE WHEN achieved = 1 THEN 0 ELSE 1 END
        WHERE mandalart_id = p_mandalart_id
          AND goal_id = p_goal_id;
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
            RAISE;
    END toggle_goal_achieved;

		-- 5/30 수정. 계산법 변경: 최하위 노드만 계산.
    PROCEDURE update_total_achieved(p_mandalart_id IN INTEGER, p_total_achieved OUT INTEGER) IS
    BEGIN
		    -- total_achieved 업데이트
		    UPDATE mandalarts m
		    SET total_achieved = (
		        SELECT COALESCE(ROUND(SUM(g.achieved) / COUNT(g.goal_name) * 100, 2), 0)
		        FROM v_mandalart_goals g
		        WHERE m.mandalart_id = g.mandalart_id
		          AND g.goal_name IS NOT NULL
		          AND g.parent_node IS NOT NULL
		          AND g.parent_node != 41
		    )
		    WHERE m.mandalart_id = p_mandalart_id;

		    -- 업데이트된 total_achieved 반환
		    SELECT total_achieved
		    INTO p_total_achieved
		    FROM mandalarts
		    WHERE mandalart_id = p_mandalart_id;

		EXCEPTION
		    WHEN NO_DATA_FOUND THEN
		        p_total_achieved := NULL;
		    WHEN OTHERS THEN
		        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
		        p_total_achieved := NULL;
    END update_total_achieved;

		-- 5/30 수정
		PROCEDURE update_last_modified_date(p_mandalart_id IN INTEGER, p_last_modified_date OUT VARCHAR2) IS
    BEGIN
		    -- last_modified_date 업데이트
		    UPDATE mandalarts
		    SET last_modified_date = SYSDATE
		    WHERE mandalart_id = p_mandalart_id;
		    -- 업데이트된 last_modified_date 반환
		    SELECT TO_CHAR(last_modified_date, 'YY-MM-DD  HH24:MI')
		    INTO p_last_modified_date
		    FROM mandalarts
		    WHERE mandalart_id = p_mandalart_id;
    EXCEPTION
        WHEN OTHERS THEN
            -- 예외 메시지를 기록하고 예외를 다시 발생시킴
            DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
            RAISE;
    END update_last_modified_date;

    PROCEDURE update_completed_date(p_mandalart_id INTEGER) IS
        v_total_achieved INTEGER;
    BEGIN
        -- total_achieved 값을 가져옴
        SELECT total_achieved
        INTO v_total_achieved
        FROM mandalarts
        WHERE mandalart_id = p_mandalart_id;

        -- total_achieved가 100일 때
        IF v_total_achieved = 100 THEN
            -- complete_date가 NULL이면 SYSDATE로 설정, 이미 있으면 그대로
            UPDATE mandalarts
            SET completed_date = COALESCE(completed_date, SYSDATE)
            WHERE mandalart_id = p_mandalart_id;
        ELSE
            -- total_achieved가 100이 아닌 경우 complete_date가 NULL이 아니면 NULL로 설정
            UPDATE mandalarts
            SET completed_date = NULL
            WHERE mandalart_id = p_mandalart_id
            AND completed_date IS NOT NULL;
        END IF;
    END update_completed_date;

    PROCEDURE get_total_achieved(p_mandalart_id INTEGER, p_total_achieved OUT INTEGER) IS
    BEGIN
        SELECT total_achieved
        INTO p_total_achieved
        FROM mandalarts
        WHERE mandalart_id = p_mandalart_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_total_achieved := NULL;
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
            p_total_achieved := NULL;
    END get_total_achieved;

    PROCEDURE clear_mandalart_data
    (p_mandalart_id INTEGER) 
    IS
    BEGIN
        -- mandalarts 테이블의 mandalart_title, complete_date NULL로 설정
        UPDATE mandalarts
        SET mandalart_title = NULL,
            completed_date = NULL
        WHERE mandalart_id = p_mandalart_id;

        -- mandalart_goals 테이블의 goal_name과 achieved를 0로 설정
        UPDATE mandalart_goals
        SET goal_name = NULL,
            achieved = 0
        WHERE mandalart_id = p_mandalart_id;
    EXCEPTION
        WHEN OTHERS THEN
            -- 예외 메시지를 기록하고 예외를 다시 발생시킴
            DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
            RAISE;
    END clear_mandalart_data;

    FUNCTION get_mandalarts_list RETURN SYS_REFCURSOR IS
        v_cur SYS_REFCURSOR;
    BEGIN
        OPEN v_cur FOR
        SELECT 
            mandalart_id,
            mandalart_title,
            total_achieved,
            TO_CHAR(created_date, 'YY-MM-DD HH24:MI') AS created_date,
            TO_CHAR(last_modified_date, 'YY-MM-DD HH24:MI') AS last_modified_date,
            TO_CHAR(completed_date, 'YY-MM-DD HH24:MI') AS completed_date
        FROM 
            mandalarts
        ORDER BY mandalart_id ASC;    

        RETURN v_cur;
    END get_mandalarts_list;

    PROCEDURE delete_mandalart(p_mandalart_id INTEGER) IS
    BEGIN
        -- 먼저 mandalart_goals 테이블에서 삭제
        DELETE FROM mandalart_goals
        WHERE mandalart_id = p_mandalart_id;

        -- 그 다음 mandalarts 테이블에서 삭제
        DELETE FROM mandalarts
        WHERE mandalart_id = p_mandalart_id;
    EXCEPTION
        WHEN OTHERS THEN
            -- 예외 발생 시 예외 메시지를 출력하고 예외를 다시 던짐
            DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
            RAISE;
    END delete_mandalart;

END mandalart_pkg;
/
