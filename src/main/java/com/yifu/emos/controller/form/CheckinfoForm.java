package com.yifu.emos.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @auther YIFU GAO
 * @date 2023/01/07/20:44
 * File Info:
 */
@Data
@ApiModel
public class CheckinfoForm {
    private String address;
    private String country;
    private String province;
    private String city;
    private String district;
}
