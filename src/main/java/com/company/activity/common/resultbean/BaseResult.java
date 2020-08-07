package com.company.activity.common.resultbean;

import com.company.activity.common.enums.ResultStatus;

public class BaseResult {
    private ResultStatus status;
    private int code;
    private String message;

    protected BaseResult(ResultStatus status, String message) {
        this.code = status.getCode();
        this.status = status;
        this.message = message;
    }

    protected BaseResult(ResultStatus status) {
        this.code = status.getCode();
        this.message = status.getMessage();
        this.status = status;
    }

    public static boolean isSuccess(BaseResult result) {
        return result != null && result.status == ResultStatus.SUCCESS && result.getCode() == ResultStatus.SUCCESS.getCode();
    }

    public BaseResult withError(ResultStatus status) {
        this.status = status;
        return this;
    }

    public BaseResult withError(String message) {
        this.status = ResultStatus.SYSTEM_ERROR;
        this.message = message;
        return this;
    }

    public BaseResult withError(int code, String message) {
        this.code = code;
        this.message = message;
        return this;
    }

    public BaseResult success() {
        this.status = ResultStatus.SUCCESS;
        return this;
    }
    public ResultStatus getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message == null ? this.status.getMessage() : this.message;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
