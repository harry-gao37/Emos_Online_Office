package com.yifu.emos.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @auther YIFU GAO
 * @date 2023/01/11/19:15
 * File Info:
 */
@Data
@ApiModel
public class SearchMessageByPageForm {
    @NotNull
    @Min(1)
    private Integer page;

    @NotNull
    @Range(min = 1,max = 40)
    private Integer length;

}
