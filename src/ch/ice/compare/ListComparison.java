package ch.ice.compare;

import java.util.ArrayList;

import ch.ice.SegmentationMain;
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

	public int dublicates;
	public int dublicates2;
	public int medical;
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

	public ArrayList<Segment> compareLists(ArrayList<Segment> Register, ArrayList<Segment> listPos) {
		regList = readCompanyName(Register);

		System.out.println("Now we are comparing Lists");
		for (int i = 0; i < listPos.size(); i++) {

			posCompany = (listPos.get(i).getCompanyName());
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

					if (minStringDistance <= 0.01) {
						medical++;
					}
					segmentedList.add(i, this.createSingleSegment());
					minStringDistance = 1;
				}

			}

		}

		System.out.println("Amount Segmented: " + medical + ";\nAmount Unsegmented: " + othercompanies
				+ ";\nRegister Size: " + Register.size() + ";\nTotal Amount of Companies: " + listPos.size()
				+ ";\nSegmented List size: " + segmentedList.size());

		return segmentedList;
	}

	public ArrayList<String> readCompanyName(ArrayList<Segment> Register) {
		for (int j = 0; j < Register.size(); j++) {
			regCompany = (Register.get(j).getCompanyName());
			regList.add(j, regCompany);

		}

		System.out.println("CompanyNamesRead");

		return regList;

	}

	public ArrayList<Segment> deDuplicate(ArrayList<Segment> customers, ArrayList<Segment> segmentedCustomers) {
		int d = 1;
		for (int c = 0; c < customers.size() - 1; c = d) {
			SegmentationMain.progressText = "Detecting Duplicats: " + (customers.get(c).getUnprocessedCompanyName());
			d = c + 1;
			while (customers.get(c).getId().equals(customers.get(d).getId())) {
				if (segmentedCustomers.get(c).getLevenDistance() <= segmentedCustomers.get(d).getLevenDistance()) {
					customers.get(c).setUnprocessedCompanyName(customers.get(c).getUnprocessedCompanyName() + ", "
							+ customers.get(d).getUnprocessedCompanyName());
					segmentedCustomers.get(c).setCompanyName(customers.get(c).getUnprocessedCompanyName());					
					segmentedCustomers.get(c).setNewCompanyName(true);
					segmentedCustomers.get(d).setDublicate(true);
					d++;
					dublicates++;
					
					
				} else {
					customers.get(d).setUnprocessedCompanyName(customers.get(d).getUnprocessedCompanyName() + ", "
							+ customers.get(c).getUnprocessedCompanyName());
					segmentedCustomers.get(d).setCompanyName(customers.get(d).getUnprocessedCompanyName());
					dublicates2++;
					segmentedCustomers.get(d).setNewCompanyName(true);
					segmentedCustomers.get(c).setDublicate(true);
					break;
				}

			}
		}
		System.out.println("Top down Duplicates count: " + dublicates + "\n Bottm up Duplicates count: " + dublicates2);
		return segmentedCustomers;

	}

}
