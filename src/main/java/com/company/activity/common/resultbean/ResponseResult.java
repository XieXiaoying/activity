package com.company.activity.common.resultbean;

import com.company.activity.common.enums.ResultStatus;

import java.io.Serializable;

public class ResponseResult<T> extends BaseResult implements Serializable {
    private static final long serialVersionUID = 867942019728196379L;
    private T data;
    private Integer count;

    protected ResponseResult(ResultStatus status, String message) {
        super(status, message);
    }
    protected ResponseResult(ResultStatus status) {
        super(status);
    }
    public static <T> ResponseResult<T> build() {
        return new ResponseResult(ResultStatus.SUCCESS, (String)null);
    }

    public static <T> ResponseResult<T> build(String message) {
        return new ResponseResult(ResultStatus.SUCCESS, message);
    }

    public static <T> ResponseResult<T> error(ResultStatus status) {
        return new ResponseResult<T>(status);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCount() {
        return this.count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void success(T value) {
        this.success();
        this.data = value;
        this.count = 0;
    }

}