package de.arduino.intelligence.mousecinema2;
/* Copyright [2013] [Frank Haverland]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/  
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Observable;
import java.util.TooManyListenersException;

/**
 * This class use the rxtx-Extension for Serial ports
 * @author frank
 *
 */
public class SerialComm extends Observable implements Runnable {

	SerialPort port;
	InputStream inputStream;
	int baudrate = 115200;
	int dataBits = SerialPort.DATABITS_8;
	int stopBits = SerialPort.STOPBITS_1;
	int parity = SerialPort.PARITY_NONE;

	public boolean exit = false;
	
	/**
	 * Find the first connection serial interface
	 * @return
	 */
	public CommPortIdentifier getSerialConnection() {

		CommPortIdentifier serialPortId;
		Enumeration enumComm;

		enumComm = CommPortIdentifier.getPortIdentifiers();
		while (enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier) enumComm.nextElement();
			if (serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				return serialPortId;
			}
		}
		return null;
	}

	/** 
	 * Runs as a thread until the application will be closed.
	 */
	public void run() {
		CommPortIdentifier portId = getSerialConnection();
		
		if (portId == null) {
			System.err.println("No serial port found for opening");
			return;
		}
		
		if (openSerialPort(portId) == false)
			return;

		while (!exit) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		closeSerialPort();

	}

	boolean openSerialPort(CommPortIdentifier commPort) {
		if (port != null) {
			System.out.println("Port is allready open");
			return false;
		}

		try {
			port = (SerialPort) commPort.open("Open port", 500);
		} catch (PortInUseException e) {
			System.out.println("Port in use");
		}
		try {
			inputStream = port.getInputStream();
		} catch (IOException e) {
			System.out.println("Can't open InputStream");
		}
		try {
			port.addEventListener(new MySerialPortEventListener());
		} catch (TooManyListenersException e) {
			System.out.println("TooManyListenersException for Serialport");
		}
		port.notifyOnDataAvailable(true);

		try {
			port.setSerialPortParams(baudrate, dataBits, stopBits, parity);
		} catch (UnsupportedCommOperationException e) {
			System.out.println("Can't set serial parameter");
		}

		return true;
	}

	void closeSerialPort() {
		if (port != null) {
			port.close();
			port = null;
		}
	}

	/**
	 * On available a bytes will be read and send to the observer.
	 */
	void dataAvailable() {
		try {
			byte[] data = new byte[1];
			while (inputStream.available() > 0) {
				inputStream.read(data, 0, data.length);
				super.setChanged();
				super.notifyObservers(new Byte(data[0]));
			}
		} catch (IOException e) {
			System.out.println("Fehler beim Lesen empfangener Daten");
		}
	}

	/**
	 * The listener have many events. We use only the DATA_AVAILABLE.
	 * @author frank
	 *
	 */
	class MySerialPortEventListener implements SerialPortEventListener {
		public void serialEvent(SerialPortEvent event) {
			switch (event.getEventType()) {
			case SerialPortEvent.DATA_AVAILABLE:
				dataAvailable();
				break;
			default:
			}
		}
	}
}
