package elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Message box animated to show messages to the user
 * @author Etienne Hüsler
 * @version 2.0
 */
public class MessageHandler extends Widget
{
	private String message;
	private boolean error;
	private long startTime;
	
	private final int TRANSITION_DURATION = 300;
	private final int DISPLAY_DURATION = 3000;
	private final int WIDTH = 600;
	private final int HEIGHT = 60;
	private final int CURVE = 20;
	
	public MessageHandler()
	{
		startTime = 0;
		message = "";
		error = false;
		
		w = WIDTH;
		h = HEIGHT;
	}
	
	/**
	 * Add a message to display and stating if it's considered as an error
	 * @param newMessage : Message to be displayed
	 * @param isError : Is the message an error or a warning
	 */
	public void addMessage(String newMessage, boolean isError)
	{
		message = newMessage;
		error = isError;
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * Update the position of the MessageHandler based on it's parent dimension
	 * @param frameWidth : Width of the Frame
	 * @param frameheight : Height of the Frame
	 */
	public void adapt(int frameWidth, int frameHeight)
	{
		x = (frameWidth - w) / 2;
		y = - h - CURVE;
	}
	
	/**
	 * Draw the MessageHandler in the given graphic context
	 * @param g : Graphic context
	 */
	public void draw(Graphics g)
	{
		// Animation in three steps
		int t = (int)(System.currentTimeMillis() - startTime);
		int realY = y;
		if (t < TRANSITION_DURATION)
		{
			// Step 1 : Shows up
			double ratio = (double) t / TRANSITION_DURATION;
			realY = y + (int)(ratio * h);
		} else if (t < TRANSITION_DURATION + DISPLAY_DURATION)
		{
			// Step 2 : Stay fixed during the display duration
			realY = y + h;
		} else if (t < 2 * TRANSITION_DURATION + DISPLAY_DURATION)
		{
			// Step 3 : Hides at the top of the Frame
			double ratio = 1 - (double) (t - TRANSITION_DURATION - DISPLAY_DURATION) / TRANSITION_DURATION;
			realY = y + (int)(ratio * h);
		}
		
		// Background
		g.setColor(Color.WHITE);
		g.fillRoundRect(x, realY, w, h, CURVE, CURVE);
		g.setColor(Color.BLACK);
		g.drawRoundRect(x, realY, w, h, CURVE, CURVE);
		
		// Message color
		if (error) g.setColor(new Color(230, 0, 0));
		else g.setColor(new Color(0, 0, 150));
		
		// Draw message
		g.setFont(new Font("Arial", Font.BOLD, 20));
		int widthText = g.getFontMetrics().stringWidth(message);
		int xText = x + (w - widthText) / 2;
		g.drawString(message, xText, realY + h - 13);
	}
}
