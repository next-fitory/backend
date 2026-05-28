package security;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("로그인이 만료되었습니다. 다시 로그인해 주세요.");
    }
}
