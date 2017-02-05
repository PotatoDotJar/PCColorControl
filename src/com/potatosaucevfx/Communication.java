package com.potatosaucevfx;

import java.io.IOException;

import com.fazecast.jSerialComm.SerialPort;

public class Communication implements Runnable {

	SerialPort port;
	
	int threadSleep = 5;
	
	byte[] rgbOut = new byte[3];
	
	public Communication(SerialPort chosenPort) {
		this.port = chosenPort;
	}
	
	
	
	@Override
	public void run() {
		while(port.isOpen()) {
			try {
				port.getOutputStream().write(rgbOut);
				Thread.sleep(threadSleep);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void sendToRGBBuffer(byte[] rgbOut) {
		this.rgbOut = rgbOut;
	}
	

}
