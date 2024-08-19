package com.util;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	
	private Image img;
	
	public ImagePanel(Image img) {
		this.img = img;
		this.setSize(img.getWidth(null), img.getHeight(null));		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);  // 부모 클래스의 paintComponent 메소드를 호출
        g.drawImage(img, 0, 0, null);  // 이미지를 (0, 0) 좌표에서부터 그리기
	}
}

