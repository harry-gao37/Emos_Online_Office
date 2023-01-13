package com.yifu.emos.controller;

import com.yifu.emos.common.util.R;
import com.yifu.emos.config.shiro.JwtUtil;
import com.yifu.emos.controller.form.DeleteMessageRefByIdForm;
import com.yifu.emos.controller.form.SearchMessageByIdForm;
import com.yifu.emos.controller.form.SearchMessageByPageForm;
import com.yifu.emos.controller.form.UpdateUnreadMessageForm;
import com.yifu.emos.service.MessageService;
import com.yifu.emos.task.MessageTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;


/**
 * @auther YIFU GAO
 * @date 2023/01/11/19:16
 * File Info:
 */
@RestController
@RequestMapping("/message")
@Api("Message Module")
public class MessageController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageTask messageTask;

    @PostMapping("/searchMessageByPage")
    @ApiOperation("Message Pagination")
    public R searchMessageByPage(@Valid @RequestBody SearchMessageByPageForm form, @RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        Integer page = form.getPage();
        Integer length = form.getLength();
        long start = (page - 1) * length;
        List<HashMap> list = messageService.searchMessageByPage(userId, start, length);
        return R.ok().put("result", list);
    }

    @PostMapping("/searchMessageById")
    @ApiOperation("Search Message by id")
    public R searchMessageById(@Valid @RequestBody SearchMessageByIdForm form) {
        HashMap map = messageService.searchMessageById(form.getId());
        return R.ok().put("result", map);
    }

    @PostMapping("/updateUnreadMessage")
    @ApiOperation("Change unread to readed")
    public R updateUnreadMessage(@Valid @RequestBody UpdateUnreadMessageForm form) {
        long rows = messageService.updateUnreadMessage(form.getId());
        //success or fail
        return R.ok().put("result", rows == 1 ? true : false);
    }

    @PostMapping("/deleteMessageRefById")
    @ApiOperation("Deleting message")
    public R deleteMessageRefById(@Valid @RequestBody DeleteMessageRefByIdForm form) {
        long rows = messageService.deleteMessageRefById(form.getId());
        return R.ok().put("result", rows == 1 ? true : false);
    }

    @GetMapping("/refreshMessage")
    @ApiOperation("Update user message")
    public R refreshMessage(@RequestHeader("token") String token){
        int userId = jwtUtil.getUserId(token);
        //allow
        messageTask.receiveAsync(userId + "");
        long lastRows = messageService.searchLastCount(userId);
        long unreadRows = messageService.searchUnreadCount(userId);
        return R.ok().put("lastRows",lastRows).put("unreadRows",unreadRows);

    }
}
