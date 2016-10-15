package ch.ice.controller.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ch.ice.model.Segment;
import ch.ice.view.SegmentationController;

public class SegmentExcelWriter {

	DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH");

	Date date = new Date();

	public void writeXLSXFile(ArrayList<Segment> segmentCustomerList) throws IOException {

		String excelFileName = SegmentationController.saveToDirectoryPath+"/Segmented"+dateFormat.format(date)+".xlsx";
		String oldExcelFile = SegmentationController.POSfilePath;
		

		int cellnum;
		int rownum;
		double segmentMargain = 0.01;
		double levenDistance;

		InputStream inp = new FileInputStream(oldExcelFile);
		XSSFWorkbook wb = new XSSFWorkbook(inp);
		System.out.println("ExcelFileRead");
		XSSFSheet sheet = wb.getSheetAt(0);
		System.out.println("found wb Sheet");

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
			row = sheet.getRow(rownum++);
			if (object.isExists()) {
				if (object.getLevenDistance() <= segmentMargain) {
					cell.setCellValue(object.getCompanySegment());
				} else {
					cell.setCellValue("other");
				}

				if (object.isDublicate()) {
					System.out.println("Dublicate found: " +object.getCompanyName());
					sheet.removeRow(row);
					continue;
				}
			}
		}
		System.out.println("Duplicates deleted");
		rownum = 3;
			
		for (Segment object : segmentCustomerList){
			row = sheet.getRow(rownum++);
		if (isRowEmpty(row)) {
			int lastRowNum = sheet.getLastRowNum();
			if (rownum < lastRowNum) {
				sheet.shiftRows(rownum, lastRowNum, -1);
				rownum--;
			}
		}
		}
		rownum = 3;
		System.out.println("Empty Row's removed");
		
		
		
		
		rownum = 3;

		// iterating r number of rows
		for (Segment object : segmentCustomerList) {
			
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

		for (int i = 0; i < 20; i++)
			sheet.autoSizeColumn(i);
		sheet.setColumnWidth(6, 2000);
		sheet.setColumnWidth(8, 2000);
		sheet.setColumnWidth(9, 2000);
		sheet.setColumnWidth(10, 2000);

		System.out.println("Ready for fileOut");

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		
		fileOut.flush();
		fileOut.close();

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
