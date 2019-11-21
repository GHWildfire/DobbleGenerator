package frames;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import content.Symbol;
import elements.Main;
import elements.Mouse;
import elements.Textfield;
import panels.PanelSymbol;

/**
 * Frame for Symbols modification
 * @author Etienne Hüsler
 * @version 2.0
 */
public class FrameSymbol extends JFrame
{
	private static final long serialVersionUID = 1L;
	private PanelSymbol panelSymbol;
	private Mouse mouse;
	private int widthDecoration;
	private boolean isClosed;
	
	private final int FPS = 60;
	
	public FrameSymbol(Symbol symbol)
	{
		widthDecoration = 21;
		panelSymbol = new PanelSymbol(symbol, this.getWidth(), this.getHeight());
		mouse = new Mouse();
		isClosed = false;

		// Frame settings
		this.setSize(900, 850);
		this.setTitle("Edition de symbole");
		this.setLocationRelativeTo(this);
		this.setMinimumSize(new Dimension(800, 600));
		this.setIconImage(new ImageIcon(Main.class.getResource("/pictures/icon.png")).getImage());

		// Panel
		this.add(panelSymbol);
		
		// Listeners 
		this.addMouseListener(mouse);
		this.addWindowStateListener(event -> new Thread(() -> stateChanged(event)).start());
		this.addMouseWheelListener(event -> new Thread(() -> scrollTags(event.getPreciseWheelRotation())).start());
		this.addComponentListener(new ComponentAdapter() 
		{
		    public void componentResized(ComponentEvent componentEvent) 
		    {
		    	new Thread(() -> panelSymbol.adaptWindows(componentEvent.getComponent().getWidth() - widthDecoration, componentEvent.getComponent().getHeight())).start();
		    }
		});
		this.addWindowFocusListener(new WindowAdapter() 
		{
			@Override
	        public void windowLostFocus(WindowEvent e) 
			{
				panelSymbol.setFocus(false);
	        }
			
		    public void windowGainedFocus(WindowEvent e) 
		    {
		    	panelSymbol.setFocus(true);
		    }
		});
		this.addWindowListener(new WindowAdapter()
		{
		    public void windowClosing(WindowEvent e)
		    {
		    	isClosed = true;
		    }
		});
		ArrayList<Textfield> textfields = panelSymbol.getTextfields();
		for (int i = 0; i < textfields.size(); i++)
		{
			this.addKeyListener(textfields.get(i));
		}

		this.setVisible(true);

		// Start painting loop
		refresh();
	}
	
	public boolean isClosed()
	{
		return isClosed;
	}
	
	public void changeSymbol(Symbol symbol)
	{
		panelSymbol.changeSymbol(symbol);
	}
	
	public void setVisibity(boolean visible)
	{
		this.setVisible(visible);
	}
	
	/**
	 * Adapt the window on a minimize or maximize event
	 * @param event : event on the window
	 */
	private void stateChanged(WindowEvent event)
	{
		boolean isMaximized = isMaximized(event.getNewState());
        boolean wasMaximized = isMaximized(event.getOldState());

        if (isMaximized && !wasMaximized) {
        	panelSymbol.adaptWindows(event.getComponent().getWidth() - widthDecoration, event.getComponent().getHeight());
        } else if (wasMaximized && !isMaximized) {
        	panelSymbol.adaptWindows(event.getComponent().getWidth() - widthDecoration, event.getComponent().getHeight());
        }
	}
	
	/**
	 * Called on a wheel event to scroll the frame content
	 * @param rotation : wheel rotation
	 */
	private void scrollTags(double rotation)
	{
		panelSymbol.scrollTags(rotation);
	}
	
	/**
	 * Checking the state of the window
	 * @param state : state of the window
	 * @return true if the window is getting maximized or minimized
	 */
	private boolean isMaximized(int state) {
	    return (state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
	}
	
	/**
	 * Call the panel drawing
	 */
	public void refresh()
	{
		panelSymbol.repaint();
		
		TimerTask task = new TimerTask() 
		{
	        public void run() 
	        {
	        	refresh();
	        }
	    };
		
		Timer timer = new Timer();
		timer.schedule(task, 1000 / FPS);
	}

}
