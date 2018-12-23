package club.itchina.snake.tools.thread.pool;

import club.itchina.snake.tools.Utils;
import club.itchina.snake.tools.common.JVMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import static club.itchina.snake.tools.thread.pool.ThreadPoolConfig.REJECTED_POLICY_ABORT;
import static club.itchina.snake.tools.thread.pool.ThreadPoolConfig.REJECTED_POLICY_CALLER_RUNS;


/**
 * @author: sabri
 * @date: 2018/12/22 11:03
 * @description:  拒绝执行策略
 */
public final class DumpThreadRejectedHandler implements RejectedExecutionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(DumpThreadRejectedHandler.class);

    private volatile boolean dumping = false;

    private final static String DUMP_DIR = "dump-dir";

    private final ThreadPoolConfig threadPoolConfig;

    private final int rejectedPolicy;

    public DumpThreadRejectedHandler(ThreadPoolConfig threadPoolConfig) {
        this.threadPoolConfig = threadPoolConfig;
        this.rejectedPolicy = threadPoolConfig.getRejectPolicy();
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        LOGGER.warn("one task rejected, poolConfig={}, poolInfo={}", threadPoolConfig, Utils.getPoolInfo(executor));
        if(!dumping){
            dumping = true;
            dumpJVMInfo();
        }
        if(rejectedPolicy == REJECTED_POLICY_ABORT){
            throw new RejectedExecutionException("one task rejected, pool=" +threadPoolConfig.getName());
        }else if(rejectedPolicy == REJECTED_POLICY_CALLER_RUNS){
            if(!executor.isShutdown()){
                r.run();
            }
        }
    }

    private void dumpJVMInfo() {
        LOGGER.info("start dump jvm info");
        // TODO: 输出JVM信息
        JVMUtils.dumpJstack(DUMP_DIR + "/" + threadPoolConfig.getName());
        LOGGER.info("end dump jvm info");
    }


}
