package elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Check box class
 * @author Etienne Hüsler
 * @version 2.0
 */
public class CheckBox 
{
	private int x;
	private int y;
	private String description;
	private boolean checked;
	private boolean hover;
	private int sizeBox;
	
	private final int MARGIN = 10;
	
	public CheckBox(String description, int sizeBox)
	{
		x = 0;
		y = 0;
		this.description = description;
		this.sizeBox = sizeBox;
		checked = false;
		hover = false;
	}
	
	public void check()
	{
		checked = true;
	}
	
	public void uncheck()
	{
		checked = false;
	}
	
	public boolean isChecked()
	{
		return checked;
	}
	
	public void adapt(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Update the check box state depending on the mouse activity 
	 * @param mouseX : Mouse x coordinate
	 * @param mouseY : Mouse y coordinate
	 */
	public void handleClick(int mouseX, int mouseY)
	{
		if (mouseX > x && mouseX < x + sizeBox && mouseY > y && mouseY < y + sizeBox)
		{
			hover = true;
			if (Mouse.leftClickedOptions)
			{
				checked = !checked;
			}
		} else
		{
			hover = false;
		}
	}
	
	/**
	 * Draw the check box in the given context
	 * @param g : Graphic context
	 */
	public void draw(Graphics g)
	{
		// Stroke size depending on the hover state
		Graphics2D g2 = (Graphics2D) g;
		if (hover)
		{
			g2.setStroke(new BasicStroke(4));
		} else
		{
			g2.setStroke(new BasicStroke(2));
		}
		
		// Background
		g.setColor(Color.WHITE);
		g.fillRect(x, y, sizeBox, sizeBox);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, sizeBox, sizeBox);
		
		// Content if checked
		if (checked)
		{
			g.setColor(new Color(51, 153, 255));
			g.fillRect(x + MARGIN, y + MARGIN, sizeBox - 2 * MARGIN, sizeBox - 2 * MARGIN);
		}
		
		// Description of the check box
		g.setFont(new Font("Arial", Font.PLAIN, 26));
		g.drawString(description, x + sizeBox + MARGIN, y + 28);
		g2.setStroke(new BasicStroke(2));
	}
}
