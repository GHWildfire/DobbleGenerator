package frames;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import content.AssociationsHandler;
import content.TagLibrary;
import elements.Main;
import elements.Mouse;
import panels.Panel;

/**
 * Main frame of the application
 * @author Etienne Hüsler
 * @version 2.0
 */
public class Frame extends JFrame
{
	private static final long serialVersionUID = 1L;
	private Panel panel;
	private Mouse mouse;
	private int widthDecoration;
	
	private final int FPS = 60;
	
	public Frame()
	{
		widthDecoration = 21;
		panel = new Panel(this.getWidth(), this.getHeight());
		mouse = new Mouse();
		
		// Frame settings
		this.setSize(1200, 900);
		this.setTitle("Dobble Generator");
		this.setLocationRelativeTo(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(1024, 768));
		this.setIconImage(new ImageIcon(Main.class.getResource("/pictures/icon.png")).getImage());

		// Panel
		this.add(panel);
		
		// Listeners 
		this.addMouseListener(mouse);
		this.addKeyListener(panel);  
		this.addKeyListener(panel.getTexfield());
		this.addWindowStateListener(event -> new Thread(() -> stateChanged(event)).start());
		this.addMouseWheelListener(event -> new Thread(() -> scrollSymbols(event.getPreciseWheelRotation())).start());
		this.addComponentListener(new ComponentAdapter() 
		{
		    public void componentResized(ComponentEvent componentEvent) 
		    {
		    	new Thread(() -> panel.adaptWindows(componentEvent.getComponent().getWidth() - widthDecoration, componentEvent.getComponent().getHeight())).start();
		    }
		});
		this.addWindowFocusListener(new WindowAdapter() 
		{
			@Override
	        public void windowLostFocus(WindowEvent e) 
			{
				panel.setFocus(false);
	        }
			
		    public void windowGainedFocus(WindowEvent e) 
		    {
		    	panel.setFocus(true);
		    }
		});
		this.addWindowListener(new WindowAdapter()
		{
		    public void windowClosing(WindowEvent e)
		    {
				AssociationsHandler.getInstance().save(panel.getSymbols());
				TagLibrary.getInstance().save();
		    }
		});

		this.setVisible(true);
		
		// Start painting loop
		refresh();
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
        	panel.adaptWindows(event.getComponent().getWidth() - widthDecoration, event.getComponent().getHeight());
        } else if (wasMaximized && !isMaximized) {
        	panel.adaptWindows(event.getComponent().getWidth() - widthDecoration, event.getComponent().getHeight());
        }
	}
	
	/**
	 * Called on a wheel event to scroll the frame content
	 * @param rotation : wheel rotation
	 */
	private void scrollSymbols(double rotation)
	{
		panel.scrollSymbols(rotation);
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
		panel.repaint();
		
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
