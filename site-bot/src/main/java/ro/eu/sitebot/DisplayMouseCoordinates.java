package ro.eu.sitebot;

import java.awt.*;

/**
 * Created by eutma on 1/19/2016.
 */
public class DisplayMouseCoordinates {
	public static void main(String[] args) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		System.out.println("Screen Widht: " + screenSize.getWidth());
		System.out.println("Screen Height: " + screenSize.getHeight());

		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		double prevx = 0;
		double prevy = 0;
		while (true) {
			if (pointerInfo.getLocation().getX() != prevx && pointerInfo.getLocation().getY() != prevy) {
				prevx = pointerInfo.getLocation().getX();
				prevy = pointerInfo.getLocation().getY();
				System.out.println(prevx + " " + prevy);
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			pointerInfo = MouseInfo.getPointerInfo();
		}
	}
}
