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

import java.util.Observable;
import java.util.Observer;


public class GraphicsReader extends Observable implements Observer {
	
	byte[] pixels = new byte[225];
	byte[] inReadPixels = pixels;
	int pixel = 0;
	
	public GraphicsReader() {
		super();
	}

	public byte[] getPixels() {
		return pixels;
	}

	public void setPixels(byte[] pixels) {
		this.pixels = pixels;
	}


	/**
	 * Read one Byte. The Byte 0xFF is a start byte. after it, 225 Pixels with
	 * grayscale will be read.
	 * 
	 * The grayscale pixel values are between 0..127. So we double the value in the method.
	 * @param readByte
	 */
	public void readByte(byte readByte) {
		System.out.printf("read:(%d) %02X\n" , pixel, readByte);
		if (readByte==-1) {
			pixel = 0;
			pixels = inReadPixels;
			inReadPixels = new byte[225];
		} else {
			if (pixel<225) {
				
				inReadPixels[pixel] = (byte)((int)readByte<<1);
				pixel++;
				if (pixel == 225) {
					System.out.println("picture ready");
					pixels = inReadPixels;
					super.setChanged();
					super.notifyObservers(pixels);
				}
			} 
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		byte readByte = (Byte) arg;
		readByte(readByte);
	}
	
	

}
