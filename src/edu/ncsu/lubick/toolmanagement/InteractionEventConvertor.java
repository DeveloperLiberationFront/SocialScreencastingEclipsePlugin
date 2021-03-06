package edu.ncsu.lubick.toolmanagement;

import static edu.ncsu.lubick.toolmanagement.InteractionEventConvertor.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ncsu.lubick.interactions.CommandEvent;
import edu.ncsu.lubick.interactions.InteractionEvent;


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
	public static final int THRESHOLD_MENU_DURATION = 2000;
	public static final int MAX_KEYBINDING_DURATION = 15000;
	public static final int DEFAULT_KEYBINDING_DURATION = 5000;
	public static final int THRESHOLD_KEYBINDING_DURATION = 2000;

	private static Logger loggerForProblems = Logger.getRootLogger();
	private boolean reloadPreviousEvent;
	private final String loggingPrefix = "["+getClass()+"]";
	private List<ToolEvent> convertedEvents = new ArrayList<>();

	private InteractionEventConversionState currentState = new DefaultState();

	public InteractionEventConvertor() {
		InteractionEventConversionState.setStateContext(this);
	}
	
	public static void setLoggerForProblems(Logger logger) {
		loggerForProblems = logger;
	}


	@Override
	public void logUnusualBehavior(String behavior)
	{
		loggerForProblems.info(this.loggingPrefix + behavior);
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

		if (isKeyBoardCommandInvocation(event))
		{
			DurationDetectionState dds = makeDurationDetectionStateForKeyBindingEvent((CommandEvent) event);
			dds.setIsKeybindingEvent(true);
			setState(dds);

		} 
		else if (isGUICommandInvocation(event))
		{
			DurationDetectionState dds = makeDurationDetectionStateForMenuEvent((CommandEvent) event);
			dds.setIsKeybindingEvent(false);
			setState(dds);
		}
		else
		{
			logUnusualBehavior("Possible error: Ignored a \"relevant\" event in DefaultState" + event);
		}

	}

	private boolean isRelevantEvent(InteractionEvent event) {
		return event instanceof CommandEvent;
	}

	@Override
	public void isShuttingDown(Date shutdowndate) {//this state is perfectly okay to just shut down quietly

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
		
		if (event instanceof CommandEvent)
		{
			createdEvent = makeToolEventEndingAtThisDate(event.getDate());
			postConvertedEvent(createdEvent);
			DefaultState newState = new DefaultState();
			setState(newState);
			setEventUnHandled(true);
		}
		else
		{
			if (currentDurationWouldBeTooShort(event.getDate()))
			{
				//this is probably left over menu/window noise.  
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
