package com.shenyang.musicfestival.service.impl;

import com.shenyang.musicfestival.dto.UserDTO;
import com.shenyang.musicfestival.entity.User;
import com.shenyang.musicfestival.repository.UserRepository;
import com.shenyang.musicfestival.service.UserService;
import com.shenyang.musicfestival.util.MaskingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of UserService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User register(String phone, String password) {
        User user = User.builder()
            .phone(phone)
            .password(passwordEncoder.encode(password))
            .role("USER")
            .isRealNameVerified(false)
            .isBlocked(false)
            .points(0L)
            .build();
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public Optional<User> findByIdNumber(String idNumber) {
        return userRepository.findByIdNumber(idNumber);
    }

    @Override
    public boolean phoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public boolean idNumberExists(String idNumber) {
        return userRepository.existsByIdNumber(idNumber);
    }

    @Override
    public User updateProfile(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (userDTO.getNickname() != null) {
            user.setNickname(userDTO.getNickname());
        }
        if (userDTO.getAvatar() != null) {
            user.setAvatar(userDTO.getAvatar());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        
        return userRepository.save(user);
    }

    @Override
    public User verifyRealName(Long userId, String realName, String idNumber) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setRealName(realName);
        user.setIdNumber(idNumber);
        user.setIsRealNameVerified(true);
        
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> login(String phone, String password) {
        Optional<User> user = userRepository.findByPhone(phone);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    @Override
    public User uploadAvatar(Long userId, String avatarUrl) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatar(avatarUrl);
        return userRepository.save(user);
    }

}
