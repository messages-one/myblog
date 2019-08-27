package references;

import com.sun.management.HotSpotDiagnosticMXBean;

import javax.management.MBeanServer;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class HeapDump {

    private static final String HOTSPOT_DIAGNOSTIC_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";

    private static final boolean DUMP_LIVE = true;

    private static class InstanceHolder {
        private static final HotSpotDiagnosticMXBean HOTSPOT_DIAGNOSTIC_MX_BEAN;

        static {
            System.setProperty("jdk.management.heapdump.allowAnyFileSuffix", "true");
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            try {
                HOTSPOT_DIAGNOSTIC_MX_BEAN = ManagementFactory.newPlatformMXBeanProxy(server,
                        HOTSPOT_DIAGNOSTIC_BEAN_NAME, HotSpotDiagnosticMXBean.class);
            } catch (Exception e) {
                throw new RuntimeException("Could not create hotspot diasgnostic bean!", e);
            }
        }
    }

    public static HotSpotDiagnosticMXBean getHotSpotDiagnosticMXBean() {
        return InstanceHolder.HOTSPOT_DIAGNOSTIC_MX_BEAN;
    }

    public static void dumpHeap(String fileName, boolean dumpLive) {
        try {
            getHotSpotDiagnosticMXBean().dumpHeap(fileName, dumpLive);
        } catch (IOException e) {
            throw new RuntimeException("Could not take heap dump!", e);
        }
    }

    public static void dumpHeap(String fileName) {
        dumpHeap(fileName, DUMP_LIVE);
    }

    public static void main(String[] args) {
        HeapDump.dumpHeap("heap-dumps/test.hprof");
        HeapDump.dumpHeap("heap-dumps/test2.hprof");
        //To analyze run -> jhat -J-Xmx1g -port 8000 test.hprof
    }
}