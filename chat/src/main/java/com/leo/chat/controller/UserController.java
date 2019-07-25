package com.leo.chat.controller;

import com.leo.chat.pojo.entity.Users;
import com.leo.chat.pojo.vo.ResultVO;
import com.leo.chat.pojo.vo.UserVO;
import com.leo.chat.service.UserService;
import com.leo.chat.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 10:45
 */
@RestController
@RequestMapping("/u")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/registerOrLogin")
    public ResultVO registerOrLogin(@RequestBody Users user) {
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return ResultVO.errorMsg("用户名或密码不能为空");
        }

        // 判断用户名是否存在
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        Users userResult;
        if (usernameIsExist) {
            // 存在则登录
            userResult = userService.selectUser(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
            if (userResult == null) {
                return ResultVO.errorMsg("用户名或密码不正确...");
            }
        } else {
            // 不存在则注册
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            userResult = userService.saveUser(user);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userResult, userVO);
        return ResultVO.ok(userVO);
    }
}
