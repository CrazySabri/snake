package club.itchina.snake.tools.thread;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: sabri
 * @date: 2018/12/22 11:02
 * @description: 耐得住寂寞，守得住繁华--寂寞高手
 */
public final class NamedPoolThreadFactory implements ThreadFactory {

    private final static AtomicInteger poolNum = new AtomicInteger(1);

    private final AtomicInteger threadNum = new AtomicInteger(1);
    private final ThreadGroup threadGroup;
    private final String namePre;
    private final boolean isDaemon;

    public NamedPoolThreadFactory(String namePre) {
        this(namePre, true);
    }

    public NamedPoolThreadFactory(String namePre, boolean isDaemon) {
        // TODO: 此处原理
        SecurityManager manager = System.getSecurityManager();
        // TODO:manager获取调用与Thread调用是一样的，为什么借助manager？
        if(Objects.nonNull(manager)){
            threadGroup = manager.getThreadGroup();
        }else{
            threadGroup = Thread.currentThread().getThreadGroup();
        }
        this.isDaemon = isDaemon;
        this.namePre = namePre + "-p-" + poolNum.getAndIncrement() + "-t-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(threadGroup, r, namePre
                + threadNum.getAndIncrement(), 0);
        // TODO:此处作用：
        thread.setContextClassLoader(NamedPoolThreadFactory.class.getClassLoader());
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.setDaemon(isDaemon);
        return thread;
    }
}
