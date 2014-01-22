package com.web;

import gnu.io.CommPort;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.TooManyListenersException;

public class SerialRunnable implements Runnable, SerialPortEventListener {

	private static final long HEARTBEAT = 1500;
	private static final long TIMEOUT = 500;
	private SerialPort port;
	private static long[] porttimeout = new long[9];
	private static boolean running = false;

	public SerialRunnable(SerialPort port) {
		super();
		this.port = port;
		this.port.removeEventListener();
		try {
			this.port.addEventListener(this);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
	}
	
	long lastTime = 0;

	@Override
	public void run() {
		running = true;
		lastTime = System.currentTimeMillis();
		send();
	}
	
	private void send()
	{
		if(!running){
			if(port!=null)
			{
				port.removeEventListener();
				port.close();
				
			}
			System.out.println("Start over");
			return;
		}
		
		try {
			
			long runtime = System.currentTimeMillis() - lastTime;
			lastTime = System.currentTimeMillis();
			OutputStream out = port.getOutputStream();
			byte[] data = getData(runtime);
			out.write(data);
			
			System.out.print("Index:");
			System.out.print(data[0]);
			System.out.print(" Timeout:");
			System.out.print(byteArray2long(data,1,data.length));
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
			stop();
		}
		serialEvent(null);
	}
	
	@Override
	public void serialEvent(SerialPortEvent arg0) {
		try {
			byte[] b = new byte[0];
			InputStream in = port.getInputStream();
			in.read(b);
			System.out.print("Receive:");
			System.out.println(new String(b));
			
			long runtime = System.currentTimeMillis() - lastTime;
			
			Thread.sleep(HEARTBEAT - TIMEOUT - runtime);
			send();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void setPortTimeout(byte index,long timeout)
	{
		porttimeout[index] = timeout;
	}
	
	
	private byte[] getData(long runtime)
	{
		byte[] data = new byte[64];
		int dataindex = 0;
		for(byte i=0;i<porttimeout.length;i++)
		{
			if(porttimeout[i]>0)
			{
				data[dataindex++] = i;
				byte[] heartbeat = long2byteArray(HEARTBEAT);
				for(int j=0;j<heartbeat.length;j++)
				{
					data[dataindex++] = heartbeat[j];
				}
				
				porttimeout[i] -= runtime;
			}
		}
		
		return data;
	}
	
	private byte[] long2byteArray(long l)
	{
//		long temp = l;
//		byte[] barr = new byte[Long.SIZE/Byte.SIZE];
//		for(int i=0;i<barr.length;i++)
//		{
//			barr[i] = (byte) (temp&0xFF);
//			temp >>= Byte.SIZE;
//		}
		
//		return barr;
		ByteBuffer bb = ByteBuffer.allocate(Byte.SIZE);
		bb.putLong(l);
		return bb.array();
	}
	
	private long byteArray2long(byte[] data,int i,int length)
	{
//		long l = 0;
//		int j = 0;
//		for(;i<length;i++)
//		{
//			long temp = (data[i]&0xFF)<<((j++)*Byte.SIZE);
//			l |= temp;
//		}
//		return l;
		byte[] arr = new byte[length-i];
		for(int j=0;j<arr.length;j++)
		{
			arr[j] = data[i++];
		}
		ByteBuffer bb = ByteBuffer.wrap(arr);
		return bb.getLong();
	}
	
	public static void stop()
	{
		running = false;
	}
	
	public static boolean IsRunning()
	{
		return running;
	}

	

}
