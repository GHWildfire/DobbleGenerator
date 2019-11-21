package panels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import content.Symbol;
import elements.Dragfield;
import elements.MessageHandler;
import elements.Mouse;
import elements.Textfield;

/**
 * Panel for the Symbol Edition Frame
 * @author Etienne Hüsler
 * @version 2.0
 */
public class PanelSymbol extends JPanel
{
	private static final long serialVersionUID = 1L;
	private boolean hasFocus;
	private int frameWidth;
	private int frameHeight;
	private int mouseX;
	private int mouseY;
	private Dimension posImage;
	
	private final int SIZE_IMAGE = 80;
	
	private Symbol symbol;
	private Textfield textfield;
	private Dragfield dragfield;
	private MessageHandler messageHandler;
	private BufferedImage symbolImage;
	
	private final int MARGING = 40;
	
	public PanelSymbol(Symbol symbol, int frameWidth, int frameHeight)
	{
		// Parameters
		this.symbol = symbol;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		
		hasFocus = true;
		mouseX = 0;
		mouseY = 0;
		posImage = new Dimension(99, 40);
		
		// Load the resized image of the symbol to display
		new Thread(new Runnable() 
		{
			@Override
			public void run() {
				symbolImage = symbol.getResizedImage(SIZE_IMAGE);
			}
		}).start();
		
		// Symbol text
		textfield = new Textfield("Nom du symbole");
		textfield.setContent(symbol.getName());
		textfield.associateSymbol(symbol);
		textfield.adapt(219, 50, 560, 60);
		
		// Drag and Drop field
		dragfield = new Dragfield(this, symbol);
		dragfield.adapt(40, 160, 799, 610);
		
		// Message handler
		messageHandler = new MessageHandler();
		messageHandler.adapt(frameWidth, frameHeight);
		
		// Adapt window
		adaptWindows(frameWidth, frameHeight);
	}
	
	public Dimension getDimension()
	{
		return new Dimension(frameWidth, frameHeight);
	}
	
	public Symbol getSymbol()
	{
		return symbol;
	}
	
	public ArrayList<Textfield> getTextfields()
	{
		ArrayList<Textfield> textfields = dragfield.getTextfields();
		textfields.add(textfield);
		return textfields;
	}

	public void setFocus(boolean focus) 
	{
		hasFocus = focus;
	}
	
	public void scrollTags(double rotation)
	{
		dragfield.scrollTags(rotation, mouseX, mouseY);
	}
	
	public void addMessage(String newMessage, boolean isError)
	{
		messageHandler.addMessage(newMessage, isError);
	}
	
	/**
	 * Associate a new symbol to the Symbol Edition Panel
	 * @param symbol : New symbol to edit
	 */
	public void changeSymbol(Symbol symbol)
	{
		this.symbol = symbol;
		textfield.setContent(symbol.getName());
		textfield.associateSymbol(symbol);
		dragfield.changeSymbol(symbol);
	}
	
	/**
	 * Called by a resize event to adapt the application's content
	 * @param frameWidth : width of the frame
	 * @param frameHeight : height of the frame
	 */
	public void adaptWindows(int frameWidth, int frameHeight)
	{
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		
		// Header
		int headerWidth = SIZE_IMAGE + MARGING + textfield.getWidth();
		int initHeaderPos = (frameWidth - headerWidth) / 2;
		posImage = new Dimension(initHeaderPos, 40);
		textfield.adapt(initHeaderPos + SIZE_IMAGE + MARGING, 50, 560, 60);
		
		// Body
		int w = frameWidth - 2 * MARGING;
		int h = frameHeight - SIZE_IMAGE - 4 * MARGING;
		int x = (frameWidth - w) / 2;
		int y = frameHeight - 2 * MARGING - h;
		dragfield.adapt(x, y, w, h);
		messageHandler.adapt(frameWidth, frameHeight);
	}

	/**
	 * Paint method from the JPanel
	 * @param g : Graphic context
	 */
	@Override
	public void paint(Graphics g)
	{	
		// Render Quality
		Graphics2D g2 = (Graphics2D) g;
	    g2.setStroke(new BasicStroke(2));
	    RenderingHints qualityHints = new RenderingHints(
	    		  RenderingHints.KEY_ANTIALIASING,
	    		  RenderingHints.VALUE_ANTIALIAS_ON );
	    qualityHints.put(
	    		  RenderingHints.KEY_RENDERING,
	    		  RenderingHints.VALUE_RENDER_QUALITY );
	    g2.setRenderingHints(qualityHints);
		
		// Mouse location
		if (MouseInfo.getPointerInfo() != null && getLocationOnScreen() != null)
		{
			mouseX = MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x;
			mouseY = MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y;
		}
	    
	    // Background
		g.setColor(new Color(240, 240, 240));
		g.fillRect(0, 0, 2 * frameWidth, 2 * frameHeight);
		
		// Header
		g.setFont(new Font("Arial", Font.PLAIN, 30));
		g.setColor(Color.BLACK);
		if (symbolImage != null)
		{
			g.drawImage(symbolImage, posImage.width, posImage.height, null);
		}
		textfield.draw(g, Mouse.leftClickedSymbol, Mouse.leftPressedSymbol);
		if (hasFocus && !dragfield.tagSelected() && !dragfield.isScrollbarsInUse()) 
		{
			textfield.handleClick(mouseX, mouseY);
		}
		
		// Body
		if (dragfield.tagSelected())
		{
			g.setColor(new Color(0, 0, 0, 120));
			g.fillRect(0, 0, 2 * frameWidth, 2 * frameHeight);
		}
		dragfield.draw(g, mouseX, mouseY, hasFocus);
		
		// Message handler
		messageHandler.draw(g);

		// Reset mouse click
		Mouse.leftClickedSymbol = false;
		Mouse.rightClickedSymbol = false;
	}
}
