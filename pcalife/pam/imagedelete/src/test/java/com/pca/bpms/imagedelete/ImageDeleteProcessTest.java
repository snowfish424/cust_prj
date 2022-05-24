package com.pca.bpms.imagedelete;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.pca.bpms.model.ApplicationForm;
import com.pca.bpms.model.IssueCase;
import com.pca.bpms.model.healthcheckup.Checkup;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
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

public class ImageDeleteProcessTest extends JbpmJUnitBaseTestCase {
    private static final Logger logger = LoggerFactory.getLogger(ImageDeleteProcessTest.class);

    public ImageDeleteProcessTest() {
        super(true, false);
    }

    private static final String MAIN_PROCESS = "com/pca/bpms/imagedelete/ImageDeleteProcess.bpmn";

    @Test
    public void testHappyPathProcess() {
        //String userId = "rhpamAdmin";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("assignedPeople", "john");
        params.put("submitToReviewer", true);
        params.put("imageDeleteReviewer", "mary");
        params.put("approved", true);
        
        createRuntimeManager(MAIN_PROCESS);
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();

        ProcessInstance processInstance = ksession.startProcess("imagedelete.imagedeleteprocess.pam7", params);
        logger.info("Process Started - ID: " + processInstance.getId());
        assertProcessInstanceActive(processInstance.getId(), ksession);
        ksession.signalEvent("startMainProcess", null, processInstance.getId());
        assertNodeTriggered(processInstance.getId(), "影像刪除處理流程開始", "影像刪除流程Signal暫停點", "刪除影像流程-初始化", "刪除影像選擇");

        //disposeRuntimeManager();
        runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();
        //assertEquals(ksessionID, ksession.getId());

        // execute 刪除影像選擇
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        logger.info("John is executing task {}", task.getName());
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", params);

        // execute 刪除影像覆核
        list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        task = list.get(0);
        logger.info("Mary is executing task {}", task.getName());
        taskService.start(task.getId(), "mary");
        taskService.complete(task.getId(), "mary", params);

        assertNodeTriggered(processInstance.getId(), "刪除影像覆核", "刪除影像流程-結束", "影像刪除處理流程完成");
    }

    // @Test
    // public void testProcessProcessInstanceStrategy() {
    //     Map<String, Object> params = new HashMap<String, Object>();
    //     Checkup checkup = getCheckup();
    //     ApplicationForm appform = getAppform();
    //     params.put("appform", appform);
    //     params.put("checkup", checkup);
    //     params.put("issue", new IssueCase());

    //     RuntimeManager manager = createRuntimeManager(Strategy.PROCESS_INSTANCE, "manager", "com/pca/bpms/healthcheckup/CheckupProcess.bpmn");
    //     RuntimeEngine runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get());
    //     KieSession ksession = runtimeEngine.getKieSession();
    //     ProcessInstance processInstance = ksession.startProcess("healthcheckupprocess.pam7", params);
    //     logger.info("Process Started - ID: " + processInstance.getId());
    //     assertProcessInstanceActive(processInstance.getId(), ksession);
    //     ksession.signalEvent("startProcess", null, processInstance.getId());
    //     assertNodeTriggered(processInstance.getId(), "流程建立", "啟動流程", "初始化作業");
    // }

    private Checkup getCheckup() {
    	Checkup checkup = new Checkup();
    	//checkup.setLifeId("123");
    	checkup.setIssueRaised(false);;
    	//checkup.setChangeNo("test");
    	
    	return checkup;
    }

    private ApplicationForm getAppform() {
    	ApplicationForm appform = new ApplicationForm();
    	appform.setPriority(0);
    	
        return appform;
    }
}