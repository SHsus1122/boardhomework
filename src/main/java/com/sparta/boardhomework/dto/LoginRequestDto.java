package com.sparta.boardhomework.dto;

import com.sparta.boardhomework.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
// Setter 를 사용해도 가능은 하나 @NoArgsConstructor 를 사용해도 된다.
public class LoginRequestDto {
    private String username;
    private String password;



/*    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }*/
}
