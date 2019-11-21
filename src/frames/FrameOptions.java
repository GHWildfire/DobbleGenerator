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

import elements.Main;
import elements.Mouse;
import panels.PanelOptions;

/**
 * Frame for the Print Settings
 * @author Etienne Hüsler
 * @version 2.0
 */
public class FrameOptions extends JFrame
{
	private static final long serialVersionUID = 1L;
	private PanelOptions panelOptions;
	private Mouse mouse;
	private int widthDecoration;
	private boolean isClosed;
	
	private final int FPS = 60;
	
	public FrameOptions()
	{
		widthDecoration = 21;
		panelOptions = new PanelOptions(this.getWidth(), this.getHeight());
		mouse = new Mouse();
		isClosed = false;

		// Frame settings
		this.setSize(1180, 820);
		this.setTitle("Paramètres d'impression");
		this.setLocationRelativeTo(this);
		this.setMinimumSize(new Dimension(1180, 820));
		this.setIconImage(new ImageIcon(Main.class.getResource("/pictures/icon.png")).getImage());

		// Panel
		this.add(panelOptions);
		
		// Listeners 
		this.addMouseListener(mouse);
		this.addWindowStateListener(event -> new Thread(() -> stateChanged(event)).start());
		this.addComponentListener(new ComponentAdapter() 
		{
		    public void componentResized(ComponentEvent componentEvent) 
		    {
		    	new Thread(() -> panelOptions.adaptWindow(componentEvent.getComponent().getWidth() - widthDecoration, componentEvent.getComponent().getHeight())).start();
		    }
		});
		this.addWindowFocusListener(new WindowAdapter() 
		{
			@Override
	        public void windowLostFocus(WindowEvent e) 
			{
				panelOptions.setFocus(false);
	        }
			
		    public void windowGainedFocus(WindowEvent e) 
		    {
		    	panelOptions.setFocus(true);
		    }
		});
		this.addWindowListener(new WindowAdapter()
		{
		    public void windowClosing(WindowEvent e)
		    {
		    	isClosed = true;
		    }
		});

		this.setVisible(true);
		
		// Start painting loop
		refresh();
	}
	
	public boolean isClosed()
	{
		return isClosed;
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
        	panelOptions.adaptWindow(event.getComponent().getWidth() - widthDecoration, event.getComponent().getHeight());
        } else if (wasMaximized && !isMaximized) {
        	panelOptions.adaptWindow(event.getComponent().getWidth() - widthDecoration, event.getComponent().getHeight());
        }
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
		panelOptions.repaint();
		
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
