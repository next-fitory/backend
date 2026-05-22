package server.tomcat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mvc.ExceptionHandlerResolver;
import mvc.HandlerAdapter;
import mvc.HandlerExecution;
import mvc.HandlerMapping;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/*")
public class DispatcherServlet extends HttpServlet {
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
        Optional<HandlerExecution> execution = handlerMapping.getHandler(req);
        if (execution.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            handlerAdapter.handle(req, resp, execution.get());
        } catch (Exception e) {
            if (!exceptionHandlerResolver.resolve(e, req, resp)) {
                throw new ServletException("Unhandled exception", e);
            }
        }
    }
}
