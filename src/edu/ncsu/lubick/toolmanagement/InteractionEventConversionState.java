package edu.ncsu.lubick.toolmanagement;

import static edu.ncsu.lubick.plugin.MylynInteractionListener.*;

import java.util.Date;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

import edu.ncsu.lubick.util.CommandNameDirectory;
import edu.ncsu.lubick.util.KeyBindingDirectory;

public abstract class InteractionEventConversionState 
{
	private static InteractionEventConversionStateContext stateContext;

	public static void setStateContext(InteractionEventConversionStateContext newStateContext) {
		stateContext = newStateContext;
	}

	public abstract void sawInteractionEvent(InteractionEvent event);

	public abstract void isShuttingDown(Date shutdowndate);
	
	
	//shared behavior
	protected void setState(InteractionEventConversionState newState)
	{
		stateContext.setState(newState);
	}
	
	protected void logUnusualBehavior(String behavior)
	{
		stateContext.logUnusualBehavior(behavior);
	}

	protected void postConvertedEvent(ToolEvent createdEvent) 
	{
		stateContext.postConvertedEvent(createdEvent);	
	}
	
	protected void setEventUnHandled(boolean b) {
		stateContext.previousEventNeedsRerun(b);		
	}

	protected boolean isKeyBindingEvent(InteractionEvent event) {
		return event.getDelta().equals(MYLYN_KEYBINDING);
	}
	
	protected boolean isMenuEvent(InteractionEvent event) {
		return event.getDelta().equals(MYLYN_MENU);
	}

	protected DurationDetectionState makeDurationDetectionStateForKeyBindingEvent(InteractionEvent event) {
		DurationDetectionState dds = makeDurationDetectionStateForMenuEvent(event);
		
		dds.setCurrentEventsKeypresses(KeyBindingDirectory.lookUpKeyBinding(event.getOriginId()));
		
		return dds;
	}
	
	protected DurationDetectionState makeDurationDetectionStateForMenuEvent(InteractionEvent event) {
		DurationDetectionState dds = new DurationDetectionState();
		
		dds.setCurrentEventsCommandName(CommandNameDirectory.lookUpCommandName(event.getOriginId()));
		dds.setCurrentEventsStartDate(event.getDate());
		
		return dds;
	}

	protected boolean wasActionEvent(InteractionEvent event) {
		return isKeyBindingEvent(event) || isMenuEvent(event);
	}
}
