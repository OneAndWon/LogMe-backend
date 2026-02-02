package com.haru.LogMe.domain.user.service;

import com.haru.LogMe.domain.user.dto.UserMeResponse;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.domain.user.repository.UserRepository;
import com.haru.LogMe.global.exception.CustomException;
import com.haru.LogMe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private  final UserRepository userRepository;

    public UserMeResponse getMyInfo(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserMeResponse.from(user);
    }
}
