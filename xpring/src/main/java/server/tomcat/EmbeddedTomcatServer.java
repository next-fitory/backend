package server.tomcat;

import jakarta.servlet.Filter;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import server.Server;

import java.util.List;

public class EmbeddedTomcatServer implements Server {
    private final Tomcat tomcat = new Tomcat();
    private final DispatcherServlet dispatcherServlet;
    private final List<Filter> filters;

    public EmbeddedTomcatServer(DispatcherServlet dispatcherServlet, List<Filter> filters) {
        this.dispatcherServlet = dispatcherServlet;
        this.filters = filters;
    }

    @Override
    public void init() {
        tomcat.setBaseDir(System.getProperty("java.io.tmpdir"));
        tomcat.getConnector();

        Context ctx = tomcat.addContext("", null);

        for (int i = 0; i < filters.size(); i++) {
            Filter filter = filters.get(i);
            String name = filter.getClass().getSimpleName() + "-" + i;

            FilterDef filterDef = new FilterDef();
            filterDef.setFilterName(name);
            filterDef.setFilter(filter);
            ctx.addFilterDef(filterDef);

            FilterMap filterMap = new FilterMap();
            filterMap.setFilterName(name);
            filterMap.addURLPattern("/*");
            ctx.addFilterMap(filterMap);
        }

        Tomcat.addServlet(ctx, "dispatcher", dispatcherServlet);
        ctx.addServletMappingDecoded("/*", "dispatcher");
    }

    @Override
    public void start(int port) throws LifecycleException {
        tomcat.setPort(port);
        init();
        tomcat.start();
    }

    @Override
    public void start() throws LifecycleException {
        start(8080);
    }

    @Override
    public void stop() throws LifecycleException {
        tomcat.stop();
    }

    @Override
    public void destroy() throws LifecycleException {
        tomcat.destroy();
    }
}
