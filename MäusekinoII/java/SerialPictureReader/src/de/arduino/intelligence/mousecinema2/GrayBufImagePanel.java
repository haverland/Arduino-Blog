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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

/**
 * Creates an fullsize image of an 15x15 pixels image. 
 */
class GrayBufImagePanel extends JPanel implements Observer {
	private static final long serialVersionUID = 1L;

	BufferedImage fBufferedImage = null;

	/** Draw the image on the panel. 
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (fBufferedImage != null)
			g.drawImage(fBufferedImage, 0, 0, this);
	}

	@Override
	public void update(Observable o, Object arg) {
		
		byte[] pixels = (byte[])arg;
		// Create a BufferedIamge of the gray values in bytes.
		BufferedImage image = new BufferedImage(15, 15, BufferedImage.TYPE_BYTE_GRAY);

		// Get the writable raster so that data can be changed.
		WritableRaster wr = image.getRaster();

		// Now write the byte data to the raster
		wr.setDataElements(0, 0, 15, 15, pixels);
		
		// Rescale the image to full size image of the frame
		fBufferedImage = new BufferedImage(super.getWidth(), super.getHeight(), image.getType());
	    Graphics2D g = fBufferedImage.createGraphics();
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g.drawImage(image, 0, 0, super.getWidth(), super.getHeight(), 0, 0, image.getWidth(), image.getHeight(), null);
	    g.dispose();
	    
	    // Send repaint to me
		this.repaint();
	} 
}
