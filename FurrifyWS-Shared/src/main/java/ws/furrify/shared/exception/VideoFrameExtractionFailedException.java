package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class VideoFrameExtractionFailedException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public VideoFrameExtractionFailedException(String message) {
        super(message);
    }

}