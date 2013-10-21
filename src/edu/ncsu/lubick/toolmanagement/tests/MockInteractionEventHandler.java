package edu.ncsu.lubick.toolmanagement.tests;

import static org.mockito.Mockito.*;

import java.util.Date;

import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;
import org.eclipse.ui.keys.IBindingService;

import edu.ncsu.lubick.util.CommandNameServce;

public class MockInteractionEventHandler 
{

	//Menu Names //which are different than NAMES.  NAMES are the offical ones listed in the Command lookup table.  These are what the MENU calls them
	static final String MENU_NAME_OPEN_CALL_HIERARCHY = "OpenCallHierarchy";

	//Keybindings
	static final String MENU_KEYBINDING = "MENU";	//not a keybinding, but this is what the local hub will be expecting.
	static final String CTRL_SPACE = "Ctrl+Space";
	
	//IDS
	static final String ID_CONTENT_ASSIST = "org.eclipse.ui.edit.text.contentAssist.proposals";	
	static final String ID_OPEN_CALL_HIERARCHY = "org.eclipse.jdt.ui.edit.text.java.open.call.hierarchy";

	//NAMES
	static final String NAME_CONTENT_ASSIST = "Content Assist";
	static final String NAME_OPEN_CALL_HIERARCHY = "Open Call Hierarchy";

	//misc
	static final int DEFAULT_DURATION = 15000;
	static final String KEYBINDING_DELTA = "keybinding";
	static final String MENU_DELTA = "menu";
	
	static InteractionEvent makeMenuCommandInteractionEvent(String informalCommandName, Date startDate, Date endDate) 
	{
		InteractionEvent ie = makeMockInteractionEvent(Kind.COMMAND, informalCommandName, MENU_DELTA, startDate, endDate);
		return ie;
	}

	static InteractionEvent makeKeyBoardCommandInteractionEvent(String commandId, Date startDate, Date endDate) 
	{	
		InteractionEvent ie = makeMockInteractionEvent(Kind.COMMAND, commandId, KEYBINDING_DELTA, startDate, endDate);
		return ie;
	}

	static InteractionEvent makeMockInteractionEvent(Kind kindOfCommand, String commandId, String deltaType, Date startDate, Date endDate) 
	{
		//could be mock(InteractionEvent), but this is more "lifelike"
		return new InteractionEvent(kindOfCommand, null, null, commandId, null, deltaType, 1.0f, startDate, endDate);
	}

	static IBindingService makeMockedKeyBindingService() 
	{
		IBindingService service = mock(IBindingService.class);
		addKeyBinding(service,ID_CONTENT_ASSIST,CTRL_SPACE);
		addKeyBinding(service, ID_OPEN_CALL_HIERARCHY, MENU_KEYBINDING);
		return service;
	}

	static void addKeyBinding(IBindingService service, String commandId, String keyBinding) {
		when(service.getBestActiveBindingFormattedFor(commandId)).thenReturn(keyBinding);
	}

	static CommandNameServce makeMockedCommandService() 
	{
		CommandNameServce testService = mock(CommandNameServce.class);
		addCommandNamePair(testService, ID_CONTENT_ASSIST, NAME_CONTENT_ASSIST);
		addCommandNamePair(testService, ID_OPEN_CALL_HIERARCHY, NAME_OPEN_CALL_HIERARCHY);
		return testService;
	}

	static void addCommandNamePair(CommandNameServce service, String commandId, String commandName) {
		when(service.lookUpCommandName(commandId)).thenReturn(commandName);
	}
	
}
