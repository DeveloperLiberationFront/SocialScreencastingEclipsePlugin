package edu.ncsu.lubick.toolmanagement;

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
public class InteractionEventConvertor 
{

	private static final String MYLYN_MENU = "menu";
	private static final String MYLYN_KEYBINDING = "keybinding";


	private boolean wasMenuEventPreviously = false;
	private Date previousTimeStamp = null;
	private Logger loggerForProblems;
	private final String loggingPrefix = "["+getClass()+"]";
	private List<ToolEvent> convertedEvents = new ArrayList<>();


	public InteractionEventConvertor() {
		this.loggerForProblems = Logger.getRootLogger();	//dummy value to avoid NPEs
	}

	public InteractionEventConvertor(Logger loggerForProblems) 
	{
		this.loggerForProblems = loggerForProblems;

	}

	@Deprecated
	public ToolEvent convert(InteractionEvent event) 
	{
		//we only handle these types of events
		if (!event.getDelta().equals(MYLYN_KEYBINDING) && !event.getDelta().equals(MYLYN_MENU))
		{
			return null;
		}


		previousTimeStamp = event.getDate();

		String keyPresses = "MENU";
		if (event.getDelta().equals(MYLYN_KEYBINDING) && !checkIfCurrentKeybindingEventMatchesPreviousMenuEvent(event))
		{
			keyPresses = KeyBindingDirectory.lookUpKeyBinding(event.getOriginId());
		}
		if (event.getDelta().equals(MYLYN_MENU))
		{
			wasMenuEventPreviously = true;
			return null;		//we'll wait until next time
		}

		wasMenuEventPreviously = false;
		String toolName = CommandNameDirectory.lookUpCommandName(event.getOriginId());

		ToolEvent retVal = new ToolEvent(toolName, "", keyPresses, event.getDate(), 15000);

		return retVal;
	}

	private boolean checkIfCurrentKeybindingEventMatchesPreviousMenuEvent(InteractionEvent event) {
		if (!wasMenuEventPreviously)
			return false;
		if (event.getDate().equals(previousTimeStamp))
			return true;

		logUnusualBehavior("Unusual happenings.  Menu item was followed by a non-matching \"keybinding\".  ");
		logUnusualBehavior(String.format("Date %s != Date %s (%d != %d)", previousTimeStamp, event.getDate(), previousTimeStamp.getTime(), event.getDate().getTime()));
		return false;
	}

	private void logUnusualBehavior(String behavior)
	{
		loggerForProblems.info(this.loggingPrefix + behavior);
		System.out.println(this.loggingPrefix + behavior);
	}

	public void foundInteractionEvents(InteractionEvent... events) {
		//we only handle these types of events
		for(InteractionEvent event:events)
		{
			if (!event.getDelta().equals(MYLYN_KEYBINDING) && !event.getDelta().equals(MYLYN_MENU))
			{
				continue;
			}


			previousTimeStamp = event.getDate();

			String keyPresses = "MENU";
			if (event.getDelta().equals(MYLYN_KEYBINDING) && !checkIfCurrentKeybindingEventMatchesPreviousMenuEvent(event))
			{
				keyPresses = KeyBindingDirectory.lookUpKeyBinding(event.getOriginId());
			}
			if (event.getDelta().equals(MYLYN_MENU))
			{
				wasMenuEventPreviously = true;
				continue;		//we'll wait until next time
			}

			wasMenuEventPreviously = false;
			String toolName = CommandNameDirectory.lookUpCommandName(event.getOriginId());

			ToolEvent madeEvent = new ToolEvent(toolName, "", keyPresses, event.getDate(), 15000);

			convertedEvents.add(madeEvent);
		}
	}

	public List<ToolEvent> getConvertedEvents() 
	{
		List<ToolEvent> retVal = new ArrayList<ToolEvent>(convertedEvents);
		convertedEvents.clear();
		return retVal;
	}

}
