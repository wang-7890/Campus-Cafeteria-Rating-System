package com.spring.restaurantdishes.model.RequestDetails;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]{4,20}$",
            message = "用户名必须为4～20位字母、数字或下划线"
    )
    private String username;

    @NotNull(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须为6～20个字符")
    private String password;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 20, message = "昵称长度不能超过20个字符")
    private String nickname;

    // Jackson 调用 setter 后再执行校验，实现先 trim、再校验
    public void setUsername(String username) {
        this.username = username == null ? null : username.strip();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.strip();
    }


}

