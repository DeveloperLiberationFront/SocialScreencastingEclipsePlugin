package edu.ncsu.lubick.toolmanagement;

import java.util.Date;

import edu.ncsu.lubick.interactions.CommandEvent;
import edu.ncsu.lubick.interactions.EventType;
import edu.ncsu.lubick.interactions.InteractionEvent;
import edu.ncsu.lubick.util.CommandNameDirectory;
import edu.ncsu.lubick.util.KeyBindingDirectory;

public abstract class InteractionEventConversionState 
{
	private static InteractionEventConversionStateContext stateContext;
	
	protected static ToolEventData startData;
	protected  static ToolEventData endData;

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

	protected boolean isKeyBoardCommandInvocation(InteractionEvent event) {
		return event.getType() == EventType.INVOCATION_KEYBOARD_SHORTCUT;
	}
	
	protected boolean isGUICommandInvocation(InteractionEvent event) {
		return event.getType() == EventType.INVOCATION_KEYBOARD_SHORTCUT;
	}

	protected DurationDetectionState makeDurationDetectionStateForKeyBindingEvent(CommandEvent event) {
		DurationDetectionState dds = makeDurationDetectionStateForMenuEvent(event);
		
		dds.setCurrentEventsKeypresses(KeyBindingDirectory.lookUpKeyBinding(event.getCommandId()));
		
		return dds;
	}
	
	protected DurationDetectionState makeDurationDetectionStateForMenuEvent(CommandEvent event) {
		DurationDetectionState dds = new DurationDetectionState();
		
		dds.setCurrentEventsCommandName(CommandNameDirectory.lookUpCommandName(event.getCommandId()));
		dds.setCurrentEventsStartDate(event.getDate());
		
		return dds;
	}
	
	public ToolEventData getStartData()
	{
		return startData;
	}
	
	public ToolEventData getEndData()
	{
		return endData;
	}
}
