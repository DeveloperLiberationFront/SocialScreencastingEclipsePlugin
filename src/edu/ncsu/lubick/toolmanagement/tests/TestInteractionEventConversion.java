package edu.ncsu.lubick.toolmanagement.tests;

import static edu.ncsu.lubick.toolmanagement.tests.MockInteractionEventHandler.*;
import static org.junit.Assert.*;

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
		InteractionEvent ie = makeKeyBoardCommandInteractionEvent(ID_CONTENT_ASSIST, new Date(), new Date());
		
		converter.foundThisInteractionEvent(ie);
		
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
		InteractionEvent correspondingKeyboardCommand = makeKeyBoardCommandInteractionEvent(ID_OPEN_CALL_HIERARCHY, startAndEndDate, startAndEndDate);
		
		converter.foundThisInteractionEvent(menuEvent);
		
		List<ToolEvent> outputEvents = converter.getConvertedEvents();
		
		assertNotNull(outputEvents);
		assertEquals(0, outputEvents.size());
		
		converter.foundThisInteractionEvent(correspondingKeyboardCommand);
		
		outputEvents = converter.getConvertedEvents();
		
		assertNotNull(outputEvents);
		assertEquals(1, outputEvents.size());
		
		ToolEvent outputEvent = outputEvents.get(0);
		assertEquals(MENU_KEYBINDING, outputEvent.getToolKeyPresses());
		assertEquals(NAME_OPEN_CALL_HIERARCHY, outputEvent.getToolName());
		assertEquals(DEFAULT_DURATION, outputEvent.getDuration());

		
	}
	
	@Test
	public void testKeystrokeConversionWithWindowNoise() throws Exception 
	{
		
	}



}
