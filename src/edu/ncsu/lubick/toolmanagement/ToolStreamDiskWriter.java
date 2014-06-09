package edu.ncsu.lubick.toolmanagement;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;

import edu.ncsu.lubick.util.RotatingFileManager;
import edu.ncsu.lubick.util.RotatingFileManagerListener;

/**
 * Uses a rotatingFileManager to queue up toolEvents before dumping them to disk.
 * @author KevinLubick
 *
 */
public class ToolStreamDiskWriter implements RotatingFileManagerListener, IToolStreamReporter {

	private static final String TOOLSTREAM_FOLDER_NAME = "Eclipse/";
	private static final long DELAY_FOR_NEW_FILE_MS = 60*1000;		//every minute
	private static File toolStreamFolder;
	private SimpleDateFormat dateInSecondsToNumber = new SimpleDateFormat("DDDyykkmmss");	//this is the same as in screencasting hub

	private RotatingFileManager rotatingFileManager;
	private Timer timer;
	protected boolean shouldGoToNextFile;
	private JSONArray jarr = new JSONArray();

	public ToolStreamDiskWriter() throws IOException 
	{
		makeRotatingFileManager();

		//sets up the timer that regulates when the file should be changed
		setUpFileChangeTimer();

	}

	private static void createOutputFolderInMonitorFolder(File monitorFolder) {
		toolStreamFolder = new File(monitorFolder,TOOLSTREAM_FOLDER_NAME);
		if (!toolStreamFolder.exists())
		{
			if (!toolStreamFolder.mkdir())
			{
				System.err.println("There was a problem setting up the toolStreamFolder");
			}
		}
	}

	private void makeRotatingFileManager() throws IOException {
		this.rotatingFileManager = new RotatingFileManager(toolStreamFolder, "Eclipse.", "log", this);
		rotatingFileManager.makeNextFile();
	}

	private void setUpFileChangeTimer() {
		timer = new Timer(true);
		timer.schedule(new TimerTask() {

			@Override
			public void run() 
			{
				synchronized (rotatingFileManager) {
					try {
						advanceToNextFile();
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

	private void advanceToNextFile() throws IOException 
	{
		writeOutCurrentJSONArray();
		jarr = new JSONArray();
		rotatingFileManager.makeNextFile();
	}

	private void writeOutCurrentJSONArray() throws IOException
	{
		String toWrite = null;
		try {
			synchronized (jarr) 
			{
				toWrite = jarr.toString(2);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		rotatingFileManager.getCurrentFileStream().write(toWrite.getBytes("UTF-8"));
	
	}

	@Override
	public void storeEvent(ToolEvent toolEvent) {
		try 
		{
			synchronized (jarr) {
				jarr.put(toolEvent.toJSONObject());
			}

		} catch (JSONException e) 
		{
			e.printStackTrace();
		}

	}

	@Override
	public String getNextSuffix() 
	{
		return this.dateInSecondsToNumber.format(new Date());
	}

	@Override
	public void isShuttingDown() 
	{
		try {
			writeOutCurrentJSONArray();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		rotatingFileManager.stop();
	}

	public static void setOutputFolder(File outputFolder)
	{
		createOutputFolderInMonitorFolder(outputFolder);
		
	}

}
