package edu.ncsu.lubick.plugin.tools;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

import edu.ncsu.lubick.plugin.InteractionEventConvertor;
import edu.ncsu.lubick.plugin.MylynInteractionListener;



public class ToolEventCompiler 
{

	private static Logger fileLogger;

	private ToolStreamDiskWriter diskWriter;
	
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
		
		
		try {
			this.diskWriter = new ToolStreamDiskWriter(monitorFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	public void handleInteractionEvent(InteractionEvent event) 
	{
		fileLogger.info(MylynInteractionListener.makePrintable(event));
		handleToolEvent(InteractionEventConvertor.convert(event));
		
	}

	private void handleToolEvent(ToolEvent toolEvent) 
	{
		if (toolEvent == null)
		{
			fileLogger.debug("Skipping previous command");
			return;
		}
		diskWriter.storeEvent(toolEvent);
		
	}

	public void isShuttingDown() 
	{
		diskWriter.isShuttingDown();
		fileLogger.info("Eclipse is shutting down "+new Date());
	}



}
