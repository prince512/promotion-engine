package com.wasoko.promotion.service.aop;

import com.wasoko.promotion.service.common.Constants;
import com.wasoko.promotion.service.common.ErrorCodes;
import com.wasoko.promotion.service.common.ErrorTypes;
import com.wasoko.promotion.service.exception.*;
import com.wasoko.promotion.service.newrelic.WasokoNewRelic;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * The type Customized response entity exception handler.
 */
@ControllerAdvice
@Slf4j
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * The constant HTTP_REQUEST.
     */
    public static final String HTTP_REQUEST = "HttpRequest";

    @InitBinder
    private void activateDirectFieldAccess(DataBinder dataBinder) {
        dataBinder.initDirectFieldAccess();
    }


    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {
        log.error(Constants.ERROR_OCCURRED_WITH_PARAM, ex.getLocalizedMessage(), ex);
        String errorMessage = String.format("%s Param should be of the type %s", ex.getParameterName(), ex.getParameterType());
        return ResponseEntity.badRequest().body(new RootResponseDTO(false,
                errorMessage, ErrorCodes.PROMO_ER_002.getErrorCode()));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String error = logError(ex, ErrorTypes.BAD_REQUEST);
        return ResponseEntity.badRequest()
                .body(new RootResponseDTO(false, error,
                        ErrorCodes.PROMO_ER_002.getErrorCode()));
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                   HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String error = logError(ex, ErrorTypes.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new RootResponseDTO(false, error,
                        ErrorCodes.PROMO_ER_008.getErrorCode()));
    }

    /**
     * Handle SokoRuntimeException exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(SokoRuntimeException.class)
    public final ResponseEntity<RootResponseDTO> handleSokoRuntimeException(SokoRuntimeException ex,
                                                                            WebRequest request) {
        String error = logError(ex, ErrorTypes.BAD_REQUEST);
        return ResponseEntity.badRequest()
                .body(new RootResponseDTO(false, error,
                        ErrorCodes.PROMO_ER_001.getErrorCode()));
    }


    /**
     * Handle IllegalArgumentException exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<RootResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        String error = logError(ex, ErrorTypes.BAD_REQUEST);
        return ResponseEntity.badRequest()
                .body(new RootResponseDTO(false, error,
                        ErrorCodes.PROMO_ER_002.getErrorCode()));
    }

    /**
     * Handle IllegalArgumentException exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(ConversionFailedException.class)
    public final ResponseEntity<RootResponseDTO> handleConversionFailedException(
            ConversionFailedException ex, WebRequest request) {
        String error = logError(ex, ErrorTypes.BAD_REQUEST);
        return ResponseEntity.internalServerError()
                .body(new RootResponseDTO(false, error,
                        ErrorCodes.PROMO_ER_002.getErrorCode()));
    }

    /**
     * Handle BusinessException exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(BusinessException.class)
    public final ResponseEntity<RootResponseDTO> handleBusinessException(
            BusinessException ex, WebRequest request) {
        String error = logError(ex, ErrorTypes.EXCEPTION_ERROR);
        return ResponseEntity.internalServerError()
                .body(new RootResponseDTO(false, error,
                        ErrorCodes.PROMO_ER_007.getErrorCode()));
    }

    /**
     * Handle RuntimeException exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<RootResponseDTO> handleRuntimeException(RuntimeException ex,
        WebRequest request) {
        String error = logError(ex, ErrorTypes.SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new RootResponseDTO(false, error,
                ErrorCodes.PROMO_ER_001.getErrorCode()));

    }

    /**
     * Handles ConstraintViolationException and generates an appropriate ResponseEntity
     * in response to a runtime constraint violation error.
     *
     * @param ex      The ConstraintViolationException that occurred.
     * @param request The WebRequest associated with the request.
     * @return A ResponseEntity containing a RootResponseDTO with error details.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<RootResponseDTO> handleRuntimeException(ConstraintViolationException ex,
        WebRequest request) {
        String error = logError(ex, ErrorTypes.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new RootResponseDTO(false, error,
                ErrorCodes.PROMO_ER_002.getErrorCode()));
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String error = logErrorWithField(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RootResponseDTO(false, error,
                        ErrorCodes.PROMO_ER_002.getErrorCode()));
    }

    private String logError(Throwable ex, ErrorTypes errorType) {
        log.error(Constants.ERROR_OCCURRED_WITH_PARAM, ex.getLocalizedMessage()); // Deliberately not passing ex here,
        //as we are printing the stack trace down here:
        ex.printStackTrace();
        String errorMessage = String.format("Error occurred: %s with message: %s", errorType.name(), ex.getLocalizedMessage());
        WasokoNewRelic.recordError(ex, HTTP_REQUEST, ex.getClass().getSimpleName());
        return errorMessage;
    }

    private String logErrorWithField(BindException ex) {
        log.error(Constants.ERRORS_OCCURRED_WITH_OVERALL_MSG, ex.getAllErrors(), ex.getLocalizedMessage());// Deliberately not passing ex here,
        //as we are printing the stack trace down here:
        ex.printStackTrace();
        FieldError error = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(
                new FieldError("", "", "Some error occurred"));
        String errorMessage = String.format("Error occurred on field %s with message: %s",
                error.getField(), error.getDefaultMessage());
        WasokoNewRelic.recordError(ex, HTTP_REQUEST, ex.getClass().getSimpleName());
        return errorMessage;
    }

    /**
     * Handle business exception exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(PromotionNotFoundException.class)
    public final ResponseEntity<RootResponseDTO> handlePromotionNotFoundException(PromotionNotFoundException ex, WebRequest request) {
        String error = logError(ex, ErrorTypes.EXCEPTION_ERROR);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RootResponseDTO(false, error, ErrorCodes.PROMO_ER_005.getErrorCode()));
    }

    /**
     * Handles exceptions related to authentication and authorization errors.
     *
     * @param ex      The AuthenticationAndAuthorizationException that was thrown.
     * @param request The WebRequest associated with the exception.
     * @return A ResponseEntity containing a RootResponseDTO with error details.
     */
    @ExceptionHandler(AuthenticationAndAuthorizationException.class)
    public final ResponseEntity<RootResponseDTO> handleAuthenticationAndAuthorizationException(
            AuthenticationAndAuthorizationException ex, WebRequest request) {
        // Respond with HTTP status code UNAUTHORIZED (401)
        return new ResponseEntity<>(new RootResponseDTO(false, logError(ex, ErrorTypes.UNAUTHORIZED), ErrorCodes.PROMO_ER_003.getErrorCode()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle invalid promotion exception exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(InvalidPromotionException.class)
    public final ResponseEntity<RootResponseDTO> handleInvalidPromotionException(InvalidPromotionException ex, WebRequest request) {
        String error = logError(ex, ErrorTypes.EXCEPTION_ERROR);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RootResponseDTO(false, error, ErrorCodes.PROMO_ER_005.getErrorCode()));
    }
}