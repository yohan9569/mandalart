package com.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.AbstractDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.model.MandalartModel;
import com.util.ImagePanel;
import com.util.RoundedBorder;
import com.util.TextSizeFilter;
import com.vo.V_MandalartGoalsVO;


public class MandalartView {
	private static int mandalartPanelCreationCount = 0; // 클래스 수준 변수 추가
	private static final int SIZE = 9;
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel mandalartPanel;
    private JPanel listPanel;
    private JFormattedTextField inputMandalartTitle;
    private JProgressBar progressBar;
    private JButton newButton;
    private JButton showListButton;
    private JButton saveButton;
    private JButton clearButton;
    private JButton currentMandalartButton;
    private JButton saveMandalartButton;
    private JButton deleteMandalartButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextPane[][] textFields;
    
    public MandalartView() {   	
        initialize();
    }

    public void initialize() {
        frame = new JFrame();
        frame.setBackground(Color.WHITE);
        frame.setTitle("MANDALART");
        frame.getContentPane().setBackground(new Color(255, 255, 255));
        frame.setSize(1000, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        mainPanel = new JPanel(new CardLayout());
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        mandalartPanel = createMandalartPanel();
        listPanel = createListPanel();
        
        mainPanel.add(mandalartPanel, "mandalart");
        mainPanel.add(listPanel, "list");
    }
    
    public JPanel createMandalartPanel() {
    	mandalartPanelCreationCount++; // 메소드 호출 시 카운터 증가
        System.out.println("createMandalartPanel called " + mandalartPanelCreationCount + " times");
        
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);

        ImagePanel imagePanel = new ImagePanel(new ImageIcon(getClass().getResource("/image/mandalart.jpg")).getImage());
        imagePanel.setBackground(new Color(255, 255, 255));
        imagePanel.setBounds(0, -28, 974, 867);
        panel.add(imagePanel);
        imagePanel.setLayout(null);

        // mandalart_title 입력창
        inputMandalartTitle = new JFormattedTextField();
        inputMandalartTitle.setBackground(Color.WHITE);
        inputMandalartTitle.setHorizontalAlignment(SwingConstants.CENTER);
        inputMandalartTitle.setFont(new Font("한컴산뜻돋움", Font.BOLD, 24));
        inputMandalartTitle.setFocusLostBehavior(JFormattedTextField.COMMIT);
        inputMandalartTitle.setBounds(214, 127, 408, 30);

        // 커스텀 Border 설정
        inputMandalartTitle.setBorder(new RoundedBorder(50, Color.white, 1));
        inputMandalartTitle.setForeground(Color.DARK_GRAY);

        // 입력창에 옵션
        inputMandalartTitle.setEnabled(false);

        imagePanel.add(inputMandalartTitle);

        // 목록보기 버튼 : Show List
        showListButton = new JButton("Show List");
        showListButton.setFont(new Font("굴림", Font.BOLD, 12));
        showListButton.setBounds(181, 209, 97, 41);
        imagePanel.add(showListButton);

        // 만다라트 새로 생성하는 버튼 : newButton
        newButton = new JButton("New");
        newButton.setFont(new Font("굴림", Font.BOLD, 12));
        newButton.setBounds(530, 209, 85, 41);
        imagePanel.add(newButton);

        // 만다라트를 commit 하는 버튼 : saveButton
        saveButton = new JButton("Save");
        saveButton.setFont(new Font("굴림", Font.BOLD, 12));
        saveButton.setBounds(627, 209, 85, 41);
        imagePanel.add(saveButton);

        // 만다라트의 목표 및 타이틀을 지우는 버튼 : clearButton
        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("굴림", Font.BOLD, 12));
        clearButton.setBounds(720, 209, 85, 41);
        imagePanel.add(clearButton);

        JLabel lblNewLabel = new JLabel("Progress Rate");
        lblNewLabel.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 12));
        lblNewLabel.setBounds(190, 264, 97, 24);
        imagePanel.add(lblNewLabel);

        // progressBar
        progressBar = new JProgressBar();
        progressBar.setBounds(279, 266, 526, 24);
        imagePanel.add(progressBar);

        // 만다라트 표 생성하는 곳
        JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE));
        gridPanel.setBounds(184, 306, 621, 523);
        textFields = new JTextPane[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                textFields[i][j] = new JTextPane();
                textFields[i][j].setEditorKit(new javax.swing.text.StyledEditorKit());

                // 중앙 정렬 설정
                SimpleAttributeSet center = new SimpleAttributeSet();
                StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
                StyledDocument doc = textFields[i][j].getStyledDocument();
                doc.setParagraphAttributes(0, doc.getLength(), center, false);
                
                
                // 각 셀당 최대 글자수 지정하기
                ((AbstractDocument) textFields[i][j].getDocument()).setDocumentFilter(new TextSizeFilter(15));

                // 라인 컬러와 굵기 설정하기
                Border border = new LineBorder(Color.lightGray, 2);
                if (i % 3 == 0) {
                    border = BorderFactory.createMatteBorder(2, 1, 1, 1, Color.lightGray);
                }
                if (i == SIZE - 1 || i % 3 == 2) {
                    border = BorderFactory.createMatteBorder(1, 1, 2, 1, Color.lightGray);
                }
                if (j % 3 == 0) {
                    border = BorderFactory.createMatteBorder(1, 2, 1, 1, Color.lightGray);
                }
                if (j == SIZE - 1 || j % 3 == 2) {
                    border = BorderFactory.createMatteBorder(1, 1, 1, 2, Color.lightGray);
                }
                if (i % 3 == 0 && j % 3 == 0) {
                    border = BorderFactory.createMatteBorder(2, 2, 1, 1, Color.lightGray);
                }
                if (i % 3 == 0 && j % 3 == 2) {
                    border = BorderFactory.createMatteBorder(2, 1, 1, 2, Color.lightGray);
                }
                if (i % 3 == 2 && j % 3 == 0) {
                    border = BorderFactory.createMatteBorder(1, 2, 2, 1, Color.lightGray);
                }
                if (i % 3 == 2 && j % 3 == 2) {
                    border = BorderFactory.createMatteBorder(1, 1, 2, 2, Color.lightGray);
                }
                textFields[i][j].setBorder(border);
                
                // 특정 셀을 비활성화
                if ((i == 1 || i == 4 || i == 7) && (j == 1 || j == 4 || j == 7) && !(i == 4 && j == 4)) {
                    textFields[i][j].setEditable(false);
                    textFields[i][j].setBackground(new Color(240, 240, 240));

                }
                gridPanel.add(textFields[i][j]);
            }
        }
        imagePanel.add(gridPanel);

        return panel;
    }
    
    
    public JPanel createListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        ImagePanel imagePanel = new ImagePanel(new ImageIcon(getClass().getResource("/image/white.jpg")).getImage());
        imagePanel.setBackground(new Color(255, 255, 255));
        imagePanel.setBounds(0, 0, 986, 922);
        panel.add(imagePanel);
        imagePanel.setLayout(null);

        // 상단에 이미지 추가
        ImagePanel topImagePanel = new ImagePanel(new ImageIcon(getClass().getResource("/image/mandalListTop.jpg")).getImage());
        topImagePanel.setBounds(12, 68, 947, 235);
        imagePanel.add(topImagePanel);

        //////// Current Mandalart 버튼
        currentMandalartButton = new JButton("Current Mandalart");
        currentMandalartButton.setFont(new Font("굴림", Font.BOLD, 12));
        currentMandalartButton.setBounds(113, 340, 148, 45);
        imagePanel.add(currentMandalartButton);

        //////// saveMandalart 버튼
        saveMandalartButton = new JButton("Save");
        saveMandalartButton.setFont(new Font("굴림", Font.BOLD, 12));
        saveMandalartButton.setBounds(682, 340, 90, 45);
        imagePanel.add(saveMandalartButton);
        
        ////// deleteMandalart 버튼
        deleteMandalartButton = new JButton("Delete");
        deleteMandalartButton.setFont(new Font("굴림", Font.BOLD, 12));
        deleteMandalartButton.setBounds(792, 340, 90, 45);
        imagePanel.add(deleteMandalartButton);

        //////// JTable 테이블 설정
        String[] columnNames = {"Mandalart Id", "Mandalart Title", "Total Achieved",
                "Create Date", "Last Modified Date", "Completed Date"};

        // Mandalart Title 열만 편집 가능
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("함초롬돋움", Font.PLAIN, 13));

        // 행 높이 설정
        table.setRowHeight(50);

        // 각 열의 너비 설정
        TableColumn column;
        int[] columnWidths = {90, 200, 110, 140, 140, 140};
        for (int i = 0; i < columnWidths.length; i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
        }

        // 셀 렌더러 설정 (가운데 정렬)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // 기본적으로 보이는 행 수를 설정하여 스크롤을 활성화
        table.setPreferredScrollableViewportSize(new Dimension(680, table.getRowHeight() * 6));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setEnabled(false);
        scrollPane.setBounds(111, 410, 775, 320);
        imagePanel.add(scrollPane);

        return panel;
    }
    
    public void goalNameAndAchivedIntoCell(List<V_MandalartGoalsVO> goalNameList) { 
    	
    	textFields[4][4].setBackground(new Color(255, 255, 230));
    	
	    for (V_MandalartGoalsVO goal : goalNameList) {
	        int row_idx = goal.getRow_idx();
	        int col_idx = goal.getCol_idx();
	        String goal_name = goal.getGoal_name();
	        int achieved = goal.getAchieved();
	        
	        System.out.println("row: " + row_idx + ", col: " + col_idx + ", name: " + goal_name + ", achieved: " + achieved);
	        
	        getTextFields()[row_idx][col_idx].setText(goal_name);
	        
	        if (achieved == 1) { // achieved == 1인 경우 색상을 노란색으로 설정
	        	textFields[row_idx][col_idx].setBackground(new Color(255, 255, 200));
	        } else {
	            // 4, 4 셀은 이미 위에서 설정했으므로 제외
	            if (!(row_idx == 4 && col_idx == 4)) {
	                textFields[row_idx][col_idx].setBackground(Color.WHITE);
	            }
	    	}
	    }
    }
    
    public void viewMandalartTitle(String title) {    	
    	 if(title != null) {
	        inputMandalartTitle.setText(title);
	        inputMandalartTitle.setForeground(Color.DARK_GRAY); // 여기서 텍스트 색상 설정
	    } else {
	        inputMandalartTitle.setText("insert title for mandalart");
	        inputMandalartTitle.setForeground(Color.DARK_GRAY); // 여기서 텍스트 색상 설정
	    }   
    }
    
	public void updateProgressBar(int total_achieved) {	
        progressBar.setValue(total_achieved);
        progressBar.setStringPainted(true); // 퍼센트로 보여줌
	}

    public JFrame getFrame() {
        return frame;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JPanel getMandalartPanel() {
        return mandalartPanel;
    }
    
	  public JTextPane[][] getTextFields() {
		  return textFields;
	}

    public JFormattedTextField getInputMandalartTitle() {
        return inputMandalartTitle;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JButton getNewButton() {
        return newButton;
    }

    public JButton getShowListButton() {
        return showListButton;
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getClearButton() {
        return clearButton;
    }

    public JButton getCurrentMandalartButton() {
        return currentMandalartButton;
    }

    public JButton getSaveMandalartButton() {
        return saveMandalartButton;
    }

    public JButton getDeleteMandalartButton() {
        return deleteMandalartButton;
    }

    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
  
    public void colorNodeAndChildren(int parentNode, Color color, int[] childIndices, MandalartModel model) {
        // 부모 노드의 위치를 찾기
        int[] parentPosition = findGoalPosition(parentNode, model);
        int parentRow = parentPosition[0];
        int parentCol = parentPosition[1];

        // 부모 노드가 유효한 위치에 있으면 색칠
        if (parentRow != -1 && parentCol != -1) {
        	System.out.println("부모 색칠: "+ parentRow + ", " + parentCol + ".  색: " + color);
            textFields[parentRow][parentCol].setBackground(color); // 상위 노드 색칠
        }

        // 자식 노드들의 위치를 찾기
        for (int child : childIndices) {
            int[] position = findGoalPosition(child, model);
            int row = position[0], col = position[1];

            // 자식 노드가 유효한 위치에 있고 텍스트가 비어 있으면 색칠
            if (row != -1 && col != -1 && textFields[row][col].getText().equals("")) {
                textFields[row][col].setBackground(color); // 하위 노드 색칠
            }
        }
    }
    
    public int[] findGoalPosition(int goalId, MandalartModel model) {
        for (V_MandalartGoalsVO goal : model.getGoalNameList()) {
            if (goal.getGoal_id() == goalId) {
                return new int[]{goal.getRow_idx(), goal.getCol_idx()};
            }
        }
        return new int[]{-1, -1};
    }
    

    
}
