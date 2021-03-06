package com.pca.bpms.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.pca.bpms.model.TaskSummary;
import com.pca.bpms.model.TaskSummaryResponse;

public class JBpmRestClient {
	
	private static Logger logger = Logger.getLogger(JBpmRestClient.class);
	
	private WebTarget baseWebTarget = null;
	private Gson gson = new Gson(); 
	
	public JBpmRestClient(String host, int port, String apiPath, String userName, String password) {
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
        this.baseWebTarget = (ResteasyWebTarget)client.target("https://" + host + ":" + port + "/" + apiPath);
        this.baseWebTarget.register(new BasicAuthentication(userName, password));
	}
	
	public String createProcessInstance(String containerId, String processId, Map<String, Object> argsMap, String user) throws Exception {
        WebTarget webTarget = this.baseWebTarget.path("containers/" + containerId + "/processes/" + processId + "/instances");

        String entityString = null;

        if(argsMap != null) entityString = gson.toJson(argsMap);
        
        logger.info("createProcessInstance argument parameters = " + entityString);
        
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.post(Entity.entity(entityString, MediaType.APPLICATION_JSON));
        
        int responseCode = response.getStatus();
        String responseResult = response.readEntity(String.class);
        
        logger.info("createProcessInstance responseCode = "+responseCode);
        logger.info("createProcessInstance responseResult = "+responseResult);
        
        if (responseCode != Response.Status.CREATED.getStatusCode()) {
        	logger.info("createProcessInstance fail.");
        	String errorContent = responseResult;
        	logger.error(errorContent);
            throw new RuntimeException("BPM??????: ??????BPM Instance??????." + errorContent);
        }
        
        logger.info("createProcessInstance success. Instance = "+responseResult);

		webTarget = this.baseWebTarget.path("containers/" + containerId + "/processes/instances/" + responseResult + "/signal/startMainProcess");
		invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        response = invocationBuilder.post(Entity.entity("{}", MediaType.APPLICATION_JSON));

		responseCode = response.getStatus();
        //responseResult = response.readEntity(String.class);
        
        logger.info("createProcessInstance responseCode = "+responseCode);
        //logger.info("createProcessInstance responseResult = "+responseResult);

        response.close();
        return responseResult;
	}
	
	public boolean proceedTask(String instanceId, Map<String, Object> argsMap, String user) throws Exception {
		
		TaskSummary currentTaskSummary = getCurrentTaskSummaryByInstanceId(instanceId);
		
		if (currentTaskSummary == null) {
			throw new RuntimeException("BPM??????: ????????????BPM Instance[ID:" + instanceId + "]????????????????????????????????????.");
		}

		String containerId = currentTaskSummary.getTaskContainerId();

		if(!currentTaskSummary.getTaskStatus().equals("InProgress")) {
			logger.info("Trying to start the task ...");
			WebTarget startedWebTarget = this.baseWebTarget.path("containers/" + containerId + "/tasks/" + currentTaskSummary.getTaskId() + "/states/started").queryParam("user", user);
			
	        Response startedResponse = startedWebTarget.request(MediaType.APPLICATION_JSON).put(Entity.entity("", MediaType.APPLICATION_JSON));
	
	        if (startedResponse.getStatus() != Response.Status.CREATED.getStatusCode()) {
	        	String error = startedResponse.readEntity(String.class);
	        	if(error.contains("does not have permissions")) {
	        		throw new RuntimeException("BPM??????: ??????["+user+"]????????????????????????["+currentTaskSummary.getTaskActualOwner()+"]???BPM??????.");
	        	} else {
	        		throw new RuntimeException("BPM??????: " + error);
	        	}
	        }
	        
	        startedResponse.close();
		} else {
			logger.info("Task is already started.");
		}
        
        String entityString = null;
        if(argsMap != null) entityString = gson.toJson(argsMap);
        
        logger.info("proceedTask argument parameters = "+entityString);
        
        WebTarget completedWebTarget = this.baseWebTarget.path("containers/" + containerId + "/tasks/" + currentTaskSummary.getTaskId() + "/states/completed").queryParam("user", user);

        Response completedResponse = completedWebTarget.request(MediaType.APPLICATION_JSON).put(Entity.entity(entityString, MediaType.APPLICATION_JSON));

        if (completedResponse.getStatus() != Response.Status.CREATED.getStatusCode()) {
        	String errorContent = completedResponse.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM??????: ??????BPM????????????." + errorContent);
        }
		
        completedResponse.close();
        return true;
	}
	
	public List<String> getAvailableProcessInstances(String user) throws Exception {
        List<String> instanceIds = new ArrayList();
        
        WebTarget webTarget = this.baseWebTarget.path("queries/tasks/instances/pot-owners").queryParam("pageSize", 100).queryParam("user", user);
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
        	String errorContent = response.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM??????: ??????????????????BPM????????????." + errorContent);
        }

		//???JSON??????      
        //TaskSummaryResponse taskSummaryResp = response.readEntity(TaskSummaryResponse.class);
        
		//EAP 7.4??????????????????
		String JSON = response.readEntity(String.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		TaskSummaryResponse taskSummaryResp = objectMapper.readValue(JSON, TaskSummaryResponse.class);
        
        for (TaskSummary taskSummary : taskSummaryResp.getTaskSummaries()) {
        	instanceIds.add(String.valueOf(taskSummary.getTaskProcInstId()));
        }
		
        response.close();
        
		// Return list of instance IDs
		return instanceIds;
	}
	
	public TaskSummary getCurrentTaskSummaryByInstanceId(String instanceId) throws Exception {
		// Query current task
		WebTarget queryCurrentTaskTarget = this.baseWebTarget.path("queries/tasks/instances/process/" + instanceId);
		Response queryCurrentTaskResponse = queryCurrentTaskTarget.request(MediaType.APPLICATION_JSON).get();
		if (queryCurrentTaskResponse.getStatus() != Response.Status.OK.getStatusCode()) {
			String errorContent = queryCurrentTaskResponse.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM??????: ????????????BPM Instance[ID:" + instanceId + "]??????????????????." + errorContent);
		}

		//???JSON??????
		//TaskSummaryResponse currentTaskSummary = queryCurrentTaskResponse.readEntity(TaskSummaryResponse.class);

		//EAP 7.4??????????????????
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
	
	@SuppressWarnings("unused")
	public boolean assignInstance(String instanceId, String assignee, String user) throws Exception {
		TaskSummary currentTaskSummary = getCurrentTaskSummaryByInstanceId(instanceId);
		String containerId = currentTaskSummary.getTaskContainerId();
		
		if (currentTaskSummary == null) {
			throw new RuntimeException("BPM??????: ????????????BPM Instance[ID:" + instanceId + "]????????????????????????????????????.");
		} else if(currentTaskSummary.getTaskActualOwner() == null || currentTaskSummary.getTaskActualOwner().isEmpty()) {
			logger.info("Trying to claim the task ...");
			WebTarget startedWebTarget = this.baseWebTarget.path("containers/"+containerId+"/tasks/" + currentTaskSummary.getTaskId() + "/states/claimed").queryParam("user", user);
			
	        Response startedResponse = startedWebTarget.request(MediaType.APPLICATION_JSON).put(Entity.entity("", MediaType.APPLICATION_JSON));
	
	        if (startedResponse.getStatus() != Response.Status.CREATED.getStatusCode()) {
	        	String error = startedResponse.readEntity(String.class);
	        	if(error.contains("does not have permissions")) {
	        		throw new RuntimeException("BPM??????: ??????["+user+"]????????????????????????["+currentTaskSummary.getTaskActualOwner()+"]???BPM??????.");
	        	} else {
	        		throw new RuntimeException("BPM??????: " + error);
	        	}
	        }
	        
	        startedResponse.close();
		} else if(!currentTaskSummary.getTaskActualOwner().equals(user)){
			throw new RuntimeException("BPM??????: ??????["+user+"]????????????????????????["+currentTaskSummary.getTaskActualOwner()+"]???BPM??????.");
		}
		
		logger.info("Assigning Instance["+instanceId+"] CurrentTask["+currentTaskSummary.getTaskId()+"] from User["+user+"] to User["+assignee+"]");
		
        WebTarget webTarget = this.baseWebTarget.path("containers/"+containerId+"/tasks/" + currentTaskSummary.getTaskId() + "/states/delegated").queryParam("targetUser", assignee).queryParam("user", user);

        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.put(Entity.entity("", MediaType.APPLICATION_JSON));

        if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
        	String errorContent = response.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM??????: BPM??????????????????." + errorContent);
        }
		
        response.close();
        
        return true;
	}
	
	public boolean abortInstance(String containerId, String instanceId, String user) throws Exception {
        WebTarget webTarget = this.baseWebTarget.path("containers/"+containerId+"/processes/instances/" + instanceId);

        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.delete();

        if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
        	String errorContent = response.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM??????: ??????BPM Instance??????." + errorContent);
        }
        
        response.close();
        return true;
		
	}

	public void anyRestRequest(String restRequest) throws Exception {
        WebTarget webTarget = this.baseWebTarget.path(restRequest).queryParam("pageSize", 100);

        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
        	String errorContent = response.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM??????: " + errorContent);
        }

        String responseStr = response.readEntity(String.class);
        logger.info(responseStr);
        
        response.close();
	}
	
	public Boolean updateProcessVariableByInstanceId(String instanceId, String variable, String updateVal) throws Exception {
    	ObjectMapper objectMapper = new ObjectMapper();
		TaskSummary currentTaskSummary = getCurrentTaskSummaryByInstanceId(instanceId);
		String containerId = currentTaskSummary.getTaskContainerId();
		Response response;
        WebTarget webTarget = this.baseWebTarget.path("containers/"+containerId+"/processes/instances/"+instanceId+"/variable/"+variable);
        response = webTarget.request(MediaType.APPLICATION_JSON).put(Entity.entity(objectMapper.writeValueAsString(updateVal), MediaType.APPLICATION_JSON));

        if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
        	String errorContent = response.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM??????: ??????BPM Instance????????????." + errorContent);
        }
	    
        response.close();
		return true;
	}
	
	public Boolean updateProcessVariable(String containerId, String instanceId, String variable, String updateVal) throws Exception {
		Response response;
        WebTarget webTarget = this.baseWebTarget.path("containers/"+containerId+"/processes/instances/"+instanceId+"/variable/"+variable);
        response = webTarget.request(MediaType.APPLICATION_JSON).put(Entity.entity(updateVal, MediaType.APPLICATION_JSON));

        if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
        	String errorContent = response.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM??????: ??????BPM Instance????????????." + errorContent);
        }
	    
        response.close();
		return true;
	}

	public Map<String, Object> getTaskVariable(String containerId, String taskId) {
		Response response;
        WebTarget webTarget = this.baseWebTarget.path("containers/"+containerId+"/tasks/"+taskId+"/contents/input");
        response = webTarget.request(MediaType.APPLICATION_JSON).get();

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
        	String errorContent = response.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM??????: ??????BPM??????????????????." + errorContent);
        }
	    
		Map<String, Object> taskVars =response.readEntity(Map.class);
		logger.debug("getTaskVariable: ===> " + taskVars);

        response.close();
		return taskVars;
	}
	
	public Boolean updateTaskVariable(String containerId, String taskId, String updateVal) {
		Response response;
        WebTarget webTarget = this.baseWebTarget.path("containers/"+containerId+"/tasks/"+taskId+"/contents/output");
        response = webTarget.request(MediaType.APPLICATION_JSON).put(Entity.entity(updateVal, MediaType.APPLICATION_JSON));

        if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
        	String errorContent = response.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM??????: ??????BPM??????????????????." + errorContent);
        }
	    
        response.close();
		return true;
	}
	
	public Boolean claimTask(String containerId, String taskId, String user) {
		Response response;

        WebTarget webTarget = this.baseWebTarget.path("containers/"+containerId+"/tasks/"+taskId+"/states/claimed").queryParam("user", user);
        response = webTarget.request(MediaType.APPLICATION_JSON).put(Entity.entity(user, MediaType.APPLICATION_JSON));

        if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
        	String errorContent = response.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM??????: ??????BPM????????????." + errorContent);
        }
        
        response.close();
        return true;
	}
	
	
	
	public Boolean releaseTask(String containerId, String taskId, String user) {
		Response response;
        WebTarget webTarget = this.baseWebTarget.path("containers/"+containerId+"/tasks/"+taskId+"/states/released").queryParam("user", user);
        response = webTarget.request(MediaType.APPLICATION_JSON).put(Entity.entity(user, MediaType.APPLICATION_JSON));

        if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
        	String errorContent = response.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM??????: ??????BPM????????????." + errorContent);
        }
        response.close();
        return true;
	}
	
	public List<String> getTaskDetailsByInstanceId(String user) throws Exception {
        List<String> instanceIds = new ArrayList();
        WebTarget webTarget = this.baseWebTarget.path("queries/tasks/instances/pot-owners").queryParam("pageSize", 100).queryParam("user", user);
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
        	String errorContent = response.readEntity(String.class);
        	logger.error(errorContent);
            throw new RuntimeException("BPM??????: ??????BPM????????????." + errorContent);
        }
        
		//???JSON??????      
        //TaskSummaryResponse taskSummaryResp = response.readEntity(TaskSummaryResponse.class);
        
		//EAP 7.4??????????????????
		String JSON = response.readEntity(String.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		TaskSummaryResponse taskSummaryResp = objectMapper.readValue(JSON, TaskSummaryResponse.class);
        
        for (TaskSummary taskSummary : taskSummaryResp.getTaskSummaries()) {
        	instanceIds.add(String.valueOf(taskSummary.getTaskProcInstId()));
        }
        
        response.close();
        
		// Return list of instance IDs
		return instanceIds;
	}
	
}