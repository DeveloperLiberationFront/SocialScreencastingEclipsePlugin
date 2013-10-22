package edu.ncsu.lubick.toolmanagement;

import java.io.File;
import java.io.IOException;
import java.util.Date;

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

	private IToolStreamDiskWriter diskWriter;
	private InteractionEventConvertor interactionEventConvertor;
	
	public static void setLogger(Logger logger) 
	{
		fileLogger = logger;
	}
	
	public ToolEventCompiler(File monitorFolder) 
	{
		if (fileLogger == null)		//just in case this hasn't been initialized, set a dummy value to avoid NPEs
		{
			fileLogger = Logger.getRootLogger();	
		}
		fileLogger.info("Eclipse has started up on "+new Date());
		
		
		createDiskWriter(monitorFolder);
		this.interactionEventConvertor = new InteractionEventConvertor(fileLogger);
		
		
	}

	private void createDiskWriter(File monitorFolder) {
		try {
			this.diskWriter = new ToolStreamDiskWriter(monitorFolder);
		} catch (IOException e) {
			e.printStackTrace();
			diskWriter = new DummyToolStreamDiskWriter();
		}
	}

	public void handleInteractionEvent(InteractionEvent iEvent) 
	{
		fileLogger.info(MylynInteractionListener.makePrintable(iEvent));
		
		ToolEvent toolEvent = interactionEventConvertor.convert(iEvent);
		handleToolEvent(toolEvent);
		
	}

	private void handleToolEvent(ToolEvent toolEvent) 
	{
		if (toolEvent == null)
		{
			fileLogger.debug("Choosing not to write previous command to disk");
			return;
		}
		diskWriter.storeEvent(toolEvent);
		
	}

	public void isShuttingDown() 
	{
		interactionEventConvertor.isShuttingDown(new Date());
		diskWriter.isShuttingDown();
		fileLogger.info("Eclipse is shutting down "+new Date());
	}



}
