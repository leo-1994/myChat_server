package com.leo.chat.repository;

import com.leo.chat.pojo.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 10:58
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

    boolean existsByUsername(String username);

    Users findByUsernameAndPassword(String username, String password);
}
