package com.yifu.emos.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @auther YIFU GAO
 * @date 2022/12/30/18:51
 * File Info: verify registration info
 */
@Data
@ApiModel
public class RegisterForm {

    @NotBlank(message = "Register Code cannot be blank")
    @Pattern(regexp = "^[0-9]{6}$",message = "code size must be 6")
    private String registerCode;


    @NotBlank(message = "temporary code cannot be blank")
    private String code;

    @NotBlank(message = "nickname cannot be blank")
    private String nickname;

    @NotBlank(message = "avatar cannot be blank")
    private String photo;

}
