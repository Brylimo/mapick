package com.enjoytrip.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class UserRequestDto {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {
        @NotEmpty(message = "이메일은 필수 입력값입니다.")
        private String email;
        @NotEmpty(message = "비밀번호는 필수 입력값입니다.")
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(email, password);
        }
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Register {
        private String name;
        private String email;
        private String password;
        private String role;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyCode {
        private String email;
        private String code;
    }
    
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class findPW {
        private String email;
        private String name;
    }
    
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class changePW {
        private String email;
        private String password;
        private String password2;
    }
    
}
