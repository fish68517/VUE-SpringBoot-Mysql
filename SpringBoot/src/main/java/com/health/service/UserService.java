package com.health.service;

import com.health.dto.UserUpdateRequest;
import com.health.entity.User;
import com.health.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 用户服务类
 * 处理用户个人信息的获取、更新和查询逻辑
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取用户个人信息
     * 根据用户ID获取用户的完整个人信息
     *
     * @param userId 用户ID
     * @return 用户对象
     * @throws IllegalArgumentException 如果用户不存在
     */
    public User getUserInfo(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        return userOptional.get();
    }

    /**
     * 更新用户个人信息
     * 验证信息格式后更新用户信息到数据库
     *
     * @param userId 用户ID
     * @param updateRequest 用户信息更新请求
     * @return 更新后的用户对象
     * @throws IllegalArgumentException 如果用户不存在或信息格式不正确
     */
    public User updateUserInfo(Long userId, UserUpdateRequest updateRequest) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        User user = userOptional.get();

        // 验证邮箱格式
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isEmpty()) {
            if (!isValidEmail(updateRequest.getEmail())) {
                throw new IllegalArgumentException("邮箱格式不正确");
            }
            user.setEmail(updateRequest.getEmail());
        }

        // 验证姓名
        if (updateRequest.getName() != null && !updateRequest.getName().isEmpty()) {
            if (!isValidName(updateRequest.getName())) {
                throw new IllegalArgumentException("姓名格式不正确");
            }
            user.setName(updateRequest.getName());
        }

        // 验证年龄
        if (updateRequest.getAge() != null) {
            if (!isValidAge(updateRequest.getAge())) {
                throw new IllegalArgumentException("年龄格式不正确，年龄应在0-150之间");
            }
            user.setAge(updateRequest.getAge());
        }

        // 验证性别
        if (updateRequest.getGender() != null && !updateRequest.getGender().isEmpty()) {
            if (!isValidGender(updateRequest.getGender())) {
                throw new IllegalArgumentException("性别格式不正确");
            }
            user.setGender(updateRequest.getGender());
        }

        // 验证电话
        if (updateRequest.getPhone() != null && !updateRequest.getPhone().isEmpty()) {
            if (!isValidPhone(updateRequest.getPhone())) {
                throw new IllegalArgumentException("电话格式不正确");
            }
            user.setPhone(updateRequest.getPhone());
        }

        // 保存更新后的用户信息
        return userRepository.save(user);
    }

    /**
     * 查询用户信息
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户对象
     * @throws IllegalArgumentException 如果用户不存在
     */
    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        return userOptional.get();
    }

    /**
     * 查询所有活跃用户
     * 获取所有状态为ACTIVE的用户
     *
     * @return 活跃用户列表
     */
    public List<User> getAllActiveUsers() {
        return userRepository.findByStatus("ACTIVE");
    }

    /**
     * 查询特定角色的用户
     * 根据角色查询所有用户
     *
     * @param role 用户角色
     * @return 该角色的用户列表
     */
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    /**
     * 验证邮箱格式
     * 使用正则表达式验证邮箱格式
     *
     * @param email 邮箱
     * @return 邮箱格式是否正确
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email);
    }

    /**
     * 验证姓名格式
     * 姓名长度应在1-100之间，只能包含中文、英文、空格和连字符
     *
     * @param name 姓名
     * @return 姓名格式是否正确
     */
    private boolean isValidName(String name) {
        if (name == null || name.isEmpty() || name.length() > 100) {
            return false;
        }
        // 允许中文、英文、空格和连字符
        String nameRegex = "^[\\u4e00-\\u9fa5a-zA-Z\\s-]+$";
        return Pattern.matches(nameRegex, name);
    }

    /**
     * 验证年龄格式
     * 年龄应在0-150之间
     *
     * @param age 年龄
     * @return 年龄格式是否正确
     */
    private boolean isValidAge(Integer age) {
        return age != null && age >= 0 && age <= 150;
    }

    /**
     * 验证性别格式
     * 性别只能是男、女或其他
     *
     * @param gender 性别
     * @return 性别格式是否正确
     */
    private boolean isValidGender(String gender) {
        return gender != null && (gender.equals("男") || gender.equals("女") || gender.equals("其他"));
    }

    /**
     * 验证电话格式
     * 电话长度应在7-20之间，只能包含数字、+、-和空格
     *
     * @param phone 电话
     * @return 电话格式是否正确
     */
    private boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty() || phone.length() < 7 || phone.length() > 20) {
            return false;
        }
        String phoneRegex = "^[0-9+\\-\\s]+$";
        return Pattern.matches(phoneRegex, phone);
    }
}
