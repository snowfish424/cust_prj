package com.pca.bpms.newbusiness;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.pca.bpms.model.ApplicationForm;
import com.pca.bpms.model.IssueCase;
import com.pca.bpms.model.newbusiness.NewContractForm;
import com.pca.bpms.model.newbusiness.DataEntry;
import com.pca.bpms.model.newbusiness.MustRunSubProcess;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class NBMainProcessTest extends JbpmJUnitBaseTestCase {
    private static final Logger logger = LoggerFactory.getLogger(NBMainProcessTest.class);

    public NBMainProcessTest() {
        super(true, true);
    }

    private static final String MAIN_PROCESS = "com/pca/bpms/newbusiness/MainProcess.bpmn";
    private static final String CONTROL_MASTER_RULE = "com/pca/bpms/newbusiness/NewContract-controlMaster.xlsx";
    private static final String SUBPROCESS_RULE = "com/pca/bpms/newbusiness/NewContract-subProcessRule.drl";
    private static final String DATAIMPORT_PROCESS = "com/pca/bpms/dataimport/DataImportProcess.bpmn";
    private static final String DATAENTRY_PROCESS = "com/pca/bpms/dataentry/CreateNBProcess.bpmn";
    private static final String INQUIRYLIA_PROCESS = "com/pca/bpms/inquirylia/InquiryLIAProcess.bpmn";

    @Test
    public void testHappyPathProcess() {
        //String userId = "rhpamAdmin";
        Map<String, Object> params = new HashMap<String, Object>();
        NewContractForm newcontractform = new NewContractForm();
        newcontractform.setChannel("BK");
        newcontractform.setSales("09AM1");
        newcontractform.setCaseInputType("電子件");
        newcontractform.setCaseKind("e-sub");
        newcontractform.setPayment("銀行自動轉帳");
        newcontractform.setPaid("否");
        //newcontractform.setJetCase("是");
        newcontractform.setJetCase("否");

        params.put("newcontractform", newcontractform);
        params.put("sender", "john");
        params.put("checkEPOS", "Y");

        Map<String, ResourceType> res = new HashMap<String, ResourceType>();
        res.put(MAIN_PROCESS, ResourceType.BPMN2);
        res.put(CONTROL_MASTER_RULE, ResourceType.DTABLE);
        res.put(SUBPROCESS_RULE, ResourceType.DRL);
        res.put(DATAIMPORT_PROCESS, ResourceType.BPMN2);
        res.put(DATAENTRY_PROCESS, ResourceType.BPMN2);
        res.put(INQUIRYLIA_PROCESS, ResourceType.BPMN2);

        RuntimeManager manager = createRuntimeManager(Strategy.PROCESS_INSTANCE, res);
        RuntimeEngine runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get());
        
        KieSession ksession = runtimeEngine.getKieSession();
        TestWorkItemHandler testHandler = getTestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Rest", testHandler);
        //ksession.getWorkItemManager().registerWorkItemHandler("Rest", new org.jbpm.process.workitem.rest.RESTWorkItemHandler("", ""));

        ProcessInstance processInstance = ksession.startProcess("newbusiness.mainprocess.pam7", params);
        logger.info("Process Started - ID: " + processInstance.getId());
        assertProcessInstanceActive(processInstance.getId(), ksession);
        //ksession.insert(newcontractform);
        //ksession.fireAllRules();
        ksession.signalEvent("startMainProcess", null, processInstance.getId());
        assertNodeTriggered(processInstance.getId(), "初始化", "建檔");

        // into 建檔子流程
        assertNodeTriggered(2L, "流程建立", "接入", "新契約建檔輸入");
        TaskService taskService = runtimeEngine.getTaskService();

        // execute 建檔輸入
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        logger.info("John is executing task {}", task.getName());
        taskService.start(task.getId(), "john");
        Map<String, Object> taskParams = taskService.getTaskContent(task.getId());
		
		logger.info("TaskName:" + taskParams.get("TaskName"));
		logger.info("NodeName:" + taskParams.get("NodeName"));
		
		DataEntry dataEntry = (DataEntry) taskParams.get("dataEntry");
		
		//是否提報問題件
		dataEntry.setIssueRaised(false);
		
		IssueCase issue = new IssueCase();
		//設定問題件類型
		issue.setIssueType("");
		
		//記錄建檔人員
		issue.setSender("john");
		dataEntry.setCreator("john");
		
		taskParams.put("dataEntry", dataEntry);
		taskParams.put("issue", issue);
        
        taskService.complete(task.getId(), "john", taskParams);
        assertNodeTriggered(2L, "判斷是否結束", "建檔結束");
        WorkItem workItem = testHandler.getWorkItem();
        runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get(3L));
        ksession = runtimeEngine.getKieSession();
        testHandler = getTestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Rest", testHandler);
        
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "true");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);
    }

}