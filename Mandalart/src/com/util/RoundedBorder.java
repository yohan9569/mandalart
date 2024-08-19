package com.util;

import java.awt.*;
import javax.swing.border.AbstractBorder;

public class RoundedBorder extends AbstractBorder {
	private int radius;
	private Color color;
	private int thickness;

	public RoundedBorder(int radius, Color color, int thickness) {
		this.radius = radius;
		this.color = color;
		this.thickness = thickness;
	}

	public Insets getBorderInsets(Component c) {
		return new Insets(this.thickness, this.thickness, this.thickness, this.thickness);
	}

	public boolean isBorderOpaque() {
		return true;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(thickness));
		g2d.drawRoundRect(x + thickness / 2, y + thickness / 2, width - thickness, height - thickness, radius, radius);
	}
}

