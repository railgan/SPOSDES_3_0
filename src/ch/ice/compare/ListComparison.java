package ch.ice.compare;

import java.util.ArrayList;

import ch.ice.SegmentationMain;
import ch.ice.controller.file.SegmentExcelParser;
import ch.ice.model.Segment;
import ch.ice.utils.Config;
import org.apache.commons.configuration.PropertiesConfiguration;

public class ListComparison {
	public String posCompany;
	public String posSegment;
	public String regCompany;
	public String companySegment;
	public String companyName;
	public String unprocessedCompanyName;

	public Segment regSegment;

	
	public int dublicates;
	public int dublicates2;
	public int segmented;
	public int othercompanies;

	public double stringDistance;
	public double minStringDistance = 1;
	public int indexOfBestResult;
	public boolean exists;

	public ArrayList<Segment> segmentedList = new ArrayList<Segment>();
	public ArrayList<String> regList = new ArrayList<String>();

	public SegmentExcelParser Parser = new SegmentExcelParser();

	public JaroWinkler jaroWinkelr = new JaroWinkler();
	public Segment singleSegment = new Segment();

	private Segment createSingleSegment() {
		Segment singleSegment = new Segment();
		singleSegment.setCompanyName(this.companyName);
		singleSegment.setLevenDistance(minStringDistance);
		singleSegment.setCompanySegment(this.companySegment);
		singleSegment.setUnprocessedCompanyName(this.unprocessedCompanyName);
		singleSegment.setExists(exists);
		return singleSegment;

	}
	public static PropertiesConfiguration config = Config.PROPERTIES;
	
	public ArrayList<Segment> compareLists(ArrayList<Segment> Register, ArrayList<Segment> listPos) {
		
		String margin = (String) config.getProperty("segmentation.segmentmargin");
		
		double segmentMargain =  Double.parseDouble(margin);
		regList = readCompanyName(Register);

		for (int i = 0; i < listPos.size(); i++) {

			posCompany = (listPos.get(i).getCompanyName());
			SegmentationMain.progressPercent = 0.3/listPos.size()*i+0.04;
			SegmentationMain.progressText = "Comparing: " + posCompany;
			exists = listPos.get(i).isExists();
			if (exists == false) {
				this.companyName = posCompany;
				this.companySegment = "None";
				this.unprocessedCompanyName = listPos.get(i).getUnprocessedCompanyName();
				segmentedList.add(i, this.createSingleSegment());
				continue;
			}
			for (int k = 0; k < Register.size(); k++) {

				stringDistance = jaroWinkelr.distance(posCompany, Register.get(k).getCompanyName());
				if (stringDistance < minStringDistance) {
					minStringDistance = stringDistance;
					indexOfBestResult = k;
				}
				if (k == Register.size() - 1) {
					this.companyName = Register.get(indexOfBestResult).getCompanyName();
					this.companySegment = Register.get(indexOfBestResult).getCompanySegment();
					this.unprocessedCompanyName = Register.get(indexOfBestResult).getUnprocessedCompanyName();

					if (minStringDistance <= segmentMargain) {
						segmented++;
					}
					segmentedList.add(i, this.createSingleSegment());
					minStringDistance = 1;
				}

			}

		}

		
		return segmentedList;
	}

	public ArrayList<String> readCompanyName(ArrayList<Segment> Register) {
		for (int j = 0; j < Register.size(); j++) {
			regCompany = (Register.get(j).getCompanyName());
			regList.add(j, regCompany);

		}

		return regList;

	}

	public ArrayList<Segment> deDuplicate(ArrayList<Segment> customers, ArrayList<Segment> segmentedCustomers) {
		int d = 1;
		for (int c = 0; c < customers.size()-1; c = d) {
			SegmentationMain.progressPercent = 0.15/customers.size()*c+0.35;
			SegmentationMain.progressText = "Detecting Duplicats: " + (customers.get(c).getUnprocessedCompanyName());
			d = c + 1;
			while (customers.get(c).getId().equals(customers.get(d).getId())) {
				if (segmentedCustomers.get(c).getLevenDistance() <= segmentedCustomers.get(d).getLevenDistance()) {
					customers.get(c).setUnprocessedCompanyName(customers.get(c).getUnprocessedCompanyName() + ", "
							+ customers.get(d).getUnprocessedCompanyName());
					segmentedCustomers.get(c).setCompanyName(customers.get(c).getUnprocessedCompanyName());					
					segmentedCustomers.get(c).setNewCompanyName(true);
					segmentedCustomers.get(d).setDublicate(true);
					if(d == customers.size()-1){
						break;
					}
					d++;
					dublicates++;
					
					
				} else {
					customers.get(d).setUnprocessedCompanyName(customers.get(d).getUnprocessedCompanyName() + ", "
							+ customers.get(c).getUnprocessedCompanyName());
					segmentedCustomers.get(d).setCompanyName(customers.get(d).getUnprocessedCompanyName());
					dublicates++;
					segmentedCustomers.get(d).setNewCompanyName(true);
					segmentedCustomers.get(c).setDublicate(true);
					break;
				}

			}
		}
		SegmentationMain.amountDuplicate = dublicates;
		return segmentedCustomers;

	}

}
