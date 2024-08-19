package com.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MandalartGoalsVo {
	
	private int serial_num;          
	private int mandalart_id;        
	private int goal_id;                    
	private int position;                   
	private String goal_name;   
	private int achieved;                                  
	private int parent_node;                 
     
}
