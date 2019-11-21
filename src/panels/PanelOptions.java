package panels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.JPanel;

import content.Settings;
import elements.CheckBox;
import elements.Mouse;
import elements.Slider;

/**
 * Panel for the Print Settings Frame
 * @author Etienne Hüsler
 * @version 2.0
 */
public class PanelOptions extends JPanel
{
	private static final long serialVersionUID = 1L;
	private boolean hasFocus;
	private int frameWidth;
	private int frameHeight;
	private int mouseX;
	private int mouseY;
	private int previewX;
	private int previewY;
	private int previewW;
	private int previewH;
	private int margin;
	private int cardSize;
	private int xCards;
	private int yCards;
	private int wCards;
	private int hCards;
	private int columns;
	private int rows;
	private int usableWidth;
	private int usableHeight;
	private double ratioCentimetersPixel;
	
	private final double RATIO_SHEET = Math.sqrt(2);
	private final int MARGIN = 20;
	private final double SHEET_WIDTH = 21.0;
	private final int MARKER_LENGTH = 8;
	private final int LENGTH_SLIDER = 400;
	private final int CHECKBOX_SIZE = 40;
	
	private CheckBox bordersCB;
	private CheckBox linesCB;
	private CheckBox markersCB;
	private Slider sliderSize;
	private Slider sliderMargin;
	private Slider sliderDPI;
	
	public PanelOptions(int frameWidth, int frameHeight)
	{
		// Parameters
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		
		// Integers
		previewX = 0;
		previewY = 0;
		previewW = 0;
		previewH = 0;
		margin = 0;
		cardSize = 0;
		xCards = 0;
		yCards = 0;
		wCards = 0;
		hCards = 0;
		columns = 0;
		rows = 0;
		usableWidth = 0;
		usableHeight = 0;
		
		// Doubles
		ratioCentimetersPixel = 0.0;
		
		// Check boxes
		bordersCB = new CheckBox("Bordures de carte", CHECKBOX_SIZE);
		if (Settings.usingBorders) bordersCB.check();
		linesCB = new CheckBox("Lignes pointillées", CHECKBOX_SIZE);
		if (Settings.usingLines) linesCB.check();
		markersCB = new CheckBox("Traits de coupe", CHECKBOX_SIZE);
		if (Settings.usingMarkers) markersCB.check();
		
		// Sliders
		sliderSize = new Slider("Taille des cartes", "cm", LENGTH_SLIDER, 4.0, 20.0, Settings.cardSize, 0.1);
		sliderMargin = new Slider("Marges d'impression", "cm", LENGTH_SLIDER, 0.5, 2.0, Settings.sheetMargin, 0.1);
		sliderDPI = new Slider("Qualité d'image", "DPI", LENGTH_SLIDER, 100.0, 1200.0, Settings.DPI, 10.0);
	}
	
	public void setFocus(boolean focus) 
	{
		hasFocus = focus;
	}
	
	public boolean slidersSelected()
	{
		return sliderSize.isSelected() || sliderMargin.isSelected() || sliderDPI.isSelected();
	}
	
	/**
	 * Adapt the content of the panel according to the Frame dimension
	 * @param frameWidth : Frame width
	 * @param frameHeight : Frame height
	 */
	public void adaptWindow(int frameWidth, int frameHeight)
	{
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		
		// Position and dimension of the sheet preview
		int sheetHeight = frameHeight - (int)(4.5 * MARGIN);
		previewX = frameWidth - MARGIN - (int)(sheetHeight / RATIO_SHEET);
		previewY = MARGIN;
		previewW = (int)(sheetHeight / RATIO_SHEET);
		previewH = sheetHeight;
		
		// Print settings
		ratioCentimetersPixel = previewW / SHEET_WIDTH;
		margin = (int)(Settings.sheetMargin * ratioCentimetersPixel);
		cardSize = (int)(Settings.cardSize * ratioCentimetersPixel);
		
		// Compute the number of rows and columns in the preview
		usableWidth = previewW - 2 * margin;
		usableHeight = previewH - 2 * margin;
		columns = (int) Math.floor(usableWidth / cardSize);
		rows = (int) Math.floor(usableHeight / cardSize);
		
		// Position and dimension of the cards in the preview
		wCards = columns * cardSize;
		hCards = rows * cardSize;
		xCards = previewX + margin + (usableWidth - wCards) / 2;
		yCards = previewY + margin + (usableHeight - hCards) / 2;
		
		int leftWidth = frameWidth - previewW - MARGIN;
		int xComboBoxes = (leftWidth - LENGTH_SLIDER) / 2;
		int yParameters = (frameHeight - 560) / 2;
		
		// Sliders
		sliderSize.adapt(xComboBoxes, yParameters + 60);
		sliderMargin.adapt(xComboBoxes, yParameters + 120);
		sliderDPI.adapt(xComboBoxes, yParameters + 180);
		
		// Check boxes
		bordersCB.adapt(xComboBoxes + 2 * CHECKBOX_SIZE, yParameters + 280);
		linesCB.adapt(xComboBoxes + 2 * CHECKBOX_SIZE, yParameters + 340);
		markersCB.adapt(xComboBoxes + 2 * CHECKBOX_SIZE, yParameters + 400);
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
		
		// Parameters
		if (hasFocus && !slidersSelected()) bordersCB.handleClick(mouseX, mouseY);
		bordersCB.draw(g);
		if (hasFocus && !slidersSelected()) linesCB.handleClick(mouseX, mouseY);
		linesCB.draw(g);
		if (hasFocus && !slidersSelected()) markersCB.handleClick(mouseX, mouseY);
		markersCB.draw(g);
		
		// Sliders
		if (hasFocus && !sliderDPI.isSelected() && !sliderMargin.isSelected()) sliderSize.handleClick(mouseX, mouseY);
		sliderSize.draw(g);
		Settings.cardSize = sliderSize.getValue();
		adaptWindow(frameWidth, frameHeight);
		
		if (hasFocus && !sliderDPI.isSelected() && !sliderSize.isSelected()) sliderMargin.handleClick(mouseX, mouseY);
		sliderMargin.draw(g);
		Settings.sheetMargin = sliderMargin.getValue();
		adaptWindow(frameWidth, frameHeight);
		
		if (hasFocus && !sliderSize.isSelected() && !sliderMargin.isSelected()) sliderDPI.handleClick(mouseX, mouseY);
		sliderDPI.draw(g);
		Settings.DPI = (int) sliderDPI.getValue();
 		
		// Preview
		g.setColor(Color.WHITE);
		g.fillRect(previewX, previewY, previewW, previewH);
		g.setColor(Color.BLACK);
		g.drawRect(previewX, previewY, previewW, previewH);
		
		// Borders
		if (bordersCB.isChecked())
		{
			Settings.usingBorders = true;
			for (int i = 0; i < columns; i++)
			{
				for (int j = 0; j < rows; j++)
				{
					g.drawRect(xCards + i * cardSize + 2, yCards + j * cardSize + 2, cardSize - 4, cardSize - 4);
				}
			}
		} else
		{
			Settings.usingBorders = false;
		}
		
		// Print margin
		if (sliderMargin.isSelected())
		{
			g.setColor(new Color(51, 153, 255));
			g2.setStroke(new BasicStroke(1));
			g.drawRect(previewX + margin, previewY + margin, previewW - 2 * margin, previewH - 2 * margin);
			g2.setStroke(new BasicStroke(2));
			
			sliderSize.setMax(SHEET_WIDTH - 2 * sliderMargin.getValue());
		}
		
		// Dashed lines
		if (linesCB.isChecked())
		{
			Settings.usingLines = true;
			
			g.setColor(Color.BLACK);
			Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
	        g2.setStroke(dashed);
	        
			for (int i = 0; i < columns + 1; i++)
			{
				g.drawLine(xCards + i * cardSize, yCards, xCards + i * cardSize, yCards + hCards);
			}
			for (int j = 0; j < rows + 1; j++)
			{
				g.drawLine(xCards, yCards + j * cardSize, xCards + wCards, yCards + j * cardSize);
			}

			g2.setStroke(new BasicStroke(2));
		} else
		{
			Settings.usingLines = false;
		}
		
		// Markers
		if (markersCB.isChecked())
		{
			Settings.usingMarkers = true;
			
			g.setColor(Color.BLACK);
			int shiftX = (usableWidth - wCards) / 2;
			int shiftY = (usableHeight - hCards) / 2;
			
			g2.setStroke(new BasicStroke(2));
			for (int i = 0; i < columns + 1; i++)
			{
				g.drawLine(previewX + margin + i * cardSize + shiftX, previewY + margin, previewX + margin + i * cardSize + shiftX, previewY + margin + MARKER_LENGTH);
				g.drawLine(previewX + margin + i * cardSize + shiftX, previewY - margin + previewH - MARKER_LENGTH, previewX + margin + i * cardSize + shiftX, previewY - margin + previewH);
			}
			for (int j = 0; j < rows + 1; j++)
			{
				g.drawLine(previewX + margin, previewY + margin + j * cardSize + shiftY, previewX + margin + MARKER_LENGTH, previewY + margin + j * cardSize + shiftY);
				g.drawLine(previewX - margin + previewW - MARKER_LENGTH, previewY + margin + j * cardSize + shiftY, previewX - margin + previewW, previewY + margin + j * cardSize + shiftY);
			}
		} else 
		{
			Settings.usingMarkers = false;
		}
			
		// Reset mouse click
		Mouse.leftClickedOptions = false;
		Mouse.rightClickedOptions = false;
	}
}
