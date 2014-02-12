package edu.ncsu.lubick.toolmanagement;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

import edu.ncsu.lubick.plugin.MylynInteractionListener;


/**
 * Acts as the conversion manager to take Mylyn output and turn it into a format readable by the local hub.
 * 
 * Passes events to the translator and then relays them to the disk writer.
 * @author KevinLubick
 *
 */
public class ToolEventCompiler 
{

	private static Logger fileLogger;

	private IToolStreamReporter toolReporter;
	private InteractionEventConvertor interactionEventConvertor;
	
	public static void setLogger(Logger logger) 
	{
		fileLogger = logger;
	}
	
	public ToolEventCompiler() 
	{
		if (fileLogger == null)		//just in case this hasn't been initialized, set a dummy value to avoid NPEs
		{
			fileLogger = Logger.getRootLogger();	
		}
		fileLogger.info("Eclipse has started up on "+new Date());
		
		
		createToolReporter();
		this.interactionEventConvertor = new InteractionEventConvertor(fileLogger);
		
		
	}

	private void createToolReporter() {
		try {
			//this.toolReporter = new ToolStreamDiskWriter(monitorFolder);
			this.toolReporter = new NetworkToolStreamReporter();
		} catch (Exception e) {
			e.printStackTrace();
			toolReporter = new DummyToolStreamReporter();
		}
	}

	public void handleInteractionEvent(InteractionEvent iEvent) 
	{
		fileLogger.info(MylynInteractionListener.makePrintable(iEvent));
		
		interactionEventConvertor.foundInteractionEvents(iEvent);
		
		List<ToolEvent> results = interactionEventConvertor.getConvertedEvents();
		
		reportToolEvents(results);
		
	}

	private void reportToolEvents(List<ToolEvent> toolEvents) 
	{
		for (ToolEvent te: toolEvents)
		{
			toolReporter.storeEvent(te);
		}
		
	}

	public void isShuttingDown() 
	{
		interactionEventConvertor.isShuttingDown(new Date());
		
		List<ToolEvent> results = interactionEventConvertor.getConvertedEvents();
		
		reportToolEvents(results);
		
		toolReporter.isShuttingDown();
		fileLogger.info("Eclipse is shutting down "+new Date());
	}



}
