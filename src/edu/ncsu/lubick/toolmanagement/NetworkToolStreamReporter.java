package edu.ncsu.lubick.toolmanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

public class NetworkToolStreamReporter implements IToolStreamReporter {

	private static final long DELAY_FOR_REPORTING_MS = 60_000;
	private static Logger logger;
	private Timer timer;
	private JSONArray jarr = new JSONArray();
	private CloseableHttpClient client = HttpClients.createDefault();

	public NetworkToolStreamReporter()
	{
		setUpToolStreamReportTimer();
		logger.debug("Network Stream Reporter set up");
	}

	private void setUpToolStreamReportTimer()
	{
		timer = new Timer(true);
		timer.schedule(new TimerTask() {

			@Override
			public void run()
			{
				JSONArray copy = new JSONArray();
				synchronized (jarr)
				{
					try
					{
						copy = new JSONArray(jarr.toString());
						jarr = new JSONArray();
					}
					catch (JSONException e)
					{
						logger.fatal("Problem copying jarr",e);
					}
				}
				try
				{
					reportThisSetOfEvents(copy);
					System.out.println("Successfully reported "+copy.length()+" tools");	//for dev purposes
					logger.info("Successfully reported "+copy.length()+" tools");	//for long term debugging
				}
				catch (IOException e)
				{
					logger.error("Problem reporting events",e);
				}
			}
		}, DELAY_FOR_REPORTING_MS, DELAY_FOR_REPORTING_MS);
	}

	protected void reportThisSetOfEvents(JSONArray copy) throws IOException
	{
		if (copy.length() < 1)
		{
			return;
		}
		HttpPost httpPost = new HttpPost("http://localhost:4443/reportTool");

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("pluginName", "Eclipse"));
		nvps.add(new BasicNameValuePair("toolUsages", copy.toString()));

		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		client.execute(httpPost);
	}

	@Override
	public void storeEvent(ToolEvent toolEvent)
	{
		try
		{
			synchronized (jarr)
			{
				jarr.put(toolEvent.toJSONObject());
			}

		}
		catch (JSONException e)
		{
			logger.error("Problem storing "+toolEvent,e);
		}

	}

	@Override
	public void isShuttingDown()
	{
		logger.debug("Got shutdown message");
		try
		{
			client.close();
		}
		catch (IOException e)
		{
			logger.error("Problem closing down HTTP client",e);
		}
	}

	public static void setLogger(Logger logger)
	{
		NetworkToolStreamReporter.logger = logger;
	}

}
