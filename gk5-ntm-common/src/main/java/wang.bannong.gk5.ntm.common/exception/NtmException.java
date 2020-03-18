package wang.bannong.gk5.ntm.common.exception;

/**
 * Created by bn. on 2019/10/12 2:43 PM
 */
public class NtmException extends RuntimeException {
    private static final long serialVersionUID = 2026390296700097742L;

    public NtmException() {
    }

    public NtmException(String message) {
        super(message);
    }

    public NtmException(Throwable throwable) {
        super(throwable);
    }

    public NtmException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
