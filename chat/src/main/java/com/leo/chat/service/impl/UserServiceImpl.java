package com.leo.chat.service.impl;

import com.leo.chat.pojo.entity.Users;
import com.leo.chat.repository.UsersRepository;
import com.leo.chat.service.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 10:58
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private Sid sid;

    @Autowired
    private UsersRepository usersRepository;

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
        // TODO 生成二维码
        user.setQrcode("");
        user.setId(sid.nextShort());
        return usersRepository.save(user);
    }


}
