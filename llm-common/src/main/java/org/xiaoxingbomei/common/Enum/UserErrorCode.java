package org.xiaoxingbomei.common.Enum;

import org.xiaoxingbomei.common.entity.ErrorCode;

public enum UserErrorCode implements ErrorCode
{
    XXX1("",""),
    XXX2("","");


    // ============================================================
    private String code;

    private String message;

    UserErrorCode(String code, String message)
    {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    // ============================================================







}
