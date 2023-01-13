package com.yifu.emos.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @auther YIFU GAO
 * @date 2023/01/13/13:28
 * File Info:
 */
@Data
@ApiModel
public class SearchUserGroupByDeptForm {

    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,15}$")
    private String keyword;
}
