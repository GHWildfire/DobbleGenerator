package elements;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Class implementing the scroll bar for the symbols
 * @author Etienne Hüsler
 * @version 2.0
 */
public class Scrollbar 
{
	private int x;
	private int y;
	private int width;
	private int height;
	private int speed;
	private boolean visible;
	private boolean firstUse;
	private int savedButtonY;
	private int savedMouseY;
	private int diffY;
	private int buttonY;
	private int exactY;
	
	private int heightButton;
	private int visibleRows;
	private int totalRows;
	
	private int MIN_SIZE = 20;
	
	public Scrollbar()
	{
		heightButton = 0;
		firstUse = true;
		savedMouseY = -1;
		savedButtonY = 0;
		speed = 20;
		diffY = 0;
		buttonY = 0;
		exactY = 0;
	}
	
	public void setVisibilty(boolean visible)
	{
		this.visible = visible;
	}
	
	public boolean isVisible()
	{
		return visible;
	}
	
	/**
	 * Adapt the size of the scroll bar when the window is resized
	 * @param x : new x position
	 * @param y : new y position
	 * @param width : new width
	 * @param height : new height
	 */
	public void adapt(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		buttonY = y;
	}
	
	/**
	 * Adapt the height of the button in the scroll bar
	 * @param visibleRows : visible number of symbol rows
	 * @param totalRows : total number of symbol rows
	 */
	public void setHeightButton(int visibleRows, int totalRows)
	{
		heightButton = (int)((1.0 * visibleRows / totalRows) * height);
		if (heightButton < MIN_SIZE) heightButton = MIN_SIZE;
		this.visibleRows = visibleRows;
		this.totalRows = totalRows;
	}
	
	/**
	 * Check if the symbol row is supposed to be seen or not
	 * @param rowNumber : row number
	 * @return true if the row is in range
	 */
	public boolean isRowInRange(int rowNumber)
	{
		return rowNumber >= getIndex() && rowNumber < getIndex() + visibleRows;
	}
	
	/**
	 * Move the button within the scroll bar on wheel event
	 * @param rotation : mouse rotation value
	 */
	public void scroll(double rotation)
	{
		buttonY += (int) (speed * rotation);
		if (buttonY < y) buttonY = y;
		if (buttonY > y + height - heightButton) buttonY = y + height - heightButton;
	}
	
	/**
	 * Compute the index of the first visible symbol row
	 * @return the first visible row
	 */
	public int getIndex()
	{
		int states = Math.max(1, totalRows - visibleRows + 1);
		int value = buttonY - y;
		float increment = (float) (height - heightButton) / (states);
		for (int i = 0; i < states; i++)
		{
			if (value <= i * increment + 0.5 * increment)
			{
				return i;
			}
		}
		return states - 1;
	}
	
	public boolean isUsed()
	{
		return !firstUse;
	}
	
	/**
	 * Handle the mouse interaction 
	 * @param mouseX : mouse x position
	 * @param mouseY : mouse y position
	 */
	public void handleClick(int mouseX, int mouseY)
	{
		if (mouseX > x && mouseX < x + width && mouseY > exactY && mouseY < exactY + heightButton && Mouse.leftPressed)
		{
			if (firstUse)
			{
				firstUse = false;
				savedMouseY = mouseY;
				savedButtonY = buttonY;
			}
		} 
		else if (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height && Mouse.leftClicked)
		{
			buttonY = mouseY - heightButton / 2;
			if (buttonY < y) buttonY = y;
			if (buttonY > y + height - heightButton) buttonY = y + height - heightButton;
		}
		
		if (savedMouseY != -1)
		{
			if (Mouse.leftPressed)
			{
				diffY = mouseY - savedMouseY;
				buttonY = savedButtonY + diffY;
			}
			else
			{
				firstUse = true;
				diffY = 0;
				savedMouseY = -1;
			}
		}
	}
	
	/**
	 * Compute the rounded height position of the button from it's index and absolute position
	 * @return the rounded button position 
	 */
	private int getButtonY()
	{
		int states = totalRows - visibleRows;
		float increment = (float) (height - heightButton) / (states);
		return y + (int)(getIndex() * increment);
	}
	
	/**
	 * Draw the scroll bar
	 * @param g : graphic context
	 */
	public void draw(Graphics g)
	{
		if (visible)
		{
			exactY = getButtonY();
			
			g.setColor(Color.white);
			g.fillRoundRect(x, exactY, width, heightButton, 20, 20);
			
			g.setColor(Color.black);
			g.drawRoundRect(x, exactY, width, heightButton, 20, 20);
			g.drawRoundRect(x, y, width, height, 20, 20);
		}
	}
}
