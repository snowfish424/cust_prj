package com.pca.bpms.model.policyownerservice;

import java.io.Serializable;

@org.kie.api.remote.Remotable
public class PolicyChangeMainForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 進件來源/方式
	 * 
	 * 紙本
	 * 建檔Reopen件
	 * 審核Reopen件
	 * 櫃檯件
	 * 一電通
	 */
	private String caseType;
	/**
	 * 案件別
	 * 
	 * 主件
	 * 拆件
	 * 分案
	 */
	private String caseRelation;
	/**
	 * 是否保單符合規範
	 * N:否 (不符合規範)
	 * Y:是 (符合規範)(預設Y)
	 */
	private String complianceFlag;
	/**
	 * 是否退件
	 * N:否 (預設N)
	 * Y:問題件退件
	 */
	private String rejectFlag;
	/**
	 * 是否JetCase
	 * N:否 (預設N)
	 * Y:是
	 */
	private String jetCase;
	/**
	 * 是否覆核返回
	 * Y: 覆核返回
	 * N: 覆核同意
	 * D: 初始值
	 */
	private String reviewRejectFlag;

	/**
	 * 是否進入自動受理子流程
	 */
	private String autoApply;
	/**
	 * 是否進入建檔子流程
	 */
	private String dataEntry;
	/**
	 * 是否進入拆件處理子流程
	 */
	private String splitPOSCase;
	/**
	 * 是否進入影像處理子流程
	 */
	private String imageHandler;
	/**
	 * 是否進入公會索引子流程
	 */
	private String inquiryLIA;
	/**
	 * 是否進入自動核保子流程
	 */
	private String autoUnderwriting;
	/**
	 * 是否進入建檔覆核子流程
	 */
	private String dataEntryReview;
	/**
	 * 是否進入審核子流程
	 */
	private String audit;
	/**
	 * 是否進入結案子流程
	 */
	private String caseClose;

	public PolicyChangeMainForm() {
		super();
	}

	public PolicyChangeMainForm(String caseRelation, String caseType,
			String rejectFlag, String complianceFlag, String jetCase, String reviewRejectFlag) {
		super();
		this.caseRelation = caseRelation;
		this.caseType = caseType;
		this.rejectFlag = rejectFlag;
		this.complianceFlag = complianceFlag ;
		this.jetCase = jetCase;
		this.reviewRejectFlag = reviewRejectFlag;
	}
	
	public void setSubProcessControl(String autoApply, String dataEntry, String splitPOSCase, String imageHandler,
			String inquiryLIA, String autoUnderwriting, String dataEntryReview, String audit, String caseClose){
		this.autoApply = autoApply;
		this.dataEntry = dataEntry;
		this.splitPOSCase = splitPOSCase;
		this.imageHandler = imageHandler;
		this.inquiryLIA = inquiryLIA;
		this.autoUnderwriting = autoUnderwriting;
		this.dataEntryReview = dataEntryReview;
		this.audit = audit;
		this.caseClose = caseClose;
		
	}

	public String getCaseRelation() {
		return caseRelation;
	}

	public void setCaseRelation(String caseRelation) {
		this.caseRelation = caseRelation;
	}

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	public String getRejectFlag() {
		return rejectFlag;
	}

	public void setRejectFlag(String rejectFlag) {
		this.rejectFlag = rejectFlag;
	}

	public String getComplianceFlag() {
		return complianceFlag;
	}

	public void setComplianceFlag(String complianceFlag) {
		this.complianceFlag = complianceFlag;
	}

	public String getJetCase() {
		return jetCase;
	}

	public void setJetCase(String jetCase) {
		this.jetCase = jetCase;
	}

	public String getReviewRejectFlag() {
		return reviewRejectFlag;
	}

	public void setReviewRejectFlag(String reviewRejectFlag) {
		this.reviewRejectFlag = reviewRejectFlag;
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

	public String getSplitPOSCase() {
		return splitPOSCase;
	}

	public void setSplitPOSCase(String splitPOSCase) {
		this.splitPOSCase = splitPOSCase;
	}

	public String getImageHandler() {
		return imageHandler;
	}

	public void setImageHandler(String imageHandler) {
		this.imageHandler = imageHandler;
	}

	public String getInquiryLIA() {
		return inquiryLIA;
	}

	public void setInquiryLIA(String inquiryLIA) {
		this.inquiryLIA = inquiryLIA;
	}

	public String getAutoUnderwriting() {
		return autoUnderwriting;
	}

	public void setAutoUnderwriting(String autoUnderwriting) {
		this.autoUnderwriting = autoUnderwriting;
	}

	public String getDataEntryReview() {
		return dataEntryReview;
	}

	public void setDataEntryReview(String dataEntryReview) {
		this.dataEntryReview = dataEntryReview;
	}

	public String getAudit() {
		return audit;
	}

	public void setAudit(String audit) {
		this.audit = audit;
	}

	public String getCaseClose() {
		return caseClose;
	}

	public void setCaseClose(String caseClose) {
		this.caseClose = caseClose;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((audit == null) ? 0 : audit.hashCode());
		result = prime * result + ((autoApply == null) ? 0 : autoApply.hashCode());
		result = prime * result + ((autoUnderwriting == null) ? 0 : autoUnderwriting.hashCode());
		result = prime * result + ((caseClose == null) ? 0 : caseClose.hashCode());
		result = prime * result + ((caseRelation == null) ? 0 : caseRelation.hashCode());
		result = prime * result + ((caseType == null) ? 0 : caseType.hashCode());
		result = prime * result + ((complianceFlag == null) ? 0 : complianceFlag.hashCode());
		result = prime * result + ((dataEntry == null) ? 0 : dataEntry.hashCode());
		result = prime * result + ((dataEntryReview == null) ? 0 : dataEntryReview.hashCode());
		result = prime * result + ((imageHandler == null) ? 0 : imageHandler.hashCode());
		result = prime * result + ((inquiryLIA == null) ? 0 : inquiryLIA.hashCode());
		result = prime * result + ((jetCase == null) ? 0 : jetCase.hashCode());
		result = prime * result + ((rejectFlag == null) ? 0 : rejectFlag.hashCode());
		result = prime * result + ((reviewRejectFlag == null) ? 0 : reviewRejectFlag.hashCode());
		result = prime * result + ((splitPOSCase == null) ? 0 : splitPOSCase.hashCode());
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
		PolicyChangeMainForm other = (PolicyChangeMainForm) obj;
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
		if (autoUnderwriting == null) {
			if (other.autoUnderwriting != null)
				return false;
		} else if (!autoUnderwriting.equals(other.autoUnderwriting))
			return false;
		if (caseClose == null) {
			if (other.caseClose != null)
				return false;
		} else if (!caseClose.equals(other.caseClose))
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
		if (complianceFlag == null) {
			if (other.complianceFlag != null)
				return false;
		} else if (!complianceFlag.equals(other.complianceFlag))
			return false;
		if (dataEntry == null) {
			if (other.dataEntry != null)
				return false;
		} else if (!dataEntry.equals(other.dataEntry))
			return false;
		if (dataEntryReview == null) {
			if (other.dataEntryReview != null)
				return false;
		} else if (!dataEntryReview.equals(other.dataEntryReview))
			return false;
		if (imageHandler == null) {
			if (other.imageHandler != null)
				return false;
		} else if (!imageHandler.equals(other.imageHandler))
			return false;
		if (inquiryLIA == null) {
			if (other.inquiryLIA != null)
				return false;
		} else if (!inquiryLIA.equals(other.inquiryLIA))
			return false;
		if (jetCase == null) {
			if (other.jetCase != null)
				return false;
		} else if (!jetCase.equals(other.jetCase))
			return false;
		if (rejectFlag == null) {
			if (other.rejectFlag != null)
				return false;
		} else if (!rejectFlag.equals(other.rejectFlag))
			return false;
		if (reviewRejectFlag == null) {
			if (other.reviewRejectFlag != null)
				return false;
		} else if (!reviewRejectFlag.equals(other.reviewRejectFlag))
			return false;
		if (splitPOSCase == null) {
			if (other.splitPOSCase != null)
				return false;
		} else if (!splitPOSCase.equals(other.splitPOSCase))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PolicyChangeMainForm [caseType=" + caseType + ", caseRelation=" + caseRelation + ", complianceFlag="
				+ complianceFlag + ", rejectFlag=" + rejectFlag + ", jetCase=" + jetCase + ", reviewRejectFlag="
				+ reviewRejectFlag + ", autoApply=" + autoApply + ", dataEntry=" + dataEntry + ", splitPOSCase="
				+ splitPOSCase + ", imageHandler=" + imageHandler + ", inquiryLIA=" + inquiryLIA + ", autoUnderwriting="
				+ autoUnderwriting + ", dataEntryReview=" + dataEntryReview + ", audit=" + audit + ", caseClose="
				+ caseClose + "]";
	}
}