package com.leo.chat.service.impl;

import com.leo.chat.enums.SearchFriendsStatusEnum;
import com.leo.chat.pojo.entity.FriendsRequest;
import com.leo.chat.pojo.entity.MyFriends;
import com.leo.chat.pojo.entity.Users;
import com.leo.chat.pojo.vo.FriendRequestVO;
import com.leo.chat.repository.FriendsRequestRepository;
import com.leo.chat.repository.MyFriendsRepository;
import com.leo.chat.repository.UsersRepository;
import com.leo.chat.service.UserService;
import com.leo.chat.utils.FastDFSClient;
import com.leo.chat.utils.FileUtils;
import com.leo.chat.utils.QRCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 10:58
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final Sid sid;

    private final UsersRepository usersRepository;

    private final MyFriendsRepository myFriendsRepository;

    private final FriendsRequestRepository friendsRequestRepository;

    private final FastDFSClient fastDfsClient;

    @Autowired
    public UserServiceImpl(Sid sid, UsersRepository usersRepository, FastDFSClient fastDfsClient, MyFriendsRepository myFriendsRepository, FriendsRequestRepository friendsRequestRepository) {
        this.sid = sid;
        this.usersRepository = usersRepository;
        this.fastDfsClient = fastDfsClient;
        this.myFriendsRepository = myFriendsRepository;
        this.friendsRequestRepository = friendsRequestRepository;
    }

    @Override
    public boolean queryUsernameIsExist(String username) {
        return usersRepository.existsByUsername(username);
    }

    @Override
    public Users selectUser(String username, String pwd) {
        return usersRepository.findByUsernameAndPassword(username, pwd);
    }

    @Override
    public Users saveUser(Users user) {
        user.setFaceImage("");
        user.setFaceImageBig("");
        user.setQrcode(getQrCode(user.getUsername()));
        user.setId(sid.nextShort());
        return usersRepository.save(user);
    }

    private String getQrCode(String username) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp_qr", ".png");
            // myChat_qrcode:[username]
            QRCodeUtils.createQRCode(tempFile.getPath(), "myChat_qrcode:" + username);
            MultipartFile qrCodeFile = FileUtils.fileToMultipart(tempFile.getPath());
            Assert.notNull(qrCodeFile, "文件转换失败");
            String url = fastDfsClient.uploadQRCode(qrCodeFile);
            System.out.println(url);
            return url;
        } catch (IOException e) {
            log.error("生成二维码失败");
            e.printStackTrace();
            return "";
        } finally {
            if (tempFile != null) {
                if (!tempFile.delete()) {
                    log.error("tempFile:{} 删除失败", tempFile.getPath());
                }
            }
        }
    }


    @Override
    public Users saveUserFace(String userId, String url) {
        Optional<Users> optional = usersRepository.findById(userId);
        if (optional.isPresent()) {
            Users users = optional.get();
            users.setFaceImageBig(url);
            users.setFaceImage(getThumpImgUrl(url));
            return usersRepository.save(users);
        }
        return null;
    }

    private String getThumpImgUrl(String url) {
        String[] strings = url.split("\\.");
        return strings[0] + THUMP + strings[1];
    }

    @Override
    public Users saveUserNickname(String userId, String nickname) {
        Optional<Users> optional = usersRepository.findById(userId);
        if (optional.isPresent()) {
            Users users = optional.get();
            users.setNickname(nickname);
            return usersRepository.save(users);
        }
        return null;
    }

    @Override
    public SearchFriendsStatusEnum preconditionSearchFriends(String myUserId, String friendUsername) {
        Users friend = usersRepository.findByUsername(friendUsername);
        // 如果搜索的用户不存在，返回 无此用户
        if (friend == null) {
            return SearchFriendsStatusEnum.USER_NOT_EXIST;
        }
        // 如果搜索账号是你自己，返回 不能添加自己
        if (friend.getId().equals(myUserId)) {
            return SearchFriendsStatusEnum.NOT_YOURSELF;
        }
        // 如果搜索的朋友已经是你的好友，返回 该用户已经是你的好友
        if (myFriendsRepository.existsByMyUserIdAndMyFriendUserId(myUserId, friend.getId())) {
            return SearchFriendsStatusEnum.ALREADY_FRIENDS;
        }
        return SearchFriendsStatusEnum.SUCCESS;
    }

    @Override
    public Users selectUserByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    public void sendFriendsRequest(String myUserId, String friendUsername) {
        Users friend = usersRepository.findByUsername(friendUsername);
        if (friendsRequestRepository.existsBySendUserIdAndAcceptUserId(myUserId, friend.getId())) {
            return;
        }
        FriendsRequest friendsRequest = new FriendsRequest();
        friendsRequest.setId(sid.nextShort());
        friendsRequest.setSendUserId(myUserId);
        friendsRequest.setAcceptUserId(friend.getId());
        friendsRequest.setRequestDateTime(new Date());
        friendsRequestRepository.save(friendsRequest);
    }

    @Override
    public List<FriendRequestVO> getFriendRequestList(String userId) {
        List<FriendsRequest> list = friendsRequestRepository.findAllByAcceptUserId(userId);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(t -> {
            Optional<Users> optional = usersRepository.findById(t.getSendUserId());
            if (!optional.isPresent()) {
                return null;
            }
            Users sendUser = optional.get();
            return new FriendRequestVO(sendUser);
        }).collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void passFriendRequest(String acceptUserId, String sendUserId) {
        FriendsRequest request = friendsRequestRepository.findBySendUserIdAndAcceptUserId(sendUserId, acceptUserId);
        if (request == null) {
            return;
        }
        // 添加双方的好友关系
        myFriendsRepository.save(new MyFriends(sid.nextShort(), acceptUserId, sendUserId));
        myFriendsRepository.save(new MyFriends(sid.nextShort(), sendUserId, acceptUserId));
        // 删除好友请求
        friendsRequestRepository.delete(request);
    }

    @Override
    public void ignoreFriendRequest(String acceptUserId, String sendUserId) {
        FriendsRequest request = friendsRequestRepository.findBySendUserIdAndAcceptUserId(sendUserId, acceptUserId);
        if (request != null) {
            // 删除好友请求
            friendsRequestRepository.delete(request);
        }
    }

    private static final String THUMP = "_80x80.";


}
