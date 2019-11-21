package elements;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Mouse listener for the program
 * @author Etienne Hüsler
 * @version 2.0
 */
public class Mouse implements MouseListener 
{
	// Events for the Main Frame
	public static boolean leftPressed;
	public static boolean leftClicked;
	public static boolean rightPressed;
	public static boolean rightClicked;
	
	// Events for the Symbol Edition Frame
	public static boolean leftPressedSymbol;
	public static boolean leftClickedSymbol;
	public static boolean rightPressedSymbol;
	public static boolean rightClickedSymbol;
	
	// Events for the Print Settings Frame
	public static boolean leftPressedOptions;
	public static boolean leftClickedOptions;
	public static boolean rightPressedOptions;
	public static boolean rightClickedOptions;

	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		if (arg0.getButton() == MouseEvent.BUTTON1) leftClicked = true;
		if (arg0.getButton() == MouseEvent.BUTTON3) rightClicked = true;
		
		if (arg0.getButton() == MouseEvent.BUTTON1) leftClickedSymbol = true;
		if (arg0.getButton() == MouseEvent.BUTTON3) rightClickedSymbol = true;
		
		if (arg0.getButton() == MouseEvent.BUTTON1) leftClickedOptions = true;
		if (arg0.getButton() == MouseEvent.BUTTON3) rightClickedOptions = true;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) 
	{}

	@Override
	public void mouseExited(MouseEvent arg0) 
	{}
	
	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		if (arg0.getButton() == MouseEvent.BUTTON1) leftPressed = true;
		if (arg0.getButton() == MouseEvent.BUTTON3) rightPressed = true;
		
		if (arg0.getButton() == MouseEvent.BUTTON1) leftPressedSymbol = true;
		if (arg0.getButton() == MouseEvent.BUTTON3) rightPressedSymbol = true;
		
		if (arg0.getButton() == MouseEvent.BUTTON1) leftPressedOptions = true;
		if (arg0.getButton() == MouseEvent.BUTTON3) rightPressedOptions = true;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		if (arg0.getButton() == MouseEvent.BUTTON1) leftPressed = false;
		if (arg0.getButton() == MouseEvent.BUTTON3) rightPressed = false;
		
		if (arg0.getButton() == MouseEvent.BUTTON1) leftPressedSymbol = false;
		if (arg0.getButton() == MouseEvent.BUTTON3) rightPressedSymbol = false;
		
		if (arg0.getButton() == MouseEvent.BUTTON1) leftPressedOptions = false;
		if (arg0.getButton() == MouseEvent.BUTTON3) rightPressedOptions = false;
	}

}
