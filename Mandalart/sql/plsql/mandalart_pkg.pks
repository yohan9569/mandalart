---------------- 패키지 : 함수 및 프로시저
---------------- mandalart_pkg 
CREATE OR REPLACE PACKAGE mandalart_pkg IS

    -- 새 만다라트 삽입 프로시저 선언
    PROCEDURE insert_mandalarts(p_mandalart_id OUT INTEGER);

    -- 최신 만다라트 ID를 가져오는 함수 선언
    FUNCTION get_latest_mandalart_id RETURN INTEGER;

    -- 만다라트 그리드 데이터를 가져오는 함수 선언
    FUNCTION get_mandalart_grid(p_mandalart_id INTEGER) RETURN SYS_REFCURSOR;

    -- 만다라트 오픈 함수 선언
    FUNCTION open_mandalart RETURN INTEGER;

    -- 만다라트 제목 업데이트 프로시저 선언
    PROCEDURE update_mandalart_title(p_mandalart_id INTEGER, p_mandalart_title VARCHAR2);
    
    -- 만다라트 제목을 가지고 오는 프로시저 선언
    PROCEDURE get_mandalart_title (p_mandalart_id IN INTEGER, p_mandalart_title OUT VARCHAR2); 

    -- 목표 이름 업데이트 프로시저 선언
    PROCEDURE update_goal_name(p_mandalart_id INTEGER, p_row_idx INTEGER, p_col_idx INTEGER, p_goal_name VARCHAR2);

    -- 목표 달성 상태 토글 프로시저 선언
    PROCEDURE toggle_goal_achieved(p_mandalart_id INTEGER, p_goal_id INTEGER);

    -- 전체 달성도 업데이트 프로시저 선언
    -- PROCEDURE update_total_achieved(p_mandalart_id INTEGER);
    PROCEDURE update_total_achieved(p_mandalart_id IN INTEGER, p_total_achieved OUT INTEGER);

    -- 마지막 수정 날짜 업데이트 프로시저 선언
    -- PROCEDURE update_last_modified_date(p_mandalart_id INTEGER);
    PROCEDURE update_last_modified_date(p_mandalart_id IN INTEGER, p_last_modified_date OUT VARCHAR2);

    -- 완료 날짜 업데이트 프로시저 선언
    PROCEDURE update_completed_date(p_mandalart_id INTEGER);

    -- 전체 달성도를 가져오는 프로시저 선언
    PROCEDURE get_total_achieved(p_mandalart_id INTEGER, p_total_achieved OUT INTEGER);

    -- 만다라트 데이터 초기화 프로시저 선언
    PROCEDURE clear_mandalart_data(p_mandalart_id INTEGER);

    -- 만다라트 리스트를 가져오는 함수 선언
    FUNCTION get_mandalarts_list RETURN SYS_REFCURSOR;

    -- 만다라트 삭제 프로시저 선언
    PROCEDURE delete_mandalart(p_mandalart_id INTEGER);

END mandalart_pkg;
/
