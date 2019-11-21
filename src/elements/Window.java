package elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Sub window class to display both Main Frame generation zones
 * @author Etienne Hüsler
 * @version 2.0
 */

public class Window extends Widget
{
	private int widthTitle;
	private int heightTitle;
	private Font font;
	private String title;
	private boolean isFab;
	
	private final double PERCENT_WIDTH_USAGE = 0.9;
	private final int GAP_BETWEEN_WINDOWS = 50;
	private final int HEIGHT_PREVIEW = 150;
	
	public Window(String title, boolean isFab)
	{
		widthTitle = 300;
		heightTitle = 40;
		font = new Font("Arial", Font.BOLD, 30);
		this.title = title;
		this.isFab = isFab;
	}
	
	/**
	 * Adapt the window to a resize event
	 * @param x : New x position
	 * @param y : New y position
	 * @param width : New width
	 * @param height : New height
	 */
	public void adapt(int frameWidth, int frameHeight)
	{
		int width = (int)(PERCENT_WIDTH_USAGE * frameWidth);
		int heightFab = frameHeight - HEIGHT_PREVIEW - 5 * GAP_BETWEEN_WINDOWS;
		
		x = (frameWidth - width) / 2;
		w = width;
		
		if (isFab)
		{
			y = 2 * GAP_BETWEEN_WINDOWS;
			h = heightFab;
		} else
		{
			y = 3 * GAP_BETWEEN_WINDOWS + heightFab;
			h = HEIGHT_PREVIEW;
		}
	}
	
	/**
	 * Draw the window
	 * @param g : Graphic context
	 */
	public void draw(Graphics g)
	{
		g.setFont(font);
		
		g.setColor(Color.white);
		g.fillRoundRect(x, y, w, h, 60, 60);
		g.setColor(Color.black);
		g.drawRoundRect(x, y, w, h, 60, 60);
		g.setColor(new Color(51, 153, 255));
		g.fillRect(x + (w / 2) - (widthTitle / 2), y - (heightTitle / 2), widthTitle, heightTitle);
		g.setColor(Color.black);
		g.drawRect(x + (w / 2) - (widthTitle / 2), y - (heightTitle / 2), widthTitle, heightTitle);
		
		g.drawString(title, x + (w / 2) - title.length() * 7, y + 10);
		
	}
}
