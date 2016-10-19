package ch.ice.controller.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ch.ice.SegmentationMain;
import ch.ice.model.Segment;
import ch.ice.utils.Config;
import ch.ice.view.SegmentationController;

/**
 * Writes the Segmented List into a new XLSX File
 * 
 * @author Mike
 *
 */
public class SegmentExcelWriter {
	// dateFormat for Output Filename
	DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH");
	// For GUI Parsing
	public static PropertiesConfiguration config = Config.PROPERTIES;
	
	public static String segmentedFilePath;
	// Actual Date
	Date date = new Date();

	/**
	 * Writes the Segmented List into a new Excel File
	 * 
	 * @param segmentCustomerList
	 *            the Segmented List
	 * @throws IOException
	 */
	public void writeXLSXFile(ArrayList<Segment> segmentCustomerList) throws IOException {

		// The new and old Excelfile name parsed by the GUI
		String excelFileName = SegmentationController.saveToDirectoryPath + "/Segmented" + dateFormat.format(date)
				+ ".xlsx";
		segmentedFilePath = excelFileName;
		String oldExcelFile = SegmentationController.POSfilePath;

		// Integer for row/cell finding
		int cellnum;
		int rownum;
		// Calculation of Segmentation
		String margin = (String) config.getProperty("segmentation.segmentmargin");
		double segmentMargain = Double.parseDouble(margin);
		double levenDistance;
		// Standard POI Variables
		InputStream inp = new FileInputStream(oldExcelFile);
		XSSFWorkbook wb = new XSSFWorkbook(inp);
		XSSFSheet sheet = wb.getSheetAt(0);

		// Start in Row 2; Cell K;
		rownum = 3;
		cellnum = 11;

		// Write Header
		XSSFRow row = sheet.getRow(2);
		XSSFCell cell = sheet.getRow(row.getRowNum()).createCell(11);
		cell.setCellValue("Segment");
		cell = sheet.getRow(row.getRowNum()).createCell(12);
		cell.setCellValue("Distance");
		cell = sheet.getRow(row.getRowNum()).createCell(13);
		cell.setCellValue("Comparison Name");
		//GUI Variable
		SegmentationMain.amountRows = segmentCustomerList.size();

		for (Segment object : segmentCustomerList) {
			// GUI Display of progressbar
			SegmentationMain.progressPercent = 0.15 / segmentCustomerList.size() * rownum + 0.5;
			//GUI Variable
			SegmentationMain.currentRows = rownum-2;
			// Get's current row
			row = sheet.getRow(rownum++);
			// Checks if the Object exists (not empty / null)
			if (object.isExists()) {
				if (object.getLevenDistance() <= segmentMargain) {
					cell.setCellValue(object.getCompanySegment());
				} else {
					cell.setCellValue("other");
				}
				// Checks if Object is a duplicate and needs to be removed
				if (object.isDublicate()) {
					// GUI Display of Progress Text
					SegmentationMain.progressText = "Removing Duplicate: " + object.getCompanyName();
					// Empties a Row
					sheet.removeRow(row);
					continue;
				}
			}
		}
		// Reset Variable
		rownum = 3;

		// Iterate the list for every object
		for (Segment object : segmentCustomerList) {
			// GUI Display of Progressbar
			SegmentationMain.progressPercent = 0.15 / segmentCustomerList.size() * rownum + 0.65;
			//GUI Variable
			SegmentationMain.currentRows = rownum-2;
			// Get's current Row
			row = sheet.getRow(rownum++);
			// Checks if row is empty
			if (isRowEmpty(row)) {
				// GUI Display of Progress Text
				SegmentationMain.progressText = "Removing empty Row: " + rownum;

				// Get's the Last row
				int lastRowNum = sheet.getLastRowNum();
				// has the end been reached?
				if (rownum < lastRowNum) {
					// Moves all Row's one up
					sheet.shiftRows(rownum, lastRowNum, -1);
					rownum--;
				}
			}
		}
		// Reset
		rownum = 3;

		// Iterated every object and writes them
		for (Segment object : segmentCustomerList) {
			// GUI Display
			SegmentationMain.progressPercent = 0.15 / segmentCustomerList.size() * rownum + 0.8;
			SegmentationMain.progressText = "Writing Customer: " + object.getCompanyName();
			SegmentationMain.currentRows = rownum-2;
			// Checks if object is duplicate and skips them
			if (object.isDublicate()) {
				continue;
			}
			// Gets the objects Distance
			levenDistance = object.getLevenDistance();
			// Gets the current row
			//GUI Variable
			
			row = sheet.getRow(rownum++);
			cell = sheet.getRow(row.getRowNum()).createCell(cellnum);

			// Checks if the object exists
			if (object.isExists()) {
				// Gets the company segment if needed
				if (object.getLevenDistance() <= segmentMargain) {
					cell.setCellValue(object.getCompanySegment());
				} else {
					cell.setCellValue("other");
				}
				// Checks if the company name changed
				if (object.isNewCompanyName()) {
					cell = sheet.getRow(row.getRowNum()).createCell(6);
					cell.setCellValue(object.getCompanyName());
				}
				// Writes the changed Data into the Cells
				cell = sheet.getRow(row.getRowNum()).createCell(cellnum + 1);
				cell.setCellValue(levenDistance);
				cell = sheet.getRow(row.getRowNum()).createCell(cellnum + 2);
				cell.setCellValue(object.getUnprocessedCompanyName());
			} else {
				cell.setCellValue(" ");
				cell = sheet.getRow(row.getRowNum()).createCell(cellnum + 1);
				cell.setCellValue(" ");
				cell = sheet.getRow(row.getRowNum()).createCell(cellnum + 2);
				cell.setCellValue("no company name provided");
			}
		}
		// Formats the Excel Sheet
		SegmentationMain.progressText = "Formatting the Sheet";
		for (int i = 0; i < 20; i++)
			sheet.autoSizeColumn(i);
		sheet.setColumnWidth(6, 2000);
		sheet.setColumnWidth(8, 2000);
		sheet.setColumnWidth(9, 2000);
		sheet.setColumnWidth(10, 2000);
		SegmentationMain.progressText = "Writing File";

		// Writes the File
		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);

		fileOut.flush();
		fileOut.close();
		SegmentationMain.progressText = "Done";

	}

	/**
	 * @row the row to checked if it's empty
	 * @return whether the Row is empty or not
	 */
	public static boolean isRowEmpty(XSSFRow row) {

		if (row == null) {
			return true;
		} else if (row.getLastCellNum() <= 0) {
			return true;
		} else {
			return false;
		}
	}
}
