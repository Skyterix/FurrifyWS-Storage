package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class ArrayCannotBeEmptyException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public ArrayCannotBeEmptyException(String message) {
        super(message);
    }

}