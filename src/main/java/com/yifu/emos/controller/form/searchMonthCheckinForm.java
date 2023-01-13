package com.yifu.emos.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @auther YIFU GAO
 * @date 2023/01/10/18:54
 * File Info:
 */
@Data
@ApiModel
public class searchMonthCheckinForm {
    @NotNull
    @Range(min = 2000, max = 3000)
    private Integer year;

    @NotNull
    @Range(min = 1, max = 12)
    private Integer month;
}
