package mvc;

public enum HttpStatus {
    // 2xx
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),
    // 3xx
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    // 4xx
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CONFLICT(409, "Conflict"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    // 5xx
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable");

    private final int value;
    private final String reasonPhrase;

    HttpStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int getValue() { return value; }
    public String getReasonPhrase() { return reasonPhrase; }

    public boolean is2xxSuccessful() { return value >= 200 && value < 300; }
    public boolean is4xxClientError() { return value >= 400 && value < 500; }
    public boolean is5xxServerError() { return value >= 500 && value < 600; }
}
