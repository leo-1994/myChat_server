package com.leo.chat.pojo.vo;

import lombok.Data;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 10:46
 */
@Data
public class ResultVO {

    private Integer code;
    private String msg;
    private Object data;

    private ResultVO(Integer code) {
        this.code = code;
    }

    private ResultVO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private ResultVO(Integer code, Object data) {
        this.code = code;
        this.data = data;
    }

    public static ResultVO errorMsg(String msg) {
        return new ResultVO(CodeEnum.ERROR.code, msg);
    }

    public static ResultVO ok() {
        return new ResultVO(CodeEnum.OK.code);
    }

    public static ResultVO ok(Object data) {
        return new ResultVO(CodeEnum.OK.code, data);
    }

    enum CodeEnum {

        /**
         * 正常
         */
        OK(200),
        /**
         * 错误
         */
        ERROR(500);

        int code;

        CodeEnum(int code) {
            this.code = code;
        }
    }
}
