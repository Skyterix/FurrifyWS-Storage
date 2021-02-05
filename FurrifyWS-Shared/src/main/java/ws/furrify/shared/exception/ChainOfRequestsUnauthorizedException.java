package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * Exception to not be registered in controller advice to leave an footprint in logs if occurred.
 *
 * @author Skyte
 */
public class ChainOfRequestsUnauthorizedException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.UNAUTHORIZED;

    public ChainOfRequestsUnauthorizedException(String message) {
        super(message);
    }

}