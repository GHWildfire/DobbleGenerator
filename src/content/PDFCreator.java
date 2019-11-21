package content;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import elements.LoadingBar;
import elements.Main;
import panels.Panel;

/**
 * Generate a PDF with given cards
 * @author Etienne Hüsler
 * @version 2.0
 */
public class PDFCreator 
{
	private String path;
	private Panel panel;
	private float pdfWidth;
	private float pdfHeight;
	private int wMax;
	private int hMax;
	private int wShift;
	private int hShift;
	private int wCards;
	private int hCards;
	private int sizeCards;
	private double margin;
	private double ratio;
	private double pdfUsableWidth;
	private double pdfUsableHeight;
	
	private final double A4_WIDTH = 21.0;
	private final int SIZE_MARKERS = 5;
	
	public PDFCreator(Panel panel, String path)
	{
		this.panel = panel;
		this.path = path;
	}
	
	/**
	 * Generate an image containing the markers at the sheet margin
	 * @param wMaxLeft : Maximum columns left
	 * @param hMaxLeft : Maximum rows left
	 * @return an image with markers at the sheet margin
	 */
	private java.awt.Image generateMarkers(int wMaxLeft, int hMaxLeft)
	{
		// Create a new image and instantiate it's graphic context
		BufferedImage img = new BufferedImage((int) pdfWidth, (int) pdfHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		
		// Render Quality
	    RenderingHints qualityHints = new RenderingHints(
	    		  RenderingHints.KEY_ANTIALIASING,
	    		  RenderingHints.VALUE_ANTIALIAS_ON );
	    qualityHints.put(
	    		  RenderingHints.KEY_RENDERING,
	    		  RenderingHints.VALUE_RENDER_QUALITY );
	    g.setRenderingHints(qualityHints);
		
	    // Transparent background
		g.setColor(new Color(255, 255, 255, 0));
		g.fillRect(0, 0, (int) pdfWidth, (int) pdfHeight);
		
		// Position of the new origin for the markers
		int shiftX = (int)(pdfUsableWidth - wMax * sizeCards) / 2;
		int shiftY = (int)(pdfUsableHeight - hMax * sizeCards) / 2;
		
		// Draw the markers depending on the margin and the cards size
		g.setColor(Color.BLACK);
	    g.setStroke(new BasicStroke(2));
		for (int i = 0; i < wMaxLeft + 1; i++)
		{
			g.drawLine((int)(margin + i * sizeCards + shiftX - 1), (int) margin, (int)(margin + i * sizeCards + shiftX - 1), (int)(margin + SIZE_MARKERS));
			g.drawLine((int)(margin + i * sizeCards + shiftX - 1), (int)(pdfHeight - margin - SIZE_MARKERS), (int)(margin + i * sizeCards + shiftX - 1), (int)(pdfHeight - margin));
		}
		for (int j = 0; j < hMaxLeft + 1; j++)
		{
			g.drawLine((int) margin, (int)(margin + j * sizeCards + shiftY - 1), (int)(margin + SIZE_MARKERS), (int)(margin + j * sizeCards + shiftY - 1));
			g.drawLine((int)(pdfWidth - SIZE_MARKERS - margin), (int)(margin + j * sizeCards + shiftY - 1), (int)(pdfWidth - margin), (int)(margin + j * sizeCards + shiftY - 1));
		}
		
		// Close the graphic context
		g.dispose();
		
		return img;
	}
	
	/**
	 * Generate an image containing the dashed line of the cards
	 * @param wMaxLeft : Maximum columns left
	 * @param hMaxLeft : Maximum rows left
	 * @return an image containing the dashed line of the cards
	 */
	private java.awt.Image generateDashedLines(int wMaxLeft, int hMaxLeft)
	{
		// Create a new image and instantiate it's graphic context
		BufferedImage img = new BufferedImage((int) pdfWidth, (int) pdfHeight, BufferedImage.TYPE_INT_ARGB);
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
		
	    // Transparent background
		g.setColor(new Color(255, 255, 255, 0));
		g.fillRect(0, 0, (int) pdfWidth, (int) pdfHeight);

		// Draw the dashed lines depending on the margin and the cards size
	    g.setColor(Color.BLACK);
		Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
		Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(dashed);
        
		for (int i = 0; i < wMaxLeft + 1; i++)
		{
			for (int j = 0; j < hMaxLeft + 1; j++)
			{
				g.drawLine((int)(wShift + i * sizeCards) - 1, hShift, (int)(wShift + i * sizeCards) - 1, (int)(hShift + j * sizeCards - 3));
				g.drawLine(wShift, (int)(hShift + j * sizeCards) - 1, (int)(wShift + i * sizeCards - 3), (int)(hShift + j * sizeCards) - 1);
			}
		}
		
		// Close the graphic context
		g.dispose();
		
		return img;
	}
	
	/**
	 * Create a PDF document with the given cards
	 * @param cards : Cards that needs to be in the document
	 */
	public void printCards(ArrayList<Card> cards)
	{
		// Check that the given cards are valid
		if (cards != null && cards.size() > 0)
		{
			// Create a document
			Document doc = new Document();
			pdfWidth = doc.getPageSize().getWidth();
			pdfHeight = doc.getPageSize().getHeight();

			// Compute settings
			ratio = pdfWidth / A4_WIDTH;
			margin = Settings.sheetMargin * ratio;
			sizeCards = (int)(Settings.cardSize * ratio);
			
			// Compute space that can be used
			pdfUsableWidth = pdfWidth - 2 * margin;
			pdfUsableHeight = pdfHeight - 2 * margin;
			
			// Indexing
			int cardsToPrint = 2 * cards.size();
			int switchPoint = cardsToPrint / 2;
			int cardIndex = 0;

			// Compute the number of rows and columns necessary
			wMax = (int) Math.floor(pdfUsableWidth / sizeCards); 
			hMax = (int) Math.floor(pdfUsableHeight / sizeCards); 
			int wMaxLeft = wMax;
			int hMaxLeft = hMax;
			wCards = (int)(pdfUsableWidth - wMax * sizeCards) / 2;
			hCards = (int)(pdfUsableHeight - hMax * sizeCards) / 2;
			wShift = (int) (margin + wCards);
			hShift = (int) (margin + hCards);
			
			// Number of pages to generate
			int nbPages = (int) Math.ceil((double) cardsToPrint / (wMax * hMax)); 
			
			// Get the border of the cards (Only using squares at this point)
			String shape;
			switch(cards.get(0).getShape())
			{
				case SQUARE:
					shape = "/pictures/squareBorder.png";
					break;
				default:
					shape = "/pictures/squareBorder.png";
			}

			// Fill the PDF
			try 
			{
				PdfWriter.getInstance(doc, new FileOutputStream(path));
				doc.open();
				
				for (int page = 0; page < nbPages; page++)
				{
					// Compute the number of cards left to print
					if (cardsToPrint < wMax * hMax)
					{
						wMaxLeft = (int) Math.min(cardsToPrint, wMax);
						hMaxLeft = (int) Math.ceil((double) cardsToPrint / wMax);
					} else
					{
						wMaxLeft = wMax;
						hMaxLeft = hMax;
					}
					
					// Background
					Image dashedLines = null;
					Image markers = null;
					if (Settings.usingLines) dashedLines = Image.getInstance(generateDashedLines(wMaxLeft, hMaxLeft), null);
					if (Settings.usingMarkers) markers = Image.getInstance(generateMarkers(wMaxLeft, hMaxLeft), null);
					
					// Add the cards to the PDF
					outerloop:
					for (int i = 0; i < hMaxLeft; i++)
					{
						for (int j = 0; j < wMaxLeft; j++)
						{
							if (LoadingBar.getInstance().isAborted())
							{
								break outerloop;   // GOTO Should not be used
							}
							if (cardsToPrint > 0)
							{
								Image border = null;
								if (Settings.usingBorders) border = Image.getInstance(Main.class.getResource(shape));
								Image img;
								
								// Detect if the card should be composed of symbols or of text
								if (cardsToPrint > switchPoint)
								{
									img = Image.getInstance(cards.get(cardIndex).getImage(), null);
								} else 
								{
									img = Image.getInstance(cards.get(cardIndex % switchPoint).getNamesImage(), null);
								}
								
								// Add the component to the PDF
								img.setAbsolutePosition(wShift + j * sizeCards, pdfHeight - hShift - sizeCards - i * sizeCards);
								img.scaleAbsolute(sizeCards, sizeCards);
								doc.add(img);
								if (Settings.usingBorders)
								{
									border.setAbsolutePosition(wShift + j * sizeCards, pdfHeight - hShift - sizeCards - i * sizeCards);
									border.scaleAbsolute(sizeCards, sizeCards);
									doc.add(border);
								}
								
								// Update the loading bar
								LoadingBar.getInstance().increment();
								
								// Update the counters
								cardIndex++;
								cardsToPrint -= 1;
							}
						}
					}
					
					// Add background if any
					if (Settings.usingLines)
					{
						dashedLines.setAbsolutePosition(0, 0);
						doc.add(dashedLines);
					}
					if (Settings.usingMarkers)
					{
						markers.setAbsolutePosition(0, 0);
						doc.add(markers);
					}
					
					// Add a new page if there is more cards to generate
					if (page < nbPages - 1) doc.newPage();
				}

				// Close the document
				doc.close();
				if (!LoadingBar.getInstance().isAborted())
				{
					Desktop.getDesktop().open(new File(path));
					LoadingBar.getInstance().increment();
				}
			} catch (FileNotFoundException e) 
			{
				panel.addMessage("File already opened", true);
				LoadingBar.getInstance().cancel();
			} catch (DocumentException e) 
			{
				panel.addMessage("Cards generation failed", true);
				LoadingBar.getInstance().cancel();
			} catch (MalformedURLException e) 
			{
				panel.addMessage("Invalid path", true);
				LoadingBar.getInstance().cancel();
			} catch (IOException e) 
			{
				panel.addMessage("Cards generation failed", true);
				LoadingBar.getInstance().cancel();
			} finally 
			{
				// Close the LoadingBar and give the focus back to the panel
				panel.setFocus(true);
				LoadingBar.getInstance().cancel();
			}
		}
	}
}
