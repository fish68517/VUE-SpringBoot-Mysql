package com.health.service;

import com.health.dto.DoctorRegisterRequest;
import com.health.dto.LoginRequest;
import com.health.dto.LoginResponse;
import com.health.dto.RegisterRequest;
import com.health.entity.Doctor;
import com.health.entity.User;
import com.health.repository.DoctorRepository;
import com.health.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * 认证服务类
 * 处理用户、医师和管理员的注册和登录逻辑
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    /**
     * 用户注册
     * 验证用户名唯一性，保存用户到数据库
     *
     * @param registerRequest 注册请求信息
     * @return 注册成功的用户信息
     * @throws IllegalArgumentException 如果用户名已存在
     */
    public User registerUser(RegisterRequest registerRequest) {
        // 验证用户名唯一性
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setAge(registerRequest.getAge());
        user.setGender(registerRequest.getGender());
        user.setPhone(registerRequest.getPhone());
        user.setRole("USER");
        user.setStatus("ACTIVE");

        // 保存用户到数据库
        return userRepository.save(user);
    }

    /**
     * 用户登录
     * 验证用户名和密码，返回登录状态
     *
     * @param loginRequest 登录请求信息
     * @return 登录响应信息
     * @throws IllegalArgumentException 如果用户名不存在或密码错误
     */
    public LoginResponse loginUser(LoginRequest loginRequest) {
        // 按用户名查询用户
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("用户名不存在");
        }

        User user = userOptional.get();

        // 验证密码
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        // 验证账户状态
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new IllegalArgumentException("账户已被禁用");
        }

        // 返回登录响应
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setName(user.getName());
        response.setMessage("登录成功");

        return response;
    }

    /**
     * 医师注册
     * 验证医师信息，保存医师到数据库
     *
     * @param doctorRegisterRequest 医师注册请求信息
     * @return 注册成功的医师信息
     * @throws IllegalArgumentException 如果用户名或执业证号已存在
     */
    public Doctor registerDoctor(DoctorRegisterRequest doctorRegisterRequest) {
        // 验证用户名唯一性
        if (userRepository.findByUsername(doctorRegisterRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 验证执业证号唯一性
        if (doctorRepository.findByLicenseNumber(doctorRegisterRequest.getLicenseNumber()).isPresent()) {
            throw new IllegalArgumentException("执业证号已存在");
        }

        // 创建医师对应的用户账户
        User user = new User();
        user.setUsername(doctorRegisterRequest.getUsername());
        user.setPassword(doctorRegisterRequest.getPassword());
        user.setEmail(doctorRegisterRequest.getEmail());
        user.setName(doctorRegisterRequest.getName());
        user.setRole("DOCTOR");
        user.setStatus("ACTIVE");

        // 保存用户到数据库
        User savedUser = userRepository.save(user);

        // 创建医师信息
        Doctor doctor = new Doctor();
        doctor.setUserId(savedUser.getId());
        doctor.setLicenseNumber(doctorRegisterRequest.getLicenseNumber());
        doctor.setSpecialization(doctorRegisterRequest.getSpecialization());
        doctor.setHospital(doctorRegisterRequest.getHospital());
        doctor.setVerified(false);

        // 保存医师到数据库
        return doctorRepository.save(doctor);
    }

    /**
     * 医师登录
     * 验证医师的用户名和密码，返回登录状态
     *
     * @param loginRequest 登录请求信息
     * @return 登录响应信息
     * @throws IllegalArgumentException 如果用户名不存在或密码错误
     */
    public LoginResponse loginDoctor(LoginRequest loginRequest) {
        // 按用户名查询用户
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("用户名不存在");
        }

        User user = userOptional.get();

        // 验证用户角色是否为医师
        if (!"DOCTOR".equals(user.getRole())) {
            throw new IllegalArgumentException("该账户不是医师账户");
        }

        // 验证密码
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        // 验证账户状态
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new IllegalArgumentException("账户已被禁用");
        }

        // 返回登录响应
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setName(user.getName());
        response.setMessage("登录成功");

        return response;
    }

    /**
     * 管理员登录
     * 验证管理员的用户名和密码，返回登录状态
     *
     * @param loginRequest 登录请求信息
     * @return 登录响应信息
     * @throws IllegalArgumentException 如果用户名不存在或密码错误
     */
    public LoginResponse loginAdmin(LoginRequest loginRequest) {
        // 按用户名查询用户
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("用户名不存在");
        }

        User user = userOptional.get();

        // 验证用户角色是否为管理员
        if (!"ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException("该账户不是管理员账户");
        }

        // 验证密码
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        // 验证账户状态
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new IllegalArgumentException("账户已被禁用");
        }

        // 返回登录响应
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setName(user.getName());
        response.setMessage("登录成功");

        return response;
    }
}
