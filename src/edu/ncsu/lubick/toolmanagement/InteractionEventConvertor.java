package edu.ncsu.lubick.toolmanagement;

import static edu.ncsu.lubick.plugin.MylynInteractionListener.*;
import static edu.ncsu.lubick.toolmanagement.InteractionEventConvertor.*;	//allows the inner classes to use the constants without a prefix

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

import edu.ncsu.lubick.util.CommandNameDirectory;
import edu.ncsu.lubick.util.KeyBindingDirectory;


/**
 * converts Eclipse's InteractionEvent to a ToolEvent that LocalHub can handle
 * @author KevinLubick
 *
 */
public class InteractionEventConvertor implements InteractionEventConversionStateContext
{

	public static final String MENU_KEYBINDING = "[GUI]";
	
	public static final int MAX_MENU_DURATION = 60000;
	public static final int DEFAULT_MENU_DURATION = 20000;
	public static final int THRESHOLD_MENU_DURATION = 3000;
	public static final int MAX_KEYBINDING_DURATION = 15000;
	public static final int DEFAULT_KEYBINDING_DURATION = 5000;
	public static final int THRESHOLD_KEYBINDING_DURATION = 2000;

	private Logger loggerForProblems;
	private boolean reloadPreviousEvent;
	private final String loggingPrefix = "["+getClass()+"]";
	private List<ToolEvent> convertedEvents = new ArrayList<>();

	private InteractionEventConversionState currentState = new DefaultState();

	public InteractionEventConvertor() {
		this(Logger.getRootLogger());	//dummy value to avoid NPEs
	}

	public InteractionEventConvertor(Logger loggerForProblems) 
	{
		this.loggerForProblems = loggerForProblems;
		InteractionEventConversionState.setStateContext(this);

	}

	@Override
	public void logUnusualBehavior(String behavior)
	{
		loggerForProblems.info(this.loggingPrefix + behavior);
		System.out.println(this.loggingPrefix + behavior);
	}

	/*
	 * Define "Action" events to be events that have deltas of "keybinding" and "menu".
	 * First, we detect an action event.  (This is done by either seeing a keybinding event, or a menu and then a keybinding event)
	 * Next, we want to detect the duration to that event.  We will ignore any non-action events prior to the threshold for
	 * the respective type (it we see an action event, cut the duration right there).  
	 * Then, we wait for either an action or non-action event, or the shut down procedure.  If the time elapsed is greater
	 * than the max for the respective type, set it to default, else set it to elapsed time.
	 */
	public void foundInteractionEvents(InteractionEvent... events) {
		//we only handle these types of events
		for(InteractionEvent event:events)
		{
			this.reloadPreviousEvent = true;
			while (reloadPreviousEvent)
			{
				reloadPreviousEvent = false;
				currentState.sawInteractionEvent(event);
			}
		}
	}
	
	List<ToolEvent> returnable = new ArrayList<ToolEvent>();

	

	public List<ToolEvent> getConvertedEvents() 
	{
		returnable.clear();
		returnable.addAll(convertedEvents);
		convertedEvents.clear();
		return returnable;
	}

	public void isShuttingDown(Date shutDownDate) 
	{
		currentState.isShuttingDown(shutDownDate);
	}

	@Override
	public void setState(InteractionEventConversionState newState) {
		this.currentState = newState;

	}

	@Override
	public void postConvertedEvent(ToolEvent createdEvent) {
		this.convertedEvents.add(createdEvent);

	}

	@Override
	public void previousEventNeedsRerun(boolean b) {
		this.reloadPreviousEvent = b;
	}

}

class DefaultState extends InteractionEventConversionState
{

	@Override
	public void sawInteractionEvent(InteractionEvent event) 
	{
		if (!isRelevantEvent(event))
		{
			return;
		}

		if (isKeyBindingEvent(event))
		{
			DurationDetectionState dds = makeDurationDetectionStateForKeyBindingEvent(event);
			dds.setIsKeybindingEvent(true);
			setState(dds);

		} 
		else if (isMenuEvent(event))
		{
			ExpectingKeyBindingState ekbs = new ExpectingKeyBindingState(event.getDate());
			setState(ekbs);
		}
		else
		{
			logUnusualBehavior("Possible error: Ignored a \"relevant\" event in DefaultState" + makePrintable(event));
		}

	}

	private boolean isRelevantEvent(InteractionEvent event) {
		return wasActionEvent(event);
	}

	@Override
	public void isShuttingDown(Date shutdowndate) {//this state is perfectly okay to just shut down quietly

	}




}

class ExpectingKeyBindingState extends InteractionEventConversionState 
{

	private Date dateOfPreviousEvent;

	public ExpectingKeyBindingState(Date dateOfMenuEvent) 
	{
		this.dateOfPreviousEvent = dateOfMenuEvent;
	}

	@Override
	public void sawInteractionEvent(InteractionEvent event) {
		if (isMenuEvent(event))
		{
			logUnusualBehavior("Two menu events in a row?");
			setState(new DefaultState());
		} 
		else if (isKeyBindingEvent(event)) 
		{
			if (!dateOfPreviousEvent.equals(event.getDate()))
			{
				logUnusualBehavior("Time was different between menu event and keybinding event");
				//continue with flow, just to see what happens
			}
			DurationDetectionState dds = makeDurationDetectionStateForMenuEvent(event);
			dds.setIsKeybindingEvent(false);
			setState(dds);
		}
		else {
			if (dateOfPreviousEvent.equals(event.getDate()))
			{
				logUnusualBehavior("Probably nothing, but "+makePrintable(event)+" was seen after a menu event, but before a keybinding event.  The time is okay, so, again, probably nothing.");
			}
			else 
			{
				logUnusualBehavior(makePrintable(event)+" was seen after a menu event, but before a keybinding event.");
				setState(new DefaultState());
			}
		}
	}

	@Override
	public void isShuttingDown(Date shutdowndate) {
		logUnusualBehavior("Was in the middle of a menu event when received shutdown");
	}


}

class DurationDetectionState extends InteractionEventConversionState 
{

	//default values
	private Date eventStartDate = null;
	private String eventCommandName = "";
	private String eventCommandClass = "";
	private String eventKeyPress = MENU_KEYBINDING;

	private int minThreshold = THRESHOLD_MENU_DURATION;
	private int defaultLength = DEFAULT_MENU_DURATION;
	private int maxThreshold = MAX_MENU_DURATION;

	@Override
	public void sawInteractionEvent(InteractionEvent event) 
	{
		ToolEvent createdEvent = null;
		
		if (wasActionEvent(event))
		{
			createdEvent = makeToolEventEndingAtThisDate(event.getDate());
			postConvertedEvent(createdEvent);
			DefaultState newState = new DefaultState();
			setState(newState);
			//newState.sawInteractionEvent(event);
			setEventUnHandled(true);
		}
		else
		{
			if (currentDurationWouldBeTooShort(event.getDate()))
			{
				return;
			}
			createdEvent = makeToolEventEndingAtThisDate(event.getDate());
			postConvertedEvent(createdEvent);
			setState(new DefaultState());
		}
		
	}


	private ToolEvent makeToolEventEndingAtThisDate(Date date) {
		ToolEvent createdEvent = null;
		if (currentDurationWouldBeTooLong(date))
		{
			createdEvent = new ToolEvent(eventCommandName, eventCommandClass, eventKeyPress, eventStartDate, defaultLength);
		}
		else
		{
			int duration = (int) getElapsedTime(date);
			createdEvent = new ToolEvent(eventCommandName, eventCommandClass, eventKeyPress, eventStartDate, duration);
		}
		return createdEvent;
	}

	private boolean currentDurationWouldBeTooShort(Date date) {
		return getElapsedTime(date) <= minThreshold;
	}

	private boolean currentDurationWouldBeTooLong(Date date) {
		return getElapsedTime(date) > maxThreshold;
	}

	private long getElapsedTime(Date thisOtherDate) {
		return thisOtherDate.getTime() - eventStartDate.getTime();
	}

	public void setCurrentEventsStartDate(Date date) {
		this.eventStartDate = date;

	}

	public void setCurrentEventsCommandName(String commandName) {
		this.eventCommandName = commandName;

	}

	public void setCurrentEventsKeypresses(String keyBinding) {
		this.eventKeyPress = keyBinding;
	}

	public void setIsKeybindingEvent(boolean wasKeyBindingEvent)
	{
		if (wasKeyBindingEvent)
		{
			minThreshold = THRESHOLD_KEYBINDING_DURATION;
			defaultLength = DEFAULT_KEYBINDING_DURATION;
			maxThreshold = MAX_KEYBINDING_DURATION;
		}
		else 
		{
			minThreshold = THRESHOLD_MENU_DURATION;
			defaultLength = DEFAULT_MENU_DURATION;
			maxThreshold = MAX_MENU_DURATION;
		}
	}

	@Override
	public void isShuttingDown(Date shutdowndate) {
		ToolEvent lastEvent = makeToolEventEndingAtThisDate(shutdowndate);
		postConvertedEvent(lastEvent);
	}

}
