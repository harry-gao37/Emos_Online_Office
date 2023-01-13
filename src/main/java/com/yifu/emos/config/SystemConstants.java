package com.yifu.emos.config;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @auther YIFU GAO
 * @date 2023/01/04/19:32
 * File Info:
 */
@Data
@Component
public class SystemConstants {
    public String attendanceStartTime;
    public String attendanceTime;
    public String attendanceEndTime;
    public String closingStartTime;
    public String closingTime;
    public String closingEndTime;

}
