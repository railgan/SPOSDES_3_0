package ch.ice.compare;

import java.util.ArrayList;

import ch.ice.controller.file.SegmentExcelParser;
import ch.ice.model.Segment;

public class ListComparison {
	public String posCompany;
	public String posSegment;
	public String regCompany;
	public String companySegment;
	public String companyName;
	public String unprocessedCompanyName;
	
	public Segment regSegment;

	
	public int medical;
	public int othercompanies;
	public double stringDistance;
	public double minStringDistance = 1;
	public int indexOfBestResult;
	
	public ArrayList<Segment> segmentedList = new ArrayList<Segment>();
	public ArrayList<String> regList = new ArrayList<String>();
	
	public SegmentExcelParser Parser = new SegmentExcelParser();
	
	public JaroWinkler jaroWinkelr = new JaroWinkler();
	public Segment singleSegment = new Segment();
	
	private Segment createLevensteinSegment(){
		Segment singleSegment = new Segment();
		
		singleSegment.setCompanyName(this.companyName);
		singleSegment.setLevenDistance(minStringDistance);
		singleSegment.setCompanySegment(this.companySegment);
		singleSegment.setUnprocessedCompanyName(this.unprocessedCompanyName);
		return singleSegment;
	
	}
	
	public   ArrayList<Segment> compareLists(ArrayList<Segment> Register, ArrayList<String> POS){
		regList = readCompanyName(Register);
	
		
		System.out.println("Now we are comparing Lists");
		for (int i = 0; i < POS.size(); i++) {
			
			posCompany = (POS.get(i));
				for (int k = 0; k<Register.size(); k++){
					
					stringDistance = jaroWinkelr.distance(posCompany, Register.get(k).getCompanyName());
					if(stringDistance < minStringDistance){
						minStringDistance = stringDistance;
						indexOfBestResult = k;
						if(minStringDistance == 0){
							this.companyName =Register.get(indexOfBestResult).getCompanyName();
							this.companySegment = Register.get(indexOfBestResult).getCompanySegment();
							this.unprocessedCompanyName = Register.get(indexOfBestResult).getUnprocessedCompanyName();
							if (minStringDistance <= 0.01){
								medical++;
							}
							segmentedList.add(i,this.createLevensteinSegment());
							minStringDistance = 1;
							break;
						}
						
					}
					if (k == Register.size()-1){
						this.companyName =Register.get(indexOfBestResult).getCompanyName();
						this.companySegment = Register.get(indexOfBestResult).getCompanySegment();
						this.unprocessedCompanyName = Register.get(indexOfBestResult).getUnprocessedCompanyName();
						if (minStringDistance <= 0.01){
							medical++;
						}
						segmentedList.add(i,this.createLevensteinSegment());
						minStringDistance = 1;
					}
				
				}		
		
		}
	
		System.out.println(
				"Amount Medical: " + medical + 
				";\nAmount Other: " + othercompanies +
				";\nRegister Size: " + Register.size() +
				";\nTotal Amount of Companies: " + POS.size() +
				";\nSegmented List size: " +segmentedList.size()
				);
	
		
		return segmentedList;
	}
		public   ArrayList<String> readCompanyName(ArrayList<Segment> Register){
			for (int j = 0; j < Register.size(); j++) {
				regCompany = (Register.get(j).getCompanyName());
				regList.add(j, regCompany);
				
			}
			System.out.println("CompanyNamesRead");
			
			return regList;
			
}
}

