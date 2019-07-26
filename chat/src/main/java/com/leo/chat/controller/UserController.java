package com.leo.chat.controller;

import com.leo.chat.pojo.bo.UserBO;
import com.leo.chat.pojo.entity.Users;
import com.leo.chat.pojo.vo.ResultVO;
import com.leo.chat.pojo.vo.UserVO;
import com.leo.chat.service.UserService;
import com.leo.chat.utils.FastDFSClient;
import com.leo.chat.utils.FileUtils;
import com.leo.chat.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 10:45
 */
@Slf4j
@RestController
@RequestMapping("/u")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;

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

    @PostMapping("/uploadFaceBase64")
    public ResultVO uploadFaceBase64(@RequestBody UserBO userBo) {
        // 获取前端传过来的base64字符串，然后转换为文件对象上传
        String base64Data = userBo.getFaceData();
        String tempPath = "/Users/leo/tmp/" + userBo.getUserId() + "userface64.png";
        try {
            if (FileUtils.base64ToFile(tempPath, base64Data)) {
                MultipartFile faceFile = FileUtils.fileToMultipart(tempPath);
                if (faceFile == null) {
                    return ResultVO.errorMsg("上传失败，无法转换文件");
                }
                String url = fastDFSClient.uploadBase64(faceFile);
                System.out.println(url);
                Users users = userService.saveUserFace(userBo.getUserId(), url);
                return ResultVO.ok(users);
            }
            return ResultVO.errorMsg("上传失败，无法创建文件");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传出错,e:{}", e.toString());
            return ResultVO.errorMsg("上传失败");
        } finally {
            org.apache.commons.io.FileUtils.deleteQuietly(new File(tempPath));
        }
    }

    @PostMapping("/setNickname")
    public ResultVO setNickname(@RequestBody UserBO userBo) {
        return ResultVO.ok(userService.saveUserNickname(userBo.getUserId(), userBo.getNickname()));
    }
}
