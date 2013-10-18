package edu.ncsu.lubick.plugin;

import java.util.Date;

import org.eclipse.mylyn.monitor.core.InteractionEvent;

import edu.ncsu.lubick.plugin.tools.ToolEvent;
import edu.ncsu.lubick.util.CommandNameDirectory;
import edu.ncsu.lubick.util.KeyBindingDirectory;


/**
 * Static class that converts Eclipse's InteractionEvent to a ToolEvent similar to what LocalHub will handle
 * @author KevinLubick
 *
 */
public class InteractionEventConvertor 
{

	private static final String MYLYN_MENU = "menu";
	private static final String MYLYN_KEYBINDING = "keybinding";
	private static boolean wasMenuEventPreviously = false;
	private static Date previousTimeStamp = null;
	
	public static ToolEvent convert(InteractionEvent event) 
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

	private static boolean checkIfCurrentKeybindingEventMatchesPreviousMenuEvent(InteractionEvent event) {
		if (!wasMenuEventPreviously)
			return false;
		if (event.getDate().equals(previousTimeStamp))
			return true;
		
		System.out.println("Unusual happenings.  Menu item was followed by a non-matching \"keybinding\".  ");
		System.out.println(String.format("Date %s != Date %s (%d != %d)", previousTimeStamp, event.getDate(), previousTimeStamp.getTime(), event.getDate().getTime()));
		return false;
	}

}
