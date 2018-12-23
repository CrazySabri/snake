package club.itchina.snake.tools;

import club.itchina.snake.tools.thread.NamedThreadFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: sabri
 * @date: 2018/12/22 17:31
 * @description: 耐得住寂寞，守得住繁华--寂寞高手
 */
public class Utils {

    private final static NamedThreadFactory NAMED_THREAD_FACTORY = new NamedThreadFactory();

    public static Thread newThread(String name, Runnable r) {
        return NAMED_THREAD_FACTORY.newThread(name, r);
    }

    public static Map<String, Object> getPoolInfo(ThreadPoolExecutor executor){
        Map<String, Object> info = new HashMap<>(5);
        info.put("corePoolSize", executor.getCorePoolSize());
        info.put("maxPoolSize", executor.getMaximumPoolSize());
        info.put("activeCount(workingThread)", executor.getActiveCount());
        info.put("poolSize(workThread)", executor.getPoolSize());
        info.put("queueSize(blockedTask)", executor.getQueue().size());
        return info;
    }
}
