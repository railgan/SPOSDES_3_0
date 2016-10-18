package ch.ice;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.ice.compare.ListComparison;
import ch.ice.controller.file.SegmentExcelParser;
import ch.ice.controller.file.SegmentExcelWriter;
import ch.ice.model.Segment;

public class SegmentationMain {
	public static String progressText;
	public static PropertiesConfiguration config = Config.PROPERTIES;
	public static void main(String[] args) throws IOException {
		
		SegmentExcelParser Parser = new SegmentExcelParser();
		SegmentExcelWriter writer = new SegmentExcelWriter();
		ListComparison Comparer = new ListComparison();

		ArrayList<Segment> listPOS = Parser.readPOSFile();
		ArrayList<Segment> listReg = Parser.readRegisterFile();
		
		
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
		System.out.println("Done");

	}
}
