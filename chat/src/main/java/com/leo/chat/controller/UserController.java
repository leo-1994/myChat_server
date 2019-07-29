package com.leo.chat.controller;

import com.leo.chat.enums.OperateFriendRequestTypeEnum;
import com.leo.chat.enums.SearchFriendsStatusEnum;
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
import java.util.ArrayList;

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
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp", ".png");
            if (FileUtils.base64ToFile(tempFile.getPath(), base64Data)) {
                MultipartFile faceFile = FileUtils.fileToMultipart(tempFile.getPath());
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
            if (tempFile != null) {
                if (!tempFile.delete()) {
                    log.error("tempFile:{} 删除失败", tempFile.getPath());
                }
            }
        }
    }

    @PostMapping("/setNickname")
    public ResultVO setNickname(@RequestBody UserBO userBo) {
        return ResultVO.ok(userService.saveUserNickname(userBo.getUserId(), userBo.getNickname()));
    }

    /**
     * 根据账号做匹配查询而不是模糊查询
     *
     * @param myUserId
     * @param friendUsername
     * @return
     */
    @PostMapping("/search")
    public ResultVO searchUser(String myUserId, String friendUsername) {
        // 判断 myUserId friendUsername 不能为空
        if (StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)) {
            return ResultVO.errorMsg("");
        }
        // 搜索的用户不存在，返回无此用户
        // 搜索的账号是自己，返回不能添加自己
        // 搜索的朋友已经是你的好友，返回该用户已是你的好友
        SearchFriendsStatusEnum status = userService.preconditionSearchFriends(myUserId, friendUsername);
        if (!SearchFriendsStatusEnum.SUCCESS.equals(status)) {
            return ResultVO.errorMsg(status.msg);
        }
        Users users = userService.selectUserByUsername(friendUsername);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(users, userVO);
        return ResultVO.ok(userVO);
    }

    /**
     * 添加好友
     *
     * @param myUserId
     * @param friendUsername
     * @return
     */
    @PostMapping("/addFriendRequest")
    public ResultVO addFriendRequest(String myUserId, String friendUsername) {
// 判断 myUserId friendUsername 不能为空
        if (StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)) {
            return ResultVO.errorMsg("");
        }
        // 搜索的用户不存在，返回无此用户
        // 搜索的账号是自己，返回不能添加自己
        // 搜索的朋友已经是你的好友，返回该用户已是你的好友
        SearchFriendsStatusEnum status = userService.preconditionSearchFriends(myUserId, friendUsername);
        if (!SearchFriendsStatusEnum.SUCCESS.equals(status)) {
            return ResultVO.errorMsg(status.msg);
        }
        userService.sendFriendsRequest(myUserId, friendUsername);
        return ResultVO.ok();
    }

    @PostMapping("/queryFriendRequests")
    public ResultVO queryFriendRequests(String userId) {
        if (StringUtils.isBlank(userId)) {
            return ResultVO.errorMsg("");
        }
        return ResultVO.ok(userService.getFriendRequestList(userId));
    }

    @PostMapping("operateFriendRequest")
    public ResultVO operateFriendRequest(String acceptUserId, String sendUserId, Integer operateType) {
        if (StringUtils.isBlank(acceptUserId) || StringUtils.isBlank(sendUserId)) {
            return ResultVO.errorMsg("");
        }
        if (OperateFriendRequestTypeEnum.IGNORE.code.equals(operateType)) {
            userService.ignoreFriendRequest(acceptUserId, sendUserId);
        } else if (OperateFriendRequestTypeEnum.PASS.code.equals(operateType)) {
            userService.passFriendRequest(acceptUserId, sendUserId);
        }
        return ResultVO.ok(userService.getMyFriends(acceptUserId));
    }

    @PostMapping("/myFriends")
    public ResultVO myFriends(String userId) {
        if (StringUtils.isBlank(userId)) {
            return ResultVO.errorMsg("");
        }
        return ResultVO.ok(userService.getMyFriends(userId));
    }

    @PostMapping("/getUnReadMsgList")
    public ResultVO getUnReadMsgList(String acceptUserId) {
        return ResultVO.ok(userService.getUnReadMsgList(acceptUserId));
    }

}
