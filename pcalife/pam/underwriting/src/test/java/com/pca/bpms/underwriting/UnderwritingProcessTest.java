package com.pca.bpms.underwriting;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.pca.bpms.model.ApplicationForm;
import com.pca.bpms.model.newbusiness.NewContractForm;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class UnderwritingProcessTest extends JbpmJUnitBaseTestCase {
    private static final Logger logger = LoggerFactory.getLogger(UnderwritingProcessTest.class);

    public UnderwritingProcessTest() {
        super(true, false);
    }

    private static final String MAIN_PROCESS = "com/pca/bpms/underwriting/UnderwritingProcess.bpmn";

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
        //params.put("previousUnderwrittingHandler", "john");
        params.put("subLevel", 0);
        
        // set domain model classes   
        ApplicationForm appform = getAppform();
        params.put("appform", appform);
        
        createRuntimeManager(MAIN_PROCESS);
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();

        TestWorkItemHandler testHandler = getTestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Rest", testHandler);

        ProcessInstance processInstance = ksession.startProcess("underwriting.underwritingprocess.pam7", params);
        logger.info("Process Started - ID: " + processInstance.getId());
        assertProcessInstanceActive(processInstance.getId(), ksession);
        //ksession.signalEvent("startProcess", null, processInstance.getId());
        assertNodeTriggered(processInstance.getId(), "初始化", "查詢跟隨件人員");
        WorkItem workItem = testHandler.getWorkItem();
        assertNotNull(workItem);

        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "john");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);

        assertNodeTriggered(processInstance.getId(), "處理核保照會");

        TaskService taskService = runtimeEngine.getTaskService();
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        logger.info("John is executing task {}", task.getName());
        taskService.start(task.getId(), "john");
        Map<String, Object> taskParams = taskService.getTaskContent(task.getId());
                
        logger.info("TaskName:" + taskParams.get("TaskName"));
        logger.info("NodeName:" + taskParams.get("NodeName"));

        taskParams.put("previousInvestigateHandler", "john");
		taskParams.put("resultOfUnderwritingHandle", "ProcessDone");
		taskParams.put("isMustReview", false);
		//taskParams.put("reviewType", "test");

        taskService.complete(task.getId(), "john", taskParams);
        assertNodeTriggered(processInstance.getId(), "處理結果", "接入", "是否須覆核？");

    }

    private ApplicationForm getAppform() {
    	ApplicationForm appform = new ApplicationForm();
    	appform.setPriority(0);
    	
        return appform;
    }
}