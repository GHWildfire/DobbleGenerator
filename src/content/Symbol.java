package content;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import elements.Mouse;
import panels.Panel;

/**
 * Symbols of the cards
 * @author Etienne Hüsler
 * @version 2.0
 */
public class Symbol 
{
	private int x;
	private int y;
	private int width;
	private int height;
	private String name;
	private String ext;
	private boolean activated;
	private boolean preselected;
	private boolean selected;
	private boolean hover;
	private boolean imageLoading;
	private Image image;
	private Image loadingImage;
	private BufferedImage scaledImage;
	private BufferedImage scaledPreviewImage;
	private Panel panel;
	private Color preselectionColor;
	private ArrayList<Tag> tags;
	private File file;
	
	private final int OFFSET_SIZE = 10;
	private final int BORDER_SIZE = 4;
	private final int PREVIEW_SIZE = 60;
	
	public Symbol(Panel panel, Image loadingImage, String name, String ext, int size, File file)
	{
		this.panel = panel;
		this.loadingImage = loadingImage;
		this.width = size;
		this.height = size;
		this.name = name;
		this.ext = ext;
		this.file = file;
		
		activated = true;
		preselected = false;
		selected = false;
		hover = false;
		imageLoading = false;
		
		preselectionColor = new Color(51, 153, 255, 80);
		tags = new ArrayList<>();
	}
	
	public File getFile()
	{
		return file;
	}
	
	public boolean isImageLoading()
	{
		return imageLoading;
	}
	
	public void setImageLoading(boolean imageLoading)
	{
		this.imageLoading = imageLoading;
	}
	
	public ArrayList<Tag> getTags()
	{
		return tags;
	}
	
	public void setTags(ArrayList<Tag> tags)
	{
		this.tags = tags;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void select()
	{
		selected = true;
	}
	
	public void deselect()
	{
		selected = false;
	}
	
	public void setPreselect(boolean preselected)
	{
		this.preselected = preselected;
	}
	
	public boolean isPreselected()
	{
		return preselected;
	}
	
	public boolean isActivated()
	{
		return activated;
	}
	
	public boolean isSelected()
	{
		return selected;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void setActivation(boolean activated)
	{
		this.activated = activated;
	}
	
	/**
	 * Set the image of the symbol at different resolution
	 * @param image : Image to be associated with the symbol
	 */
	public void setImage(Image image)
	{
		this.image = image;
		scaledImage = resize(image, width - OFFSET_SIZE, height - OFFSET_SIZE);
		scaledPreviewImage = resize(image, PREVIEW_SIZE - OFFSET_SIZE, PREVIEW_SIZE - OFFSET_SIZE);
	}
	
	/**
	 * Check the existence of the image
	 * @return true if the image of the symbol is loaded
	 */
	public boolean isLoaded()
	{
		return image != null;
	}
	
	/**
	 * Change the color of the foreground depending if it's a selection or deselection
	 * @param controlKey : The state of the control key
	 */
	public void changePreselectionColor(boolean controlKey)
	{
		if (controlKey)
		{
			preselectionColor = new Color(255, 50, 51, 80);
		} else
		{
			preselectionColor = new Color(51, 153, 255, 80);
		}
	}
	
	/**
	 * Change the name of the image for this symbol
	 * @param newName : The new name that will replace the old one
	 */
	public void rename(String newName)
	{
		// Check for differences
		if (!name.equals(newName))
		{
			SymbolsRefactor.getInstance().renameSymbol(name + "." + ext, newName + "." + ext);
			name = newName;
		}
	}
	
	/**
	 * Check if the symbol is fully bounded by the multiple selection
	 * @param rX : Selection rectangle x coordinate
	 * @param rY : Selection rectangle y coordinate
	 * @param rW : Selection rectangle width
	 * @param rH : Selection rectangle height
	 * @return true if the rectangle bounds the symbol completely
	 */
	public boolean isBounded(int rX, int rY, int rW, int rH)
	{
		return (x > rX && x + width < rX + rW && y > rY && y + height < rY + rH);
	}
	
	/**
	 * Return the image of the symbol in a given size
	 * @param size : Size of the image
	 * @return the image of the symbol resized
	 */
	public BufferedImage getResizedImage(int size)
	{
		return resize(image, size, size);
	}
	
	/**
	 * Change the position of the symbol
	 * @param newX : New x coordinate
	 * @param newY : New y coordinate
	 */
	public void replace(int newX, int newY)
	{
		x = newX;
		y = newY;
	}
	
	/**
	 * Mouse interaction
	 * @param mouseX : mouse x position
	 * @param mouseY : mouse y position
	 */
	public void handleClick(int mouseX, int mouseY)
	{
		if (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height)
		{
			hover = true;
			if (Mouse.leftClicked)
			{
				boolean available;
				if (selected)
				{
					// Remove symbol
					panel.removeSelectedSymbol(this);
					selected = false;
				} else if (isLoaded())
				{
					// Add symbol
					available = panel.addSelectedSymbol(this);
					if (available) selected = true;
				} else 
				{
					panel.addMessage("Le symbole n'a pas fini de charger", false);
				}
			}
			
			// Symbol edition
			if (Mouse.rightClicked)
			{
				panel.editSelectedSymbol(this);
				hover = false;
			}
		} else 
		{
			hover = false;
		}
	}
	
	/**
	 * Draw the symbol
	 * @param g : graphic context
	 */
	public void draw(Graphics g)
	{
		// Draw the hover background 
		if (hover)
		{
			g.setColor(Color.BLACK);
			g.fillRect(x - BORDER_SIZE, y - BORDER_SIZE, width + 2 * BORDER_SIZE + 1, height + 2 * BORDER_SIZE + 1);
		}
		
		// White background
		g.setColor(Color.WHITE);
		g.fillRect(x, y, width, height);
		
		// Draw the scaled image or the loading animation if it's not loaded
		if (scaledImage != null)
		{
			g.drawImage(scaledImage, x + (width - scaledImage.getWidth(null)) / 2, y + (height - scaledImage.getHeight(null)) / 2, null);
		} else
		{
			Graphics2D g2d = (Graphics2D) g;
			
			// Rotation animation
			AffineTransform oldTransform = g2d.getTransform();
			g2d.translate(x + width / 2 - loadingImage.getWidth(null) / 2, y + height / 2 - loadingImage.getHeight(null) / 2);
			AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians((System.currentTimeMillis() / 6.0) % 360), 
					loadingImage.getWidth(null) / 2, loadingImage.getHeight(null) / 2);
			g2d.drawImage(loadingImage, transform, null);
			g2d.setTransform(oldTransform);
		}
		
		// Put a gray mask on the symbol if it's selected
		if (isSelected())
		{
			g.setColor(new Color(120, 120, 120, 150));
			g.fillRect(x, y, width, height);
		}
		
		// Put a colored mask on the symbol if it's preselected
		if (preselected)
		{
			g.setColor(preselectionColor);
			g.fillRect(x, y, width, height);
		}
		
		// Black borders
		g.setColor(Color.BLACK);
		g.drawRect(x, y, width, height);
	}
	
	/**
	 * Draw a preview of the symbol for the selected symbols
	 * @param g : Graphic context
	 * @param xPreview : Preview x coordinate
	 * @param yPreview : Preview y coordinate
	 */
	public void drawPreview(Graphics g, int xPreview, int yPreview)
	{
		// Background and borders
		g.setColor(Color.white);
		g.fillRect(xPreview, yPreview, PREVIEW_SIZE, PREVIEW_SIZE);
		g.setColor(Color.black);
		g.drawRect(xPreview, yPreview, PREVIEW_SIZE, PREVIEW_SIZE);
		
		// Draw the scaled image or the loading animation if it's not loaded
		if (scaledPreviewImage != null)
		{
			g.drawImage(scaledPreviewImage, xPreview + (PREVIEW_SIZE - scaledPreviewImage.getWidth(null)) / 2, yPreview + (PREVIEW_SIZE - scaledPreviewImage.getHeight(null)) / 2, null);
		}
	}
	
	/**
	 * Resize an image to a new resolution
	 * @param img : Image that needs a resize operation
	 * @param width : Width for the resize
	 * @param height : Height for the resize
	 * @return the image resized
	 */
	private BufferedImage resize(Image img, int width, int height) 
	{ 
		// Scale the given image
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		double ratio = Math.max((double) w / width, (double) h / height);
	    Image tmp = img.getScaledInstance((int) (w / ratio), (int)(h / ratio), Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

	    // Place the scaled image into a new image that specify the new size
	    Graphics2D g2d = dimg.createGraphics();
	    int xOffset = (int)((width - (w / ratio)) / 2);
	    int yOffset = (int)((height - (h / ratio)) / 2);
	    g2d.drawImage(tmp, xOffset, yOffset, null);
	    g2d.dispose();

	    return dimg;
	}  
}
