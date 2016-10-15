package ch.ice.model;

public class Segment {

	private String id;
	private String companySegment;
	private String companyName;
	private String unprocessedCompanyName;
	private double levenDistance;
	private boolean exists;
	private boolean dublicate = false;
	private boolean newCompanyName = false;

	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isNewCompanyName() {
		return newCompanyName;
	}

	public void setNewCompanyName(boolean newCompanyName) {
		this.newCompanyName = newCompanyName;
	}

	public boolean isDublicate() {
		return dublicate;
	}

	public void setDublicate(boolean dublicate) {
		this.dublicate = dublicate;
	}

	
}