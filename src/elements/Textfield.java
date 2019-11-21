package elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import content.Symbol;

/**
 * Used for any input text
 * @author Etienne Hüsler
 * @version 2.0
 */
public class Textfield implements KeyListener
{
	private String content;
	private String defaultContent;
	private boolean selected;
	private Symbol symbol;
	private int mouseX;
	private int mouseY;
	private int contentIndex;
	private Graphics g;
	private int maxLength;
	private int x;
	private int y;
	private int w;
	private int h;
	private int hExtension;
	
	private final int CURVE = 20;
	private final int SIZE_BORDERS = 3;
	
	public Textfield(String defaultContent)
	{
		content = "";
		this.defaultContent = defaultContent;
		selected = false;
		hExtension = 0;
	}
	
	public void addHeightExtension(int hExtension)
	{
		this.hExtension = hExtension;
	}
	
	public void select()
	{
		selected = true;
	}
	
	public boolean isSelected()
	{
		return selected;
	}
	
	public void setContent(String content)
	{
		this.content = content;
		contentIndex = content.length();
	}
	
	public void associateSymbol(Symbol symbol)
	{
		this.symbol = symbol;
	}
	
	public String getContent()
	{
		return content;
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
	 * Set position and dimension to the text field
	 * @param x : Text field x coordinate
	 * @param y : Text field y coordinate
	 * @param w : Text field width
	 * @param h : Text field height
	 */
	public void adapt(int x, int y, int w, int h) 
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		
		maxLength = w - 2 * CURVE;
	}
	
	/**
	 * Update the text field depending on the mouse activity
	 * @param mouseX : Mouse x coordinate
	 * @param mouseY : Mouse y coordinate
	 */
	public void handleClick(int mouseX, int mouseY)
	{
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	/**
	 * Draw the text field
	 * @param g : Graphics context
	 * @param mouseLeftClick : Mouse left click state
	 * @param mouseLeftPressed : Mouse left pressed state
	 */
	public void draw(Graphics g, boolean mouseLeftClick, boolean mouseLeftPressed) 
	{
		this.g = g;
		
		// Handle the selection
		if (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h + hExtension)
		{
			if (selected)
			{
				g.setColor(Color.BLACK);
				g.fillRoundRect(x - SIZE_BORDERS, y - SIZE_BORDERS, w + 2 * SIZE_BORDERS + 1, h + 2 * SIZE_BORDERS + 1, (int)(1.3 * CURVE), (int)(1.3 * CURVE));
			}
			if (mouseLeftClick)
			{
				selected = true;
			}
		} else 
		{
			if ((mouseLeftClick || mouseLeftPressed || Mouse.rightClicked) && selected)
			{
				selected = false;
				hExtension = 0;
				if (symbol != null)
				{
					symbol.rename(content);
				}
			}	
		}
		
		// Draw the borders
		if (selected)
		{
			g.setColor(Color.BLACK);
			g.fillRoundRect(x - SIZE_BORDERS, y - SIZE_BORDERS, w + 2 * SIZE_BORDERS + 1, h + 2 * SIZE_BORDERS + 1, (int)(1.3 * CURVE), (int)(1.3 * CURVE));
		}
		
		// Draw the text field
		g.setColor(Color.WHITE);
		g.fillRoundRect(x, y, w, h, CURVE, CURVE);
		g.setColor(Color.BLACK);
		g.drawRoundRect(x, y, w, h, CURVE, CURVE);
		if (content.equals("") && !selected)
		{
			g.setFont(new Font("Arial", Font.ITALIC, 20));
			g.drawString(defaultContent, x + CURVE, y + CURVE + 17);
		} else 
		{
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.drawString(content, x + CURVE, y + CURVE + 17);
		}
		
		// Draw the text position bar
		if (selected)
		{
			g.setColor(Color.BLACK);
			long time = System.currentTimeMillis();
			if (time % 1000 < 500)
			{
				String text = content.substring(0, contentIndex);
				int sizeText = g.getFontMetrics().stringWidth(text);
				g.fillRect(x + CURVE + sizeText, y + 16, 2, 25);
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent event) 
	{
		if (selected)
		{
			int key = event.getKeyCode();
			switch (key)
			{
				case KeyEvent.VK_LEFT:
					if (contentIndex > 0) contentIndex--;
					break;
				case KeyEvent.VK_RIGHT:
					if (contentIndex < content.length()) contentIndex++;
					break;
				case KeyEvent.VK_ENTER:
					if (symbol != null)
					{
						symbol.rename(content);
					}
					selected = false;
					break;
				case KeyEvent.VK_BACK_SPACE:
					if (content.length() > 0)
					{
						content = content.substring(0, contentIndex - 1) + content.substring(contentIndex, content.length());
						contentIndex--;
					}
					break;
				case KeyEvent.VK_DELETE:
					content = "";
					contentIndex = 0;
					break;
				case KeyEvent.VK_SPACE:
					String tmp = content.substring(0, contentIndex) + " " + content.substring(contentIndex, content.length());
					if (g.getFontMetrics().stringWidth(tmp) < maxLength)
					{
						content = tmp;
						contentIndex++;
					}
					break;
				default:
					char c = event.getKeyChar();
					if (c != KeyEvent.CHAR_UNDEFINED)
					{
						String newContent = content.substring(0, contentIndex) + c + content.substring(contentIndex, content.length());
						if (g.getFontMetrics().stringWidth(newContent) < maxLength)
						{
							content = newContent;
							contentIndex++;
						}
					}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {}

	@Override
	public void keyTyped(KeyEvent event) {}
}
