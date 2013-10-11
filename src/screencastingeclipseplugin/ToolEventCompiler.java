package screencastingeclipseplugin;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.mylyn.monitor.core.InteractionEvent;



public class ToolEventCompiler 
{

	private static Logger fileLogger;

	public static void setLogger(Logger logger) 
	{
		fileLogger = logger;
	}

	private ToolStreamDiskWriter diskWriter;
	
	public ToolEventCompiler(File monitorFolder) 
	{
		if (fileLogger == null)		//just in case this hasn't been initialized
		{
			fileLogger = Logger.getRootLogger();	
		}
		fileLogger.info("Eclipse has started up on "+new Date());
		
		
		this.diskWriter = new ToolStreamDiskWriter(monitorFolder);
		
		
	}

	public void handleInteractionEvent(InteractionEvent event) 
	{
		fileLogger.info(MylynInteractionListener.makePrintable(event));
		handleToolEvent(InteractionEventConvertor.convert(event));
		
	}

	private void handleToolEvent(ToolEvent toolEvent) 
	{
		// TODO Auto-generated method stub
		
	}



}
