package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class FileUploadCannotCreatePathException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public FileUploadCannotCreatePathException(String message) {
        super(message);
    }

}