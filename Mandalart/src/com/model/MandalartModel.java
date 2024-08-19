package com.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.vo.MandalartVo;
import com.vo.V_MandalartGoalsVO;

import oracle.jdbc.OracleTypes;

public class MandalartModel {
	private Connection conn;
	private CallableStatement cstmt;
	private MandalartVo mandalartVo;
	private List<V_MandalartGoalsVO> goalNameList;
	public Map<Integer, int[]> parentMappings;
	
	public MandalartModel() { }
	
	public MandalartModel(Connection conn) {
		this.conn = conn;
		this.mandalartVo = new MandalartVo();
		this.goalNameList = new ArrayList<>();
		parentMappings = new HashMap<>();
		initializeParentMappings();
	}
	
    private void initializeParentMappings() {
        parentMappings.put(31, new int[]{1, 2, 3, 10, 12, 19, 20, 21});
        parentMappings.put(32, new int[]{4, 5, 6, 13, 15, 22, 23, 24});
        parentMappings.put(33, new int[]{7, 8, 9, 16, 18, 25, 26, 27});
        parentMappings.put(40, new int[]{28, 29, 30, 37, 39, 46, 47, 48});
        parentMappings.put(41, new int[]{31, 32, 33, 40, 42, 49, 50, 51});
        parentMappings.put(42, new int[]{34, 35, 36, 43, 45, 52, 53, 54});
        parentMappings.put(49, new int[]{55, 56, 57, 64, 66, 73, 74, 75});
        parentMappings.put(50, new int[]{58, 59, 60, 67, 69, 76, 77, 78});
        parentMappings.put(51, new int[]{61, 62, 63, 70, 72, 79, 80, 81});
    }
	
	public Connection getConnection() {
		return conn;
	}
	
	public int[][] getGoalMappings() {
		int[][] goalMappings = {
	    	    {3, 3, 1, 1}, {3, 4, 1, 4}, {3, 5, 1, 7},
	    	    {4, 3, 4, 1},               {4, 5, 4, 7},
	    	    {5, 3, 7, 1}, {5, 4, 7, 4}, {5, 5, 7, 7}
		};
		return goalMappings;
	}
	
	public int[][] getIdxNotUsed() {
        int[][] idxNotUsed = {
                {1, 1}, {1, 4}, {1, 7},
                {3, 3}, {3, 4}, {3, 5},
                {4, 1}, {4, 3}, {4, 4}, {4, 5}, {4, 7},
                {5, 3}, {5, 4}, {5, 5},
                {7, 1}, {7, 4}, {7, 7}
        };
		return idxNotUsed;
	}
	
	public Map<Integer, int[]> getParentMappings() {
        return parentMappings;
	}
	
	public MandalartVo getMandalartVo() {
		return mandalartVo;
	}
	
	public List<V_MandalartGoalsVO> getGoalNameList() {
		return goalNameList;
	}

	public void openMandalart() throws SQLException {
		String openSql = "{ ? = call mandalart_pkg.open_mandalart() }";
		cstmt = conn.prepareCall(openSql);
		cstmt.registerOutParameter(1, OracleTypes.INTEGER);
		cstmt.execute();
		
		int mandalart_id = cstmt.getInt(1);
		mandalartVo.setMandalart_id(mandalart_id);

		getMandalartGrid(); 
        updateTotalAchieved();                             
        getMandalartTitle();
	}
	

	public void insertMandalart() throws SQLException {
		String insertSql = "{ call mandalart_pkg.insert_mandalarts(?) }";
		cstmt = conn.prepareCall(insertSql);
		cstmt.registerOutParameter(1, OracleTypes.INTEGER);
		cstmt.execute();
		
		int mandalart_id = cstmt.getInt(1);
		mandalartVo.setMandalart_id(mandalart_id);
	}

	public void updateGoalName(int row_idx, int col_idx, String goal_name) throws SQLException {
		String updateSql = "{ CALL mandalart_pkg.update_goal_name(?, ?, ?, ?) }";
		cstmt = conn.prepareCall(updateSql);
		cstmt.setInt(1, mandalartVo.getMandalart_id());
		cstmt.setInt(2, row_idx);
		cstmt.setInt(3, col_idx);
		cstmt.setString(4, goal_name);
		cstmt.executeUpdate();
		
		System.out.println("updateGoalName => -- " + mandalartVo.getMandalart_id() + ", " + goal_name);
	}

	public void clearMandalartData() throws SQLException {
		String clearMandalSql = "{ call mandalart_pkg.clear_mandalart_data(?) }";
		cstmt = conn.prepareCall(clearMandalSql);
		cstmt.setInt(1, mandalartVo.getMandalart_id());
		cstmt.execute();
	}
	
	public void getMandalartGrid() throws SQLException {
		String getGridSql = "{  ? = call mandalart_pkg.get_mandalart_grid (?) }";
		cstmt = conn.prepareCall(getGridSql);
		cstmt.registerOutParameter(1, OracleTypes.CURSOR);
		cstmt.setInt(2, mandalartVo.getMandalart_id());
		
		System.out.println("getMandalartGrid : " + mandalartVo.getMandalart_id());
		cstmt.execute();
		
		ResultSet rs = (ResultSet) cstmt.getObject(1);
		
		goalNameList.clear();
		while (rs.next()) {
			V_MandalartGoalsVO goal = new V_MandalartGoalsVO();
			
			goal.setMandalart_id(rs.getInt("mandalart_id"));
			goal.setGoal_id(rs.getInt("goal_id"));
			goal.setRow_idx(rs.getInt("row_idx"));
			goal.setCol_idx(rs.getInt("col_idx"));
			goal.setGoal_name(rs.getString("goal_name"));
			goal.setAchieved(rs.getInt("achieved"));
			goal.setParent_node(rs.getInt("parent_node"));
			
			goalNameList.add(goal);
		}
	}

	public void getMandalartTitle() throws SQLException {
		String getTitleSql = "{ call mandalart_pkg.get_mandalart_title (?, ?) }";
		cstmt = conn.prepareCall(getTitleSql);
		cstmt.setInt(1, mandalartVo.getMandalart_id());
		cstmt.registerOutParameter(2, OracleTypes.VARCHAR);
		cstmt.execute();
		
		String title = cstmt.getString(2);
		mandalartVo.setMandalart_title(title);
		
		System.out.println("getMandalartTitle : " + title);
	}

	public void updateMandalartTitle(String mandalart_title, int mandalart_id) throws SQLException {
		String updateTitleSql = "{ call mandalart_pkg.update_mandalart_title(?, ?) }";
		cstmt = conn.prepareCall(updateTitleSql);
		cstmt.setInt(1, mandalart_id);
		cstmt.setString(2, mandalart_title);
		cstmt.execute();
		
		System.out.println("updateMandalartTitle 호출 => " + mandalart_id + ", " + mandalart_title);
	}

	public void updateLastModifiedDate(int mandalart_id) throws SQLException {
		String updateLastModDateSql = "{ call mandalart_pkg.update_last_modified_date(?, ?) }";
		cstmt = conn.prepareCall(updateLastModDateSql);
		cstmt.setInt(1, mandalart_id);
		cstmt.registerOutParameter(2, OracleTypes.VARCHAR);
		cstmt.execute();
		
		String last_modified_date= cstmt.getString(2);	
		mandalartVo.setLast_modified_date(last_modified_date);
	}

	public void updateCompletedDate() throws SQLException {
		String updateComplteDateSql = "{ call mandalart_pkg.update_completed_date(?) }";
		cstmt = conn.prepareCall(updateComplteDateSql);
		cstmt.setInt(1, mandalartVo.getMandalart_id());
		cstmt.execute();
	}

	public void toggleGoalAchieved(int row_idx, int col_idx) throws SQLException {
		String goalAchievedSql = "{ call mandalart_pkg.toggle_goal_achieved(?, ?) }";
		cstmt = conn.prepareCall(goalAchievedSql);

		int goal_id = findGoalId(row_idx, col_idx);
		cstmt.setInt(1, mandalartVo.getMandalart_id());
		cstmt.setInt(2, goal_id);
		cstmt.execute();
			
		System.out.println("toggleGoalAchieved => " + row_idx + ", " + col_idx + ", " +  goal_id);
	}

	public void updateTotalAchieved() throws SQLException {
		// 총 달성 비율을 업데이트하는 프로시저 호출
		String totalAchievedSql = "{ call mandalart_pkg.update_total_achieved(?, ?) }";
		cstmt = conn.prepareCall(totalAchievedSql);
		cstmt.setInt(1, mandalartVo.getMandalart_id());
		cstmt.registerOutParameter(2, OracleTypes.INTEGER);
		cstmt.execute();
		
		int total_achieved = cstmt.getInt(2);  // total_achieved 값을 가져와서 progressBar에서 사용
		System.out.println("updateTotalAchieved => " + total_achieved);
		mandalartVo.setTotal_achieved(total_achieved);
	}

	public List<MandalartVo> getMandalartList() throws SQLException {
		String mandalListSql = "{ ? = call mandalart_pkg.get_mandalarts_list() }";
		cstmt = conn.prepareCall(mandalListSql);
		cstmt.registerOutParameter(1, OracleTypes.CURSOR);
		cstmt.execute();
		
		ResultSet rs = (ResultSet) cstmt.getObject(1);

		List<MandalartVo> mandalartList = new ArrayList<>();
		while (rs.next()) {
			MandalartVo mandalartVo = new MandalartVo();
			mandalartVo.setMandalart_id(rs.getInt("mandalart_id"));
			mandalartVo.setMandalart_title(rs.getString("mandalart_title"));
			mandalartVo.setTotal_achieved(rs.getInt("total_achieved"));
			mandalartVo.setCreated_date(rs.getString("created_date"));
			mandalartVo.setLast_modified_date(rs.getString("last_modified_date"));
			mandalartVo.setCompleted_date(rs.getString("completed_date"));
			mandalartList.add(mandalartVo);
		}
		return mandalartList;
	}
	
	// viewMandalartList: mandalart의 전체 List를 출력    
    private void viewMandalartList(DefaultTableModel tableModel) {
    	String mandalListSql = "{ ? = call mandalart_pkg.get_mandalarts_list() }";
        try {
            cstmt = conn.prepareCall(mandalListSql);
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();
            
            ResultSet rs = (ResultSet) cstmt.getObject(1);    
            while (rs.next()) {
                int mandalart_id = rs.getInt("mandalart_id");
                String mandalart_title = rs.getString("mandalart_title");
                int total_achieved = rs.getInt("total_achieved");
                String create_date = rs.getString("created_date"); // to_char는 String 변수로 받기
                String last_modified_date = rs.getString("last_modified_date"); 
                String completed_date = rs.getString("completed_date"); 
                  
                // 테이블에 1행 추가
                tableModel.addRow( new Object[]{mandalart_id, mandalart_title, total_achieved, 
                				   create_date, last_modified_date, completed_date} );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
    
    public void refreshMandalartList(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // 객체에서 모든 행을 제거하여 테이블을 초기화하는 역할
        viewMandalartList(tableModel); // 새 데이터 로드
    }
    
    
	public void deleteMandalart(int mandalart_id) throws SQLException {
		String deleteMandalSql = "{ call mandalart_pkg.delete_mandalart(?) }";
		cstmt = conn.prepareCall(deleteMandalSql);
		cstmt.setInt(1, mandalart_id);
		cstmt.execute();
	}

	public void saveCommit() throws SQLException {
		try {
            if (conn != null) {
                conn.commit();
                JOptionPane.showMessageDialog(null, "저장이 완료 되었습니다!"); // null을 사용하면 화면 가운데에 대화 상자가 표시
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "저장이 완료되지 않았습니다! ");
        }
	}

	public int findGoalId(int row_idx, int col_idx) {	
	    for (V_MandalartGoalsVO goal : goalNameList) {
	        if (goal.getRow_idx() == row_idx && goal.getCol_idx() == col_idx) {
	            return goal.getGoal_id();
	        }
	    }
	    return -1; // goal_id를 찾지 못한 경우 -1 반환
	}
}