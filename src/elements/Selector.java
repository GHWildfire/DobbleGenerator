package elements;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

import panels.Panel;

/**
 * Selector to choose the number of symbols per card
 * @author Etienne Hüsler
 * @version 2.0
 */
public class Selector extends Widget
{
	private int x;
	private int y;
	private int index;
	private int buttonsSpacing;
	private int sizeImages;
	private Button left;
	private Button right;
	private String[] values;
	private String description;
	private int lengthDecription;
	private boolean firstFrame;
	
	private final int OFFSET_IMAGE = 10;
	private final int MARGING = 10;
	
	public Selector(String description, int sizeImages, int buttonsSpacing, String[] values, Widget parent)
	{
		this.description = description;
		this.buttonsSpacing = buttonsSpacing;
		this.values = values;
		this.sizeImages = sizeImages;
		index = 1;
		this.parent = parent;
		lengthDecription = 0;
		firstFrame = true;
		
		left = new Button(generateImage("/pictures/minus.png", sizeImages - OFFSET_IMAGE), 0, 0, sizeImages, sizeImages, 5, null);
		right = new Button(generateImage("/pictures/add.png", sizeImages - OFFSET_IMAGE), 0, 0, sizeImages, sizeImages, 5, null);
	}
	
	public String getValue()
	{
		return values[index];
	}
	
	/**
	 * Helper method to generate an image
	 * @param path : Path of the image
	 * @param size : Size of the image
	 * @return the resized generated image found at given path
	 */
	private Image generateImage(String path, int size)
	{
		return new ImageIcon(Main.class.getResource(path)).getImage().getScaledInstance(size, size, Image.SCALE_DEFAULT);
	}
	
	/**
	 * Adapt the content of the Selector depending on the Frame dimension
	 * @param frameWidth : Frame width
	 * @param frameHeight : Frame height
	 */
	public void adapt(int frameWidth, int frameHeight)
	{
		x = parent.getX() + 50;
		y = parent.getY() + 46;
		left.adapt(x + lengthDecription + MARGING + 5, y - 6);
		right.adapt(x + lengthDecription + buttonsSpacing + MARGING, y - 6);
	}
	
	/**
	 * Update the content of the Selector depending on the mouse activity
	 * @param panel : Panel containing the Selector
	 * @param mouseX : Mouse x coordinate
	 * @param mouseY : Mouse y coordinate
	 */
	public void handleClick(Panel panel, int mouseX, int mouseY)
	{
		left.handleClick(mouseX, mouseY, Mouse.leftClicked);
		right.handleClick(mouseX, mouseY, Mouse.leftClicked);
	
		if (values.length > 0)
		{
			if (left.isClicked()) 
			{
				// decrement the number of symbols per card
				index = Math.max(0, index - 1);
				if (index == 0) left.setActivation(false);
				right.setActivation(true);
				panel.computeNumberOfCards();
			}
			if (right.isClicked()) 
			{
				// increment the number of symbols per card
				index = Math.min(values.length - 1, index + 1);
				if (index == values.length - 1) right.setActivation(false);
				left.setActivation(true);
				panel.computeNumberOfCards();
			}
		}
	}
	
	/**
	 * Draw the Selector
	 * @param g : Graphic context
	 */
	public void draw(Graphics g)
	{
		// Draw the buttons
		left.draw(g);
		right.draw(g);
		
		// Draw the description
		g.setFont(new Font("Arial", Font.PLAIN, 20));
		g.drawString(description, x, y + 20);
		lengthDecription = g.getFontMetrics().stringWidth(description);
		
		// Security to ensure the Selector adaption to the Frame
		if (firstFrame)
		{
			firstFrame = false;
			adapt(0, 0);
		}
		
		// Draw the selected number of symbols per page
		if (values.length > 0) 
		{
			g.setFont(new Font("Arial", Font.BOLD, 22));
			int valuePosition = x + lengthDecription + (int)(2.6 * MARGING) + sizeImages;
			g.drawString(values[index], valuePosition, y + 23);
		}
		else 
		{
			g.setFont(new Font("Arial", Font.PLAIN, 20));
			g.drawString("No values", x, y);
		}
	}
}
