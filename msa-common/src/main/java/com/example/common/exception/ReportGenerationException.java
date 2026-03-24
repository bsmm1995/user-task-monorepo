package com.example.common.exception;

import lombok.Getter;

@Getter
public class ReportGenerationException extends DomainException {
    public ReportGenerationException(String message, Throwable cause) {
        super(message, "REPORT_GENERATION_ERROR");
    }
}
