package com.pca.bpms.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@org.kie.api.remote.Remotable
public class ApplicationForm implements Serializable {

	static final long serialVersionUID = 1L;

	// App ID
	private long id;

	// 流程ID ===> parent process ins id
	private Long processInstanceId;
	// 區域
	private String area;
	// 指派人員 ===> task owner ===> 可拿掉
	// private String assignedPeople;
	// 指派角色 ===> task role ===> 可拿掉
	// private String assignedRole;
	// 案件核保等級
	private int caseLevel;

	// 案件年化保費
	private BigDecimal caseAnnualPremium;

	// 案件保額
	private BigDecimal caseFaceAmount;

	// 案件類型 ===> 進件型態 ex. 紙本, e-submission...
	private String caseType;

	// 通路
	private String channel;

	// 銷售單位代碼 ex. 09AM1(渣打) 09AJ1(玉山)
	private String salesNo;

	// 險種名稱 2017/5/24 remove
	// private String kind;

	// 首期繳費方式代碼 ex. 轉帳, 信用卡, 現金...
	private String payment;

	// 重要性 0, 1, 2 (0優先)
	private int priority;

	// 流程名
	private String processName;

	// 流程中文名
	private String processDesc;

	// 狀態 (控制流程預留用)
	//private String status;

	// 關卡名 ===> remove
	// private String taskStageName;

	// process啟動時間 (先保留)
	private Date triggerTime;

	// 險種名稱code
	private java.lang.String planCode;

	// 包裹號碼
	private java.lang.String packageNum;

	// 當前活動名稱
	private java.lang.String nodeName;

	// 主流程啟始時間
	private Date mainFlowStartTime;
	
	//主約1 核保決定
	private String contractApproval1;
	
	//主約2 核保決定
	private String contractApproval2;
	
	//主約3 核保決定
	private String contractApproval3;

	public ApplicationForm() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public java.lang.String getArea() {
		return area;
	}

	public int getCaseLevel() {
		return caseLevel;
	}

	public java.lang.String getCaseType() {
		return caseType;
	}

	public java.lang.String getChannel() {
		return channel;
	}

	public java.lang.String getPayment() {
		return payment;
	}

	public int getPriority() {
		return priority;
	}

	public Date getTriggerTime() {
		return triggerTime;
	}

	public void setArea(java.lang.String area) {
		this.area = area;
	}

	public void setCaseLevel(int caseLevel) {
		this.caseLevel = caseLevel;
	}

	public void setCaseType(java.lang.String caseType) {
		this.caseType = caseType;
	}

	public void setChannel(java.lang.String channel) {
		this.channel = channel;
	}

	public void setPayment(java.lang.String payment) {
		this.payment = payment;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setTriggerTime(Date triggerTime) {
		this.triggerTime = triggerTime;
	}

	public Long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public BigDecimal getCaseAnnualPremium() {
		return caseAnnualPremium;
	}

	public void setCaseAnnualPremium(BigDecimal caseAnnualPremium) {
		this.caseAnnualPremium = caseAnnualPremium;
	}

	public BigDecimal getCaseFaceAmount() {
		return caseFaceAmount;
	}

	public void setCaseFaceAmount(BigDecimal caseFaceAmount) {
		this.caseFaceAmount = caseFaceAmount;
	}

	public String getSalesNo() {
		return salesNo;
	}

	public void setSalesNo(String salesNo) {
		this.salesNo = salesNo;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessDesc() {
		return processDesc;
	}

	public void setProcessDesc(String processDesc) {
		this.processDesc = processDesc;
	}

	public java.lang.String getPlanCode() {
		return this.planCode;
	}

	public void setPlanCode(java.lang.String planCode) {
		this.planCode = planCode;
	}

	public java.lang.String getPackageNum() {
		return this.packageNum;
	}

	public void setPackageNum(java.lang.String packageNum) {
		this.packageNum = packageNum;
	}

	public java.lang.String getNodeName() {
		return nodeName;
	}

	public void setNodeName(java.lang.String nodeName) {
		this.nodeName = nodeName;
	}

	public Date getMainFlowStartTime() {
		return mainFlowStartTime;
	}

	public void setMainFlowStartTime(Date mainFlowStartTime) {
		this.mainFlowStartTime = mainFlowStartTime;
	}
	
	

	public java.lang.String getContractApproval1() {
		return contractApproval1;
	}

	public void setContractApproval1(java.lang.String contractApproval1) {
		this.contractApproval1 = contractApproval1;
	}

	public java.lang.String getContractApproval2() {
		return contractApproval2;
	}

	public void setContractApproval2(java.lang.String contractApproval2) {
		this.contractApproval2 = contractApproval2;
	}

	public java.lang.String getContractApproval3() {
		return contractApproval3;
	}

	public void setContractApproval3(java.lang.String contractApproval3) {
		this.contractApproval3 = contractApproval3;
	}

	public ApplicationForm(long id, Long processInstanceId, String area, int caseLevel, BigDecimal caseAnnualPremium,
			BigDecimal caseFaceAmount, String caseType, String channel,	String salesNo, String payment, int priority,
			String processName,	String processDesc, Date triggerTime, String planCode,	String packageNum, 
			String nodeName, Date mainFlowStartTime, String contractApproval1, String contractApproval2, String contractApproval3) {
		super();
		this.id = id;
		this.processInstanceId = processInstanceId;
		this.area = area;
		this.caseLevel = caseLevel;
		this.caseAnnualPremium = caseAnnualPremium;
		this.caseFaceAmount = caseFaceAmount;
		this.caseType = caseType;
		this.channel = channel;
		this.salesNo = salesNo;
		this.payment = payment;
		this.priority = priority;
		this.processName = processName;
		this.processDesc = processDesc;
		this.triggerTime = triggerTime;
		this.planCode = planCode;
		this.packageNum = packageNum;
		this.nodeName = nodeName;
		this.mainFlowStartTime = mainFlowStartTime;
		this.contractApproval1 = contractApproval1;
		this.contractApproval2 = contractApproval2;
		this.contractApproval3 = contractApproval3;
	}

	
}