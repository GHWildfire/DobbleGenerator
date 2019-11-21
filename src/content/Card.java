package content;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import enums.CardShape;

/**
 * Card of the Dobble game
 * @author Etienne Hüsler
 * @version 2.0
 */
public class Card 
{
	private CardShape shape;
	private Symbol[] symbols;
	private int size;
	private int sizeSymbol;
	
	private final double RATIO_FONT = 0.08;
	private final int PADDING = 15;
	
	public Card(CardShape shape, Symbol[] symbols, int size)
	{
		this.shape = shape;
		this.symbols = symbols;
		this.size = size;
		sizeSymbol = size / 5;
	}
	
	public CardShape getShape()
	{
		return shape;
	}
	
	/**
	 * Generate a card with the symbols text
	 * @return the generated card with symbols texts
	 */
	public Image getNamesImage()
	{
		// Create a new RGB image
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		
		// Render Quality
	    g.setStroke(new BasicStroke(2));
	    RenderingHints qualityHints = new RenderingHints(
	    		  RenderingHints.KEY_ANTIALIASING,
	    		  RenderingHints.VALUE_ANTIALIAS_ON );
	    qualityHints.put(
	    		  RenderingHints.KEY_RENDERING,
	    		  RenderingHints.VALUE_RENDER_QUALITY );
	    g.setRenderingHints( qualityHints );
	    
	    // White background
	    g.setColor(Color.WHITE);
		g.fillRect(0, 0, size, size);
		
		// Algorithm to place the name of the symbols randomly
		g.setColor(Color.BLACK);
		if (symbols.length > 0)
		{
			int heightName = size / (symbols.length + 1);
			int fontSize = (int)(size * RATIO_FONT);
			g.setFont(new Font("Arial", Font.BOLD, fontSize));
			for (int i = 0; i < symbols.length; i++)
			{
				String name = symbols[i].getName();
				int widthText = g.getFontMetrics().stringWidth(name);
				
				int xPos = PADDING + (int)(Math.random() * (size - widthText - 2 * PADDING));
				int yPos = (heightName / 8) + (i + 1) * heightName;
				g.drawString(name, xPos, yPos);
			}
		}
		
		// Close the graphic context
		g.dispose();
		
		return img;
	}
	
	/**
	 * Generate a card with the symbols image
	 * @return the generated card with symbols images
	 */
	public Image getImage()
	{
		// Create a new RGB image
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		
		// Render Quality
	    g.setStroke(new BasicStroke(2));
	    RenderingHints qualityHints = new RenderingHints(
	    		  RenderingHints.KEY_ANTIALIASING,
	    		  RenderingHints.VALUE_ANTIALIAS_ON );
	    qualityHints.put(
	    		  RenderingHints.KEY_RENDERING,
	    		  RenderingHints.VALUE_RENDER_QUALITY );
	    g.setRenderingHints( qualityHints );
		
	    // White background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, size, size);
		
		// Algorithm to place the image of the symbols in a circle with a random rotation
		g.setColor(Color.BLACK);
		if (symbols.length > 0)
		{
			BufferedImage s0 = symbols[0].getResizedImage(sizeSymbol);
			g.drawImage(s0, size / 2 - s0.getWidth(null) / 2, size / 2 - s0.getHeight(null) / 2, null);
			double dAngle = (2 * Math.PI) / (symbols.length - 1);
			int initAngle = (int) (Math.random() * 360);
			double radius = size / 3;
			for (int i = 1; i < symbols.length; i++)
			{
				double angle = initAngle + dAngle * (i - 1) + Math.PI / 4;
				BufferedImage si = symbols[i].getResizedImage(sizeSymbol);
				int xSymbol = size / 2 + (int)(radius * Math.sin(angle) - si.getWidth(null) / 2);
				int ySymbol = size / 2 + (int)(radius * Math.cos(angle) - si.getHeight(null) / 2);
				g.drawImage(si, xSymbol, ySymbol, null);
			}
		}
		
		// Close the graphic context
		g.dispose();
		
		return img;
	}
}
