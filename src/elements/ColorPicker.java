package elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JColorChooser;

/**
 * Color picker to select the color of a tag
 * @author Etienne Hüsler
 * @version 2.0
 */
public class ColorPicker 
{
	private int x;
	private int y;
	private int w;
	private int h;
	private boolean selected;
	private boolean open;
	
	private final int CURVE = 10;
	
	private Color color;
	
	public ColorPicker()
	{
		color = Color.GREEN;
		selected = false;
		open = false;
	}
	
	/**
	 * Update the color picker dimension and position
	 * @param x : Color picker x coordinate
	 * @param y : Color picker y coordinate
	 * @param w : Color picker width
	 * @param h : Color picker height
	 */
	public void adapt(int x, int y, int w, int h)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public int getWidth()
	{
		return w;
	}
	
	public int getHeight()
	{
		return h;
	}
	
	/**
	 * Update the color picker depending on the mouse activity
	 * @param mouseX : Mouse X coordinate
	 * @param mouseY : Mouse Y coordinate
	 * @param leftClicked : Mouse left click state
	 */
	public void handleClick(int mouseX, int mouseY, boolean leftClicked)
	{
		if (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h)
		{ 
			selected = true;
			if (leftClicked && !open)
			{
				// Open dialog
				open = true;
				Color savedColor = color;
				color = JColorChooser.showDialog(null, "Tag Color Selection", color);
				
				// Reset the previous color if no one was selected
				if (color == null)
				{
					color = savedColor;
				}
				open = false;
			}
		} else
		{
			selected = false;
		}
	}
	
	/**
	 * Generate a random color for the picker
	 */
	public void randomize()
	{
		color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
	}
	
	/**
	 * Draw the color picker in the given graphic context
	 * @param g : Graphic context
	 */
	public void draw(Graphics g)
	{
		// Selected stroke size
		Graphics2D g2 = (Graphics2D) g;
		if (selected)
		{
			g2.setStroke(new BasicStroke(4));
		} else
		{
			g2.setStroke(new BasicStroke(2));
		}
		
		// Draw the color picker
		g.setColor(color);
		g.fillRoundRect(x, y, w, h, CURVE, CURVE);
		g.setColor(Color.BLACK);
		g.drawRoundRect(x, y, w, h, CURVE, CURVE);
	}
}
