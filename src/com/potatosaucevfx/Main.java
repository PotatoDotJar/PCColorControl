package com.potatosaucevfx;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.fazecast.jSerialComm.*;



public class Main {
	
	String TITLE = "Potato's Duino Controller - Version 0.4 Beta";
	
	
	SerialPort chosenPort;
	JComboBox<String> portList;
	JButton btnConnect;

	JSlider redSlider;
	JSlider greenSlider;
	JSlider blueSlider;
	
	JRadioButton modeCustom;
	JRadioButton modeRandomFading;
	JSlider fadeSpeed;

	JButton btnRandomColor;

	JFrame window;
	
	Thread sender;
	Communication com;
	
	Thread fadeThread;
	Fade fade;
	
	long speedMilis = 8;
	
	
	public enum Mode {
		CUSTOM, FADING
	}
	
	
	// Init Status
	Mode currentMode = Mode.FADING;

	
	public Main() {
		window = new JFrame();
		//window.getContentPane().setFont(new Font("Segoe UI", Font.PLAIN, 11));
		window.setBounds(100, 100, 800, 600);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().setLayout(null);
		window.setResizable(false);
		window.setTitle(TITLE);

		JLabel lblLedController = new JLabel("LED Controller");
		lblLedController.setBounds(11, 11, 175, 36);
		lblLedController.setFont(new Font("Segoe UI", Font.BOLD, 26));
		lblLedController.setForeground(new Color(100, 149, 237));
		window.getContentPane().add(lblLedController);

		portList = new JComboBox<String>();
		portList.setBounds(95, 62, 125, 21);
		portList.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		window.getContentPane().add(portList);

		btnConnect = new JButton("Connect");
		btnConnect.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnConnect.setBounds(11, 61, 83, 23);
		window.getContentPane().add(btnConnect);

		redSlider = new JSlider();
		redSlider.setForeground(Color.RED);
		redSlider.setBounds(83, 299, 200, 26);
		redSlider.setMaximum(255);
		window.getContentPane().add(redSlider);

		JLabel lblRed = new JLabel("Red");
		lblRed.setBounds(11, 298, 34, 27);
		lblRed.setForeground(Color.RED);
		lblRed.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		window.getContentPane().add(lblRed);

		greenSlider = new JSlider();
		greenSlider.setForeground(Color.GREEN);
		greenSlider.setBounds(83, 335, 200, 26);
		greenSlider.setMaximum(255);
		window.getContentPane().add(greenSlider);

		JLabel lblGreen = new JLabel("Green");
		lblGreen.setBounds(11, 334, 52, 27);
		lblGreen.setForeground(Color.GREEN);
		lblGreen.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		window.getContentPane().add(lblGreen);

		blueSlider = new JSlider();
		blueSlider.setForeground(Color.BLUE);
		blueSlider.setBounds(83, 372, 200, 26);
		blueSlider.setMaximum(255);
		window.getContentPane().add(blueSlider);

		JLabel lblBlue = new JLabel("Blue");
		lblBlue.setBounds(11, 371, 37, 27);
		lblBlue.setForeground(Color.BLUE);
		lblBlue.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		window.getContentPane().add(lblBlue);
		
		fadeSpeed = new JSlider();
		fadeSpeed.setBounds(129, 133, 200, 26);
		window.getContentPane().add(fadeSpeed);
		fadeSpeed.setMaximum(10);
		fadeSpeed.setMinimum(1);
		fadeSpeed.setValue((int) speedMilis);
		fadeSpeed.setPaintLabels(true);
		fadeSpeed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				fade.setSpeedMillis(fadeSpeed.getValue());
			}
		});
		
		JLabel lblFadeSpeed = new JLabel("Fade Speed");
		lblFadeSpeed.setForeground(Color.ORANGE);
		lblFadeSpeed.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		lblFadeSpeed.setBounds(21, 132, 107, 27);
		window.getContentPane().add(lblFadeSpeed);
		
		
		
		modeRandomFading = new JRadioButton("Random Fading");
		modeRandomFading.setBounds(11, 102, 116, 23);
		window.getContentPane().add(modeRandomFading);
		modeRandomFading.setSelected(true);
		
		modeRandomFading.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(currentMode == Mode.CUSTOM) {
					currentMode = Mode.FADING;
					modeCustom.setSelected(false);
					
				}
				
				fadeSpeed.setEnabled(true);
				
				redSlider.setEnabled(false);
				greenSlider.setEnabled(false);
				blueSlider.setEnabled(false);
				btnRandomColor.setEnabled(false);
				
				fade = new Fade(currentMode, com);
				fadeThread = new Thread(fade);
				fadeThread.start();
				
			}
		});
		
		modeCustom = new JRadioButton("Custom");
		modeCustom.setBounds(6, 268, 76, 23);
		window.getContentPane().add(modeCustom);
		modeCustom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(currentMode == Mode.FADING) {
					currentMode = Mode.CUSTOM;
					modeRandomFading.setSelected(false);
					fadeThread.stop();
				}
				
				fadeSpeed.setEnabled(false);
				
				redSlider.setEnabled(true);
				greenSlider.setEnabled(true);
				blueSlider.setEnabled(true);
				btnRandomColor.setEnabled(true);
				
				
				com.sendToRGBBuffer(new byte[] {
					(byte) redSlider.getValue(),
					(byte) greenSlider.getValue(),
					(byte) blueSlider.getValue()
				});	
			}
		});
		
		btnRandomColor = new JButton("Random Color");
		btnRandomColor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnRandomColor.setBounds(293, 335, 107, 23);
		btnRandomColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Random rand = new Random();
				
				byte[] randomRGB = new byte[3];
				randomRGB[0] = (byte) rand.nextInt(256);
				randomRGB[1] = (byte) rand.nextInt(256);
				randomRGB[2] = (byte) rand.nextInt(256);
				
				redSlider.setValue(randomRGB[0]);
				greenSlider.setValue(randomRGB[1]);
				blueSlider.setValue(randomRGB[2]);
				com.sendToRGBBuffer(randomRGB);
				
			}
		});
		
		
		window.getContentPane().add(btnRandomColor);
		byte[] sliderBuffer = new byte[3];
		redSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sliderBuffer[0] = (byte) redSlider.getValue();
				com.sendToRGBBuffer(sliderBuffer);
			}
		});

		greenSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sliderBuffer[1] = (byte) greenSlider.getValue();
				com.sendToRGBBuffer(sliderBuffer);
			}
		});

		blueSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sliderBuffer[2] = (byte) blueSlider.getValue();
				com.sendToRGBBuffer(sliderBuffer);
			}
		});
		redSlider.setEnabled(false);
		greenSlider.setEnabled(false);
		blueSlider.setEnabled(false);
		btnRandomColor.setEnabled(false);
		
		
		
		// Show window
		window.setVisible(true);

		// Populate Dropdown Box
		SerialPort[] portNames = SerialPort.getCommPorts();
		for(int i = 0; i < portNames.length; i++) {
			portList.addItem(portNames[i].getSystemPortName());
		}

		// Configure Connect Button
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(btnConnect.getText().equals("Connect")) {
					// Connect to serial port
					chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
					chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
					chosenPort.setBaudRate(250000);

					if(chosenPort.openPort()) {
						btnConnect.setText("Disconnect");
						portList.setEnabled(false);
					}
					
					// create thread for sending data
					com = new Communication(chosenPort);
					sender = new Thread(com);
					sender.start();
					chosenPort.openPort();
					
					fade = new Fade(currentMode, com);
					fadeThread = new Thread(fade);
					fadeThread.start();
					
				} else {
					// Disconnect from serial port
					chosenPort.closePort();
					portList.setEnabled(true);
					btnConnect.setText("Connect");
				}
			}
		});
	}
	
	public static void main(String[] args) {
		new Main();
	}
	
	// This program is known to the state of California to cause cancer and birth defects.
	
}
