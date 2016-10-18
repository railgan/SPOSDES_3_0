package ch.ice.controller.threads;

import java.io.IOException;

import ch.ice.SegmentationMain;

public class SegmentationThread extends Thread{
	
	public static void main(String args[]) throws IOException {
		 (new SegmentationThread()).start();
        
}
	public void run() {
		try {
			SegmentationMain.main(null);
		} catch (IOException e) {
		e.printStackTrace();
		}
	}
}
