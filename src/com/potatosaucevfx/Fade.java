package com.potatosaucevfx;

import java.util.Random;

import com.potatosaucevfx.Main.Mode;

public class Fade implements Runnable {
	Mode currentMode;
	Communication com;
	
	int valueR, valueG, valueB;
	int currR, currG, currB;
	
	int speedMilis = 4;
	
	byte[] fadeBuffer = new byte[3];
	
	public Fade(Mode currentMode, Communication com) {
		this.currentMode = currentMode;
		this.com = com;
	}
	
	public void setSpeedMillis(int speedMillis) {
		this.speedMilis = speedMillis;
	}
	
	
	@Override
	public void run() {
		Random randomColor = new Random();
		valueR = randomColor.nextInt(256);
		currR = valueR;
		valueG = randomColor.nextInt(256);
		currG = valueG;
		valueB = randomColor.nextInt(256);
		currB = valueB;
		
		while(currentMode == Mode.FADING) {
			if(valueR > currR) {
				currR++;
				fadeBuffer[0] = (byte) currR;
				com.sendToRGBBuffer(fadeBuffer);
				try {
					Thread.sleep(speedMilis);
				} catch (InterruptedException e) {}
			}
			else if(valueR < currR) {
				currR--;
				fadeBuffer[0] = (byte) currR;
				com.sendToRGBBuffer(fadeBuffer);
				try {
					Thread.sleep(speedMilis);
				} catch (InterruptedException e) {}
			}
			else if(valueR == currR) {
				fadeBuffer[0] = (byte) currR;
				com.sendToRGBBuffer(fadeBuffer);
				valueR = randomColor.nextInt(256);
				try {
					Thread.sleep(speedMilis);
				} catch (InterruptedException e) {}
			}
			
			// =============================================
			
			if(valueG > currG) {
				currG++;
				fadeBuffer[1] = (byte) currG;
				com.sendToRGBBuffer(fadeBuffer);
				try {
					Thread.sleep(speedMilis);
				} catch (InterruptedException e) {}
			}
			else if(valueG < currG) {
				currG--;
				fadeBuffer[1] = (byte) currG;
				com.sendToRGBBuffer(fadeBuffer);
				try {
					Thread.sleep(speedMilis);
				} catch (InterruptedException e) {}
			}
			else if(valueG == currG) {
				fadeBuffer[1] = (byte) currG;
				valueG = randomColor.nextInt(256);
				com.sendToRGBBuffer(fadeBuffer);
				try {
					Thread.sleep(speedMilis);
				} catch (InterruptedException e) {}
			}
			
			// =============================================
			
			if(valueB > currB) {
				currB++;
				fadeBuffer[2] = (byte) currB;
				com.sendToRGBBuffer(fadeBuffer);
				try {
					Thread.sleep(speedMilis);
				} catch (InterruptedException e) {}
			}
			else if(valueB < currB) {
				currB--;
				fadeBuffer[2] = (byte) currB;
				com.sendToRGBBuffer(fadeBuffer);
				try {
					Thread.sleep(speedMilis);
				} catch (InterruptedException e) {}
			}
			else if(valueB == currB) {
				fadeBuffer[2] = (byte) currB;
				com.sendToRGBBuffer(fadeBuffer);
				valueB = randomColor.nextInt(256);
				try {
					Thread.sleep(speedMilis);
				} catch (InterruptedException e) {}
			}
		}
		
	}

}
