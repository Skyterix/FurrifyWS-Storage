package ws.furrify.posts.exception;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

/**
 * Class contains error messages enum which can be accessed using getErrorMessage().
 * Messages can contain parenthesis which can be filled using ex. Your id is {0}! and use method getErrorMessage(3);
 * Messages also can contain multiple parenthesis all can be filled using ex. Your id is {0} and your name is {1}! and use method getErrorMessage(new String[]{"3", "John"})
 * It would be nice to also indicate what value was filled for ex. [uuid={0}].
 * <p>
 * Each exception that wants to use those error messages should be registered in RestExceptionControllerAdvice.
 *
 * @author Skyte
 */
@RequiredArgsConstructor
public enum Errors {
    /**
     * Exception types.
     */

    NO_RECORD_FOUND("Record [uuid={0}] was not found."),
    NO_TAG_FOUND("Tag [value={0}] was not found."),
    RECORD_ALREADY_EXISTS("Record [uuid={0}] already exists."),
    TAG_ALREADY_EXISTS("Tag [value={0}] already exists."),
    BAD_REQUEST("Given request data is invalid."),
    UNIDENTIFIED("Unknown error occurred.");

    private final String errorMessage;

    public String getErrorMessage(Object[] data) {
        return MessageFormat.format(errorMessage, data);
    }

    public String getErrorMessage(Object data) {
        return MessageFormat.format(errorMessage, data);
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}