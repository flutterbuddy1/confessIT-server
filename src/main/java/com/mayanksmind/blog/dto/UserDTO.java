package com.mayanksmind.blog.dto;

import com.mayanksmind.blog.enums.Role;
import com.mayanksmind.blog.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private long id;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime created_at;

    public UserDTO(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.created_at = user.getCreated_at();
    }
}
