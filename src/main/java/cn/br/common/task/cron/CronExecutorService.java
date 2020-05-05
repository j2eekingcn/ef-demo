package cn.br.common.task.cron;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;

import cn.br.common.task.Task;


/**
 * Executor service that schedules a runnable task for execution via a cron expression.
 */
public interface CronExecutorService extends ExecutorService {

    ScheduledFuture<?> submit(Task task);

}