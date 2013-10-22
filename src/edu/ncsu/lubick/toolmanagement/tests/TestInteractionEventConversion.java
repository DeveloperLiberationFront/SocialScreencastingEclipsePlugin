package edu.ncsu.lubick.toolmanagement.tests;

import static edu.ncsu.lubick.toolmanagement.tests.MockInteractionEventHandler.*;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.mylyn.monitor.core.InteractionEvent;
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
	public void testTestingEnvironment()
	{
		assertTrue(true);
	}

	@Test
	public void testBasicKeystrokeConversion() throws Exception
	{
		InteractionEvent ie = makeKeyBoardCommandInteractionEvent(ID_CONTENT_ASSIST, new Date());

		converter.foundInteractionEvents(ie);

		List<ToolEvent> outputEvents = converter.getConvertedEvents();

		assertNotNull(outputEvents);
		assertEquals(1, outputEvents.size());

		ToolEvent outputEvent = outputEvents.get(0);
		assertEquals(CTRL_SPACE, outputEvent.getToolKeyPresses());
		assertEquals(NAME_CONTENT_ASSIST, outputEvent.getToolName());
		assertEquals(DEFAULT_DURATION, outputEvent.getDuration());


	}

	@Test
	public void testBasicMenuConversion() throws Exception 
	{//org.eclipse.jdt.ui.edit.text.java.open.call.hierarchy
		//Open Call Hierarchy
		//Eclipse generates two events for menu operations : a menu one and then a keyboard one that matches what was done.
		//This emulates that behavior

		Date startAndEndDate = new Date();
		InteractionEvent menuEvent = makeMenuCommandInteractionEvent(MENU_NAME_OPEN_CALL_HIERARCHY, startAndEndDate, startAndEndDate);
		InteractionEvent correspondingKeyboardCommand = makeKeyBoardCommandInteractionEvent(ID_OPEN_CALL_HIERARCHY, startAndEndDate);

		converter.foundInteractionEvents(menuEvent);

		List<ToolEvent> outputEvents = converter.getConvertedEvents();

		assertNotNull(outputEvents);
		assertEquals(0, outputEvents.size());

		converter.foundInteractionEvents(correspondingKeyboardCommand);

		outputEvents = converter.getConvertedEvents();

		assertNotNull(outputEvents);
		assertEquals(1, outputEvents.size());

		ToolEvent outputEvent = outputEvents.get(0);
		assertEquals(MENU_KEYBINDING, outputEvent.getToolKeyPresses());
		assertEquals(NAME_OPEN_CALL_HIERARCHY, outputEvent.getToolName());
		assertEquals(DEFAULT_DURATION, outputEvent.getDuration());


	}

	@Test
	public void testDoubleKeystrokeConversionWithWindowNoise() throws Exception 
	{
		/*  This test emulates this course of action
		 * 	[startdate: Wed Oct 16 21:12:47 EDT 2013, kind: command, sourceHandle: null, origin: org.eclipse.ui.internal.WorkbenchWindow,
		 * 			delta: activated, endDate: Wed Oct 16 21:12:47 EDT 2013, navigation: null, interestContribution: 1.0, StructureKind: null, StructureHandle: null]
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
		assertEquals(DEFAULT_DURATION, outputEvent.getDuration());
		
		outputEvent = outputEvents.get(1);
		assertEquals(ALT_SHIFT_R, outputEvent.getToolKeyPresses());
		assertEquals(NAME_RENAME_REFACTOR, outputEvent.getToolName());
		assertEquals(DEFAULT_DURATION, outputEvent.getDuration());
		
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




}
