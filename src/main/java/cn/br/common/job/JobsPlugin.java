package cn.br.common.job;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.reflections.Reflections;
import cn.br.common.exception.EfException;
import cn.br.common.exception.UnexpectedException;
import cn.br.common.function.Time;
import cn.br.common.job.anotation.Every;
import cn.br.common.job.anotation.On;
import cn.br.common.job.anotation.OnApplicationStart;
import cn.br.common.job.anotation.OnApplicationStop;
import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务插件
 * @author ZJL
 * onApplicationStart	<br/>
 * afterApplicationStart <br/>
 * 
 *
 */
@Slf4j
public class JobsPlugin {

	public String packageName = "net.chenlin.dp";

	/**
	 * 线程池
	 */
	public static ScheduledThreadPoolExecutor executor;

	/**
	 * 定时任务
	 */
	public static List<Job> scheduledJobs;

	private static ThreadLocal<List<Callable<?>>> afterInvocationActions = new ThreadLocal<List<Callable<?>>>();

	public String getStatus() {
		StringWriter sw = new StringWriter();
		PrintWriter out = new PrintWriter(sw);
		if (executor == null) {
			out.println("Jobs execution pool:");
			out.println("~~~~~~~~~~~~~~~~~~~");
			out.println("(not yet started)");
			return sw.toString();
		}
		out.println("Jobs execution pool:");
		out.println("~~~~~~~~~~~~~~~~~~~");
		out.println("Pool size: " + executor.getPoolSize());
		out.println("Active count: " + executor.getActiveCount());
		out.println("Scheduled task count: " + executor.getTaskCount());
		out.println("Queue size: " + executor.getQueue().size());
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		if (!scheduledJobs.isEmpty()) {
			out.println();
			out.println("Scheduled jobs (" + scheduledJobs.size() + "):");
			out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~");
			for (Job job : scheduledJobs) {
				out.print(job);
				if (job.getClass().isAnnotationPresent(OnApplicationStart.class)
						&& !(job.getClass().isAnnotationPresent(On.class) || job.getClass().isAnnotationPresent(Every.class))) {
					OnApplicationStart appStartAnnotation = job.getClass().getAnnotation(OnApplicationStart.class);
					out.print(" run at application start" + (appStartAnnotation.async() ? " (async)" : "") + ".");
				}

				if (job.getClass().isAnnotationPresent(On.class)) {
					String cron = job.getClass().getAnnotation(On.class).value();
					//TODO
					//					if (cron != null && cron.startsWith("cron.")) {
					//读取配置中 cron.noon=0 0 12 * * ?
					//						cron = Play.configuration.getProperty(cron);
					//					}
					out.print(" run with cron expression " + cron + ".");
				}
				if (job.getClass().isAnnotationPresent(Every.class)) {
					out.print(" run every " + job.getClass().getAnnotation(Every.class).value() + ".");
				}
				if (job.lastRun > 0) {
					out.print(" (last run at " + df.format(new Date(job.lastRun)));
					if (job.wasError) {
						out.print(" with error)");
					} else {
						out.print(")");
					}
				} else {
					out.print(" (has never run)");
				}
				out.println();
			}
		}
		if (!executor.getQueue().isEmpty()) {
			out.println();
			out.println("Waiting jobs:");
			out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			ScheduledFuture[] q = executor.getQueue().toArray(new ScheduledFuture[executor.getQueue().size()]);
			for (ScheduledFuture task : q) {
				out.println(Java.extractUnderlyingCallable((FutureTask<?>) task) + " will run in " + task.getDelay(TimeUnit.SECONDS) + " seconds");
			}
		}
		return sw.toString();
	}

	/**
	 * Jobs插件初始化
	 */
	public void afterApplicationStart() {
		//Set<Class<?>> jobs = ClassUtil.scanPackageBySuper(packageName, Job.class);
		Reflections reflections = new Reflections(packageName);//扫描指定包
		Set<Class<? extends Job>> jobs = reflections.getSubTypesOf(Job.class);

		for (final Class<?> clazz : jobs) {
			// @OnApplicationStart
			if (clazz.isAnnotationPresent(OnApplicationStart.class)) {
				// check if we're going to run the job sync or async
				OnApplicationStart appStartAnnotation = clazz.getAnnotation(OnApplicationStart.class);//先查找程序启动注解
				if (!appStartAnnotation.async()) {//判断不是异步
					// run job sync
					try {
						Job<?> job = createJob(clazz);
						job.run();
						if (job.wasError) {
							if (job.lastException != null) {
								throw job.lastException;
							}
							throw new RuntimeException("@OnApplicationStart Job has failed");
						}
					} catch (InstantiationException e) {
						throw new UnexpectedException("Job could not be instantiated", e);
					} catch (IllegalAccessException e) {
						throw new UnexpectedException("Job could not be instantiated", e);
					} catch (Throwable ex) {
						if (ex instanceof EfException) {
							throw (EfException) ex;
						}
						throw new UnexpectedException(ex);
					}
				} else {
					// run job async 异步执行
					try {
						Job<?> job = createJob(clazz);
						// start running job now in the background
						@SuppressWarnings("unchecked")
						Callable<Job> callable = (Callable<Job>) job;
						executor.submit(callable);
					} catch (InstantiationException ex) {
						throw new UnexpectedException("Cannot instanciate Job " + clazz.getName());
					} catch (IllegalAccessException ex) {
						throw new UnexpectedException("Cannot instanciate Job " + clazz.getName());
					}
				}
			}

			// @On
			if (clazz.isAnnotationPresent(On.class)) {
				try {
					Job<?> job = createJob(clazz);
					scheduleForCRON(job);//CRON定时任务
				} catch (InstantiationException ex) {
					throw new UnexpectedException("Cannot instanciate Job " + clazz.getName());
				} catch (IllegalAccessException ex) {
					throw new UnexpectedException("Cannot instanciate Job " + clazz.getName());
				}
			}
			// @Every
			if (clazz.isAnnotationPresent(Every.class)) {
				try {
					Job job = createJob(clazz);
					String value = job.getClass().getAnnotation(Every.class).value();
					//TODO
					//					if (value.startsWith("cron.")) {
					//						value = Play.configuration.getProperty(value);
					//					}
					value = Expression.evaluate(value, value).toString();
					if (!"never".equalsIgnoreCase(value)) {
						executor.scheduleWithFixedDelay(job, Time.parseDuration(value), Time.parseDuration(value), TimeUnit.SECONDS);
					}
				} catch (InstantiationException ex) {
					throw new UnexpectedException("Cannot instanciate Job " + clazz.getName());
				} catch (IllegalAccessException ex) {
					throw new UnexpectedException("Cannot instanciate Job " + clazz.getName());
				}
			}
		}
	}

	/**
	 * 创建任务
	 * @param clazz
	 */
	private Job<?> createJob(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		Job<?> job = (Job<?>) clazz.newInstance();
		scheduledJobs.add(job);
		return job;
	}

	/**
	 * 启动定时任务
	 */
	public void onApplicationStart() {
		//TODO
		//		int core = Integer.parseInt(Play.configuration.getProperty("play.jobs.pool", "10"));
		int core = 10;
		executor = new ScheduledThreadPoolExecutor(core, new PThreadFactory("jobs"), new ThreadPoolExecutor.AbortPolicy());
		scheduledJobs = new ArrayList<Job>();
	}

	public static <V> void scheduleForCRON(Job<V> job) {
		if (!job.getClass().isAnnotationPresent(On.class)) {
			return;
		}
		String cron = job.getClass().getAnnotation(On.class).value();
		//TODO
		//		if (cron.startsWith("cron.")) {
		//			cron = Play.configuration.getProperty(cron);
		//		}
		cron = Expression.evaluate(cron, cron).toString();
		if (cron == null || cron.isEmpty() || "never".equalsIgnoreCase(cron)) {
			log.info("Skipping job {}, cron expression is not defined", job.getClass().getName());
			return;
		}
		try {
			Date now = new Date();
			cron = Expression.evaluate(cron, cron).toString();
			CronExpression cronExp = new CronExpression(cron);//定时任务表达式
			Date nextDate = cronExp.getNextValidTimeAfter(now);//获取下一次执行的时间
			if (nextDate == null) {
				log.warn("The cron expression for job {} doesn't have any match in the future, will never be executed", job.getClass().getName());
				return;
			}
			if (nextDate.equals(job.nextPlannedExecution)) {//如果是空
				// Bug #13: avoid running the job twice for the same time
				// (happens when we end up running the job a few minutes before
				// the planned time)
				Date nextInvalid = cronExp.getNextInvalidTimeAfter(nextDate);//避免同时运行2次任务
				nextDate = cronExp.getNextValidTimeAfter(nextInvalid);//返回给定时间之后的下一次任务执行时间
			}
			job.nextPlannedExecution = nextDate;
			//放入线程池中等待执行
			executor.schedule((Callable<V>) job, nextDate.getTime() - now.getTime(), TimeUnit.MILLISECONDS);
			job.executor = executor;
		} catch (Exception ex) {
			throw new UnexpectedException(ex);
		}
	}

	/**
	 * 程序关闭
	 */
	public void onApplicationStop() {
		/**
		 * 获取所有继承了Job的类
		 */
		Reflections reflections = new Reflections(packageName);
		Set<Class<? extends Job>> jobs = reflections.getSubTypesOf(Job.class);

		//Set<Class<?>> jobs = ClassUtil.scanPackageBySuper(packageName, Job.class);
		//List<Class> jobs = classloader.getAssignableClasses(Job.class);

		for (final Class clazz : jobs) {
			// @OnApplicationStop
			if (clazz.isAnnotationPresent(OnApplicationStop.class)) {
				try {
					Job<?> job = createJob(clazz);
					job.run();
					if (job.wasError) {
						if (job.lastException != null) {
							throw job.lastException;
						}
						throw new RuntimeException("@OnApplicationStop Job has failed");
					}
				} catch (InstantiationException e) {
					throw new UnexpectedException("Job could not be instantiated", e);
				} catch (IllegalAccessException e) {
					throw new UnexpectedException("Job could not be instantiated", e);
				} catch (Throwable ex) {
					if (ex instanceof EfException) {
						throw (EfException) ex;
					}
					throw new UnexpectedException(ex);
				}
			}
		}
		executor.shutdownNow();
		executor.getQueue().clear();
	}

	/**
	 * 任务执行之前
	 */
	public void beforeInvocation() {
		afterInvocationActions.set(new LinkedList<Callable<?>>());
	}

	/**
	 * 任务执行之后
	 */
	public void afterInvocation() {
		List<Callable<?>> currentActions = afterInvocationActions.get();
		afterInvocationActions.set(null);
		for (Callable<?> callable : currentActions) {
			executor.submit(callable);
		}
	}

	/**
	 * 添加任务之后的
	 * @param c
	 */
	// default visibility, because we want to use this only from Job.java
	static void addAfterRequestAction(Callable<?> c) {
		afterInvocationActions.get().add(c);
	}
}
