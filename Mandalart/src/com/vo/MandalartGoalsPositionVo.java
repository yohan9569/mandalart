package com.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MandalartGoalsPositionVo {
	
	private int goal_id;                             
	private int row_idx;                   
	private int col_idx;   
	private int position;                                  
	private int parent_node;                 
     
}
