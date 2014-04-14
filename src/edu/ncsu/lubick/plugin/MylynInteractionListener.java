package edu.ncsu.lubick.plugin;

import org.apache.log4j.Logger;
import org.eclipse.mylyn.monitor.core.IInteractionEventListener;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

import edu.ncsu.lubick.toolmanagement.ToolEventCompiler;
import edu.ncsu.lubick.util.CommandNameDirectory;
import edu.ncsu.lubick.util.KeyBindingDirectory;

public class MylynInteractionListener implements IInteractionEventListener 
{

	public static final String MYLYN_MENU = "menu";
	public static final String MYLYN_KEYBINDING = "keybinding";
	private static Logger logger;


	private ToolEventCompiler toolHandler;


	public MylynInteractionListener(ToolEventCompiler compiler) 
	{
		this.toolHandler = compiler;
		logger.debug("MylynInteractionListener[" +this.toString()+"] is started ");
	}

	@Override
	public void interactionObserved(InteractionEvent event) {
		logger.debug("Event observed: "+ makePrintable(event));

		if (logger.isDebugEnabled() && "keybinding".equals(event.getDelta()))
		{

			logger.debug("With KeyBinding: "+KeyBindingDirectory.lookUpKeyBinding(event.getOriginId())+
					"And Command Name: "+CommandNameDirectory.lookUpCommandName(event.getOriginId()));
		}

		toolHandler.handleInteractionEvent(event);

	}

	@Override
	public void startMonitoring() {}

	@Override
	public void stopMonitoring() {
		//This is the signal that we are shutting down
		logger.info("Got message to shut down");
		if (toolHandler != null)
		{
			toolHandler.isShuttingDown();
			toolHandler = null;
		}
	}


	public static String makePrintable(InteractionEvent event)
	{
		if (event == null) {
			return "[null InteractionEvent]";
		}
		String prebuiltString = event.toString();
		StringBuilder builder = new StringBuilder("[start");
		//Removes the square brackets
		builder.append(prebuiltString.substring(1, prebuiltString.length()-1));
		builder.append(", endDate: ");
		builder.append(event.getEndDate());
		builder.append(", navigation: ");
		builder.append(event.getNavigation());
		builder.append(", interestContribution: ");
		builder.append(event.getInterestContribution());
		builder.append(", StructureKind: ");
		builder.append(event.getStructureKind());
		builder.append(", StructureHandle: ");
		builder.append(event.getStructureHandle());
		builder.append("]");
		return builder.toString();
	}

	public static void setLogger(Logger logger)
	{
		MylynInteractionListener.logger = logger;
		
	}

}
