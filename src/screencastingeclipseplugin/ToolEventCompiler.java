package screencastingeclipseplugin;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;



public class ToolEventCompiler 
{

	private static Logger fileLogger;

	static {
		Bundle log4jBundle = Platform.getBundle("org.apache.log4j");
		if (log4jBundle != null)
		{
			System.out.println("There was a big problem");
		}
		PropertyConfigurator.configure("./log4j.settings");
		fileLogger = Logger.getLogger("FileLogging" + ToolEventCompiler.class.getName());

	}
	
	public ToolEventCompiler(File outputFolder) 
	{
		fileLogger.info("This is a test message");
		// TODO Auto-generated constructor stub
	}

}
