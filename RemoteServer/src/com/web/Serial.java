package com.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Path("/serial")
public class Serial {
	private static final String PORT = "COM1";
	private static SerialPort port = null;
	private long[] data;

	public static boolean running = false;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject start(
			@QueryParam("index") byte index,
			@QueryParam("timeout") long timeout) 
					throws JSONException, 
					NoSuchPortException, 
					PortInUseException
	{
		JSONObject obj = new JSONObject();
		long begin = System.currentTimeMillis();
		CommPortIdentifier commport = CommPortIdentifier.getPortIdentifier(PORT);
		SerialRunnable.setPortTimeout(index, timeout);
		if(commport.isCurrentlyOwned())
		{
			obj.put("msg", "Port already in use by another application");
		}
		else if(!SerialRunnable.IsRunning())
		{
			port  = (SerialPort)(commport.open(this.getClass().getName(), 500));
			
//			Thread thread = new Thread(new SerialRunnable(port));
//			thread.start();
			new SerialRunnable(port).run();
		}
		
		obj.put("processtime", (System.currentTimeMillis() - begin));
		return obj;
	}

	@GET
	@Path("stop")
	@Produces(MediaType.APPLICATION_JSON)
	public String stop()
	{
		SerialRunnable.stop();
		return "success";
	}
}
