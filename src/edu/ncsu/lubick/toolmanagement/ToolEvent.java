package edu.ncsu.lubick.toolmanagement;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;


public class ToolEvent 
{
	private String toolName, toolClass, keyPresses;
	private float rating;
	private Date timeStamp;
	private int duration;
	
	public static final String TOOL_NAME = "Tool_Name";
	public static final String TOOL_CLASS = "Tool_Class";
	public static final String TOOL_KEY_PRESSES = "Tool_Key_Presses";
	public static final String TOOL_RATING = "Tool_Rating";
	public static final String TOOL_TIMESTAMP = "Tool_Timestamp";
	public static final String TOOL_DURATION = "Tool_Duration";
	
	public ToolEvent(String toolName, String toolClass, String keyPresses, float rating, Date timeStamp, int duration) 
	{
		this.toolName = toolName;
		this.toolClass = toolClass;
		this.keyPresses = keyPresses == null? InteractionEventConvertor.MENU_KEYBINDING:keyPresses;
		this.rating = rating;
		this.timeStamp = timeStamp;
		this.duration = duration;
	}

	public JSONObject toJSONObject() throws JSONException 
	{
		JSONObject jobj = new JSONObject();
		
		jobj.put(TOOL_NAME, toolName);
		jobj.put(TOOL_CLASS, toolClass);
		jobj.put(TOOL_KEY_PRESSES, keyPresses);
		jobj.put(TOOL_RATING, rating);
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
	
	public float getRating() {
		return rating;
	}

	@Override
	public String toString() {
		return "ToolEvent [toolName=" + toolName + ", toolClass=" + toolClass
				+ ", keyPresses=" + keyPresses + ", timeStamp=" + timeStamp
				+ ", duration=" + duration + "]";
	}
	
	
}
