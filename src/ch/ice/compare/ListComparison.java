package ch.ice.compare;

import java.util.ArrayList;

import ch.ice.SegmentationMain;
import ch.ice.controller.file.SegmentExcelParser;
import ch.ice.model.Segment;
import ch.ice.utils.Config;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * compares Strings and deduplicates lists
 * 
 * @author Mike
 * 
 */
public class ListComparison {
	// Variables used to save Customer information
	public String posCompany;
	public String posSegment;
	public String regCompany;
	public String companySegment;
	public String companyName;
	public String unprocessedCompanyName;
	public Segment regSegment;

	// Filler Data Integers. Mostly used to display in GUI
	public int dublicates;
	public int segmented;
	public int othercompanies;

	// Variables used for Jaro Winkler
	public double stringDistance;
	public double minStringDistance = 1;
	public int indexOfBestResult;
	public boolean exists;

	// List of customers
	public ArrayList<Segment> segmentedList = new ArrayList<Segment>();

	// List of company Names
	public ArrayList<String> regList = new ArrayList<String>();

	// other Classes used
	public SegmentExcelParser Parser = new SegmentExcelParser();
	public JaroWinkler jaroWinkelr = new JaroWinkler();
	public Segment singleSegment = new Segment();

	/**
	 * @return a new Segmented Customer
	 */
	private Segment createSingleSegment() {
		Segment singleSegment = new Segment();
		singleSegment.setCompanyName(this.companyName);
		singleSegment.setLevenDistance(minStringDistance);
		singleSegment.setCompanySegment(this.companySegment);
		singleSegment.setUnprocessedCompanyName(this.unprocessedCompanyName);
		singleSegment.setExists(exists);
		return singleSegment;

	}
	// needed to read data from GUI

	public static PropertiesConfiguration config = Config.PROPERTIES;

	/**
	 * 
	 * @param Register
	 *            the Industry Segmentation File
	 * @param listPos
	 *            SCHURTER's POS Data
	 * @return a Segmented List of Customers
	 */
	public ArrayList<Segment> compareLists(ArrayList<Segment> Register, ArrayList<Segment> listPos) {
		// Decided when a String is similar

		String margin = (String) config.getProperty("segmentation.segmentmargin");
		double segmentMargain = Double.parseDouble(margin);
		// Reads all names from Register into a seperate ArrayList

		regList = readCompanyName(Register);
		//GUI Variable
		SegmentationMain.amountRows = listPos.size();

		// goes through every POS Customer
		for (int i = 0; i < listPos.size(); i++) {
			//GUI Variable
			SegmentationMain.currentRows = i+1;
			// the current customer
			posCompany = (listPos.get(i).getCompanyName());
			// GUI Display Info
			SegmentationMain.progressPercent = 0.3 / listPos.size() * i + 0.04;
			SegmentationMain.progressText = "Comparing: " + posCompany;

			// checks if the customer exists
			exists = listPos.get(i).isExists();

			// if it doesn't exist
			if (exists == false) {
				this.companyName = posCompany;
				// leaves the segment as None
				this.companySegment = "None";
				this.unprocessedCompanyName = listPos.get(i).getUnprocessedCompanyName();
				// add's the customer to the list
				segmentedList.add(i, this.createSingleSegment());
				// next customer
				continue;
			}
			// if it exists, goes through the Register to find the best math
			for (int k = 0; k < Register.size(); k++) {
				// obtains jaro winkler distance for each string pair
				stringDistance = jaroWinkelr.distance(posCompany, Register.get(k).getCompanyName());
				// checks if the result is better
				if (stringDistance < minStringDistance) {
					// saves best result
					minStringDistance = stringDistance;
					indexOfBestResult = k;
				}
				// checks if the end of the list is reached
				if (k == Register.size() - 1) {
					// Creates a new Segmented Object
					this.companyName = Register.get(indexOfBestResult).getCompanyName();
					this.companySegment = Register.get(indexOfBestResult).getCompanySegment();
					this.unprocessedCompanyName = Register.get(indexOfBestResult).getUnprocessedCompanyName();
					// Used for GUI Display
					if (minStringDistance <= segmentMargain) {
						SegmentationMain.amountSegmented++;
					}
					// Add's the new object to the list
					segmentedList.add(i, this.createSingleSegment());
					// Resets variable
					minStringDistance = 1;
				}

			}

		}

		return segmentedList;
	}

	/**
	 * 
	 * @param Register
	 *            the Industry Segmentation File
	 * @return a list of Company Names as String
	 */
	public ArrayList<String> readCompanyName(ArrayList<Segment> Register) {
		for (int j = 0; j < Register.size(); j++) {
			regCompany = (Register.get(j).getCompanyName());
			regList.add(j, regCompany);

		}

		return regList;

	}

	/**
	 * 
	 * @param customers
	 *            SCHURTER's POS Data
	 * @param segmentedCustomers
	 *            the Segmented Customer Data
	 * @return a segmented & deduplicated Customer List
	 */
	public ArrayList<Segment> deDuplicate(ArrayList<Segment> customers, ArrayList<Segment> segmentedCustomers) {
		// initialize d
		int d = 1;
		//GUI Variable
		SegmentationMain.amountRows = customers.size();
		
		// goes through all POS Customer
		for (int c = 0; c < customers.size() - 1; c = d) {
			//GUI Variable
			SegmentationMain.currentRows = c+1;
			// GUI Text
			SegmentationMain.progressPercent = 0.15 / customers.size() * c + 0.35;
			SegmentationMain.progressText = "Detecting Duplicats: " + (customers.get(c).getUnprocessedCompanyName());
			// Set's d to always be the next customer
			d = c + 1;
			// Checks if the ID's of customer c and d are the same
			while (customers.get(c).getId().equals(customers.get(d).getId())) {
				// Checks which customer has the better segmentation result
				if (segmentedCustomers.get(c).getLevenDistance() <= segmentedCustomers.get(d).getLevenDistance()) {
					// Add's d's name to c
					customers.get(c).setUnprocessedCompanyName(customers.get(c).getUnprocessedCompanyName() + ", "
							+ customers.get(d).getUnprocessedCompanyName());
					segmentedCustomers.get(c).setCompanyName(customers.get(c).getUnprocessedCompanyName());
					// Variables used in write to remove / write customers
					segmentedCustomers.get(c).setNewCompanyName(true);
					segmentedCustomers.get(d).setDublicate(true);
					// Checks if the end of the file has been reached
					if (d == customers.size() - 1) {
						break;
					}
					// next
					d++;
					// GUI data
					SegmentationMain.amountDuplicate++;

				} else {
					// Add's c's name to d
					customers.get(d).setUnprocessedCompanyName(customers.get(d).getUnprocessedCompanyName() + ", "
							+ customers.get(c).getUnprocessedCompanyName());
					segmentedCustomers.get(d).setCompanyName(customers.get(d).getUnprocessedCompanyName());
					// GUI Data
					SegmentationMain.amountDuplicate++;
					/// Variables used in write to remove / write customers
					segmentedCustomers.get(d).setNewCompanyName(true);
					segmentedCustomers.get(c).setDublicate(true);
					break;
				}

			}
		}
		// GUI Data
		return segmentedCustomers;

	}

}
