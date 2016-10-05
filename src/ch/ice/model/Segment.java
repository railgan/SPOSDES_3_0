package ch.ice.model;

public class Segment {

	public  String companySegment;
	public  String companyName;
	public String unprocessedCompanyName;
	public  double levenDistance;
	
	
	
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanySegment() {
		return companySegment;
	}
	public void setCompanySegment(String companySegment) {
		this.companySegment = companySegment;
	}
	public double getLevenDistance() {
		return levenDistance;
	}
	public void setLevenDistance(double levenDistance) {
		this.levenDistance = levenDistance;
	}
	public void setUnprocessedCompanyName(String unprocessedCompanyName) {
		this.unprocessedCompanyName = unprocessedCompanyName;
	}
	public String getUnprocessedCompanyName() {
		return unprocessedCompanyName;
	}
}