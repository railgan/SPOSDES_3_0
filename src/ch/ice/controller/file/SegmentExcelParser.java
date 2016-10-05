package ch.ice.controller.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ch.ice.model.Segment;

public class SegmentExcelParser {

	//Variables in which SCHURTER's List of Companies can be found
	private   String companyNamePOS;
	private   ArrayList<String> companiesPOS = new ArrayList<String>();
	
	private   ArrayList<Segment> companiesRegister = new ArrayList<Segment>();
	
	private   boolean removeSpecialCharacters = false;
	private   boolean removeCapitalLetters = true;
	
	private String companyName;
	private String companySegment;
	private String unprocessedCompanyName;
	
	private Segment createSegment(){
		Segment segment = new Segment();
		segment.setUnprocessedCompanyName(this.unprocessedCompanyName);
		segment.setCompanyName(this.companyName);
		segment.setCompanySegment(this.companySegment);
		return segment;
	
	}
	
	
	public   ArrayList<String> readPOSFile() throws IOException
	{
		
		//Where the Test file has to be located
		InputStream ExcelFileToRead = new FileInputStream("C:/Javatest/POS.xlsx");
		XSSFWorkbook  wb = new XSSFWorkbook(ExcelFileToRead);
		
		XSSFWorkbook test = new XSSFWorkbook(); 
		
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFRow row; 
		XSSFCell cell;

		Iterator rows = sheet.rowIterator();

		while (rows.hasNext())
		{
			row=(XSSFRow) rows.next();
			if (row.getRowNum() == 0 || row.getRowNum() == 1 || row.getRowNum() == 3)
				continue;
			Iterator cells = row.cellIterator();
			while (cells.hasNext())
			{
				cell=(XSSFCell) cells.next();
				if(cell.getColumnIndex()==6){
		
				if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
				{
					companyNamePOS = cell.getStringCellValue();
					
					
					//Removes Special Characters and Whitespaces & LowerCase
					if(removeSpecialCharacters){
						companyNamePOS = companyNamePOS.replaceAll("[\\W]","");
					}
					if(removeCapitalLetters){
					companyNamePOS = companyNamePOS.toLowerCase();
					}
					companiesPOS.add(companyNamePOS);
				}
				else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
				{
					System.out.print(cell.getNumericCellValue()+" ");
				}
				else
				{
					//U Can Handel Boolean, Formula, Errors
				}
			}
		 
		}
		}
		System.out.println("POS File Read");
		return companiesPOS;
		
	}
	
	
	
	public  ArrayList<Segment> readRegisterFile() throws IOException
	{
		//Where the Test file has to be located
		InputStream ExcelFileToRead = new FileInputStream("C:/Javatest/Medical.xlsx");
		XSSFWorkbook  wb = new XSSFWorkbook(ExcelFileToRead);
		
		XSSFWorkbook test = new XSSFWorkbook(); 
		
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFRow row; 
		XSSFCell cell;
				
				Iterator rows = sheet.rowIterator();

				while (rows.hasNext())
				{ 
					row=(XSSFRow) rows.next();
					Iterator cells = row.cellIterator();
					while (cells.hasNext())
					{
						cell=(XSSFCell) cells.next();
						if (cell.getColumnIndex() >= 2)
							continue;
						
						switch (cell.getColumnIndex()) {
						case 0:
							this.companySegment = this.checkForCellType(cell);
							

							break;

							
						case 1:
							this.companyName = this.checkForCellType(cell);
							this.unprocessedCompanyName = this.companyName;
							if(removeSpecialCharacters){
								this.companyName = this.companyName.replaceAll("[\\W]","");
							}
							if(removeCapitalLetters){
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
	
	private String checkForCellType(Cell cell) {
		
		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			return cell.getStringCellValue().toString();
		}
		else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return Double.toString(cell.getNumericCellValue());
		}
		return "";
	}



}