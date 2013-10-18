package edu.ncsu.lubick.screencasting.eclipse.plugin;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;


public class ToolEvent 
{
	private String toolName, toolClass, keyPresses;
	private Date timeStamp;
	private int duration;
	
	public static final String TOOL_NAME = "Tool_Name";
	public static final String TOOL_CLASS = "Tool_Class";
	public static final String TOOL_KEY_PRESSES = "Tool_Key_Presses";
	public static final String TOOL_TIMESTAMP = "Tool_Timestamp";
	public static final String TOOL_DURATION = "Tool_Duration";
	
	public ToolEvent(String toolName, String toolClass, String keyPresses, Date timeStamp, int duration) 
	{
		this.toolName = toolName;
		this.toolClass = toolClass;
		this.keyPresses = keyPresses;
		this.timeStamp = timeStamp;
		this.duration = duration;
	}

	public JSONObject toJSONObject() throws JSONException 
	{
		JSONObject jobj = new JSONObject();
		
		jobj.put(TOOL_NAME, toolName);
		jobj.put(TOOL_CLASS, toolClass);
		jobj.put(TOOL_KEY_PRESSES, keyPresses);
		jobj.put(TOOL_TIMESTAMP, timeStamp.getTime());
		jobj.put(TOOL_DURATION, duration);
		
		return jobj;
	}


	public String getToolName() {
		return toolName;
	}

	public String getToolClass() {
		return toolClass;
	}

	public String getToolKeyPresses() {
		return keyPresses;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public int getDuration() {
		return duration;
	}
}
