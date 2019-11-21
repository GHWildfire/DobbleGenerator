package elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 * Button class
 * @author Etienne Hüsler
 * @version 2.0
 */
public class Button extends Widget
{
	private Image image;
	private enum State {NORMAL, OVERLAY, CLICKED, DISABLED};
	private State state;
	private int offset;
	
	private final int CURVE = 10;
	
	public Button(Image image, int relativeX, int relativeY, int w, int h, int offset, Widget parent)
	{
		this.image = image;
		this.parent = parent;
		this.w = w;
		this.h = h;
		this.offset = offset;
		state = State.NORMAL;
	}
	
	/**
	 * Set the position of the button
	 * @param newX : X coordinate
	 * @param newY : Y coordinate
	 */
	public void adapt(int newX, int newY)
	{
		x = newX;
		y = newY;
	}
	
	/**
	 * Activate the button by changing it's state
	 * @param activated : Activation of the button
	 */
	public void setActivation(boolean activated)
	{
		if (activated) state = State.NORMAL;
		else state = State.DISABLED;
	}
	
	/**
	 * Define if the button is in the clicked state
	 * @return true if the button is clicked
	 */
	public boolean isClicked()
	{
		return state == State.CLICKED;
	}
	
	/**
	 * Update the state of the button depending on the mouse activity
	 * @param mouseX : Mouse X coordinate
	 * @param mouseY : Mouse Y coordinate
	 * @param leftClicked : Mouse left click state
	 */
	public void handleClick(int mouseX, int mouseY, boolean leftClicked)
	{
		if (state != State.DISABLED)
		{
			if (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h)
			{
				state = State.OVERLAY;
				if (leftClicked)
				{
					state = State.CLICKED;
					leftClicked = false;
				} 
			}
			else
			{
				state = State.NORMAL;
			}
		}
	}
	
	/**
	 * Draw the button in the given context
	 * @param g : Graphic context
	 */
	public void draw(Graphics g)
	{
		// Background
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.WHITE);
		g.fillRoundRect(x, y, w, h, CURVE, CURVE);
		
		// Content depending on the state
		g.setColor(Color.BLACK);
		switch (state)
		{
			case NORMAL:
			    g2.setStroke(new BasicStroke(2));
				g.drawImage(image, x + offset, y + offset, null);
				g.drawRoundRect(x, y, w, h, CURVE, CURVE);
				break;
			case OVERLAY:
			    g2.setStroke(new BasicStroke(4));
				g.drawImage(image, x + offset, y + offset, null);
				g.drawRoundRect(x, y, w, h, CURVE, CURVE);
			    g.setColor(new Color(150, 150, 150, 50));
			    g.fillRoundRect(x, y, w, h, CURVE, CURVE);
				break;
			case CLICKED:
				g.drawImage(image, x + offset, y + offset, null);
			    g2.setStroke(new BasicStroke(4));
				g.drawRoundRect(x, y, w, h, CURVE, CURVE);
			    break;
			case DISABLED:
				break;
		}
		
		// Reset the previous context state
		g.setColor(Color.BLACK);
	    g2.setStroke(new BasicStroke(2));
	}
}

