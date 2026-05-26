package server.tomcat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import log.Logger;
import log.XpringLoggerFactory;
import mvc.ExceptionHandlerResolver;
import mvc.HandlerAdapter;
import mvc.HandlerExecution;
import mvc.HandlerMapping;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/*")
public class DispatcherServlet extends HttpServlet {
    private static final Logger log = XpringLoggerFactory.getLogger(DispatcherServlet.class);

    private final HandlerMapping handlerMapping;
    private final HandlerAdapter handlerAdapter;
    private final ExceptionHandlerResolver exceptionHandlerResolver;

    public DispatcherServlet(HandlerMapping handlerMapping, HandlerAdapter handlerAdapter,
                              ExceptionHandlerResolver exceptionHandlerResolver) {
        this.handlerMapping = handlerMapping;
        this.handlerAdapter = handlerAdapter;
        this.exceptionHandlerResolver = exceptionHandlerResolver;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("{} {}", req.getMethod(), req.getRequestURI());

        if ("/favicon.ico".equals(req.getRequestURI())) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        Optional<HandlerExecution> execution = handlerMapping.getHandler(req);
        if (execution.isEmpty()) {
            log.warn("No handler found for {} {}", req.getMethod(), req.getRequestURI());
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            handlerAdapter.handle(req, resp, execution.get());
            log.debug("{} {} -> {}", req.getMethod(), req.getRequestURI(), resp.getStatus());
        } catch (Exception e) {
            if (!exceptionHandlerResolver.resolve(e, req, resp)) {
                log.error("Unhandled exception for {} {}", req.getMethod(), req.getRequestURI());
                log.error("Unhandled exception", e);
                throw new ServletException("Unhandled exception", e);
            }
        }
    }
}
