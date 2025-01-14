package com.ymd.cloud.authorizeCenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ymd.cloud.api.RedisService;
import com.ymd.cloud.authorizeCenter.enums.AuthConstants;
import com.ymd.cloud.authorizeCenter.job.queue.MyLinkedQueue;
import com.ymd.cloud.authorizeCenter.job.queue.MyQueue;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTask;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTaskNotes;
import com.ymd.cloud.authorizeCenter.model.entity.TaskResultEntity;
import com.ymd.cloud.authorizeCenter.service.*;
import com.ymd.cloud.common.enumsSupport.Constants;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class ConsumerChannelAuthTaskNotesServiceImpl implements ConsumeChannelAuthTaskNotesService {
	private static int queueLength=5;
	private static int queueCount=3;
	public static int limit=queueLength*queueCount;
	public static Integer channelAuthProcessRetryCount=0;
	private static String REDIS_KEY = Constants.MQ_CHANNEL_AUTH_PROCESS_REDIS_KEY;
	private static String process_key_0 = REDIS_KEY + "_0";
	private static String process_key_1 = REDIS_KEY + "_1";
	private static String process_key_2 = REDIS_KEY + "_2";
	private MyQueue queue_0 =new MyLinkedQueue(process_key_0,queueLength+1,0,20);
	private MyQueue queue_1 =new MyLinkedQueue(process_key_1,queueLength+1,20,1000);
	private MyQueue queue_2 =new MyLinkedQueue(process_key_2,queueLength+1,1000,Long.MAX_VALUE);
	private ExecutorService threadPool = Executors.newFixedThreadPool(limit);
	@Autowired
	AuthorizeCenterTaskResultService authorizeCenterTaskResultService;
	@Autowired
	AuthorizeCenterTaskNotesService authorizeCenterTaskNotesService;
	@Autowired
	AuthorizeCenterTaskService authorizeCenterTaskService;
	@Autowired
	RedisService redisService;
	@Autowired
	AuthorizeService authorizeService;

	//TODO authorize_center_channel_4_channel_auth_push_topic消费
	@Override
	public JSONObject channelAuthConsumerHandler(String taskNo) {
		JSONObject result = new JSONObject();
		result.put("status", ErrorCodeEnum.SUCCESS.code());
		result.put("message", ErrorCodeEnum.SUCCESS.msg());
		processAuthQueue();
		return result;
	}
	public void processAuthQueue(){
		//判断清空、重新赋值队列之前是否存在数据
		boolean queue_0_empty=queue_0.isEmpty();
		boolean queue_1_empty=queue_1.isEmpty();
		boolean queue_2_empty=queue_2.isEmpty();
		log.info("==========================>>>>>>>队列" + process_key_0 + "【刷新数据-前】 count:"+queue_0.size()+" 队列是否为空："+queue_0_empty);
		log.info("==========================>>>>>>>队列" + process_key_1 + "【刷新数据-前】 count:"+queue_1.size()+" 队列是否为空："+queue_1_empty);
		log.info("==========================>>>>>>>队列" + process_key_2 + "【刷新数据-前】 count:"+queue_2.size()+" 队列是否为空："+queue_2_empty);
		refreshCacheQueue();
		log.info("==========================>>>>>>>队列" + process_key_0 + "【刷新数据-后】 count:"+queue_0.size());
		log.info("==========================>>>>>>>队列" + process_key_1 + "【刷新数据-后】 count:"+queue_1.size());
		log.info("==========================>>>>>>>队列" + process_key_2 + "【刷新数据-后】 count:"+queue_2.size());
		processTaskPool(queue_0_empty,queue_1_empty,queue_2_empty);
	}
	public void refreshCacheQueue(){
		delCacheQueue();
		List<AuthorizeCenterTask> taskNoFinishList=authorizeCenterTaskService.selectNoFinishTaskByTime(channelAuthProcessRetryCount+1,limit);
		if(EmptyUtil.isNotEmpty(taskNoFinishList)) {
			List<List<AuthorizeCenterTask>> batchTaskNoFinishList = Lists.partition(taskNoFinishList, queueLength);
			if (EmptyUtil.isNotEmpty(batchTaskNoFinishList)) {
				for (int i = 0; i < batchTaskNoFinishList.size(); i++) {
					List<AuthorizeCenterTask> noFinishList = batchTaskNoFinishList.get(i);
					//使用Collections集合工具类进行排序
					Collections.sort(noFinishList, Comparator.comparingInt(AuthorizeCenterTask::getTaskCount));
					for (AuthorizeCenterTask authorizeCenterTask : noFinishList) {
						if (authorizeCenterTask.getRetryCount()<=channelAuthProcessRetryCount) {
							try {
								getQueue(authorizeCenterTask.getTaskCount()).offer(authorizeCenterTask);
							} catch (Exception e) { }
						} else {
							authorizeCenterTaskService.updateSyncStatusByTaskNo(authorizeCenterTask.getTaskNo(), 3, "[1]该任务已执行" +
									authorizeCenterTask.getRetryCount() + "次,超出最大重试次数" + channelAuthProcessRetryCount);
						}
					}
				}
			}
		}
	}

	private void processTaskPool(boolean queue_0_empty,boolean queue_1_empty,boolean queue_2_empty) {
		if(queue_0_empty) {
			log.info("==========================>>>>>>>队列"+ process_key_0 +",【刷新数据-前】标识为空,所以创建新的线程池线程处理队列");
			threadPool.execute(() -> processTaskNoCache(queue_0));
		}
		if(queue_1_empty) {
			log.info("==========================>>>>>>>队列"+ process_key_1 +"【刷新数据-前】标识为空,所以创建新的线程池线程处理队列");
			threadPool.execute(() -> processTaskNoCache(queue_1));
		}
		if(queue_2_empty) {
			log.info("==========================>>>>>>>队列"+ process_key_2 +"【刷新数据-前】标识为空,所以创建新的线程池线程处理队列");
			threadPool.execute(() -> processTaskNoCache(queue_2));
		}
	}

	public void processTaskNoCache(MyQueue queue) {
		String process_key=queue.queueName();
		String key=process_key+"-processTaskNoCache";
		synchronized (key) {
			while (!queue.isEmpty()) {
				if (redisService.lock(key, "true", 3600)) {
					try {
						int count = queue.size();
						if (count > 0) {
							AuthorizeCenterTask authorizeCenterTask= (AuthorizeCenterTask) queue.peek();
							if(EmptyUtil.isNotEmpty(authorizeCenterTask)) {
								String taskNo = authorizeCenterTask.getTaskNo();
								try {
									authorizeCenterTaskService.updateSyncStatusByTaskNo(taskNo, 2, "mq处理");
									batchProcessTaskNotes(taskNo, authorizeCenterTask);
								}catch (Exception e){}finally {
									queue.remove(authorizeCenterTask);
								}
								log.info("==========================>>>>>>>队列" + process_key + "中" + taskNo + "授权任务已处理完毕");
							}
						}
					} finally {
						redisService.unLock(key);
					}
				}
			}
			log.info("==========================>>>>>>>队列" + process_key + "授权中心授权任务处理完毕");
		}
	}


	private void batchProcessTaskNotes(String taskNo,AuthorizeCenterTask tblSyncTask){
		long total = tblSyncTask.getTaskCount();
		authorizeCenterTaskService.updateSyncStatusByTaskNo(taskNo, 2, "Notes处理环节,notes total" + total);
		log.info("处理授权中心下发处理taskNo{}数量{}",taskNo,total);
		if (total != 0) {
			try {
				AuthorizeCenterTaskNotes tblSyncTaskNotes = new AuthorizeCenterTaskNotes();
				tblSyncTaskNotes.setTaskNo(taskNo);
				List<AuthorizeCenterTaskNotes> allAuthList = authorizeCenterTaskNotesService.selectTaskNotesPageByTaskNo(tblSyncTaskNotes);
				if(EmptyUtil.isNotEmpty(allAuthList)) {
					int listSize=allAuthList.size();
					List<List<AuthorizeCenterTaskNotes>> batchAllAuthList = Lists.partition(allAuthList, AuthConstants.pageSize);
					if(EmptyUtil.isNotEmpty(batchAllAuthList)) {
						int batchSize =batchAllAuthList.size();
						for (int i = 0; i <batchSize; i++) {
							List<AuthorizeCenterTaskNotes> batchAllAuth=batchAllAuthList.get(i);
							if(EmptyUtil.isNotEmpty(batchAllAuth)) {
								processTaskNotes(taskNo, batchAllAuth);
							}
							log.info("========================处理授权中心下发处理taskNo{}进入批量授权 总量:{},一共{}批,当前第{}批,每批{}",
									taskNo,listSize,batchSize,i,AuthConstants.pageSize);
						}
					}
				}

				total = authorizeCenterTaskNotesService.selectTaskNotesCountByTaskNo(taskNo);
				if(total==0) {
					finishAuthTask(taskNo);
				}else{
					authorizeCenterTaskService.updateRetryCountByTaskNo(taskNo);
					authorizeCenterTaskService.updateSyncStatusByTaskNo(taskNo, 2, "Notes处理失败,[3]该任务已执行"
							+  (tblSyncTask.getRetryCount()+1));
				}
			} catch (Exception e) {
				log.info("taskNo{},[处理任务授权]异常" ,taskNo,e);
				authorizeCenterTaskService.updateRetryCountByTaskNo(taskNo);
				authorizeCenterTaskService.updateSyncStatusByTaskNo(taskNo,2,"Notes处理环节,[处理任务授权]异常 ");
				redisService.lSet("channelPushAuth_error_"+taskNo,"[处理任务授权]异常:"+ e.fillInStackTrace().toString());
			}finally {

				tblSyncTask= authorizeCenterTaskResultService.getTaskResultCountInfoLog(tblSyncTask);
				if(EmptyUtil.isNotEmpty(tblSyncTask)) {
					authorizeCenterTaskService.updateAuthCountByTaskNo(tblSyncTask);
				}

				redisService.expire("channelPushAuth_error_"+taskNo, Constants.redisKeyTime*2);
				authorizeCenterTaskService.updateUpdateTimeByTaskNo(taskNo);
			}
		}else{
			finishAuthTask(taskNo);
		}
	}

	private void processTaskNotes(String taskNo,List<AuthorizeCenterTaskNotes> allAuthList){
		if(EmptyUtil.isNotEmpty(allAuthList)) {
			List<TaskResultEntity> batchAddTaskResultLogList = processAuth(taskNo, allAuthList);
			saveTaskResultLog(taskNo,batchAddTaskResultLogList);
		}
	}

	private void saveTaskResultLog(String taskNo,List<TaskResultEntity> batchAddTaskResultLogList){
		try {
			if(EmptyUtil.isNotEmpty(batchAddTaskResultLogList)){
				for (TaskResultEntity taskResultEntity:batchAddTaskResultLogList) {
					AuthorizeCenterTaskNotes authorizeCenterTaskNotes=taskResultEntity.getAuthorizeCenterTaskNotes();
					if (EmptyUtil.isNotEmpty(authorizeCenterTaskNotes)&&authorizeCenterTaskNotes.getTaskNo().equals(taskNo)) {
						authorizeCenterTaskResultService.saveTaskResultLog(taskResultEntity);
					}
				}
				batchAddTaskResultLogList.clear();
			}
		}catch (Exception e){}
	}

	private List<TaskResultEntity> processAuth(String taskNo,List<AuthorizeCenterTaskNotes> allAuthList){
		authorizeCenterTaskService.updateSyncStatusByTaskNo(taskNo, 2, "进入授权处理环节");
		List<TaskResultEntity> batchAddTaskResultLogList=new ArrayList<>();
		if(EmptyUtil.isNotEmpty(allAuthList)) {
			Iterator<AuthorizeCenterTaskNotes> iterator = allAuthList.iterator();
			while (iterator.hasNext()) {
				AuthorizeCenterTaskNotes authorizeCenterTaskNotes = iterator.next();
				JSONObject result = new JSONObject();
				String authModel = authorizeCenterTaskNotes.getAuthContent();
				String authNo = authorizeCenterTaskNotes.getAuthNo();
				AuthorizeCenterAuthorizeRecord	authorizeCenterAuthorizeRecord = JSONObject.parseObject(authModel, AuthorizeCenterAuthorizeRecord.class);
				if (EmptyUtil.isNotEmpty(authorizeCenterAuthorizeRecord)&&authorizeCenterTaskNotes.getTaskNo().equals(taskNo)) {
					try {
						log.info("处理授权中心下发处理taskNo{},authNo{}进入批量解析授权数据成功{}", taskNo,authNo, JSONObject.toJSONString(authorizeCenterAuthorizeRecord));
						result = authorizeService.processChannelAuthorize(authorizeCenterAuthorizeRecord);
						boolean isSuccess = ErrorCodeEnum.SUCCESS.code().equals(result.getString("status"));
						if (!isSuccess) {
							result.put("authId", "0");
						} else {
							try {
								if (result.containsKey("authId")) {
									if (StringUtils.isEmpty(result.getString("authId"))) {
										result.put("authId", "0");
									} else {
										if ("null".equals(result.getString("authId"))) {
											result.put("authId", "0");
										}
									}
								} else {
									result.put("authId", "0");
								}
							} catch (Exception e) {
								result.put("authId", "0");
							}
						}

					} catch (Exception e) {
						log.info("taskNo{},authNo{}[处理授权业务]异常", taskNo, authNo, e);
						result.put("message", "taskNo" + taskNo + ",authNo" + authNo + "[处理授权业务]异常");
						result.put("status", ErrorCodeEnum.FAIL.code());
						result.put("authId", "0");
						redisService.lSet("channelPushAuth_error_" + taskNo, "taskNo" + taskNo + ",authNo" + authNo + "[处理授权业务]异常" + e.fillInStackTrace().toString());
					}
					iterator.remove();
					log.info("处理授权中心下发处理taskNo{},authNo{}进notes删除", taskNo, authNo);
					TaskResultEntity taskResultEntity=new TaskResultEntity();
					taskResultEntity.setAuthorizeCenterAuthorizeRecord(authorizeCenterAuthorizeRecord);
					taskResultEntity.setAuthorizeCenterTaskNotes(authorizeCenterTaskNotes);
					taskResultEntity.setResult(result);
					batchAddTaskResultLogList.add(taskResultEntity);
				}
			}
		}
		return batchAddTaskResultLogList;
	}

	private void finishAuthTask(String taskNo){
		AuthorizeCenterTask authorizeCenterTask=authorizeCenterTaskService.selectByTaskNo(taskNo);
		if(EmptyUtil.isNotEmpty(authorizeCenterTask)){
			log.info("处理授权中心下发处理taskNo{} TaskType:{}",taskNo,authorizeCenterTask.getTaskType());
		}
		authorizeService.delPersonnelAuthorizeCache(taskNo);
		log.info("处理授权中心下发处理taskNo{}缓存删除成功", taskNo);
		authorizeCenterTaskService.updateSyncStatusByTaskNo(taskNo, 1, "处理成功");
		log.info("处理授权中心下发处理taskNo{}状态【1】更新成功", taskNo);

	}
	private boolean checkInLength(MyQueue queue){
		//判断队列长度是否满足设置大小
		int count = queue.size();
		return count<queueLength;
	}
	private MyQueue getQueue(int taskAuthCount){
		MyQueue queue;
		if(taskAuthCount>=queue_0.belongStart()&&taskAuthCount<=queue_0.belongEnd()) {
			queue=queue_0;
			if(!checkInLength(queue)){
				queue=queue_1;
				if(!checkInLength(queue)) {
					queue=queue_2;
					if (!checkInLength(queue)) {
						return null;
					}
				}
			}
		}else if(taskAuthCount>=queue_1.belongStart()&&taskAuthCount<=queue_1.belongEnd()) {
			queue=queue_1;
			if(!checkInLength(queue)){
				queue=queue_2;
				if(!checkInLength(queue)) {
					queue=queue_0;
					if (!checkInLength(queue)) {
						return null;
					}
				}
			}
		} else{
			queue=queue_2;
			if(!checkInLength(queue)){
				queue=queue_1;
				if(!checkInLength(queue)) {
					queue=queue_0;
					if (!checkInLength(queue)) {
						return null;
					}
				}
			}
		}
		return queue;
	}

	private MyQueue checkKeyQueue(String process_key){
		if(process_key.equals(queue_0.queueName())){
			return queue_0;
		}else if(process_key.equals(queue_1.queueName())){
			return queue_1;
		}else {
			return queue_2;
		}
	}

	public void delCacheQueue(){
		for (int i = 0; i < queueCount; i++) {
			String process_key = REDIS_KEY + "_"+i;
			checkKeyQueue(process_key).clear();
			String key=process_key+"-processTaskNoCache";
			redisService.remove(key);
		}
		authorizeCenterTaskService.updateExceptionNull();
	}
}
