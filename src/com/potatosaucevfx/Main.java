package com.potatosaucevfx;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.fazecast.jSerialComm.*;



public class Main {

	SerialPort chosenPort;
	JComboBox<String> portList;
	JButton btnConnect;

	JSlider redSlider;
	JSlider greenSlider;
	JSlider blueSlider;

	JLabel lblColorOutput;

	JTextArea debugConsole;
	JToggleButton tglbtnShowDebugConsole;
	JButton btnRandomColor;

	JFrame window;

	byte[] rgbOut = new byte[3];

	public void initialize() {

	}

	public Main() {
		window = new JFrame();
		//window.getContentPane().setFont(new Font("Segoe UI", Font.PLAIN, 11));
		window.setBounds(100, 100, 800, 600);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().setLayout(null);
		window.setResizable(false);

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
		lblRed.setBounds(0, 298, 34, 27);
		lblRed.setForeground(Color.RED);
		lblRed.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		window.getContentPane().add(lblRed);

		greenSlider = new JSlider();
		greenSlider.setForeground(Color.GREEN);
		greenSlider.setBounds(83, 335, 200, 26);
		greenSlider.setMaximum(255);
		window.getContentPane().add(greenSlider);

		JLabel lblGreen = new JLabel("Green");
		lblGreen.setBounds(0, 334, 52, 27);
		lblGreen.setForeground(Color.GREEN);
		lblGreen.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		window.getContentPane().add(lblGreen);

		blueSlider = new JSlider();
		blueSlider.setForeground(Color.BLUE);
		blueSlider.setBounds(83, 372, 200, 26);
		blueSlider.setMaximum(255);
		window.getContentPane().add(blueSlider);

		JLabel lblBlue = new JLabel("Blue");
		lblBlue.setBounds(0, 371, 37, 27);
		lblBlue.setForeground(Color.BLUE);
		lblBlue.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		window.getContentPane().add(lblBlue);

		lblColorOutput = new JLabel("Color Output");
		lblColorOutput.setFont(new Font("Segoe UI", Font.BOLD, 30));
		lblColorOutput.setBounds(0, 409, 186, 36);
		window.getContentPane().add(lblColorOutput);

		debugConsole = new JTextArea();
		debugConsole.setForeground(Color.GREEN);
		debugConsole.setBackground(Color.BLACK);
		debugConsole.setBounds(479, 11, 305, 276);
		debugConsole.setVisible(false);
		window.getContentPane().add(debugConsole);

		tglbtnShowDebugConsole = new JToggleButton("Show Debug Console");
		tglbtnShowDebugConsole.setBounds(336, 11, 133, 23);
		tglbtnShowDebugConsole.setSelected(false);
		window.getContentPane().add(tglbtnShowDebugConsole);

		tglbtnShowDebugConsole.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dPrintln(tglbtnShowDebugConsole.isSelected() + "");
				debugConsole.setVisible(tglbtnShowDebugConsole.isSelected());
			}
		});
		
		btnRandomColor = new JButton("Random Color");
		btnRandomColor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnRandomColor.setBounds(293, 335, 107, 23);
		btnRandomColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Random rand = new Random();
				rgbOut[0] = (byte) rand.nextInt(256);
				rgbOut[1] = (byte) rand.nextInt(256);
				rgbOut[2] = (byte) rand.nextInt(256);
				
				redSlider.setValue(rgbOut[0]);
				greenSlider.setValue(rgbOut[1]);
				blueSlider.setValue(rgbOut[2]);
				
			}
		});
		
		
		window.getContentPane().add(btnRandomColor);
		
		redSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				rgbOut[0] = (byte) redSlider.getValue();

			}
		});

		greenSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				rgbOut[1] = (byte) greenSlider.getValue();
			}
		});

		blueSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				rgbOut[2] = (byte) blueSlider.getValue();

			}
		});

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
					Thread sender = new Thread() {
						@Override
						public void run() {
							while(chosenPort.isOpen()) {
								try {
									chosenPort.getOutputStream().write(rgbOut);
									
									Thread.sleep(1);
								} catch (IOException e) {
									e.printStackTrace();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					};
					sender.start();


					Thread thread = new Thread() {
						@Override
						public void run() {
							while(chosenPort.isOpen()) {
								

								lblColorOutput.setForeground(new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue()));
							
								window.repaint();
							}
						}
					};
					thread.start();
					chosenPort.openPort();

				} else {
					// Disconnect from serial port
					chosenPort.closePort();
					portList.setEnabled(true);
					btnConnect.setText("Connect");

				}

			}
		});








	}

	public void dPrintln(String in) {
		debugConsole.append(in + "\n");
	}

	public void dPrint(String in) {
		debugConsole.append(in);
	}

















	public static void main(String[] args) {
		new Main();
	}
	
	// This program is known to the state of California to cause cancer and birth defects.
	
}
