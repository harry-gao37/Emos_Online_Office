package com.yifu.emos.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @auther YIFU GAO
 * @date 2023/01/11/20:12
 * File Info:
 */
@Data
@ApiModel
public class UpdateUnreadMessageForm {
    @NotNull
    private String id;
}
