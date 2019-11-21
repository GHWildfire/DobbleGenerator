package elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Slider class to change print settings
 * @author Etienne Hüsler
 * @version 2.0
 */
public class Slider 
{
	private String description;
	private String units;
	private int x;
	private int y;
	private int length;
	private double min;
	private double max;
	private double step;
	private double value;
	private double range;
	private double xSlider;
	private boolean hover;
	private boolean selected;
	
	private final int SIZE_SLIDER = 20;
	
	public Slider(String description, String units, int length, double min, double max, double init, double step)
	{
		this.description = description;
		this.units = units;
		this.length = length;
		this.min = min;
		this.max = max;
		this.step = step;
		value = init;
		range = max - min;
		x = 0;
		y = 0;
		xSlider = x + length * (init - min) / range;
		hover = false;
		selected = false;
	}
	
	public boolean isSelected()
	{
		return selected;
	}
	
	public double getValue()
	{
		return value;
	}
	
	/**
	 * Update the slider depending on the mouse activity
	 * @param mouseX : Mouse x coordinate
	 * @param mouseY : Mouse y coordinate
	 */
	public void handleClick(int mouseX, int mouseY)
	{
		if (mouseX > xSlider - SIZE_SLIDER / 2 && mouseX < xSlider + SIZE_SLIDER / 2 && mouseY > y - SIZE_SLIDER / 2 && mouseY < y + SIZE_SLIDER / 2)
		{
			if (Mouse.leftPressedOptions)
			{
				selected = true;
				hover = false;
			}
			if (!selected)
			{
				hover = true;
			}
		} else
		{
			hover = false;
		}
		
		if (!Mouse.leftPressedOptions)
		{
			selected = false;
		}
		
		if (selected)
		{
			xSlider = mouseX;
			if (xSlider < x) xSlider = x;
			if (xSlider > x + length) xSlider = x + length;
			
			value = min + (xSlider - x) / length * range;
			double rest = value % step;
			if (rest < step / 2) value -= rest;
			else value += (step - rest);
		}
	}
	
	/**
	 * Update maximum slider value
	 * @param newMax
	 */
	public void setMax(double newMax)
	{
		max = newMax;
		
		range = newMax - min;
		if (value > newMax)
		{
			value = newMax;
		}
	}
	
	/**
	 * Update slider position
	 * @param x : New x coordinate
	 * @param y : New y coordinate
	 */
	public void adapt(int x, int y)
	{
		this.x = x;
		this.y = y;
		xSlider = x + length * (value - min) / range;
	}
	
	/**
	 * Draw the slider
	 * @param g : Graphic context
	 */
	public void draw(Graphics g)
	{
		// Hover stroke width
		Graphics2D g2 = (Graphics2D) g;
		if (hover)
		{
			g2.setStroke(new BasicStroke(4));
		} else
		{
			g2.setStroke(new BasicStroke(2));
		}
		
		// Slider
		g.setColor(Color.BLACK);
		g.fillRect(x, y, length, 5);
		g.fillRect(x, y, 2, 15);
		g.fillRect(x + length - 2, y, 2, 15);

		// Button of the slider
		g.setColor(Color.WHITE);
		g.fillOval((int) xSlider - 9, y - 8, SIZE_SLIDER, SIZE_SLIDER);
		g.setColor(new Color(51, 153, 255));
		g.drawOval((int) xSlider - 9, y - 8, SIZE_SLIDER, SIZE_SLIDER);
		
		// Strings
		int decimals = 0;
		if (step < 1) decimals = 1;
		String fullDescription = description + " (" + String.format("%,." + decimals + "f", value) + " " + units + ")";
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.PLAIN, 22));
		int decriptionWidth = g.getFontMetrics().stringWidth(fullDescription);
		g.drawString(fullDescription, x + (length - decriptionWidth) / 2, y - 10);
		
		g.setFont(new Font("Arial", Font.PLAIN, 16));
		int minWidth = g.getFontMetrics().stringWidth(String.format("%,." + decimals + "f", min));
		int maxWidth = g.getFontMetrics().stringWidth(String.format("%,." + decimals + "f", max));
		g.drawString(String.format("%,." + decimals + "f", min), x + 1 - minWidth / 2, y + 30);
		g.drawString(String.format("%,." + decimals + "f", max), x + length - 1 - maxWidth / 2, y + 30);
		
		g2.setStroke(new BasicStroke(2));
	}
}
