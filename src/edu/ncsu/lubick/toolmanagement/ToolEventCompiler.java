package edu.ncsu.lubick.toolmanagement;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ncsu.lubick.interactions.InteractionEvent;
import edu.ncsu.lubick.plugin.CommandReceiver;


/**
 * Acts as the conversion manager to take Mylyn output and turn it into a format readable by the local hub.
 * 
 * Passes events to the translator and then relays them to the disk writer.
 * @author KevinLubick
 *
 */
public class ToolEventCompiler implements CommandReceiver
{
	private static Logger fileLogger = Logger.getLogger(ToolEventCompiler.class);

	private IToolStreamReporter toolReporter;
	private InteractionEventConvertor interactionEventConvertor;
	
	public ToolEventCompiler() 
	{
		if (fileLogger == null)		//just in case this hasn't been initialized, set a dummy value to avoid NPEs
		{
			fileLogger = Logger.getRootLogger();	
		}
		fileLogger.info("Screencasting Eclipse Plugin v1.05 has started up on "+new Date());
		
		
		createToolReporter();
		InteractionEventConvertor.setLoggerForProblems(fileLogger);
		this.interactionEventConvertor = new InteractionEventConvertor();
		
		
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

	@Override
	public void handleInteractionEvent(InteractionEvent event)
	{
		//fileLogger.info(MylynInteractionListener.makePrintable(iEvent));
		
		interactionEventConvertor.foundInteractionEvents(event);
		
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

	@Override
	public void isShuttingDown() 
	{
		interactionEventConvertor.isShuttingDown(new Date());
		
		List<ToolEvent> results = interactionEventConvertor.getConvertedEvents();
		
		reportToolEvents(results);
		
		toolReporter.isShuttingDown();
		fileLogger.info("Eclipse is shutting down "+new Date());
	}

	public static void setupLogging() {
		//does nothing.  A call to this will invoke the static initializer, making logging work at the right time.
	}

}
