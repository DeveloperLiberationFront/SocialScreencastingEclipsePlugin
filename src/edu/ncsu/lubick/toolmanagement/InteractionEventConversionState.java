package edu.ncsu.lubick.toolmanagement;

import static edu.ncsu.lubick.plugin.MylynInteractionListener.*;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

import edu.ncsu.lubick.util.CommandNameDirectory;
import edu.ncsu.lubick.util.KeyBindingDirectory;

public abstract class InteractionEventConversionState 
{
	private static InteractionEventConversionStateContext stateContext;

	public abstract void sawInteractionEvent(InteractionEvent event);

	public static void setStateContext(InteractionEventConversionStateContext newStateContext) {
		stateContext = newStateContext;
	}
	
	protected void setState(InteractionEventConversionState newState)
	{
		stateContext.setState(newState);
	}
	
	protected void logUnusualBehavior(String behavior)
	{
		stateContext.logUnusualBehavior(behavior);
	}

	protected boolean isKeyBindingEvent(InteractionEvent event) {
		return event.getDelta().equals(MYLYN_KEYBINDING);
	}
	
	protected boolean isMenuEvent(InteractionEvent event) {
		return event.getDelta().equals(MYLYN_MENU);
	}

	protected DurationDetectionState makeDurationDetectionStateForEvent(InteractionEvent event) {
		DurationDetectionState dds = new DurationDetectionState();
		
		dds.setCurrentEventsKeypresses(KeyBindingDirectory.lookUpKeyBinding(event.getOriginId()));
		dds.setCurrentEventsCommandName(CommandNameDirectory.lookUpCommandName(event.getOriginId()));
		dds.setCurrentEventsStartDate(event.getDate());
		
		return dds;
	}
}
