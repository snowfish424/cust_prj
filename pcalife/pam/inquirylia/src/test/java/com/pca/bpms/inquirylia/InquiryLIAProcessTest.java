package com.pca.bpms.inquirylia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.pca.bpms.model.ApplicationForm;
import com.pca.bpms.model.IssueCase;
import com.pca.bpms.model.newbusiness.InquiryLIA;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class InquiryLIAProcessTest extends JbpmJUnitBaseTestCase {
    private static final Logger logger = LoggerFactory.getLogger(InquiryLIAProcessTest.class);

    public InquiryLIAProcessTest() {
        super(true, false);
    }

    private static final String MAIN_PROCESS = "com/pca/bpms/inquirylia/InquiryLIAProcess.bpmn";
    private static final String EXCEPTIONHANDLE_PROCESS = "com/pca/bpms/inquirylia/ExceptionHandle.bpmn";

    @Test
    public void testHappyPathProcess() {
        //String userId = "rhpamAdmin";
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> liaIds = new ArrayList<String>();
        liaIds.add("123");
        liaIds.add("123");
        
        // set domain model classes   
        ApplicationForm appform = getAppform();
        InquiryLIA iqry = getInquiryLIA(); 
        params.put("appform", appform);
        params.put("iqry", iqry);
        params.put("issue", new IssueCase());
        params.put("rootProcessId", "123");
        params.put("subLevel", 0);
        params.put("parentProcessId", "123");
        params.put("procinsIDS", "123");
        params.put("inquiryLIAIds", liaIds);
        
        createRuntimeManager(MAIN_PROCESS, EXCEPTIONHANDLE_PROCESS);
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();

        TestWorkItemHandler testHandler = getTestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Rest", testHandler);

        ProcessInstance processInstance = ksession.startProcess("newbusiness.inquiryliaprocess.pam7", params);
        logger.info("Process Started - ID: " + processInstance.getId());
        assertProcessInstanceActive(processInstance.getId(), ksession);
        //ksession.signalEvent("startProcess", null, processInstance.getId());
        assertNodeTriggered(processInstance.getId(), "公會索引流程開始", "初始化", "接入");

        assertNodeTriggered(processInstance.getId(), "公會連線及索引");
        WorkItem workItem = testHandler.getWorkItem();
        assertNotNull(workItem);

        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "true");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);

        assertNodeTriggered(processInstance.getId(), "公會資料檢核");
        workItem = testHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);

        workItem = testHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);

        assertNodeTriggered(processInstance.getId(), "資料上傳", "流程結束");
    }

    private InquiryLIA getInquiryLIA() {
    	InquiryLIA lia = new InquiryLIA();
    	lia.setIssueRaised(false);
    	//checkup.setLifeId("123");
    	//checkup.setChangeNo("test");
    	
    	return lia;
    }

    private ApplicationForm getAppform() {
    	ApplicationForm appform = new ApplicationForm();
    	appform.setPriority(0);
    	appform.setProcessInstanceId((long) 123);
    	
        return appform;
    }
}