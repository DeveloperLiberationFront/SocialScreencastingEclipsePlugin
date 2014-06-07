package edu.ncsu.lubick.plugin;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.swt.widgets.Display;

public class EclipseCommandListener implements IExecutionListener {

	private static Logger logger;
//	private final AtomicBoolean keyDown = new AtomicBoolean();
	private CommandReceiver receiver;
	
	public EclipseCommandListener(Display display, CommandReceiver receiver)
	{
		this.receiver = receiver;
//		display.addFilter(SWT.KeyDown, new Listener() {
//
//			@Override
//			public void handleEvent(Event event) {
//				keyDown.set(true);
//			}
//		});
//		display.addFilter(SWT.KeyUp, new Listener() {
//
//			@Override
//			public void handleEvent(Event event) {
//				keyDown.set(false);
//			}
//		});
	}

	@Override
	public void notHandled(String commandId, NotHandledException exception)
	{
		//do nothing
	}

	@Override
	public void postExecuteFailure(String commandId, ExecutionException exception)
	{
		//do nothing
	}

	@Override
	public void postExecuteSuccess(String commandId, Object returnValue)
	{
		//do nothing
	}

	@Override
	public void preExecute(String commandId, ExecutionEvent event)
	{
		System.out.printf("commandId: %s%nevent:%s %n", commandId, event); //$NON-NLS-1$
		
		//System.out.println("keyDown: " + keyDown); //$NON-NLS-1$
		
		boolean keyInvocation = false;
		
		try
		{
			throw new Exception();
		}
		catch (Exception e)
		{
			StackTraceElement[] stackTrace = e.getStackTrace();
			
			for(StackTraceElement ste : stackTrace) {
				System.out.println(ste.getClassName());
				if (ste.getClassName().contains("$KeyDownFilter")) {
					keyInvocation = true;
					break;
				} 
				//we can short circuit if we get to the display invocation
				else if ("org.eclipse.swt.widgets.Display".equals(ste.getClassName())) {
					break;
				}
			}
		}
		System.out.println("Was key binding: "+keyInvocation);
		System.out.println();
		
		receiver.handleInteractionEvent(CommandEvent.makeCommandEvent(commandId, keyInvocation));
	}
	
	public void stopMonitoring() {
		//This is the signal that we are shutting down
		logger.info("Got message to shut down");
		if (receiver != null)
		{
			receiver.isShuttingDown();
			receiver = null;
		}
	}

	public static void setLogger(Logger logger)
	{
		EclipseCommandListener.logger = logger;
	}
}
