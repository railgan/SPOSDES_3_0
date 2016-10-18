package ch.ice;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.configuration.PropertiesConfiguration;

import ch.ice.compare.ListComparison;
import ch.ice.controller.file.SegmentExcelParser;
import ch.ice.controller.file.SegmentExcelWriter;
import ch.ice.model.Segment;
import ch.ice.utils.Config;

/**
 * Main Method for Segmentation. Started via Thread
 * 
 * @author Mike
 *
 */
public class SegmentationMain {
	// Variables displayed in ProgressSegmentation GUI
	public static double progressPercent = 0;
	public static int amountSegmented = 0;
	public static int amountDuplicate = 0;
	public static String progressText = "Starting";

	// Config needed to enable / disable deduplication function
	public static PropertiesConfiguration config = Config.PROPERTIES;

	/**
	 * Main, starts the Segmentation. Started via Segmentation Thread.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// Creating 3 Class Instances used to Parse, Compare and Write the POS
		// Data
		SegmentExcelParser Parser = new SegmentExcelParser();
		SegmentExcelWriter writer = new SegmentExcelWriter();
		ListComparison Comparer = new ListComparison();

		// Changing Progressbar variable for GUI
		progressPercent = 0.01;

		// Parsing SCHURTER's POS Data
		ArrayList<Segment> listPOS = Parser.readPOSFile();

		// Changing Progressbar variable for GUI
		progressPercent = 0.02;

		// Reading Industry Segmentation Data
		ArrayList<Segment> listReg = Parser.readRegisterFile();

		// Changing Progressbar variable for GUI
		progressPercent = 0.03;

		// Comparing the two Lists to create the Segmentation
		ArrayList<Segment> listSegmented = Comparer.compareLists(listReg, listPOS);

		// Removing unneeded List
		listReg = null;

		// Checks if the POS data should be deduplicated and starts the method
		String deduplicate = (String) config.getProperty("segmentation.deduplicate");
		if (deduplicate.equals("true")) {
			listSegmented = Comparer.deDuplicate(listPOS, listSegmented);
		}

		// Removing unneeded List
		listPOS = null;

		// Writes the segmented (deduplicated) data into a new xlsx file
		writer.writeXLSXFile(listSegmented);

		// Set's the Progressbar in the GUI to full
		progressPercent = 1;
	}
}
