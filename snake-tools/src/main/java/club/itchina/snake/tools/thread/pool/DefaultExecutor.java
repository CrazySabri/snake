package club.itchina.snake.tools.thread.pool;

import java.util.concurrent.*;

/**
 * @author: sabri
 * @date: 2018/12/22 11:02
 * @description: 默认的规范化的Executor
 */
public final class DefaultExecutor extends ThreadPoolExecutor {
    public DefaultExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                           TimeUnit unit, BlockingQueue<Runnable> workQueue,
                           ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }
}
