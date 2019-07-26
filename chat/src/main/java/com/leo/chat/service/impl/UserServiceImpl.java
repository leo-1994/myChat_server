package com.leo.chat.service.impl;

import com.leo.chat.pojo.entity.Users;
import com.leo.chat.pojo.vo.ResultVO;
import com.leo.chat.repository.UsersRepository;
import com.leo.chat.service.UserService;
import com.leo.chat.utils.FastDFSClient;
import com.leo.chat.utils.FileUtils;
import com.leo.chat.utils.QRCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 10:58
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final Sid sid;

    private final UsersRepository usersRepository;

    private final FastDFSClient fastDFSClient;

    @Autowired
    public UserServiceImpl(Sid sid, UsersRepository usersRepository, FastDFSClient fastDFSClient) {
        this.sid = sid;
        this.usersRepository = usersRepository;
        this.fastDFSClient = fastDFSClient;
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
            String url = fastDFSClient.uploadQRCode(qrCodeFile);
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

    private static final String THUMP = "_80x80.";


}
