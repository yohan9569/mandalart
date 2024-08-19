package com.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MandalartVo {
	
	private int mandalart_id;
	private String mandalart_title;
	private int total_achieved;  
	private String created_date;  
	private String last_modified_date;  
	private String completed_date;
     
}
