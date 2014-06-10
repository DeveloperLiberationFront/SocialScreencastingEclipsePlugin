package edu.ncsu.lubick.instrumentation;

import org.apache.log4j.Logger;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;

import edu.ncsu.lubick.interactions.EventType;
import edu.ncsu.lubick.interactions.WindowInteractionEvent;
import edu.ncsu.lubick.plugin.CommandReceiver;

public class EclipseWindowListener implements IWindowListener {
	
	private CommandReceiver receiver;
	private static Logger logger;
	
	public EclipseWindowListener(CommandReceiver toolStreamCompiler)
	{
		this.receiver = toolStreamCompiler;
	}
	
	public static void setLogger(Logger newLogger) {
		logger = newLogger;
	}

	@Override
	public void windowActivated(IWorkbenchWindow window) {
		logger.info("windowActivated " + window);
		receiver.handleInteractionEvent(new WindowInteractionEvent(EventType.WINDOW_FOCUSED));
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
		logger.info("windowDeactivated " + window);
		receiver.handleInteractionEvent(new WindowInteractionEvent(EventType.WINDOW_UNFOCUSED));
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		logger.info("windowOpened " + window);
		receiver.handleInteractionEvent(new WindowInteractionEvent(EventType.WINDOW_OPENED));
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
		logger.info("windowClosed " + window);
		receiver.handleInteractionEvent(new WindowInteractionEvent(EventType.WINDOW_CLOSED));
	}
}
