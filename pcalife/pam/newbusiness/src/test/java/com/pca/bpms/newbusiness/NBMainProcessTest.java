package com.pca.bpms.newbusiness;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.pca.bpms.client.JBpmRestClient;
import com.pca.bpms.model.IssueCase;
import com.pca.bpms.model.TaskSummaryResponse;
import com.pca.bpms.model.newbusiness.NewContractForm;
import com.pca.bpms.model.newbusiness.DataEntry;

import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Before;
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

    private static final String URL = "http://localhost:8080/kie-server/services/rest/server";
    private static final String USER = "rhpamAdmin";
    private static final String PASSWORD = "1qaz@WSX";

    private static final String CONTAINER_ID = "newbusiness_1.0.0-SNAPSHOT";
    private static final String PROCESS_ID = "newbusiness.mainprocess.pam7";

    private Gson gson = new Gson();

    @Before  
    public void before() {   
        logger.info("@Before");   
		SSLContext sslcontext = null;
        try {
           sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
        } catch (Exception e) {
           e.printStackTrace();
        }
		
		ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilder();
		resteasyClientBuilder = resteasyClientBuilder.connectionPoolSize(100);
		ResteasyClient client = resteasyClientBuilder
			.sslContext(sslcontext)
			.build();
        this.baseWebTarget = (ResteasyWebTarget)client.target(URL);
        this.baseWebTarget.register(new BasicAuthentication(USER, PASSWORD));
    }  

    @Test
    public void testPAM7RestClient() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();

        Map<String, Object> newcontractform = new HashMap<String, Object>();
        newcontractform.put("com.pca.bpms.model.newbusiness.NewContractForm", this.getNewContractForm());

        params.put("newcontractform", newcontractform);
        params.put("sender", USER);
        params.put("checkEPOS", "Y");

        JBpmRestClient client = new JBpmRestClient("localhost", 8443, "kie-server/services/rest/server", USER, PASSWORD);
        String processInsId = client.createProcessInstance(CONTAINER_ID, PROCESS_ID, params, USER);
        //String processInsId = "1";

        Thread.sleep(2000);

        com.pca.bpms.model.TaskSummary taskSummary = this.getCurrentTaskSummaryByInstanceId(String.valueOf(Long.valueOf(processInsId)+1L));
        //com.pca.bpms.model.TaskSummary taskSummary = this.getCurrentTaskSummaryByInstanceId("2");
        logger.info("Task id: ===> " + taskSummary.getTaskId());

        ParseContext parseContext = JsonPath.using(
            Configuration.builder()
            .jsonProvider(new JacksonJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .build()
        );

        //String returnModel = this.getNestedValue(taskParams, "dataEntry", "com.pca.bpms.model.newbusiness.DataEntry").toString();
        //logger.info("taskVars: ===> " + this.getNestedValue(taskParams, "dataEntry", "com.pca.bpms.model.newbusiness.DataEntry"));
        //DataEntry dataEntry = gson.fromJson(returnModel, DataEntry.class);

        List<DataEntry> results = parseContext.parse(
            this.getTaskVariableByJSON(CONTAINER_ID, String.valueOf(taskSummary.getTaskId())))
            .read("$..['com.pca.bpms.model.newbusiness.DataEntry']", new TypeRef<List<DataEntry>>() {});

        logger.info("getTaskVariableByJSON results: ===> " + results);

        Map<String, Object> taskParams = client.getTaskVariable(CONTAINER_ID, String.valueOf(taskSummary.getTaskId()));
        logger.info("get taskVars: ===> " + taskParams);

        DataEntry dataEntry = results.get(0);

        //是否提報問題件
		dataEntry.setIssueRaised(false);
		
		IssueCase issue = new IssueCase();
		//設定問題件類型
		issue.setIssueType("");
		
		//記錄建檔人員
		issue.setSender(USER);
		dataEntry.setCreator(USER);

        Map<String, Object> dataEntryMap = new HashMap<String, Object>();
        Map<String, Object> issueMap = new HashMap<String, Object>();
        dataEntryMap.put("com.pca.bpms.model.newbusiness.DataEntry", dataEntry);
        issueMap.put("com.pca.bpms.model.IssueCase", issue);

        taskParams.put("dataEntry", dataEntryMap);
		taskParams.put("issue", issueMap);

        //client.proceedTask("2", params, "rhpamAdmin");
        client.proceedTask(String.valueOf(Long.valueOf(processInsId)+1L), taskParams, USER);

        Thread.sleep(2000);

        taskSummary = this.getCurrentTaskSummaryByInstanceId(String.valueOf(Long.valueOf(processInsId)+4L));
        taskParams.clear();
        taskParams = client.getTaskVariable(CONTAINER_ID, String.valueOf(taskSummary.getTaskId()));
        logger.info("get taskVars: ===> " + taskParams);

        taskParams.put("previousInvestigateHandler", USER);
		taskParams.put("resultOfUnderwritingHandle", "ProcessDone");
		taskParams.put("isMustReview", false);

        client.proceedTask(String.valueOf(Long.valueOf(processInsId)+4L), taskParams, USER);

        Thread.sleep(1000);

        taskSummary = this.getCurrentTaskSummaryByInstanceId(String.valueOf(Long.valueOf(processInsId)+5L));
        taskParams.clear();
        taskParams = client.getTaskVariable(CONTAINER_ID, String.valueOf(taskSummary.getTaskId()));
        logger.info("get taskVars: ===> " + taskParams);

        taskParams.put("reOpen", Boolean.FALSE);
		taskParams.put("reOpenReason", "");
		taskParams.put("reOpenHandler", USER);

        client.proceedTask(String.valueOf(Long.valueOf(processInsId)+5L), taskParams, USER);        
    }

    public static <T> T getNestedValue(Map map, String... keys) {
        Object value = map;
    
        for (String key : keys) {
            value = ((Map) value).get(key);
        }
    
        return (T) value;
    }

    private WebTarget baseWebTarget = null;
    public com.pca.bpms.model.TaskSummary getCurrentTaskSummaryByInstanceId(String instanceId) throws Exception {
		// Query current task
		WebTarget queryCurrentTaskTarget = this.baseWebTarget.path("queries/tasks/instances/process/" + instanceId);
		Response queryCurrentTaskResponse = queryCurrentTaskTarget.request(MediaType.APPLICATION_JSON).get();
		if (queryCurrentTaskResponse.getStatus() != Response.Status.OK.getStatusCode()) {
			String errorContent = queryCurrentTaskResponse.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM錯誤: 無法獲取BPM Instance[ID:" + instanceId + "]運行中的任務." + errorContent);
		}

		String JSON = queryCurrentTaskResponse.readEntity(String.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		TaskSummaryResponse currentTaskSummary = objectMapper.readValue(JSON, TaskSummaryResponse.class);

		if (currentTaskSummary != null && currentTaskSummary.getTaskSummaries() != null && currentTaskSummary.getTaskSummaries().size() > 0) {
			logger.info("getCurrentTaskSummaryByInstanceId:"+currentTaskSummary.getTaskSummaries().get(0));
			return currentTaskSummary.getTaskSummaries().get(0);
		}
		
		queryCurrentTaskResponse.close();
		return null;
	}

    public String getTaskVariableByJSON(String containerId, String taskId) {
		Response response;
        WebTarget webTarget = this.baseWebTarget.path("containers/"+containerId+"/tasks/"+taskId+"/contents/input");
        response = webTarget.request(MediaType.APPLICATION_JSON).get();

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
        	String errorContent = response.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM錯誤: 取得BPM任務變量失敗." + errorContent);
        }

		String taskVars =response.readEntity(String.class);

        response.close();
		return taskVars;
	}

    //@Test ***** 無法實現呼叫子流程的Rest task *****
    public void testHappyPathProcess() {
        //String userId = "rhpamAdmin";
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("newcontractform", this.getNewContractForm());
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

    private NewContractForm getNewContractForm() {
        NewContractForm newcontractform = new NewContractForm();

        newcontractform.setChannel("-");
        //newcontractform.setSales("09AM1");
        newcontractform.setCaseInputType("紙本");
        newcontractform.setCaseKind("紙本");
        //newcontractform.setPayment("銀行自動轉帳");
        newcontractform.setPaid("否");
        //newcontractform.setJetCase("是");
        newcontractform.setJetCase("否");

        // newcontractform.setChannel("BK");
        // newcontractform.setSales("09AM1");
        // newcontractform.setCaseInputType("電子件");
        // newcontractform.setCaseKind("e-Sub");
        // newcontractform.setPayment("銀行自動轉帳");
        // newcontractform.setPaid("否");
        // newcontractform.setJetCase("否");
    	
    	return newcontractform;
    }
}