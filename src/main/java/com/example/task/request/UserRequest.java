package com.example.task.request;

import lombok.Data;

@Data
public class UserRequest extends BaseRequest{
        private String email;
        private String password;
}
