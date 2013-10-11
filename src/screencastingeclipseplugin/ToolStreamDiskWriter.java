package screencastingeclipseplugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ToolStreamDiskWriter implements RotatingFileManagerListener {

	private static final String TOOLSTREAM_FOLDER_NAME = "Eclipse/";
	private static final long DELAY_FOR_NEW_FILE_MS = 60*1000;		//every minute
	private File toolStreamFolder;
	private SimpleDateFormat dateInSecondsToNumber = new SimpleDateFormat("DDDyykkmmss");
	
	private RotatingFileManager rotatingFileManager;
	private Timer timer;
	protected boolean shouldGoToNextFile;
	
	public ToolStreamDiskWriter(File monitorFolder) 
	{
		this.toolStreamFolder = new File(monitorFolder,TOOLSTREAM_FOLDER_NAME);
		if (!toolStreamFolder.exists())
		{
			if (!toolStreamFolder.mkdir())
			{
				System.err.println("There was a problem setting up the toolStreamFolder");
			}
		}
	
		this.rotatingFileManager = new RotatingFileManager(toolStreamFolder, "Eclipse", "log", this);
		
		//sets up the timer that regulates when the file should be changed
		timer = new Timer(true);
		timer.schedule(new TimerTask() {

			@Override
			public void run() 
			{
				synchronized (rotatingFileManager) {
					try {
						rotatingFileManager.makeNextFile();
					}
					catch (IOException e) 
					{
						System.err.println("Problem advancing to next file in the timerTask");
						e.printStackTrace();
					}
				}
				
			}
		}, DELAY_FOR_NEW_FILE_MS, DELAY_FOR_NEW_FILE_MS);
	}

	@Override
	public String getNextSuffix() 
	{
		return this.dateInSecondsToNumber.format(new Date());
	}

}
