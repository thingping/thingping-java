package net._95point2.thingping;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThingPing 
{
	public URL url;
	private static final Logger log = LoggerFactory.getLogger(ThingPing.class);
	
	public static ClientBuilder clientBuilder(){
		return new ClientBuilder();
	}
	
	public static class ClientBuilder
	{
		private String accountId;
		private String thingId;
		private String interval;
		private String notify;
		private String urlBase = "http://thingping.net/v1/ping";
		
		ClientBuilder withUrlBase(String urlBase){
			this.urlBase = urlBase;
			return this;
		}
		
		public ClientBuilder withAccount(String accountId){
			this.accountId = accountId;
			return this;
		}
		
		public ClientBuilder withThingId(String thingId){
			this.thingId = thingId;
			return this;
		}
		
		public ClientBuilder withInterval(String interval){
			this.interval = interval;
			return this;
		}
		
		public ClientBuilder withNotify(String notify){
			this.notify = notify;
			return this;
		}
		
		/**
		 * @return ThingPing client object ready for use
		 * @throws IllegalArgumentException if the params are not valid
		 */
		public ThingPing build()
		{
			if(accountId == null){ throw new IllegalArgumentException("no accountId provided"); }
			if(thingId == null){ throw new IllegalArgumentException("no thingId provided"); }
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(urlBase).append("?")
				.append("account=").append(enc(accountId))
				.append("&")
				.append("thing=").append(enc(thingId));
			
			if(interval != null || notify != null){
				sb.append("&");
			}
			
			if(interval != null){
				if( !interval.equalsIgnoreCase("daily") && !interval.equalsIgnoreCase("hourly")){
					try {
						Duration.parse(interval); // this will throw DateTimeParseException if the text cannot be parsed to a duration
					} catch(DateTimeParseException dtpe) {
						throw new IllegalArgumentException("interval is not valid: " + interval, dtpe);
					}
				}
				
				sb.append("freq=").append(interval);
			}
			
			if(interval != null && notify != null){
				sb.append("&");
			}
			
			if(notify != null){
				sb.append("notify=").append(enc(notify));
			}
			
			try{
				return new ThingPing(new URL(sb.toString()));
			} catch(MalformedURLException murle) {
				throw new IllegalArgumentException("Cannot build URL from: " + sb.toString(), murle);
			}
		}
		
		public static String enc(String param){
			try {
				return URLEncoder.encode(param, StandardCharsets.UTF_8.name());
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}
	
	public ThingPing(URL url) {
		this.url = url;
	}
	
	/**
	 * This version won't interrupt your other code, at the expense of swallowing exceptions. 
	 * Just make sure you have SLF4J logging enabled so you can discover the pathology of failure 
	 * when your things expire! 
	 */
	public void ping() 
	{
		ping(false);
	}

	/**
	 * This version can use `true` argument to allow ThingPingException to be thrown which wraps any other exception.
	 * Using `false` to won't interrupt your other code, at the expense of swallowing exceptions. 
	 * Just make sure you have SLF4J logging enabled so you can discover the pathology of failure 
	 * @param throwEx true to throw ThingPingException wrapper or false to swallow all exceptions
	 * @throws ThingPingException 
	 * 
	 */
	public void ping(boolean throwEx) 
	{
		try
		{
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			huc.setConnectTimeout(30 * 1000);
			huc.setRequestMethod("GET");
			huc.setRequestProperty("User-Agent", "net._95point2.thingping.ThingPing-Java (v1)");
			huc.connect();
			int code = huc.getResponseCode();
			String message = huc.getResponseMessage();
			
			if(code >= 400)
			{
				log.error("ThingPing Notify Error: {} - {}", code, message);
				if(throwEx){
					throw new ThingPingException("ThingPing Response: " + code + " - " + message, null);
				}
			}
		}
		catch(Exception e){
			if(throwEx){
				throw new ThingPingException(e.getMessage(), e);
			}
		}
	}
	
	public static class ThingPingException extends RuntimeException
	{
		private static final long serialVersionUID = -7817546370260928807L;

		public ThingPingException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
