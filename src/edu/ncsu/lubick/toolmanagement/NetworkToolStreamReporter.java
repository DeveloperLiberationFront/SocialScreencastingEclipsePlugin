package edu.ncsu.lubick.toolmanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

public class NetworkToolStreamReporter implements IToolStreamReporter {

	private static final long DELAY_FOR_REPORTING_MS = 600_000; //XXX fix back to one minute
	private static Logger logger;
	private Timer timer;
	private JSONArray jarr = new JSONArray();
	private CloseableHttpClient client = HttpClients.createDefault();
	private TimerTask task;

	public NetworkToolStreamReporter()
	{
		setUpToolStreamReportTimer();
		logger.debug("Network Stream Reporter set up");
	}

	private void setUpToolStreamReportTimer()
	{
		timer = new Timer(true);
		this.task = new TimerTask() {

			@Override
			public void run()
			{
				logger.debug("ping");
				JSONArray copy = new JSONArray();
				synchronized (jarr)
				{
					try
					{
						logger.debug("data: " + copy);
						copy = new JSONArray(jarr.toString());
						jarr = new JSONArray();
					}
					catch (JSONException e)
					{
						logger.fatal("Problem copying jarr",e);
					}
				}
				logger.debug("sending " + new Date());
				try
				{
					reportThisSetOfEvents(copy);
					logger.info("Successfully reported "+copy.length()+" tools");	//for long term debugging
				}
				catch (IOException | JSONException e)
				{
					logger.error("Problem reporting "+copy.length()+" events",e);
				}
				logger.debug("pong");
			}
		};
		timer.scheduleAtFixedRate(task, DELAY_FOR_REPORTING_MS, DELAY_FOR_REPORTING_MS);

	}

	protected void reportThisSetOfEvents(JSONArray copy) throws IOException, JSONException
	{
		if (copy.length() < 1)
		{
			return;
		}
		HttpPost httpPost = new HttpPost("http://localhost:4443/reportTool");
		try {
			httpPost.setConfig(RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000).build());


			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("pluginName", "Eclipse"));
			nvps.add(new BasicNameValuePair("toolUsages", copy.toString()));

			System.out.println("JSON data: ");
			System.out.println("\t" + copy.toString(2));

			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			client.execute(httpPost);
		}
		finally {
			httpPost.reset();
		}

	}

	@Override
	public void storeEvent(ToolEvent toolEvent)
	{
		synchronized (jarr)
		{
			try
			{
				jarr.put(toolEvent.toJSONObject());
				logger.debug(jarr);
			}
			catch (JSONException e)
			{
				logger.error("Problem storing "+toolEvent,e);
			}
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
