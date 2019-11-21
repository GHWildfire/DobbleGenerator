package elements;

import java.awt.Graphics;

/**
 * Abstract class for panel components (not always used)
 * @author Etienne Hüsler
 * @version 2.0
 */
public abstract class Widget 
{
	protected int x;
	protected int y;
	protected int w;
	protected int h;
	protected Widget parent;
	
	public abstract void adapt(int frameWidth, int frameHeight);
	
	public abstract void draw(Graphics g);
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getWidth()
	{
		return w;
	}
	
	public int getHeight()
	{
		return h;
	}
	
	public Widget getParent()
	{
		return parent;
	}
}
