	package ch.ice;

import java.io.IOException;
import java.util.ArrayList;

import ch.ice.compare.ListComparison;
import ch.ice.controller.file.SegmentExcelParser;
import ch.ice.controller.file.SegmentExcelWriter;
import ch.ice.model.Segment;

public class SegmentationMain {

	public static void main(String[] args) throws IOException {
			
			
			
			SegmentExcelParser Parser = new SegmentExcelParser();
			ListComparison Comparer = new ListComparison();
			SegmentExcelWriter Writer = new SegmentExcelWriter();
			
			ArrayList<String> ListPos2 = Parser.readPOSFile();
			ArrayList<Segment> ListReg2 = Parser.readRegisterFile();
			
			ArrayList<Segment> ListSegmented= Comparer.compareLists(ListReg2, ListPos2);
			
			System.out.println("Time to Segment");
		
			Writer.writeXLSXFile(ListSegmented);
			System.out.println("Done");
			
			
			
	}
}
