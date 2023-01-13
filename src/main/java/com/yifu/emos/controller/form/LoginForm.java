package com.yifu.emos.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @auther YIFU GAO
 * @date 2023/01/01/19:57
 * File Info:
 */

@Data
@ApiModel
public class LoginForm {

    @NotBlank(message = "temporary authorization code cannot be null")
    private String code;
}
