package com.yifu.emos.controller;

import com.yifu.emos.common.util.R;
import com.yifu.emos.config.shiro.JwtUtil;
import com.yifu.emos.controller.form.SearchMyMeetingListByPageForm;
import com.yifu.emos.service.MeetingService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @auther YIFU GAO
 * @date 2023/01/12/21:21
 * File Info:
 */
@RestController
@RequestMapping("/meeting")
public class MeetingController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MeetingService meetingService;

    @PostMapping("/searchMyMeetingListByPage")
    @ApiOperation("Search Meeting List by page")
    public R searchMyMeetingListByPage( @Valid @RequestBody SearchMyMeetingListByPageForm form, @RequestHeader("token") String token){
        int userId = jwtUtil.getUserId(token);
        Integer page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        HashMap map = new HashMap();
        map.put("userId", userId);
        map.put("start",start);
        map.put("length", length);
        ArrayList<HashMap> list = meetingService.searchMyMeetingListByPage(map);
        return R.ok().put("result",list);
    }
}
