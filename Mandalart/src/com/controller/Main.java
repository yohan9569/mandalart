package com.controller;

import java.sql.Connection;
import java.sql.SQLException;

import com.model.DBConnection;
import com.model.MandalartModel;
import com.view.MandalartView;

public class Main {
	
	public static void main(String[] args) throws SQLException {
	    Connection conn = DBConnection.getConnection();
	    System.out.println("연결 : " + conn);
	    
	    conn.setAutoCommit(false);  // 자동 커밋 false
	    
	    MandalartModel model = new MandalartModel(conn);
	    MandalartView view = new MandalartView();
	    
	    new MandalartController(model, view);
	}
}