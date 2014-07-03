package edu.ncsu.lubick.instrumentation;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import edu.ncsu.lubick.interactions.EventType;
import edu.ncsu.lubick.interactions.SWT_Event;
import edu.ncsu.lubick.plugin.CommandReceiver;


public class SWT_Instrumentation implements Runnable {
	
	private static final Logger logger = Logger.getLogger(SWT_Instrumentation.class);
	private final Display display;
	private CommandReceiver receiver;

	public SWT_Instrumentation(Display display, CommandReceiver receiver)
	{
		this.display = display;
		this.receiver = receiver;
	}

	@Override
	public void run()
	{
		display.addFilter(SWT.Show, new Listener() {
			
			@Override
			public void handleEvent(Event event)
			{
				logger.info("SWT.Show: display:"+event.display+" widget:"+ event.widget);
				logger.info("\t"+event);
				if (event.widget instanceof Shell) {
					event.widget.addListener(SWT.Dispose, new Listener() {

						@Override
						public void handleEvent(Event e)
						{
							logger.info("Shell disposed: "+e);
							receiver.handleInteractionEvent(new SWT_Event(EventType.DISPOSE));
						}
					});
				}

			}
		});
		
		display.addFilter(SWT.KeyUp, new Listener() {
			
			@Override
			public void handleEvent(Event event)
			{
				if (event.keyCode == SWT.ESC) {
					logger.info("Esc pressed");
					receiver.handleInteractionEvent(new SWT_Event(EventType.ESC_PRESSED));
				}
			}
		});
	}

	public static void setupLogging() {
		//does nothing.  A call to this will invoke the static initializer, making logging work at the right time.
	}
}