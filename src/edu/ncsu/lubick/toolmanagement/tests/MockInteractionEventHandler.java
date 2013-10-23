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
	static final String MENU_INTRODUCE_PARAMETER_OBJECT = "IntroduceParameterObjectAction";
	static final String MENU_RUN_JUNIT_TESTS = "org.eclipse.debug.internal.ui.actions.LaunchShortcutAction";

	//Keybindings
	static final String MENU_KEYBINDING = "MENU";	//not a keybinding, but this is what the local hub will be expecting for menu actions
	static final String CTRL_SPACE = "Ctrl+Space";
	static final String F3 = "F3";
	static final String ALT_SHIFT_R = "Alt+Shift+R";
	static final String CTRL_S = "Ctrl+S";
	
	//IDS
	static final String ID_CONTENT_ASSIST = "org.eclipse.ui.edit.text.contentAssist.proposals";	
	static final String ID_OPEN_CALL_HIERARCHY = "org.eclipse.jdt.ui.edit.text.java.open.call.hierarchy";
	static final String ID_OPEN_DECLARATION= "org.eclipse.jdt.ui.edit.text.java.open.editor";
	static final String ID_RENAME_REFACTOR = "org.eclipse.jdt.ui.edit.text.java.rename.element";
	static final String ID_INTRODUCE_PARAMETER_OBJECT = "org.eclipse.jdt.ui.edit.text.java.introduce.parameter.object";
	static final String ID_SAVE = "org.eclipse.ui.file.save";
	static final String ID_RUN_JUNIT_TESTS = "org.eclipse.jdt.junit.junitShortcut.run";
	
	//NAMES
	static final String NAME_CONTENT_ASSIST = "Content Assist";
	static final String NAME_OPEN_CALL_HIERARCHY = "Open Call Hierarchy";
	static final String NAME_OPEN_DECLARATION = "Open Declaration";
	static final String NAME_RENAME_REFACTOR = "Rename - Refactoring";
	static final String NAME_INTRODUCE_PARAMETER_OBJECT = "Introduce Parameter Object";
	static final String NAME_SAVE = "Save";
	static final String NAME_RUN_JUNIT_TESTS = "Run JUnit Test";
	
	//misc
	static final int THRESHOLD_KEYBINDING_DURATION = 2000;
	static final int DEFAULT_KEYBINDING_DURATION = 5000;
	static final int MAX_KEYBINDING_DURATION = 15000;
	static final int THRESHOLD_MENU_DURATION = 3000;
	static final int DEFAULT_MENU_DURATION = 20000;
	static final int MAX_MENU_DURATION = 60000;
	
	static final String DELTA_KEYBINDING = "keybinding";
	static final String DELTA_MENU = "menu";
	static final String DELTA_ACTIVATED = "activated";
	
	private MockInteractionEventHandler() {}
	
	static InteractionEvent makeMenuCommandInteractionEvent(String informalCommandName, Date startDate, Date endDate) 
	{
		InteractionEvent ie = makeMockInteractionEvent(Kind.COMMAND, informalCommandName, DELTA_MENU, startDate, endDate);
		return ie;
	}

	static InteractionEvent makeKeyBoardCommandInteractionEvent(String commandId, Date startDate, Date endDate) 
	{	
		InteractionEvent ie = makeMockInteractionEvent(Kind.COMMAND, commandId, DELTA_KEYBINDING, startDate, endDate);
		return ie;
	}
	
	static InteractionEvent makeKeyBoardCommandInteractionEvent(String commandId, Date startAndEndDate) 
	{	
		InteractionEvent ie = makeMockInteractionEvent(Kind.COMMAND, commandId, DELTA_KEYBINDING, startAndEndDate, startAndEndDate);
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
		addKeyBinding(service, ID_OPEN_DECLARATION, F3);
		addKeyBinding(service, ID_RENAME_REFACTOR, ALT_SHIFT_R);
		addKeyBinding(service, ID_INTRODUCE_PARAMETER_OBJECT, MENU_KEYBINDING);
		return service;
	}

	//This is common enough that it gets its own convenience method.
	static InteractionEvent makeWorkbenchWindowEvent(Date startAndEndDate) {
		return makeMockInteractionEvent(Kind.COMMAND, "org.eclipse.ui.internal.WorkbenchWindow", DELTA_ACTIVATED, startAndEndDate, startAndEndDate);
	}

	private static void addKeyBinding(IBindingService service, String commandId, String keyBinding) {
		when(service.getBestActiveBindingFormattedFor(commandId)).thenReturn(keyBinding);
	}

	static CommandNameServce makeMockedCommandService() 
	{
		CommandNameServce service = mock(CommandNameServce.class);
		addCommandNamePair(service, ID_CONTENT_ASSIST, NAME_CONTENT_ASSIST);
		addCommandNamePair(service, ID_OPEN_CALL_HIERARCHY, NAME_OPEN_CALL_HIERARCHY);
		addCommandNamePair(service, ID_OPEN_DECLARATION, NAME_OPEN_DECLARATION);
		addCommandNamePair(service, ID_RENAME_REFACTOR, NAME_RENAME_REFACTOR);
		addCommandNamePair(service, ID_INTRODUCE_PARAMETER_OBJECT, NAME_INTRODUCE_PARAMETER_OBJECT);
		return service;
	}

	private static void addCommandNamePair(CommandNameServce service, String commandId, String commandName) {
		when(service.lookUpCommandName(commandId)).thenReturn(commandName);
	}
}
