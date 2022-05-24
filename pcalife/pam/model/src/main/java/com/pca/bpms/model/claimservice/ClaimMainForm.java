package com.pca.bpms.model.claimservice;

import java.io.Serializable;

@org.kie.api.remote.Remotable
public class ClaimMainForm implements Serializable {

	private static final long serialVersionUID = 1L;
	/**	
	 * caseType	進件方式
	 * caseRelation	案件別
	 * reEntry	是否重新建檔 
	 * jetCase	是否JetCase
	 * autoApply	是否進入理賠自動受理
	 * dataEntry	是否進入理賠建檔
	 * magnum	是否進入Magnum Rule
	 * reviewDataEntry	是否進入建檔覆核
	 * audit	是否進入理賠審核 *
	 **/


	private String caseType;
	private String caseRelation;
	private String reEntry;
	private String jetCase;
	private String autoApply;
	private String dataEntry;
	private String magnum;
	private String reviewDataEntry;
	private String audit;
	

	public ClaimMainForm() {
		super();
	}

	public ClaimMainForm(String caseType ,String caseRelation,
			String reEntry, String jetCase) {
		super();
		this.caseType = caseType;
		this.caseRelation = caseRelation;
		this.reEntry = reEntry;
		this.jetCase = jetCase;
		
	}
	
	public void setSubProcessControl(String autoApply, String dataEntry,
			String magnum, String reviewDataEntry, String audit){

		this.autoApply = autoApply;
		this.dataEntry = dataEntry;
		this.magnum = magnum;
		this.reviewDataEntry = reviewDataEntry ;
		this.audit = audit;
		
	}

	

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	public String getCaseRelation() {
		return caseRelation;
	}

	public void setCaseRelation(String caseRelation) {
		this.caseRelation = caseRelation;
	}

	public String getReEntry() {
		return reEntry;
	}

	public void setReEntry(String reEntry) {
		this.reEntry = reEntry;
	}

	public String getJetCase() {
		return jetCase;
	}

	public void setJetCase(String jetCase) {
		this.jetCase = jetCase;
	}

	public String getAutoApply() {
		return autoApply;
	}

	public void setAutoApply(String autoApply) {
		this.autoApply = autoApply;
	}

	public String getDataEntry() {
		return dataEntry;
	}

	public void setDataEntry(String dataEntry) {
		this.dataEntry = dataEntry;
	}

	public String getMagnum() {
		return magnum;
	}

	public void setMagnum(String magnum) {
		this.magnum = magnum;
	}

	public String getReviewDataEntry() {
		return reviewDataEntry;
	}

	public void setReviewDataEntry(String reviewDataEntry) {
		this.reviewDataEntry = reviewDataEntry;
	}

	public String getAudit() {
		return audit;
	}

	public void setAudit(String audit) {
		this.audit = audit;
	}

	

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((audit == null) ? 0 : audit.hashCode());
		result = prime * result + ((autoApply == null) ? 0 : autoApply.hashCode());
		result = prime * result + ((caseRelation == null) ? 0 : caseRelation.hashCode());
		result = prime * result + ((caseType == null) ? 0 : caseType.hashCode());
		result = prime * result + ((dataEntry == null) ? 0 : dataEntry.hashCode());
		result = prime * result + ((jetCase == null) ? 0 : jetCase.hashCode());
		result = prime * result + ((magnum == null) ? 0 : magnum.hashCode());
		result = prime * result + ((reEntry == null) ? 0 : reEntry.hashCode());
		result = prime * result + ((reviewDataEntry == null) ? 0 : reviewDataEntry.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClaimMainForm other = (ClaimMainForm) obj;
		if (audit == null) {
			if (other.audit != null)
				return false;
		} else if (!audit.equals(other.audit))
			return false;
		if (autoApply == null) {
			if (other.autoApply != null)
				return false;
		} else if (!autoApply.equals(other.autoApply))
			return false;
		if (caseRelation == null) {
			if (other.caseRelation != null)
				return false;
		} else if (!caseRelation.equals(other.caseRelation))
			return false;
		if (caseType == null) {
			if (other.caseType != null)
				return false;
		} else if (!caseType.equals(other.caseType))
			return false;
		if (dataEntry == null) {
			if (other.dataEntry != null)
				return false;
		} else if (!dataEntry.equals(other.dataEntry))
			return false;
		if (jetCase == null) {
			if (other.jetCase != null)
				return false;
		} else if (!jetCase.equals(other.jetCase))
			return false;
		if (magnum == null) {
			if (other.magnum != null)
				return false;
		} else if (!magnum.equals(other.magnum))
			return false;
		if (reEntry == null) {
			if (other.reEntry != null)
				return false;
		} else if (!reEntry.equals(other.reEntry))
			return false;
		if (reviewDataEntry == null) {
			if (other.reviewDataEntry != null)
				return false;
		} else if (!reviewDataEntry.equals(other.reviewDataEntry))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClaimMainForm [caseType=" + caseType + ", caseRelation=" + caseRelation + ", reEntry=" + reEntry
				+ ", jetCase=" + jetCase + ", autoApply=" + autoApply + ", dataEntry=" + dataEntry + ", magnum="
				+ magnum + ", reviewDataEntry=" + reviewDataEntry + ", audit=" + audit + "]";
	}

}