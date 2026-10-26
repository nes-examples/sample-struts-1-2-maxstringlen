package example;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.config.ControllerConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.PlugInConfig;

/**
 * What the browser example shows in its header. The Tiles and before/after labels come from the
 * running Struts config, not the URL, so the header can't disagree with what is deployed.
 */
public class DemoInfo {

    private final HttpServletRequest request;
    private final ServletContext application;

    public DemoInfo(HttpServletRequest request, ServletContext application) {
        this.request = request;
        this.application = application;
    }

    private ModuleConfig module() {
        ModuleConfig mc = (ModuleConfig) request.getAttribute(Globals.MODULE_KEY);
        return mc != null ? mc : (ModuleConfig) application.getAttribute(Globals.MODULE_KEY);
    }

    private ControllerConfig controller() {
        return module().getControllerConfig();
    }

    public boolean usesTiles() {
        PlugInConfig[] plugIns = module().findPlugInConfigs();
        for (int i = 0; i < plugIns.length; i++) {
            if (plugIns[i].getClassName().endsWith(".TilesPlugin")) return true;
        }
        return false;
    }

    /** "After" means one of the example's limit-aware processors is configured. */
    public boolean isAfter() {
        return processorClass().endsWith("LimitAwareRequestProcessor");
    }

    public String plugIns() {
        PlugInConfig[] plugIns = module().findPlugInConfigs();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < plugIns.length; i++) {
            String c = plugIns[i].getClassName();
            if (sb.length() > 0) sb.append(", ");
            sb.append(c.substring(c.lastIndexOf('.') + 1));
        }
        return sb.toString();
    }

    public String processorClass() {
        return controller().getProcessorClass();
    }

    public String maxStringLen() {
        return controller().getMaxStringLen();
    }

    /** Same units as CommonsMultipartRequestHandler: K, M and G are powers of 1024. */
    public long maxStringLenBytes() {
        String v = maxStringLen().trim().toUpperCase();
        long multiplier = 1;
        char unit = v.charAt(v.length() - 1);
        if (unit == 'K') multiplier = 1024L;
        if (unit == 'M') multiplier = 1024L * 1024;
        if (unit == 'G') multiplier = 1024L * 1024 * 1024;
        if (multiplier > 1) v = v.substring(0, v.length() - 1);
        return Long.parseLong(v) * multiplier;
    }

    public boolean isCurrent(String contextPath) {
        return contextPath.equals(request.getContextPath());
    }

    public static String jarOf(Class c) {
        String p = c.getProtectionDomain().getCodeSource().getLocation().getPath();
        return p.substring(p.lastIndexOf('/') + 1);
    }

    public String server() {
        return application.getServerInfo() + ", Java " + System.getProperty("java.version");
    }

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
