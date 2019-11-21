package panels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import elements.Main;

/**
 * Panel for the information Frame
 * @author Etienne Hüsler
 * @version 2.0
 */
public class PanelAbout extends JPanel
{
	private static final long serialVersionUID = 1L;
	private int frameWidth;
	private int frameHeight;
	private Image logo;
	
	private final int POS_TEXT = 140;
	private final int GAP = 30;
	private final String AUTHOR = "Etienne Hüsler";
	private final String PROJECT = "Dobble Generator";
	private final String SCHOOL = "Haute école Arc";
	private final String RIGHTS = "Tous droits réservés";
	private final String IMAGE_AUTHORS_TITLE = "Auteurs des images";
	private final String IMAGE_AUTHORS = "Freepik - Gregor Cresnar - Dave Gandy - Kiranshastry - fjstudio";
	
	public PanelAbout(int frameWidth, int frameHeight)
	{
		// Parameters
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		
		// Logo
		logo = new ImageIcon(Main.class.getResource("/pictures/logo.png")).getImage();
		logo = logo.getScaledInstance(365, 78, Image.SCALE_SMOOTH);
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
 	    
 	    // Background
 		g.setColor(new Color(240, 240, 240));
 		g.fillRect(0, 0, 2 * frameWidth, 2 * frameHeight);
 		
 		// Text
 		g.setColor(Color.BLACK);
 		g.setFont(new Font("Arial", Font.PLAIN, 26));
 		g.drawString(AUTHOR, 20, POS_TEXT);
 		g.drawString(SCHOOL, 20, POS_TEXT + GAP);
 		g.drawString(PROJECT, 20, POS_TEXT + 2 * GAP);
 		
 		g.drawString("_______________________________________", 20, POS_TEXT + 2 * GAP + 10);
 		
 		g.setFont(new Font("Arial", Font.ITALIC, 18));
 		g.drawString(RIGHTS, 20, POS_TEXT + 3 * GAP + 10);

 		g.setFont(new Font("Arial", Font.PLAIN, 26));
 		g.drawString(IMAGE_AUTHORS_TITLE, 20, POS_TEXT + 6 * GAP);
 		g.drawString("_______________________________________", 20, POS_TEXT + 6 * GAP + 10);
 		g.setFont(new Font("Arial", Font.ITALIC, 18));
 		g.drawString(IMAGE_AUTHORS, 20, POS_TEXT + 7 * GAP + 10);
 		
 		// Logo
 		g.drawImage(logo, frameWidth - logo.getWidth(null) - 20, 20, null);
	}
}
