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

/**
 * Parses ExcelFiles and writes them into ArrayLists
 * 
 * @author Mike
 *
 */
public class SegmentExcelParser {
	/**
	 * 
	 * @param row
	 *            the row to check
	 * @return if the row is empty
	 */
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
	// Customer Data
	private String companyNamePOS;
	private boolean exists = true;
	private String companyName;
	private String companySegment;
	private String unprocessedCompanyName;
	private String companyID;

	// Two ArrayLists of Segment that get returned
	private ArrayList<Segment> companiesPOS = new ArrayList<Segment>();
	private ArrayList<Segment> companiesRegister = new ArrayList<Segment>();

	// booleans used for REGEX
	private boolean removeSpecialCharacters;
	private boolean removeCapitalLetters = true;

	// Read from app.properties and set booleans
	public void initialize() {
		String removeSpecial = (String) config.getProperty("segmentation.removeSpecialCharakters");
		if (removeSpecial.equals("true")) {
			removeSpecialCharacters = true;
		} else {
			removeSpecialCharacters = false;
		}

	}

	/**
	 * 
	 * @param cell
	 *            the cell to check
	 * @return the type of Cell
	 */
	private String checkForCellType(Cell cell) {
		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			return cell.getStringCellValue().toString();
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return Double.toString(cell.getNumericCellValue());
		}
		return "";
	}

	/**
	 * Used to Create POS Customers
	 * 
	 * @return a new Segments
	 */
	private Segment createCustomer() {
		Segment customer = new Segment();
		customer.setExists(exists);
		customer.setCompanyName(this.companyNamePOS);
		customer.setId(this.companyID);
		customer.setUnprocessedCompanyName(this.unprocessedCompanyName);
		// GUI Data
		SegmentationMain.progressText = "Parsing Customer: " + this.companyNamePOS;
		return customer;

	}

	/**
	 * Used to create Industry Segment Objects
	 * 
	 * @return a new Segment
	 */
	private Segment createSegment() {
		Segment segment = new Segment();
		segment.setUnprocessedCompanyName(this.unprocessedCompanyName);
		segment.setCompanyName(this.companyName);
		segment.setCompanySegment(this.companySegment);
		// GUI Data
		SegmentationMain.progressText = "Parsing Company: " + this.companyNamePOS;
		return segment;

	}

	/**
	 * 
	 * @return ArrayList of POS Customers
	 * @throws IOException
	 */
	public ArrayList<Segment> readPOSFile() throws IOException {

		// Where the File has to be located
		InputStream ExcelFileToRead = new FileInputStream(SegmentationController.POSfilePath);

		// Standard POI Variables
		XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFRow row;
		XSSFCell cell;
		Iterator<Row> rows = sheet.rowIterator();
		//GUI Variable
		SegmentationMain.amountRows = sheet.getLastRowNum();

		// Iterates all Rows
		while (rows.hasNext()) {

			row = (XSSFRow) rows.next();
			// Skips the first two Rows
			if (row.getRowNum() == 0 || row.getRowNum() == 1 || row.getRowNum() == 2)
				continue;
			// checks if the row is Empty
			if (isRowEmpty(row)) {
				continue;
			}
			Iterator<Cell> cells = row.cellIterator();
			// Iterates all cells of a Row
			while (cells.hasNext()) {
				cell = (XSSFCell) cells.next();
				// Switch: 0 = ID; 6 = Customer Name
				switch (cell.getColumnIndex()) {
				case 0:
					// Checks if Cell Exists
					if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
						this.companyID = cell.getStringCellValue();
					} else {
						this.companyID = "CompanyID is not a String or does not exist";
					}
					break;
				case 6:
					// Checks if Cell exists
					if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
						this.companyNamePOS = "Customer Name does not exist";
						this.unprocessedCompanyName = this.companyNamePOS;
						exists = false;
						this.companiesPOS.add(this.createCustomer());
						exists = true;
						// Checks if Cell is String
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
					//GUI Variable
					SegmentationMain.currentRows = this.companiesPOS.size();
					break;
				}
			}
		}

		return companiesPOS;

	}

	/**
	 * 
	 * @return Industry Segmentation List
	 * @throws IOException
	 */
	public ArrayList<Segment> readRegisterFile() throws IOException {
		// Where the File has to be located
		
		InputStream ExcelFileToRead = new FileInputStream(SegmentationController.SegmentationFilePath);

		// Standard POI Variables
		XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFRow row;
		XSSFCell cell;
		Iterator<Row> rows = sheet.rowIterator();
		
		//GUI Variable
		SegmentationMain.amountRows = sheet.getLastRowNum();

		// Iterates every Row
		while (rows.hasNext()) {
			row = (XSSFRow) rows.next();
			Iterator<Cell> cells = row.cellIterator();
			// Iterates every Cell
			while (cells.hasNext()) {
				cell = (XSSFCell) cells.next();
				// Skips all Row's past the 2nd
				if (cell.getColumnIndex() >= 2)
					continue;
				// Switch: 0 = Segmentation; 1 = Company Name
				switch (cell.getColumnIndex()) {
				case 0:
					this.companySegment = this.checkForCellType(cell);
					break;
				case 1:
					this.companyName = this.checkForCellType(cell);
					this.unprocessedCompanyName = this.companyName;
					// REGEX
					if (removeSpecialCharacters) {
						this.companyName = this.companyName.replaceAll("[\\W]", "");
					}
					if (removeCapitalLetters) {
						this.companyName = this.companyName.toLowerCase();
					}
					break;
				}
			}
			// Add the Segment to the List
			this.companiesRegister.add(this.createSegment());
			//GUI Variable
			SegmentationMain.currentRows = this.companiesRegister.size();
		}
		return companiesRegister;
	}

}