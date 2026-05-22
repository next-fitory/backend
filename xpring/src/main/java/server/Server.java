package server;

import org.apache.catalina.LifecycleException;

public interface Server {
    void start(int port) throws LifecycleException;
    void start() throws LifecycleException;
    void stop() throws LifecycleException;
    void destroy() throws LifecycleException;
    void init();
}
