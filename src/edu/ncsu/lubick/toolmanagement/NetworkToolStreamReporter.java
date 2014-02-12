package edu.ncsu.lubick.toolmanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

public class NetworkToolStreamReporter implements IToolStreamReporter {

	private static final long DELAY_FOR_REPORTING_MS = 60_000;
	private Timer timer;
	private JSONArray jarr = new JSONArray();
	private CloseableHttpClient client = HttpClients.createDefault();

	public NetworkToolStreamReporter()
	{
		setUpFileChangeTimer();
	}

	private void setUpFileChangeTimer()
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
						copy = new JSONArray(jarr);
						jarr = new JSONArray();
					}
					catch (JSONException e)
					{
						System.err.println("Problem copying jarr");
						e.printStackTrace();
					}
				}
				try
				{
					reportThisSetOfEvents(copy);
				}
				catch (IOException e)
				{
					System.err.println("Problem reporting events");
					e.printStackTrace();
				}
			}
		}, DELAY_FOR_REPORTING_MS, DELAY_FOR_REPORTING_MS);
	}

	protected void reportThisSetOfEvents(JSONArray copy) throws IOException
	{
		HttpPost httpPost = new HttpPost("http://targethost/login");
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
			e.printStackTrace();
		}

	}

	@Override
	public void isShuttingDown()
	{
		// TODO Auto-generated method stub

		try
		{
			client.close();
		}
		catch (IOException e)
		{
			System.err.println("Problem closing down HTTP client");
			e.printStackTrace();
		}
	}

}
