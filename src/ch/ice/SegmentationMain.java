package ch.ice;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.configuration.PropertiesConfiguration;


import ch.ice.compare.ListComparison;
import ch.ice.controller.file.SegmentExcelParser;
import ch.ice.controller.file.SegmentExcelWriter;
import ch.ice.model.Segment;
import ch.ice.utils.Config;

public class SegmentationMain {
	
	public static double progressPercent = 0;
	public static String progressText = "Starting";
	public static PropertiesConfiguration config = Config.PROPERTIES;
	public static void main(String[] args) throws IOException {
		
		
		SegmentExcelParser Parser = new SegmentExcelParser();
		
		SegmentExcelWriter writer = new SegmentExcelWriter();
		ListComparison Comparer = new ListComparison();
		progressPercent = 0.01;

		ArrayList<Segment> listPOS = Parser.readPOSFile();
		progressPercent = 0.02;
		ArrayList<Segment> listReg = Parser.readRegisterFile();
		progressPercent = 0.03;
		
		System.out.println(progressText);
		ArrayList<Segment> listSegmented = Comparer.compareLists(listReg, listPOS);
		
		
		listReg = null;
		
		System.out.println("Time to deduplicate");
		
		String deduplicate = (String) config.getProperty("segmentation.deduplicate");
		if ( deduplicate.equals("true")){
			listSegmented = Comparer.deDuplicate(listPOS, listSegmented);
			}else{
				System.out.println("Es wird nich dedupliziert");
			}
		listPOS = null;
		System.out.println("Time to Segment");

				
		writer.writeXLSXFile(listSegmented);
		progressPercent = 1;
		System.out.println("Done");

	}
}
