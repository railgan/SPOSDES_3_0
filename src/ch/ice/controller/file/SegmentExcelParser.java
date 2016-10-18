package ch.ice.controller.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ch.ice.SegmentationMain;
import ch.ice.model.Segment;
import ch.ice.utils.Config;
import ch.ice.view.SegmentationController;

public class SegmentExcelParser {

	public static boolean isRowEmpty(XSSFRow row) {
		for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
			Cell cell = row.getCell(c);
			if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
				return false;
		}
		return true;
	}

	// Variables in which SCHURTER's List of Companies can be found
	public static PropertiesConfiguration config = Config.PROPERTIES;
	
	private String companyNamePOS;

	private ArrayList<Segment> companiesPOS = new ArrayList<Segment>();

	private ArrayList<Segment> companiesRegister = new ArrayList<Segment>();
	
	
	private boolean removeSpecialCharacters;
	private boolean removeCapitalLetters = true;
	
	private boolean exists = true;
	private String companyName;
	private String companySegment;
	private String unprocessedCompanyName;

	private String companyID;
	
	
	// Read from app.properties
	public void initialize(){
		String removeSpecial = (String) config.getProperty("segmentation.removeSpecialCharakters");
		if (removeSpecial.equals("true")){
			removeSpecialCharacters = true;
			}else{
				removeSpecialCharacters = false;	
			}
	
	}

	private String checkForCellType(Cell cell) {

		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			return cell.getStringCellValue().toString();
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return Double.toString(cell.getNumericCellValue());
		}
		return "";
	}

	private Segment createCustomer() {
		Segment customer = new Segment();
		customer.setExists(exists);
		customer.setCompanyName(this.companyNamePOS);
		customer.setId(this.companyID);
		customer.setUnprocessedCompanyName(this.unprocessedCompanyName);
		SegmentationMain.progressText = "Parsing Customer: " + this.companyNamePOS;
		return customer;

	}

	private Segment createSegment() {
		Segment segment = new Segment();
		segment.setUnprocessedCompanyName(this.unprocessedCompanyName);
		segment.setCompanyName(this.companyName);
		segment.setCompanySegment(this.companySegment);
		SegmentationMain.progressText = "Parsing Company: " + this.companyNamePOS;
		return segment;

	}

	public ArrayList<Segment> readPOSFile() throws IOException {

		// Where the Test file has to be located

		InputStream ExcelFileToRead = new FileInputStream(SegmentationController.POSfilePath);

		XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);

		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFRow row;
		XSSFCell cell;

		Iterator<Row> rows = sheet.rowIterator();

		while (rows.hasNext()) {

			row = (XSSFRow) rows.next();
			if (row.getRowNum() == 0 || row.getRowNum() == 1 || row.getRowNum() == 2)
				continue;
			if (isRowEmpty(row)) {
				continue;
			}
			if (row.getRowNum() == 0 || row.getRowNum() == 1 || row.getRowNum() == 2)
				continue;

			Iterator<Cell> cells = row.cellIterator();
			while (cells.hasNext()) {
				cell = (XSSFCell) cells.next();
				switch (cell.getColumnIndex()) {
				case 0:
					if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
						this.companyID = cell.getStringCellValue();
					} else {
						this.companyID = "CompanyID is not a String or does not exist";
					}
					break;
				case 6:

					if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
						this.companyNamePOS = "Customer Name does not exist";

						this.unprocessedCompanyName = this.companyNamePOS;
						exists = false;
						this.companiesPOS.add(this.createCustomer());
						exists = true;

					} else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {

						this.companyNamePOS = cell.getStringCellValue();
						this.unprocessedCompanyName = this.companyNamePOS;

						// Removes Special Characters and Whitespaces &
						// LowerCase
						if (removeSpecialCharacters) {
							this.companyNamePOS = this.companyNamePOS.replaceAll("[\\W]", "");
						}
						if (removeCapitalLetters) {
							this.companyNamePOS = this.companyNamePOS.toLowerCase();
						}
						this.companiesPOS.add(this.createCustomer());
					} else {
						this.companyNamePOS = "Customer Name is not a String";
						this.unprocessedCompanyName = this.companyNamePOS;
						exists = false;
						this.companiesPOS.add(this.createCustomer());
						exists = true;
					}
					break;
				}
			}
		}
		
		System.out.println("POS File Read");
		return companiesPOS;

	}

	public ArrayList<Segment> readRegisterFile() throws IOException {
		// Where the Test file has to be located
		InputStream ExcelFileToRead = new FileInputStream("C:/Javatest/Medical.xlsx");
		XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFRow row;
		XSSFCell cell;

		Iterator<Row> rows = sheet.rowIterator();

		while (rows.hasNext()) {
			row = (XSSFRow) rows.next();
			Iterator<Cell> cells = row.cellIterator();
			while (cells.hasNext()) {
				cell = (XSSFCell) cells.next();
				if (cell.getColumnIndex() >= 2)
					continue;

				switch (cell.getColumnIndex()) {
				case 0:
					this.companySegment = this.checkForCellType(cell);

					break;

				case 1:
					this.companyName = this.checkForCellType(cell);
					this.unprocessedCompanyName = this.companyName;
					if (removeSpecialCharacters) {
						this.companyName = this.companyName.replaceAll("[\\W]", "");
					}
					if (removeCapitalLetters) {
						this.companyName = this.companyName.toLowerCase();
					}
					break;

				}

			}

			this.companiesRegister.add(this.createSegment());

		}
		System.out.println("Registry File read");
		return companiesRegister;
	}

}