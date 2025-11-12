package com.haru.LogMe.global.jwt;

import com.haru.LogMe.domain.user.repository.UserRepository;
import com.haru.LogMe.global.exception.CustomException;
import com.haru.LogMe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userIdString) throws UsernameNotFoundException {

        try {
            Long userId = Long.parseLong(userIdString);

            return userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        } catch (NumberFormatException e) {
            //토큰에 숫자가 아닌 값이 들어왔을 때
            log.warn("Invalid user ID in token: {}", userIdString);
            throw new CustomException(ErrorCode.INVALID_ACCESSTOKEN);
        }
    }
}
