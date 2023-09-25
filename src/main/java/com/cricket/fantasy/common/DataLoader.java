package com.cricket.fantasy.common;

import com.cricket.fantasy.entity.user.User;
import com.cricket.fantasy.repository.cricket.TeamRepository;
import com.cricket.fantasy.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (args.containsOption("gen-users")) {
            setupUsers();
        }


    }

    private void setupUsers() {
        List<String> names = Arrays.asList("nafath", "siva", "hafeel", "sachin");
        for (String name : names) {
            if (!userRepository.existsByUsername(name)) {
                userRepository.save(new User(name));
            }
        }
    }

    private void generateSquadsForUsers() {

    }
}
