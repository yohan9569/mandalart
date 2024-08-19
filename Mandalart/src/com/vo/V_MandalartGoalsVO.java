package com.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class V_MandalartGoalsVO {
    private int serial_num;
    private int mandalart_id;
    private int goal_id;
    private String goal_name;
    private int achieved;
    private int row_idx;
    private int col_idx;
    private int parent_node;		
}