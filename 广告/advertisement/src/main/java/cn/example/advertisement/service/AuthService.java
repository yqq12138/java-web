package cn.example.advertisement.service;


import cn.example.advertisement.model.User;
import cn.example.advertisement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;  // 用户不存在
        }
        
        if (!user.getPassword().equals(password)) {
            return null;  // 密码错误
        }
        
        return user;  // 登录成功
    }
} 