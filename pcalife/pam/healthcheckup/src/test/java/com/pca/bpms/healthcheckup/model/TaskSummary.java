package com.pca.bpms.healthcheckup.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskSummary {

    @JsonProperty("task-id")
    private long taskId;

    @JsonProperty("task-name")
    private String taskName;

    @JsonProperty("task-subject")
    private String taskSubject;

    @JsonProperty("task-description")
    private String taskDescription;
    
    @JsonProperty("task-form")
    private String taskForm;

    @JsonProperty("task-status")
    private String taskStatus;

    @JsonProperty("task-priority")
    private int taskPriority;

    @JsonProperty("task-is-skipable")
    private boolean taskIsSkipable;
    
    @JsonProperty("task-actual-owner")
    private String taskActualOwner;

    //@JsonProperty("task-created-by")
    private String taskCreatedBy;
    
    @JsonProperty("task-created-on")
    private Date taskCreatedOn;

    //@JsonProperty("task-activation-time")
    private Date taskActivationTime;

    @JsonProperty("task-proc-inst-id")
    private long taskProcInstId;

    @JsonProperty("task-proc-def-id")
    private String taskProcDefId;

    @JsonProperty("task-container-id")
    private String taskContainerId;

    @JsonProperty("task-parent-id")
    private long taskParentId;
    
    @JsonProperty("correlation-key")
    private long correlationKey;
    
    @JsonProperty("process-type")
    private String processType;
    
	public long getCorrelationKey() {
		return correlationKey;
	}

	public void setCorrelationKey(long correlationKey) {
		this.correlationKey = correlationKey;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskSubject() {
		return taskSubject;
	}

	public void setTaskSubject(String taskSubject) {
		this.taskSubject = taskSubject;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getTaskForm() {
		return taskForm;
	}

	public void setTaskForm(String taskForm) {
		this.taskForm = taskForm;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public int getTaskPriority() {
		return taskPriority;
	}

	public void setTaskPriority(int taskPriority) {
		this.taskPriority = taskPriority;
	}

	public boolean isTaskIsSkipable() {
		return taskIsSkipable;
	}

	public void setTaskIsSkipable(boolean taskIsSkipable) {
		this.taskIsSkipable = taskIsSkipable;
	}

	public String getTaskActualOwner() {
		return taskActualOwner;
	}

	public void setTaskActualOwner(String taskActualOwner) {
		this.taskActualOwner = taskActualOwner;
		this.taskCreatedBy = taskActualOwner;
	}

	public String getTaskCreatedBy() {
		return taskCreatedBy;
	}

	public void setTaskCreatedBy(String taskCreatedBy) {
		this.taskCreatedBy = taskCreatedBy;
	}

	public Date getTaskCreatedOn() {
		return taskCreatedOn;
	}

	public void setTaskCreatedOn(Date taskCreatedOn) {
		this.taskCreatedOn = taskCreatedOn;
	}

	public Date getTaskActivationTime() {
		return taskActivationTime;
	}

	public void setTaskActivationTime(Date taskActivationTime) {
		this.taskActivationTime = taskActivationTime;
	}

	public long getTaskProcInstId() {
		return taskProcInstId;
	}

	public void setTaskProcInstId(long taskProcInstId) {
		this.taskProcInstId = taskProcInstId;
	}

	public String getTaskProcDefId() {
		return taskProcDefId;
	}

	public void setTaskProcDefId(String taskProcDefId) {
		this.taskProcDefId = taskProcDefId;
	}

	public String getTaskContainerId() {
		return taskContainerId;
	}

	public void setTaskContainerId(String taskContainerId) {
		this.taskContainerId = taskContainerId;
	}

	public long getTaskParentId() {
		return taskParentId;
	}

	public void setTaskParentId(long taskParentId) {
		this.taskParentId = taskParentId;
	}

	@Override
	public String toString() {
		return "TaskSummary [taskId=" + taskId + ",\ntaskName=" + taskName + ",\ntaskSubject=" + taskSubject
				+ ",\ntaskDescription=" + taskDescription + ",\ntaskForm=" + taskForm + ",\ntaskStatus=" + taskStatus
				+ ",\ntaskPriority=" + taskPriority + ",\ntaskIsSkipable=" + taskIsSkipable + ",\ntaskActualOwner="
				+ taskActualOwner + ",\ntaskCreatedBy=" + taskCreatedBy + ",\ntaskCreatedOn=" + taskCreatedOn
				+ ",\ntaskActivationTime=" + taskActivationTime + ",\ntaskProcInstId=" + taskProcInstId
				+ ",\ntaskProcDefId=" + taskProcDefId + ",\ntaskContainerId=" + taskContainerId + ",\ntaskParentId="
				+ taskParentId + "]";
	}
	
}
