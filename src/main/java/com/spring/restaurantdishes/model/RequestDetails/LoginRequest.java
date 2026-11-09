package com.spring.restaurantdishes.model.RequestDetails;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    public void setUsername(String username) {
        this.username = username == null ? null : username.strip();
    }
}

