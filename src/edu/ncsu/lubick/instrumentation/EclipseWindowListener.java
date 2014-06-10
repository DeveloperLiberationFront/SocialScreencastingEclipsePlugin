package edu.ncsu.lubick.instrumentation;

import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;

import edu.ncsu.lubick.interactions.EventType;
import edu.ncsu.lubick.interactions.WindowInteractionEvent;
import edu.ncsu.lubick.plugin.CommandReceiver;

public class EclipseWindowListener implements IWindowListener {
	
	private CommandReceiver receiver;
	
	public EclipseWindowListener(CommandReceiver toolStreamCompiler)
	{
		this.receiver = toolStreamCompiler;
	}

	@Override
	public void windowActivated(IWorkbenchWindow window) {
		System.out.println("windowActivated " + window);
		receiver.handleInteractionEvent(new WindowInteractionEvent(EventType.WINDOW_FOCUSED));
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
		System.out.println("windowDeactivated " + window);
		receiver.handleInteractionEvent(new WindowInteractionEvent(EventType.WINDOW_UNFOCUSED));
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		System.out.println("windowOpened " + window);
		receiver.handleInteractionEvent(new WindowInteractionEvent(EventType.WINDOW_OPENED));
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
		System.out.println("windowClosed " + window);
	}
}
