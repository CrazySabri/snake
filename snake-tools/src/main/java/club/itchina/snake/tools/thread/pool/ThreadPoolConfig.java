package club.itchina.snake.tools.thread.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author: sabri
 * @date: 2018/12/22 11:02
 * @description: 线程池配置类
 */
public final class ThreadPoolConfig {
    public final static int REJECTED_POLICY_ABORT = 0;
    public final static int REJECTED_POLICY_DISCARD = 1;
    public final static int REJECTED_POLICY_CALLER_RUNS = 2;

    private String name;
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;// 允许缓冲在队列中的任务数 (0:不缓冲、负数：无限大、正数：缓冲的任务数)
    private int keepAliveSeconds;
    private int rejectPolicy = REJECTED_POLICY_ABORT;

    public ThreadPoolConfig(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ThreadPoolConfig setName(String name) {
        this.name = name;
        return this;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public ThreadPoolConfig setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public ThreadPoolConfig setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public ThreadPoolConfig setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
        return this;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public ThreadPoolConfig setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
        return this;
    }

    public int getRejectPolicy() {
        return rejectPolicy;
    }

    public ThreadPoolConfig setRejectPolicy(int rejectPolicy) {
        this.rejectPolicy = rejectPolicy;
        return this;
    }

    public static ThreadPoolConfig buildFixed(String name, int threads, int queueCapacity) {
        return new ThreadPoolConfig(name)
                .setCorePoolSize(threads)
                .setMaxPoolSize(threads)
                .setQueueCapacity(queueCapacity)
                .setKeepAliveSeconds(0);
    }

    public ThreadPoolConfig buildCached(String name){
        return new ThreadPoolConfig(name)
                .setKeepAliveSeconds(0);
    }

    public ThreadPoolConfig build(String name){
        return new ThreadPoolConfig(name);
    }

    public BlockingQueue<Runnable> getQueue() {
        BlockingQueue<Runnable> blockingQueue;
        if(queueCapacity == 0){
            blockingQueue = new SynchronousQueue<>();
        }else if(queueCapacity < 0){
            // 无界队列
            blockingQueue = new LinkedBlockingQueue<>();
        }else{
            // 有界队列
            blockingQueue = new LinkedBlockingQueue<>(queueCapacity);
        }
        return blockingQueue;
    }

    @Override
    public String toString() {
        return "ThreadPoolConfig{" +
                "name='" + name + '\'' +
                ", corePoolSize=" + corePoolSize +
                ", maxPoolSize=" + maxPoolSize +
                ", queueCapacity=" + queueCapacity +
                ", keepAliveSeconds=" + keepAliveSeconds +
                ", rejectPolicy=" + rejectPolicy +
                '}';
    }
}
