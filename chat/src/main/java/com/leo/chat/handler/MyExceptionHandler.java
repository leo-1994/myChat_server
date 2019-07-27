package com.leo.chat.handler;

import com.leo.chat.pojo.vo.ResultVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-27 17:49
 */
@ControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultVO exceptionHandler(HttpServletRequest request, Exception exception) throws Exception {
        return ResultVO.errorMsg("未知错误");
    }
}
