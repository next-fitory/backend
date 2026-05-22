package mvc;

public class ResponseEntity<T> {
    private final HttpStatus status;
    private final T body;

    private ResponseEntity(HttpStatus status, T body) {
        this.status = status;
        this.body = body;
    }

    // --- 정적 팩토리 ---

    public static <T> ResponseEntity<T> ok(T body) {
        return new ResponseEntity<>(HttpStatus.OK, body);
    }

    public static <T> ResponseEntity<T> created(T body) {
        return new ResponseEntity<>(HttpStatus.CREATED, body);
    }

    public static ResponseEntity<Void> noContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT, null);
    }

    public static <T> ResponseEntity<T> badRequest(T body) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST, body);
    }

    public static <T> ResponseEntity<T> notFound(T body) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND, body);
    }

    public static <T> ResponseEntity<T> unauthorized(T body) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED, body);
    }

    public static <T> ResponseEntity<T> forbidden(T body) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN, body);
    }

    public static <T> ResponseEntity<T> internalServerError(T body) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR, body);
    }

    public static <T> ResponseEntity<T> status(HttpStatus status, T body) {
        return new ResponseEntity<>(status, body);
    }

    public HttpStatus getStatus() { return status; }
    public T getBody() { return body; }
}
