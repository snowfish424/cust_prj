package com.pca.bpms.healthcheckup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

import com.pca.bpms.healthcheckup.client.JBpmRestClient;
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
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UserTaskServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class CheckupProcessTest extends JbpmJUnitBaseTestCase {
    private static final Logger logger = LoggerFactory.getLogger(CheckupProcessTest.class);

    public CheckupProcessTest() {
        super(true, false);
    }

    private static final String URL = "http://localhost:8080/kie-server/services/rest/server";
    private static final String USER = "rhpamAdmin";
    private static final String PASSWORD = "1qaz@WSX";

    private static final String MAIN_PROCESS = "com/pca/bpms/healthcheckup/CheckupProcess.bpmn";
    private static final String EXCEPTIONHANDLE_PROCESS = "com/pca/bpms/healthcheckup/ExceptionHandle.bpmn";

    @Test
    public void testPAM7RestClient() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();

        Map<String, Object> appform = new HashMap<String, Object>();
        Map<String, Object> checkup = new HashMap<String, Object>();
        Map<String, Object> issue = new HashMap<String, Object>();

        appform.put("com.pca.bpms.model.ApplicationForm", getAppform());
        checkup.put("com.pca.bpms.model.healthcheckup.Checkup", getCheckup());
        issue.put("com.pca.bpms.model.IssueCase", new IssueCase());

        params.put("appform", appform);
        params.put("checkup", checkup);
        params.put("issue", issue);
        params.put("sender", "rhpamAdmin");

        JBpmRestClient client = new JBpmRestClient("localhost", 8080, "kie-server/services/rest/server", "rhpamAdmin", "1qaz@WSX");
        String processId = client.createProcessInstance("healthcheckup_1.0.0-SNAPSHOT", "healthcheckupprocess.pam7", params, "john");

        client.proceedTask(processId, params, "rhpamAdmin");
    }

    @Test
    public void testPAM7KieClient() throws Exception {
        this.initialize();
        logger.info("== Starting a process ==");

        Map<String, Object> params = new HashMap<String, Object>();
        Checkup checkup = getCheckup();
        ApplicationForm appform = getAppform();
        params.put("appform", appform);
        params.put("checkup", checkup);
        params.put("issue", new IssueCase());
        params.put("sender", "john");

        Long processInsId = processClient.startProcess("healthcheckup_1.0.0-SNAPSHOT", "healthcheckupprocess.pam7", params);
        logger.info("Process started!");
        processClient.signalProcessInstance("healthcheckup_1.0.0-SNAPSHOT", processInsId, "startProcess", "");

        //List<org.kie.server.api.model.instance.TaskSummary> tasks = userTaskClient.findTasksAssignedAsPotentialOwner("john",1,1);
        //logger.info("== Task ID: " +tasks.get(0).getId());
        userTaskClient.completeTask("healthcheckup_1.0.0-SNAPSHOT", 1L, "john", params);
    }

    KieServicesConfiguration conf;
    KieServicesClient kieServicesClient;
    ProcessServicesClient processClient;
    UserTaskServicesClient userTaskClient;

    public void initialize() {
        logger.info("Configuration initializing");
        conf = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD);
        Set<Class<?>> extraClassList = new HashSet<Class<?>>();
        extraClassList.add(ApplicationForm.class);
        extraClassList.add(Checkup.class);
        extraClassList.add(IssueCase.class);
        conf.addExtraClasses(extraClassList);
        conf.setMarshallingFormat(MarshallingFormat.JSON);
        kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
        processClient = kieServicesClient.getServicesClient(ProcessServicesClient.class);
        userTaskClient = kieServicesClient.getServicesClient(UserTaskServicesClient.class);
        logger.info("Cofig Initialized with success!");
    }

    @Test
    public void testHappyPathProcess() {
        //String userId = "rhpamAdmin";
        Map<String, Object> params = new HashMap<String, Object>();
        Checkup checkup = getCheckup();
        ApplicationForm appform = getAppform();
        params.put("appform", appform);
        params.put("checkup", checkup);
        params.put("issue", new IssueCase());
        params.put("sender", "john");
        
        createRuntimeManager(MAIN_PROCESS, EXCEPTIONHANDLE_PROCESS);
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();

        TestWorkItemHandler testHandler = getTestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Rest", testHandler);

        ProcessInstance processInstance = ksession.startProcess("healthcheckupprocess.pam7", params);
        logger.info("Process Started - ID: " + processInstance.getId());
        assertProcessInstanceActive(processInstance.getId(), ksession);
        ksession.signalEvent("startProcess", null, processInstance.getId());
        assertNodeTriggered(processInstance.getId(), "流程建立", "啟動流程", "初始化作業", "體檢建檔輸入");

        //disposeRuntimeManager();
        runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();
        //assertEquals(ksessionID, ksession.getId());

        // execute 體檢建檔輸入
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        logger.info("John is executing task {}", task.getName());
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", params);

        assertNodeTriggered(processInstance.getId(), "回寫CM Index");
        WorkItem workItem = testHandler.getWorkItem();
        assertNotNull(workItem);
        // assertEquals("Email", workItem.getName());
        // assertEquals("me@mail.com", workItem.getParameter("From"));
        // assertEquals("you@mail.com", workItem.getParameter("To"));

        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "true");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);

        assertNodeTriggered(processInstance.getId(), "NB補充文件通知");
        workItem = testHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);

        workItem = testHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);

        assertNodeTriggered(processInstance.getId(), "補充文件通知結果處理", "結束主流程");
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
    	checkup.setChangeNo("test");
    	
    	return checkup;
    }

    private ApplicationForm getAppform() {
    	ApplicationForm appform = new ApplicationForm();
    	appform.setPriority(1);
    	
        return appform;
    }
}