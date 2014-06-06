package edu.ncsu.lubick.toolmanagement;

import java.util.Date;

import edu.ncsu.lubick.plugin.EventType;
import edu.ncsu.lubick.plugin.InteractionEvent;
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
		return event.getType() == EventType.KEYBOARD_SHORTCUT_INVOCATION;
	}
	
	protected boolean isMenuEvent(InteractionEvent event) {
		return event.getType() == EventType.KEYBOARD_SHORTCUT_INVOCATION;
	}

	protected DurationDetectionState makeDurationDetectionStateForKeyBindingEvent(InteractionEvent event) {
		DurationDetectionState dds = makeDurationDetectionStateForMenuEvent(event);
		
		dds.setCurrentEventsKeypresses(KeyBindingDirectory.lookUpKeyBinding(event.getCommandId()));
		
		return dds;
	}
	
	protected DurationDetectionState makeDurationDetectionStateForMenuEvent(InteractionEvent event) {
		DurationDetectionState dds = new DurationDetectionState();
		
		dds.setCurrentEventsCommandName(CommandNameDirectory.lookUpCommandName(event.getCommandId()));
		dds.setCurrentEventsStartDate(event.getDate());
		
		return dds;
	}

	protected boolean wasActionEvent(InteractionEvent event) {
		return isKeyBindingEvent(event) || isMenuEvent(event);
	}
}
