package com.spring.restaurantdishes.model.dataBasePojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.ToString;

/**
 * user 表实体。
 */
@Data
@Table("user")
public class user {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private String username;
    private String nickname;
    private String role;

    @JsonIgnore
    @ToString.Exclude
    private String password;

    private Integer status;
}
