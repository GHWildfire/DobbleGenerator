package elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;

/**
 * Loading bar for the cards generation
 * @author Etienne Hüsler
 * @version 2.0
 */
public class LoadingBar 
{
	private int currentValue;
	private int maxValue;
	private String description;
	private int x;
	private int y;
	private int w;
	private int h;
	private int frameWidth;
	private int frameHeight;
	private boolean active;
	private boolean abort;
	private long lastIncrementTime;
	private String estimatedLeftTime;
	private ArrayList<Long> incrementDurations;
	private Button cancelButton;
	
	private static LoadingBar instance;
	
	private final int SIZE_BUTTON = 30;
	private final int OFFSET_IMAGE = 5;
	private final int MARGIN = 10;
	
	private LoadingBar()
	{
		currentValue = 0;
		maxValue = 0;
		description = "";
		w = 400;
		h = SIZE_BUTTON;
		active = false;
		abort = false;
		
		incrementDurations = new ArrayList<>();
		cancelButton = new Button(generateImage("/pictures/delete.png", (SIZE_BUTTON - OFFSET_IMAGE)), 0, 0, SIZE_BUTTON, SIZE_BUTTON, 3, null);
	}
	
	public static LoadingBar getInstance()
	{
		if (instance == null)
		{
			instance = new LoadingBar();
		}
		return instance;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public void cancel()
	{
		active = false;
	}
	
	public boolean isAborted()
	{
		return abort;
	}
	
	/**
	 * Helper method to generate an image
	 * @param path : Path of the image
	 * @param size : Size of the image
	 * @return a generated image from the path and with the size specified
	 */
	private Image generateImage(String path, int size)
	{
		Image image = new ImageIcon(Main.class.getResource(path)).getImage();
		return image.getScaledInstance(size, size, Image.SCALE_DEFAULT);
	}
	
	/**
	 * Adapt the position of the LoadingBar based on it's parent dimension
	 * @param frameWidth : Frame width
	 * @param frameHeight : Frame height
	 */
	public void adapt(int frameWidth, int frameHeight)
	{
		x = (frameWidth - w) / 2;
		y = (frameHeight - h) / 2;
		
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		
		cancelButton.adapt(x + w + MARGIN, y);
	}
	
	/**
	 * Initialize the LoadingBar with a new description and the number of steps of the next process
	 * @param description : Description of the process
	 * @param maxValue : Maximum operations in the process to iterate on
	 */
	public void init(String description, int maxValue)
	{
		this.description = description; 
		this.maxValue = maxValue;
		currentValue = 0;
		active = true;
		abort = false;
		
		lastIncrementTime = System.currentTimeMillis();
		incrementDurations = new ArrayList<>();
		estimatedLeftTime = "";
	}
	
	/**
	 * Increment the progression of the LoadingBar by one step
	 */
	public void increment()
	{
		if (currentValue < maxValue)
		{
			// Process active, increment it's state
			currentValue++;
			
			// Compute the time passed since the last increment
			long currentTime = System.currentTimeMillis();
			incrementDurations.add(currentTime - lastIncrementTime);
			
			// Compute the average time taken for each process step
			double average = incrementDurations.stream().parallel().mapToLong(Long::new).average().getAsDouble();
			int incrementsLeft = maxValue - currentValue;
			double timeLeft = incrementsLeft * average;
			
			// Compute and format the time left based on the steps left and the average time
			if (timeLeft < 60000)
			{
				String timeLeftString = (int)(timeLeft / 1000) + "";
				estimatedLeftTime = "Temps restant estimé à " + timeLeftString + " seconde";
				if (Integer.parseInt(timeLeftString) > 1)
				{
					estimatedLeftTime += "s";
				}
			} else
			{
				String timeLeftString = (int)(timeLeft / 60000) + "";
				estimatedLeftTime = "Temps restant estimé à " + timeLeftString + " minute";
				if (Integer.parseInt(timeLeftString) > 1)
				{
					estimatedLeftTime += "s";
				}
			}
			
			// Actualize the timer
			lastIncrementTime = currentTime;
		} else
		{
			// Process finished, hide the LoadingBar
			currentValue = 0;
			active = false;
		}
	}
	
	/**
	 * Draw the LoadingBar in the given graphic context
	 * @param g : Graphic context
	 * @param mouseX : Mouse x coordinate
	 * @param mouseY : Mouse y coordinate
	 */
	public void draw(Graphics g, int mouseX, int mouseY)
	{
		// Background
		g.setColor(new Color(250, 250, 250, 220));
		g.fillRect(0, 0, 2 * frameWidth, 2 * frameHeight);
		
		// Compute the progression percentage 
		String progress = (int)(100.0 * currentValue / maxValue) + " %";
		int progressSize = g.getFontMetrics().stringWidth(progress);
		
		// Draw the description and the percentage
		g.setFont(new Font("Arial", Font.BOLD, 24));
		g.setColor(Color.BLACK);
		g.drawString(description, x, y - 5);
		g.drawString(progress, x + w - progressSize, y - 5);
		
		// Draw the estimated time left
		g.setFont(new Font("Arial", Font.BOLD, 18));
		g.drawString(estimatedLeftTime, x, y + h + 20);
		
		// Draw the LoadingBar itself
		g.setColor(new Color(51, 153, 255));
		g.fillRect(x, y, (int)(w * ((double) currentValue / maxValue)), h);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, w, h);
		
		// Draw the button to cancel the process and activate the abort flag
		cancelButton.handleClick(mouseX, mouseY, Mouse.leftClicked);
		cancelButton.draw(g);
		if (cancelButton.isClicked())
		{
			active = false;
			abort = true;
		}
	}
}
