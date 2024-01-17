package com.wasoko.promotion.service.aop;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Root response dto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RootResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1027088842259947392L;
    private boolean status;
    private Integer successCode;
    private String errorMessage;
    private Serializable data;
    private Integer errorCode;

    /**
     * Instantiates a new Root response dto.
     *
     * @param status      the status
     * @param errorMessage the error message
     * @param errorCode    the error code
     * @author Prince
     */
    public RootResponseDTO(boolean status, String errorMessage, int errorCode) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    /**
     * Instantiates a new Root response dto.
     *
     * @param data the data
     * @author Prince
     */
    public <T> RootResponseDTO(T data) {
        this.status = true;
        this.successCode = HttpStatus.OK.value();
        this.data = (Serializable) data;
    }
}