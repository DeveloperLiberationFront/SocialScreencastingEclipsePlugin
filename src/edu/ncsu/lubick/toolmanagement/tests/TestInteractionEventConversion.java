package edu.ncsu.lubick.toolmanagement.tests;

import static edu.ncsu.lubick.toolmanagement.tests.MockInteractionEventHandler.*;
import static edu.ncsu.lubick.toolmanagement.InteractionEventConvertor.*;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ncsu.lubick.toolmanagement.InteractionEventConvertor;
import edu.ncsu.lubick.toolmanagement.ToolEvent;
import edu.ncsu.lubick.util.CommandNameDirectory;
import edu.ncsu.lubick.util.KeyBindingDirectory;

public class TestInteractionEventConversion 
{

	private DateFormat humanDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

	private InteractionEventConvertor converter;

	@BeforeClass
	public static void setUpBeforeClass()
	{
		KeyBindingDirectory.initializeBindingService(makeMockedKeyBindingService());
		CommandNameDirectory.initializeCommandService(makeMockedCommandService());
	}

	@Before
	public void setUp()
	{
		this.converter = new InteractionEventConvertor();
	}

	@Test
	public void testBasicKeystrokeConversion() throws Exception
	{
		Date firstDate = new Date(0);
		InteractionEvent contentAssist = makeKeyBoardCommandInteractionEvent(ID_CONTENT_ASSIST, firstDate);

		converter.foundInteractionEvents(contentAssist);

		List<ToolEvent> outputEvents = converter.getConvertedEvents();

		assertNotNull(outputEvents);
		assertEquals(0, outputEvents.size());
		//This event shouldn't be ready yet because we don't know the duration of the random event above (which is, of course,
		//not random)
		
		converter.isShuttingDown(new Date(60*1000));
		
		outputEvents = converter.getConvertedEvents();

		assertNotNull(outputEvents);
		assertEquals(1, outputEvents.size());
		
		ToolEvent outputEvent = outputEvents.get(0);
		assertEquals(CTRL_SPACE, outputEvent.getToolKeyPresses());
		assertEquals(NAME_CONTENT_ASSIST, outputEvent.getToolName());
		assertEquals(DEFAULT_KEYBINDING_DURATION, outputEvent.getDuration());


	}

	@Test
	public void testMenuOpenCallHierarchyConversion() throws Exception 
	{
		//Open Call Hierarchy
		//Eclipse generates two events for menu operations : a menu one and then a keyboard one that matches what was done.
		//This emulates that behavior, with some additional events that are a direct cause of the tool (not related to the user) and then 
		//the user clicks on something related to the tool 4 seconds later
		/*
		  	1830440 [startdate: Wed Oct 16 21:16:14 EDT 2013, kind: command, sourceHandle: null, origin: OpenCallHierarchy,
		  			delta: menu, endDate: Wed Oct 16 21:16:14 EDT 2013, navigation: null
			1830441 [startdate: Wed Oct 16 21:16:14 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.jdt.ui.edit.text.java.open.call.hierarchy,
			 		delta: keybinding, endDate: Wed Oct 16 21:16:14 EDT 2013, navigation: null
			1830498 [startdate: Wed Oct 16 21:16:14 EDT 2013, kind: preference, sourceHandle: null, origin: org.eclipse.jdt.ui.JavaPerspective, 
					delta: perspective changed: actionSetShow, endDate: Wed Oct 16 21:16:14 EDT 2013, navigation: null
			1830503 [startdate: Wed Oct 16 21:16:14 EDT 2013, kind: preference, sourceHandle: null, origin: org.eclipse.jdt.callhierarchy.view, 
					delta: perspective changed: viewShow, endDate: Wed Oct 16 21:16:14 EDT 2013, navigation: null
			1830503 [startdate: Wed Oct 16 21:16:14 EDT 2013, kind: preference, sourceHandle: null, origin: org.eclipse.jdt.ui.JavaPerspective, 
					delta: perspective changed: viewShow, endDate: Wed Oct 16 21:16:14 EDT 2013, navigation: null
			1834561 [startdate: Wed Oct 16 21:16:18 EDT 2013, kind: preference, sourceHandle: null, origin: org.eclipse.jdt.ui.JavaPerspective, 
					delta: perspective changed: actionSetShow, endDate: Wed Oct 16 21:16:18 EDT 2013, navigation: null
		 */

		Date startAndEndDate = humanDateFormat.parse("Wed Oct 16 21:16:14 EDT 2013");
		Date userEventDate = humanDateFormat.parse("Wed Oct 16 21:16:18 EDT 2013");
		InteractionEvent menuEvent = makeMenuCommandInteractionEvent(MENU_NAME_OPEN_CALL_HIERARCHY, startAndEndDate, startAndEndDate);
		InteractionEvent correspondingKeyboardCommand = makeKeyBoardCommandInteractionEvent(ID_OPEN_CALL_HIERARCHY, startAndEndDate);
		InteractionEvent sideEffectPerspectiveEvent1 = makeMockInteractionEvent(Kind.PREFERENCE, "org.eclipse.jdt.ui.JavaPerspective", "perspective changed: actionSetShow", startAndEndDate, startAndEndDate);
		InteractionEvent sideEffectPerspectiveEvent2 = makeMockInteractionEvent(Kind.PREFERENCE, "org.eclipse.jdt.callhierarchy.view", "perspective changed: viewShow", startAndEndDate, startAndEndDate);
		InteractionEvent sideEffectPerspectiveEvent3 = makeMockInteractionEvent(Kind.PREFERENCE, "org.eclipse.jdt.ui.JavaPerspective", "perspective changed: viewShow", startAndEndDate, startAndEndDate);
		InteractionEvent userGeneratedPerspectiveEvent = makeMockInteractionEvent(Kind.PREFERENCE, "org.eclipse.jdt.ui.JavaPerspective", "perspective changed: actionSetShow", userEventDate, userEventDate);
		
		converter.foundInteractionEvents(menuEvent);

		List<ToolEvent> outputEvents = converter.getConvertedEvents();

		assertNotNull(outputEvents);
		assertEquals(0, outputEvents.size());

		converter.foundInteractionEvents(correspondingKeyboardCommand);
		outputEvents = converter.getConvertedEvents();

		assertNotNull(outputEvents);
		assertEquals(0, outputEvents.size());
		
		converter.foundInteractionEvents(sideEffectPerspectiveEvent1,sideEffectPerspectiveEvent2,sideEffectPerspectiveEvent3);
		outputEvents = converter.getConvertedEvents();

		assertNotNull(outputEvents);
		assertEquals(0, outputEvents.size());
		
		converter.foundInteractionEvents(userGeneratedPerspectiveEvent);
		outputEvents = converter.getConvertedEvents();

		assertNotNull(outputEvents);
		assertEquals(1, outputEvents.size());
		
		ToolEvent outputEvent = outputEvents.get(0);
		assertEquals(MENU_KEYBINDING, outputEvent.getToolKeyPresses());
		assertEquals(NAME_OPEN_CALL_HIERARCHY, outputEvent.getToolName());
		assertEquals(4*1000, outputEvent.getDuration());


	}

	@Test
	public void testDoubleKeystrokeConversionWithWindowNoise() throws Exception 
	{
		/*  This test emulates this course of action
		  	[startdate: Wed Oct 16 21:12:47 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.ui.internal.WorkbenchWindow,
		  			delta: activated, endDate: Wed Oct 16 21:12:47 EDT 2013, navigation: null, interestContribution: 1.0, StructureKind: null, StructureHandle: null]
			[startdate: Wed Oct 16 21:12:57 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.jdt.ui.edit.text.java.open.editor,
			 		delta: keybinding, endDate: Wed Oct 16 21:12:57 EDT 2013, navigation: null, interestContribution: 1.0, StructureKind: null, StructureHandle: null]
			[startdate: Wed Oct 16 21:13:00 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.jdt.ui.edit.text.java.rename.element,
			 		delta: keybinding, endDate: Wed Oct 16 21:13:00 EDT 2013, navigation: null, interestContribution: 1.0, StructureKind: null, StructureHandle: null]
			[startdate: Wed Oct 16 21:13:09 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.ui.internal.WorkbenchWindow,
			 		delta: activated, endDate: Wed Oct 16 21:13:09 EDT 2013, navigation: null, interestContribution: 1.0, StructureKind: null, StructureHandle: null]
		 */
		
		Date firstDate = humanDateFormat.parse("Wed Oct 16 21:12:47 EDT 2013");
		Date secondDate = humanDateFormat.parse("Wed Oct 16 21:12:57 EDT 2013");
		Date thirdDate = humanDateFormat.parse("Wed Oct 16 21:13:00 EDT 2013");
		Date fourthDate = humanDateFormat.parse("Wed Oct 16 21:13:09 EDT 2013");
		
		InteractionEvent firstWorkbenchWindowEvent = makeWorkbenchWindowEvent(firstDate);
		InteractionEvent secondWorkbenchWindowEvent = makeWorkbenchWindowEvent(fourthDate);
		
		InteractionEvent openDeclarationEvent = makeKeyBoardCommandInteractionEvent(ID_OPEN_DECLARATION, secondDate);
		InteractionEvent renameRefactorEvent = makeKeyBoardCommandInteractionEvent(ID_RENAME_REFACTOR, thirdDate);
		
		converter.foundInteractionEvents(firstWorkbenchWindowEvent, openDeclarationEvent, renameRefactorEvent, secondWorkbenchWindowEvent);
		
		List<ToolEvent> outputEvents = converter.getConvertedEvents();

		assertNotNull(outputEvents);
		assertEquals(2, outputEvents.size());

		ToolEvent outputEvent = outputEvents.get(0);
		assertEquals(F3, outputEvent.getToolKeyPresses());
		assertEquals(NAME_OPEN_DECLARATION, outputEvent.getToolName());
		assertEquals(3*1000, outputEvent.getDuration());
		
		outputEvent = outputEvents.get(1);
		assertEquals(ALT_SHIFT_R, outputEvent.getToolKeyPresses());
		assertEquals(NAME_RENAME_REFACTOR, outputEvent.getToolName());
		assertEquals(9*1000, outputEvent.getDuration());
		
	}
	
	@Test
	public void testDurationCheckingWithWorkbenchWindowMethod() throws Exception 
	{
		/*  This emulates this pattern
		  	[startdate: Wed Oct 16 21:08:43 EDT 2013, kind: command, sourceHandle: null, origin: IntroduceParameterObjectAction,
		  			delta: menu, endDate: Wed Oct 16 21:08:43 EDT 2013, navigation: null, interestContribution: 1.0, StructureKind: null, StructureHandle: null]
			[startdate: Wed Oct 16 21:08:43 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.jdt.ui.edit.text.java.introduce.parameter.object,
			 		delta: keybinding, endDate: Wed Oct 16 21:08:43 EDT 2013, navigation: null, interestContribution: 1.0, StructureKind: null, StructureHandle: null]
			
			[startdate: Wed Oct 16 21:09:04 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.ui.internal.WorkbenchWindow,
			 		delta: activated, endDate: Wed Oct 16 21:09:04 EDT 2013, navigation: null, interestContribution: 1.0, StructureKind: null, StructureHandle: null]
		 */
		Date firstAndSecondDate = humanDateFormat.parse("Wed Oct 16 21:08:43 EDT 2013");
		Date thirdDate = humanDateFormat.parse("Wed Oct 16 21:09:04 EDT 2013");
		
		InteractionEvent introduceParameterMenuEvent = makeMenuCommandInteractionEvent(MENU_INTRODUCE_PARAMETER_OBJECT, firstAndSecondDate, firstAndSecondDate);
		InteractionEvent introduceParameterKeyboardEvent = makeKeyBoardCommandInteractionEvent(ID_INTRODUCE_PARAMETER_OBJECT, firstAndSecondDate);
		InteractionEvent workbenchWindowEvent = makeWorkbenchWindowEvent(thirdDate);
		
		converter.foundInteractionEvents(introduceParameterMenuEvent,introduceParameterKeyboardEvent,workbenchWindowEvent);
		
		List<ToolEvent> outputEvents = converter.getConvertedEvents();

		assertNotNull(outputEvents);
		assertEquals(1, outputEvents.size());

		ToolEvent outputEvent = outputEvents.get(0);
		assertEquals(MENU_KEYBINDING, outputEvent.getToolKeyPresses());
		assertEquals(NAME_INTRODUCE_PARAMETER_OBJECT, outputEvent.getToolName());
		assertEquals(21*1000, outputEvent.getDuration()); //This should be 21:09:04 - 21:08:43 == 21 seconds
	}

	@Test
	public void testKeyBindingTimeoutAndSeveralTools() throws Exception {
		/* Keybinding commands shouldn't be too long, so if the user hits a key binding, does stuff that doesn't trigger a perspective change
		 * or other view change until past the KEY_BINDING_TIMEOUT, then the default key binding time should be used.
		 * 
		 	[startdate: Wed Oct 16 22:41:51 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.ui.internal.WorkbenchWindow, 
		 			delta: activated, endDate: Wed Oct 16 22:41:51 EDT 2013]
			[startdate: Wed Oct 16 22:42:06 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.jdt.ui.edit.text.java.open.editor,
			 		delta: keybinding, endDate: Wed Oct 16 22:42:06 EDT 2013]
			[startdate: Wed Oct 16 22:42:59 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.ui.edit.text.contentAssist.proposals,
			 		delta: keybinding, endDate: Wed Oct 16 22:42:59 EDT 2013]
			[startdate: Wed Oct 16 22:43:00 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.ui.file.save,
			 		delta: keybinding, endDate: Wed Oct 16 22:43:00 EDT 2013]
			[startdate: Wed Oct 16 22:43:17 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.ui.file.save,
			 		delta: keybinding, endDate: Wed Oct 16 22:43:17 EDT 2013]
		 */
		
		Date firstDate = humanDateFormat.parse("Wed Oct 16 22:41:51 EDT 2013");
		Date secondDate = humanDateFormat.parse("Wed Oct 16 22:42:06 EDT 2013");
		Date thirdDate = humanDateFormat.parse("Wed Oct 16 22:42:59 EDT 2013");
		Date fourthDate = humanDateFormat.parse("Wed Oct 16 22:43:00 EDT 2013");
		Date fifthDate = humanDateFormat.parse("Wed Oct 16 22:43:17 EDT 2013");
		Date shutDownDate = humanDateFormat.parse("Wed Oct 16 22:55:44 EDT 2013");
		
		InteractionEvent workbenchWindowEvent = makeWorkbenchWindowEvent(firstDate);
		InteractionEvent openDeclarationEvent = makeKeyBoardCommandInteractionEvent(ID_OPEN_DECLARATION, secondDate);
		InteractionEvent contentAssistEvent = makeKeyBoardCommandInteractionEvent(ID_CONTENT_ASSIST, thirdDate);
		InteractionEvent saveEvent1 = makeKeyBoardCommandInteractionEvent(ID_SAVE, fourthDate);
		InteractionEvent saveEvent2 = makeKeyBoardCommandInteractionEvent(ID_SAVE, fifthDate);	
		
		converter.foundInteractionEvents(workbenchWindowEvent,openDeclarationEvent,contentAssistEvent,saveEvent1,saveEvent2);
		
		converter.isShuttingDown(shutDownDate);
		
		List<ToolEvent> outputEvents = converter.getConvertedEvents();

		assertNotNull(outputEvents);
		assertEquals(4, outputEvents.size());

		ToolEvent outputEvent = outputEvents.get(0);
		assertEquals(F3, outputEvent.getToolKeyPresses());
		assertEquals(NAME_OPEN_DECLARATION, outputEvent.getToolName());
		assertEquals(DEFAULT_KEYBINDING_DURATION, outputEvent.getDuration()); //This would have been 22:42:59 - 22:42:06 = 53, but that is more than the 
														 //timeout for a keybinding, so it goes to default time
		outputEvent = outputEvents.get(1);
		assertEquals(CTRL_SPACE, outputEvent.getToolKeyPresses());
		assertEquals(NAME_CONTENT_ASSIST, outputEvent.getToolName());
		assertEquals(1 * 1000, outputEvent.getDuration());  //22:43:00 - 22:42:59 = 1 second
		
		outputEvent = outputEvents.get(2);
		assertEquals(CTRL_S, outputEvent.getToolKeyPresses());
		assertEquals(NAME_SAVE, outputEvent.getToolName());
		assertEquals(DEFAULT_KEYBINDING_DURATION, outputEvent.getDuration());
		
		outputEvent = outputEvents.get(3);
		assertEquals(CTRL_S, outputEvent.getToolKeyPresses());
		assertEquals(NAME_SAVE, outputEvent.getToolName());
		assertEquals(DEFAULT_KEYBINDING_DURATION, outputEvent.getDuration());
	}
	
	@Test
	public void testMenuTimeoutAndThreshold() throws Exception {
		/*
		 	7385371 [startdate: Wed Oct 16 22:48:49 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.debug.internal.ui.actions.LaunchShortcutAction,
		 	 		delta: menu, endDate: Wed Oct 16 22:48:49 EDT 2013]
			7385371 [startdate: Wed Oct 16 22:48:49 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.jdt.junit.junitShortcut.run,
			 		delta: keybinding, endDate: Wed Oct 16 22:48:49 EDT 2013]
			7386518 [startdate: Wed Oct 16 22:48:50 EDT 2013, kind: preference, sourceHandle: null, origin: org.eclipse.jdt.ui.JavaPerspective,
			 		delta: perspective changed: actionSetShow, endDate: Wed Oct 16 22:48:50 EDT 2013]
			7517027 [startdate: Wed Oct 16 22:51:01 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.ui.internal.WorkbenchWindow,
			 		delta: activated, endDate: Wed Oct 16 22:51:01 EDT 2013]
		 */
		
		Date firstAndSecondDate = humanDateFormat.parse("Wed Oct 16 22:48:49 EDT 2013");
		Date thirdDate = humanDateFormat.parse("Wed Oct 16 22:48:50 EDT 2013");	//should be within the threshold for menu items
		Date fourthDate = humanDateFormat.parse("Wed Oct 16 22:51:01 EDT 2013"); //should cause menu timeout
		
		InteractionEvent menuJUnitTestsEvent = makeMenuCommandInteractionEvent(MENU_RUN_JUNIT_TESTS, firstAndSecondDate, firstAndSecondDate);
		InteractionEvent keyBindingJUnitTestsEvent = makeKeyBoardCommandInteractionEvent(ID_RUN_JUNIT_TESTS, firstAndSecondDate);
		InteractionEvent sideEffectPerspectiveEvent = makeMockInteractionEvent(Kind.PREFERENCE, "org.eclipse.jdt.ui.JavaPerspective", "perspective changed: actionSetShow", thirdDate, thirdDate);

		InteractionEvent lastWorkbenchWindowEvent = makeWorkbenchWindowEvent(fourthDate);
		
		converter.foundInteractionEvents(menuJUnitTestsEvent,keyBindingJUnitTestsEvent,sideEffectPerspectiveEvent,lastWorkbenchWindowEvent);
		
		List<ToolEvent> outputEvents = converter.getConvertedEvents();

		assertNotNull(outputEvents);
		assertEquals(1, outputEvents.size());

		ToolEvent outputEvent = outputEvents.get(0);
		assertEquals(MENU_KEYBINDING, outputEvent.getToolKeyPresses());
		assertEquals(NAME_RUN_JUNIT_TESTS, outputEvent.getToolName());
		assertEquals(DEFAULT_MENU_DURATION, outputEvent.getDuration());
	}


}
