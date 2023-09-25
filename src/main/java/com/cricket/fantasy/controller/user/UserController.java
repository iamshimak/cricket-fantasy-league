package com.cricket.fantasy.controller.user;

import com.cricket.fantasy.controller.generic.impl.ControllerGenericImpl;
import com.cricket.fantasy.entity.user.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class UserController extends ControllerGenericImpl<User> {
}
