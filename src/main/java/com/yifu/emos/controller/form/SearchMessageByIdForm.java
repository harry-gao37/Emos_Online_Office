package com.yifu.emos.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @auther YIFU GAO
 * @date 2023/01/11/20:04
 * File Info:
 */
@Data
@ApiModel
public class SearchMessageByIdForm {
    @NotNull
    private String id;
}
