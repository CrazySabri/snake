package club.itchina.snake.tools.common;

import club.itchina.snake.tools.Utils;
import com.sun.management.HotSpotDiagnosticMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author: sabri
 * @date: 2018/12/22 17:31
 * @description: JVM 信息获取类
 */
public class JVMUtils {
    private final static String HOT_SPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";

    private final static Logger LOGGER = LoggerFactory.getLogger(JVMUtils.class);

    private static HotSpotDiagnosticMXBean hotSpotMXBean;

    private static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public static void dumpJstack(final String jvmPath) {
        Utils.newThread("dump-jstack-t", (() -> {
            File path = new File(jvmPath);
            if(path.exists() || path.mkdir()){
                File file = new File(path, System.currentTimeMillis() + "-jstatck.log");
                try(FileOutputStream out = new FileOutputStream(file)){
                    JVMUtils.jstack(out);
                }catch(Throwable t){
                    LOGGER.error("Dump JVM cache Error!", t);
                }

            }
        })).start();
    }

    public static void jstack(OutputStream stream) throws Exception {
        PrintStream out = new PrintStream(stream);
        boolean cpuTimeEnabled = threadMXBean.isThreadCpuTimeSupported() && threadMXBean.isThreadCpuTimeEnabled();
        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();

        for(Map.Entry<Thread, StackTraceElement[]> entry:map.entrySet()) {
            Thread t = entry.getKey();
            StackTraceElement[] elements = entry.getValue();

            long tid = t.getId();
            ThreadInfo tt = threadMXBean.getThreadInfo(tid);
            Thread.State state = t.getState();
            long cpuTimeMillis = cpuTimeEnabled? threadMXBean.getThreadCpuTime(tid)/1000000:-1;
            long userTimeMillis = cpuTimeEnabled? threadMXBean.getThreadUserTime(tid)/1000000:-1;

            out.printf("%s id=%d state=%s daemon=%s priority=%s cpu[totle=%sms, user=%sms]",
                    t.getName(), tid, state, t.isDaemon(), t.getPriority(), cpuTimeMillis, userTimeMillis);
            final LockInfo lock = tt.getLockInfo();
            if(lock != null && state != Thread.State.BLOCKED) {
                out.printf("%n    - waiting on <0x%08x> (a %s)", lock.getIdentityHashCode(), lock.getClassName());
                out.printf("%n    - locked <0x%08x> (a %s)", lock.getIdentityHashCode(), lock.getClassName());
            }else if(lock != null && state == Thread.State.BLOCKED) {
                out.printf("%n    - waiting to lock <0x%08x> (a %s)", lock.getIdentityHashCode(), lock.getClassName() );
            }

            if(tt.isSuspended()) {
                out.print("(Suspended)");
            }

            if(tt.isInNative()) {
                out.print(" (running in native) ");
            }
            out.println();

            final LockInfo[] locks = tt.getLockedSynchronizers();
            if(locks.length > 0) {
                out.printf("    Locked synchronizers: count = %d%n", locks.length);
                for(LockInfo l : locks) {
                    out.printf("    - %s%n", l);
                }
                out.println();
            }

        }
    }

    public static void dumpJmap(final String jvmPath) {
        Utils.newThread("dump-jmap-t", () -> jMap(jvmPath, false)).start();
    }

    private static void jMap(String jvmPath, boolean live) {
        File f = new File(jvmPath, System.currentTimeMillis() + "-jmap.log");
        String currentFileName = f.getPath();
        try{
            initHotSpotMXBean();
            if(f.exists()){
                f.delete();
            }
            hotSpotMXBean.dumpHeap(currentFileName, live);
        }catch (Exception e) {
            LOGGER.error("dumpHeap Error!" + currentFileName, e);
        }
    }

    private static void initHotSpotMXBean() throws Exception {
        if(Objects.isNull(hotSpotMXBean)) {
            synchronized (JVMUtils.class) {
                if(Objects.isNull(hotSpotMXBean)){
                    hotSpotMXBean = getHotSpotMXBean();
                }
            }
        }
    }

    public static HotSpotDiagnosticMXBean getHotSpotMXBean() {
        try{
            return AccessController.doPrivileged(new PrivilegedExceptionAction<HotSpotDiagnosticMXBean>() {
                @Override
                public HotSpotDiagnosticMXBean run() throws Exception {
                    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                    Set<ObjectName> s = server.queryNames(new ObjectName(HOT_SPOT_BEAN_NAME), null);
                    Iterator<ObjectName> itr = s.iterator();
                    if(itr.hasNext()) {
                        ObjectName name = itr.next();
                        HotSpotDiagnosticMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server,
                                name.toString(), HotSpotDiagnosticMXBean.class);
                        return bean;
                    }else {
                        return null;
                    }
                }
            });
        }catch(Exception e) {
            LOGGER.error("getHotSpotMXBean Error", e);
            return null;
        }
    }
}
