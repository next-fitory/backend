package mvc;

import java.util.Map;

public record HandlerExecution(HandlerMethod handler, Map<String, String> pathVariables) {
}
