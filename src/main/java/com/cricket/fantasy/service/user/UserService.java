package com.cricket.fantasy.service.user;

import com.cricket.fantasy.entity.user.User;
import com.cricket.fantasy.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> saveUsers(List<String> usernames) {
        List<User> newUsers = new ArrayList<>();
        for (String username : usernames) {
            if (!userRepository.existsByUsername(username)) {
                newUsers.add(new User(username));
            }
        }

        userRepository.saveAllAndFlush(newUsers);
        return userRepository.findByUsernameIn(usernames);
    }
}
