package mvc;

import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

class ResponseWriter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static void write(Object result, HttpServletResponse resp) throws IOException {
        if (result instanceof ResponseEntity<?> re) {
            resp.setStatus(re.getStatus().getValue());
            if (re.getLocation() != null) {
                resp.setHeader("Location", re.getLocation());
                return;
            }
            if (re.getBody() != null) {
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write(objectMapper.writeValueAsString(re.getBody()));
            }
        } else if (result != null) {
            resp.setStatus(HttpStatus.OK.getValue());
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(objectMapper.writeValueAsString(result));
        }
    }
}
