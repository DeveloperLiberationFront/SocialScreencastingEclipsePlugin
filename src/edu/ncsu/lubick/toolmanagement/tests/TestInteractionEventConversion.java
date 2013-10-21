package edu.ncsu.lubick.toolmanagement.tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;

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

public class TestInteractionEventConversion {
	
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
		InteractionEvent ie = makeMockedInteractionEvent();
		
		ToolEvent outputEvent = converter.convert(ie);
		
		assertNotNull(outputEvent);
		
		System.out.println(outputEvent);
		
	}

	private static InteractionEvent makeMockedInteractionEvent() {
		
		InteractionEvent ie = mock(InteractionEvent.class);
		Date startDate = new Date();
		Date endDate = new Date();
		
		when(ie.getDelta()).thenReturn("keybinding");
		
		when(ie.getDate()).thenReturn(startDate);
		when(ie.getEndDate()).thenReturn(endDate);
		when(ie.getKind()).thenReturn(Kind.COMMAND);
		when(ie.getOriginId()).thenReturn("org.eclipse.ui.edit.text.contentAssist.proposals");
		
		//InteractionEvent ie = new InteractionEvent(Kind.COMMAND, null, null, "org.eclipse.ui.edit.text.contentAssist.proposals", null, "keybinding", 1.0f, new Date(), new Date());
		return ie;
	}
	
	private static IBindingService makeMockedKeyBindingService() 
	{
		IBindingService service = mock(IBindingService.class);
		return service;
	}

	private static CommandNameServce makeMockedCommandService() 
	{
		CommandNameServce testService = mock(CommandNameServce.class);
		
		return testService;
	}

}
