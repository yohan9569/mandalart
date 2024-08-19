package com.controller;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.model.MandalartModel;
import com.view.MandalartView;
import com.vo.V_MandalartGoalsVO;

public class MandalartController {
	private MandalartModel model;
	private MandalartView view;
	private V_MandalartGoalsVO goal;
	private Color achievedColor;

	public MandalartController(MandalartModel model, MandalartView view) {
		this.model = model;
		this.view = view;
		achievedColor = new Color(255, 255, 200);

		initView();
		initController();

		try {
			model.openMandalart();
			updateViewWithData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void initView() {
		view.getFrame().setVisible(true);
	}


	public void updateViewWithData() throws SQLException {
		view.goalNameAndAchivedIntoCell( model.getGoalNameList() );
		view.updateProgressBar( model.getMandalartVo().getTotal_achieved() );
		view.viewMandalartTitle( model.getMandalartVo().getMandalart_title() );
		
		for (int k : model.getParentMappings().keySet()) {
			if (findGoalAchieved(k) == 1) {
				view.colorNodeAndChildren(k, achievedColor, model.getParentMappings().get(k), model);
			}
		}
	}


	public void updateParentNode(int row_idx, int col_idx) throws SQLException {
		int parent_node = findParentNode(row_idx, col_idx);
		int[] position = findGoalPosition(parent_node);
		int parent_row = position[0], parent_col = position[1];
		boolean allAchieved = checkChildNodes(parent_node, achievedColor);

		System.out.println("updateParentNode : 실행 ! " + row_idx + ", " + col_idx );


		if (allAchieved) {
			if (!isGoalAchieved(parent_node)) {
				model.toggleGoalAchieved(parent_row, parent_col);
			}
			view.colorNodeAndChildren(parent_node, achievedColor, model.getParentMappings().get(parent_node), model);
		} else {
			if (isGoalAchieved(parent_node)) {
				model.toggleGoalAchieved(parent_row, parent_col);
			}
			view.colorNodeAndChildren(parent_node, Color.WHITE, model.getParentMappings().get(parent_node), model);
		}

		boolean rootAchieved = checkChildNodes(41, achievedColor);
		int[] rootPosition = findGoalPosition(41);
		int root_row = rootPosition[0], root_col = rootPosition[1];

		if (rootAchieved) {
			if (!isGoalAchieved(41)) {
				model.toggleGoalAchieved(root_row, root_col);
			}
			view.colorNodeAndChildren(41, achievedColor, model.getParentMappings().get(41), model);
		} else {
			if (isGoalAchieved(41)) {
				model.toggleGoalAchieved(root_row, root_col);
			}
			view.colorNodeAndChildren(41, Color.WHITE, model.getParentMappings().get(41), model);
		}
	}

	private boolean isGoalAchieved(int goal_id) {
		for (V_MandalartGoalsVO goal : model.getGoalNameList()) {
			if (goal.getGoal_id() == goal_id) {
				return goal.getAchieved() == 1;
			}
		}
		return false;
	}

	// 하위 노드들을 확인하여 텍스트 가진 노드가 하나 이상 있고, 그들이 모두 달성되었을 때 true 반환
	private boolean checkChildNodes(int parentNode, Color targetColor) {
		boolean allAchieved = false; // 텍스트를 가진 노드가 있는지 여부

		for (int child : model.getParentMappings().get(parentNode)) {
			int[] position = findGoalPosition(child);
			int row = position[0], col = position[1];

			if (row == -1 || col == -1) continue; // 목표를 찾지 못한 경우 다음 child로 넘어감

			if (!view.getTextFields()[row][col].getText().equals("")) {
				allAchieved = true; // 텍스트를 가진 노드가 있음을 표시
				if (!view.getTextFields()[row][col].getBackground().equals(targetColor)) {
					return false; // 텍스트를 가진 노드가 달성되지 않았으면 false 반환
				}
			}
		}
		return allAchieved; // 텍스트를 가진 노드가 하나 이상 있고 모두 달성되었으면 true 반환
	}

	private int findParentNode(int row_idx, int col_idx) {
		for (V_MandalartGoalsVO goal : model.getGoalNameList()) {
			if (goal.getRow_idx() == row_idx && goal.getCol_idx() == col_idx) {
				System.err.println();
				return goal.getParent_node();
			}
		}
		return -1;
	}

	private int[] findGoalPosition(int goalId) {
		for (V_MandalartGoalsVO goal : model.getGoalNameList()) {
			if (goal.getGoal_id() == goalId) {
				return new int[]{goal.getRow_idx(), goal.getCol_idx()};
			}
		}
		return new int[]{-1, -1};
	}
	
	private int findGoalAchieved(int goalId) {
		for (V_MandalartGoalsVO goal : model.getGoalNameList()) {
			if (goal.getGoal_id() == goalId) {
				return goal.getAchieved();
			}
		}
		return -1;
	}
	
	public void initController() {
		// 제목을 클릭했을 때, 커서가 나오도록 설정
		view.getInputMandalartTitle().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!view.getInputMandalartTitle().isEnabled()) {
					view.getInputMandalartTitle().setEnabled(true);
					view.getInputMandalartTitle().setText("");
					view.getInputMandalartTitle().setForeground(Color.DARK_GRAY);
					view.getInputMandalartTitle().requestFocus();

					System.out.println("제목을 클릭함");
				}
			}
		});

		// 제목 입력하다가 포커스 읽으면, DB update
		view.getInputMandalartTitle().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String title = view.getInputMandalartTitle().getText();

				try {
					model.updateMandalartTitle(title, model.getMandalartVo().getMandalart_id());
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});


		view.getNewButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane.showConfirmDialog(view.getFrame(), "현재 상태를 저장하고 새 만다라트를 생성합니다.", "알림", JOptionPane.YES_NO_OPTION);
				if (response == JOptionPane.YES_OPTION) {
					try {
						model.getConnection().commit();

						model.insertMandalart();
						model.getMandalartGrid();
						model.getMandalartTitle();
						model.updateTotalAchieved();

						updateViewWithData();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
		});


		view.getSaveButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					model.updateLastModifiedDate(model.getMandalartVo().getMandalart_id());
					model.updateCompletedDate();
					model.saveCommit();
					model.updateTotalAchieved();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});

		view.getClearButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane.showConfirmDialog(view.getFrame(), "정말 비우시겠습니까?", "경고", JOptionPane.YES_NO_OPTION);
				if (response == JOptionPane.YES_OPTION) {
					try {
						model.clearMandalartData();
						model.getMandalartGrid();
						model.getMandalartTitle();
						model.updateTotalAchieved(); 

						updateViewWithData();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
		});


		view.getShowListButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.refreshMandalartList(view.getTableModel());
				((CardLayout) view.getMainPanel().getLayout()).show(view.getMainPanel(), "list");
			}
		});


		view.getCurrentMandalartButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					model.openMandalart();
					updateViewWithData();

				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				((CardLayout) view.getMainPanel().getLayout()).show(view.getMainPanel(), "mandalart");
			}
		});

		view.getSaveMandalartButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					model.saveCommit();
					model.refreshMandalartList(view.getTableModel());
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});


		// mandalartList 목록 화면에서 제목 변경했을 때: 제목 update, 최근 수정일자 update, 화면에 출력 
		view.getTableModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE) {
					int column = e.getColumn();
					int row = e.getFirstRow();
					if (column == 1) {
						DefaultTableModel tableModel = (DefaultTableModel) e.getSource();
						String update_mandalart_title = (String) tableModel.getValueAt(row, column);
						int mandalart_id = (int) tableModel.getValueAt(row, 0);
						try {
							model.updateMandalartTitle(update_mandalart_title, mandalart_id);
							model.updateLastModifiedDate(mandalart_id);

							tableModel.setValueAt(model.getMandalartVo().getLast_modified_date(), row, 4);
						} catch (SQLException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});


		view.getDeleteMandalartButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane.showConfirmDialog(view.getFrame(), "만다라트를 삭제하시겠습니까?", "경고", JOptionPane.YES_NO_OPTION);
				if (response == JOptionPane.YES_OPTION) {
					int selectedRow = view.getTable().getSelectedRow();
					if (selectedRow != -1) {
						DefaultTableModel tableModel = (DefaultTableModel) view.getTable().getModel();
						int mandalart_id = (int) tableModel.getValueAt(selectedRow, 0);
						try {
							model.deleteMandalart(mandalart_id);
							tableModel.removeRow(selectedRow);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});

		// 테이블의 1개의 행을 더블클릭하면, 해당 만다라트로 이동
		view.getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && view.getTable().getSelectedRow() != -1) {
					// 선택된 행의 인덱스를 가져옴
					int selectedRow = view.getTable().getSelectedRow();

					// 해당 행의 mandalart_id를 가져옴
					int mandalart_id = (int) view.getTable().getValueAt(selectedRow, 0);  // 선택한 행의 mandalart_id는 0번째에 있음

					// mandalart_id를 설정하고 데이터를 가져옴
					model.getMandalartVo().setMandalart_id(mandalart_id);

					try {
						model.getMandalartGrid();
						model.updateTotalAchieved();                             
						model.getMandalartTitle();

						updateViewWithData();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
					((CardLayout) view.getMainPanel().getLayout()).show(view.getMainPanel(), "mandalart");
				}
			}
		});


		for (int i = 0; i < view.getTextFields().length; i++) {
			for (int j = 0; j < view.getTextFields()[i].length; j++) {
				final int row_idx = i;
				final int col_idx = j;


				// 목표(goal_name) 입력하다가 포커스 잃었을 때, DB에 업데이트
				view.getTextFields()[i][j].addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent e) {
						String goal_name = view.getTextFields()[row_idx][col_idx].getText();
						try {
							if (goal_name == null || goal_name.trim().isEmpty()) {
								goal_name = null;
								
								if (findGoalAchieved(model.findGoalId(row_idx, col_idx))==1) {
									model.toggleGoalAchieved(row_idx, col_idx);
									view.getTextFields()[row_idx][col_idx].setBackground(Color.WHITE);
									updateParentNode(row_idx, col_idx);
									model.updateTotalAchieved();
									view.updateProgressBar(model.getMandalartVo().getTotal_achieved());
								}
							}
							model.updateGoalName(row_idx, col_idx, goal_name); 
							if (findGoalAchieved(model.findGoalId(row_idx, col_idx))==0) {
								view.getTextFields()[row_idx][col_idx].setBackground(Color.WHITE);
								updateParentNode(row_idx, col_idx); 
								model.updateTotalAchieved();
								view.updateProgressBar(model.getMandalartVo().getTotal_achieved());								
							}
						} catch (SQLException ex) {
							ex.printStackTrace();
						}
					};
				});

				// 오른쪽 클릭이 금지된 좌표인지 확인
				boolean isNotUsed = false;
				for (int[] mapping : model.getIdxNotUsed()) {
					if (mapping[0] == row_idx && mapping[1] == col_idx) {
						isNotUsed = true;
						break;
					}
				}

				// 오른쪽 클릭이 가능한 경우, 오른쪽 버튼 클릭 이벤트 추가
				if (!isNotUsed) {
					view.getTextFields()[row_idx][col_idx].addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {
							if (SwingUtilities.isRightMouseButton(e)) {
								String goalName = view.getTextFields()[row_idx][col_idx].getText();

								if (goalName != null && !goalName.trim().isEmpty()) {
									Color currentColor = view.getTextFields()[row_idx][col_idx].getBackground();
									Color newColor = (currentColor.equals(new Color(255, 255, 200))) ? Color.WHITE : new Color(255, 255, 200);

									view.getTextFields()[row_idx][col_idx].setBackground(newColor);

									try {
										model.toggleGoalAchieved(row_idx, col_idx);
										updateParentNode(row_idx, col_idx);
										model.updateTotalAchieved();

										view.updateProgressBar(model.getMandalartVo().getTotal_achieved());
										if (model.getMandalartVo().getTotal_achieved() == 100) {
											JOptionPane.showMessageDialog(null, "짝짝짝! 100% 목표 달성을 축하드립니다!");
										}
									} catch (SQLException e1) {
										e1.printStackTrace();
									}
								}
							}
						}
					});
				}
			}
		}


		for (int i = 0; i < view.getTextFields().length; i++) {
			for (int j = 0; j < view.getTextFields()[i].length; j++) {
				final int row = i;
				final int col = j;

				// 클론 노드 동기화 
				// 향상된 for 문 돌면서  원본 노드 행렬값, 클론 노드 행렬값 가져옴
				for (int[] mapping : model.getGoalMappings()) {
					if (mapping[0] == row && mapping[1] == col) {

						// DocumentListener를 추가하여 텍스트 변경 사항을 동기화
						view.getTextFields()[row][col].getDocument().addDocumentListener(new DocumentListener() {
							@Override
							public void insertUpdate(DocumentEvent e) {
								view.getTextFields()[mapping[2]][mapping[3]].setText(view.getTextFields()[row][col].getText());
							}

							@Override
							public void removeUpdate(DocumentEvent e) {
								view.getTextFields()[mapping[2]][mapping[3]].setText(view.getTextFields()[row][col].getText());
							}

							@Override
							public void changedUpdate(DocumentEvent e) {
								view.getTextFields()[mapping[2]][mapping[3]].setText(view.getTextFields()[row][col].getText());
							}
						});


						// PropertyChangeListener를 추가하여 배경색 변경 사항을 동기화
						view.getTextFields()[row][col].addPropertyChangeListener("background", new PropertyChangeListener() {
							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								Color orgColor = view.getTextFields()[row][col].getBackground();
								if (orgColor == Color.WHITE) orgColor = new Color(240, 240, 240);
								view.getTextFields()[mapping[2]][mapping[3]].setBackground(orgColor);
							}
						});
					}
				}
			}
		}

	}
}