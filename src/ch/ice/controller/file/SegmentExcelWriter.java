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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ch.ice.SegmentationMain;
import ch.ice.model.Segment;
import ch.ice.utils.Config;
import ch.ice.view.SegmentationController;

public class SegmentExcelWriter {

	DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH");
	
	public static PropertiesConfiguration config = Config.PROPERTIES;

	Date date = new Date();

	public void writeXLSXFile(ArrayList<Segment> segmentCustomerList) throws IOException {

		String excelFileName = SegmentationController.saveToDirectoryPath+"/Segmented"+dateFormat.format(date)+".xlsx";
		String oldExcelFile = SegmentationController.POSfilePath;
		

		int cellnum;
		int rownum;
		String margin = (String) config.getProperty("segmentation.segmentmargin");
		double segmentMargain =  Double.parseDouble(margin);
		double levenDistance;
		
		InputStream inp = new FileInputStream(oldExcelFile);
		XSSFWorkbook wb = new XSSFWorkbook(inp);
		
		XSSFSheet sheet = wb.getSheetAt(0);
		

		rownum = 3;
		cellnum = 11;

		XSSFRow row = sheet.getRow(2);
		XSSFCell cell = sheet.getRow(row.getRowNum()).createCell(11);
		cell.setCellValue("Segment");
		cell = sheet.getRow(row.getRowNum()).createCell(12);
		cell.setCellValue("Distance");
		cell = sheet.getRow(row.getRowNum()).createCell(13);
		cell.setCellValue("Comparison Name");

		for (Segment object : segmentCustomerList){
			SegmentationMain.progressPercent = 0.15/segmentCustomerList.size()*rownum+0.5;
			row = sheet.getRow(rownum++);
			if (object.isExists()) {
				if (object.getLevenDistance() <= segmentMargain) {
					cell.setCellValue(object.getCompanySegment());
				} else {
					cell.setCellValue("other");
				}

				if (object.isDublicate()) {
					SegmentationMain.progressText = "Removing Duplicate: "+ object.getCompanyName();
					
					sheet.removeRow(row);
					continue;
				}
			}
		}
		rownum = 3;
			
		for (Segment object : segmentCustomerList){
			SegmentationMain.progressPercent = 0.15/segmentCustomerList.size()*rownum+0.65;
			row = sheet.getRow(rownum++);
		if (isRowEmpty(row)) {
		SegmentationMain.progressText = "Removing empty Row: "+ rownum;
	
			int lastRowNum = sheet.getLastRowNum();
			if (rownum < lastRowNum) {
				sheet.shiftRows(rownum, lastRowNum, -1);
				rownum--;
			}
		}
		}
		rownum = 3;
		

		rownum = 3;

		// iterating r number of rows
		for (Segment object : segmentCustomerList) {
			SegmentationMain.progressPercent = 0.15/segmentCustomerList.size()*rownum+0.8;
			SegmentationMain.progressText = "Writing Customer: "+ object.getCompanyName();
			if (object.isDublicate()) {
				continue;
			}
			levenDistance = object.getLevenDistance();
			row = sheet.getRow(rownum++);
			cell = sheet.getRow(row.getRowNum()).createCell(cellnum);

			if (object.isExists()) {
				if (object.getLevenDistance() <= segmentMargain) {
					cell.setCellValue(object.getCompanySegment());
				} else {
					cell.setCellValue("other");
				}

				
				if (object.isNewCompanyName()) {
					cell = sheet.getRow(row.getRowNum()).createCell(6);
					cell.setCellValue(object.getCompanyName());
				}

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
		SegmentationMain.progressText = "Formatting the Sheet";
		for (int i = 0; i < 20; i++)
			sheet.autoSizeColumn(i);
		sheet.setColumnWidth(6, 2000);
		sheet.setColumnWidth(8, 2000);
		sheet.setColumnWidth(9, 2000);
		sheet.setColumnWidth(10, 2000);
		SegmentationMain.progressText = "Writing File";
		

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		
		fileOut.flush();
		fileOut.close();
		SegmentationMain.progressText = "Done";

	}

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
