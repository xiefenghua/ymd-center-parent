package com.ymd.cloud.common.enumsSupport;

public class Constants {
    /**
     * 系统提示
     **/
    public static final String PRE_FIX = "ymd";
    public static final String LOCK_PWD_ATTRIBUTE = "1";
    public static final String LOCK_FINGER_ATTRIBUTE = "1";
    public static final int redisKeyTime=7*24*60*60;// 秒
    public static final int redisTimeOutTime=30;// 秒

    public static final String MQ_QUEUE_TASK_REDIS_KEY = PRE_FIX+".job.mq.delay.queue.task.key";
    public static final String MQ_CHANNEL_AUTH_PROCESS_REDIS_KEY = PRE_FIX+".job.channel_auth_process.taskNo.key";
    public static final String MQ_AUTHORIZE_SYNC_TOPIC_REDIS_KEY = PRE_FIX+".job.user_authorize_sync_topic.key";

    // 同步状态  1同步 0未同步
    public static final int SYNC = 1;
    public static final int NOT_SYNC = 0;
    // 删除标识 0删除 1正常
    public static final int STATUS_DEL_CODE = 0;
    public static final int STATUS_CODE = 1;
    // 是否有效 1=有效 0=无效
    public static final int FLAG = 1;
    public static final int NOT_FLAG = 0;
    // 授权任务状态 1=同步 0=未同步
    public static final int TASK_STATUS_SYNC = 1;
    public static final int TASK_STATUS_NOT_SYNC = 0;
    //冻结状态 1正常 0冻结
    public static final int FREEZE_STATUS_DONE = 1;
    public static final int FREEZE_STATUS_NOT_DONE = 0;
    // 删除标识 0失败 1成功
    public static final int SUCCESS_CODE = 0;
    public static final int FAIL_CODE = 1;

    // 上报同步状态  1已上报 0未上报
    public static final int NOT_UPLOAD_STATUS =0;
    public static final int UPLOAD_STATUS = 1;
    //下发同步状态 1下发 0未下发
    public static final int NOT_PUSH_STATUS = 0;
    public static final int PUSH_STATUS = 1;

    public static final String commonPhone="18888881234";

}
