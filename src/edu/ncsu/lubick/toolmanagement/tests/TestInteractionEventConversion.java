package edu.ncsu.lubick.toolmanagement.tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;

import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;
import org.eclipse.ui.keys.IBindingService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ncsu.lubick.toolmanagement.InteractionEventConvertor;
import edu.ncsu.lubick.toolmanagement.ToolEvent;
import edu.ncsu.lubick.util.CommandNameDirectory;
import edu.ncsu.lubick.util.CommandNameServce;
import edu.ncsu.lubick.util.KeyBindingDirectory;

public class TestInteractionEventConversion 
{
	//Keybindings
	private static final String CTRL_SPACE = "Ctrl+Space";
	
	//IDS
	private static final String ID_CONTENT_ASSIST = "org.eclipse.ui.edit.text.contentAssist.proposals";
	
	//NAMES
	private static final String NAME_CONTENT_ASSIST = "Content Assist";
	
	
	//misc
	private static final int DEFAULT_DURATION = 15000;
	private static final String KEYBINDING_DELTA = "keybinding";
	
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
	public void testBasicConversion() 
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

	private static InteractionEvent makeMockInteractionEvent(Kind kindOfCommand, String commandId, String deltaType, Date startDate, Date endDate) 
	{
		//could be mock(InteractionEvent), but this is more "lifelike"
		return new InteractionEvent(kindOfCommand, null, null, commandId, null, deltaType, 1.0f, startDate, endDate);
	}

	private static InteractionEvent makeKeyBoardCommandInteractionEvent(String commandId, Date startDate, Date endDate) 
	{	
		InteractionEvent ie = makeMockInteractionEvent(Kind.COMMAND, commandId ,KEYBINDING_DELTA, startDate, endDate);
		return ie;
	}

	private static IBindingService makeMockedKeyBindingService() 
	{
		IBindingService service = mock(IBindingService.class);
		when(service.getBestActiveBindingFormattedFor(ID_CONTENT_ASSIST)).thenReturn(CTRL_SPACE);
		return service;
	}

	private static CommandNameServce makeMockedCommandService() 
	{
		CommandNameServce testService = mock(CommandNameServce.class);
		when(testService.lookUpCommandName(ID_CONTENT_ASSIST)).thenReturn(NAME_CONTENT_ASSIST);
		return testService;
	}

}
