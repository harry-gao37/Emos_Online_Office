package com.yifu.emos;

import cn.hutool.core.util.StrUtil;
import com.yifu.emos.config.SystemConstants;
import com.yifu.emos.db.dao.SysConfigDao;
import com.yifu.emos.db.pojo.SysConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

@ServletComponentScan
@SpringBootApplication
@Slf4j
@EnableAsync
public class EmosApplication {
    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private SystemConstants constants;

    @Value("${emos.image-folder}")
    private String imageFolder;

    public static void main(String[] args) {
        SpringApplication.run(EmosApplication.class, args);
    }

    @PostConstruct
    public void init(){
        List<SysConfig> configs = sysConfigDao.selectAllParam();
        //watch out: using reflection because spring framework
        //here is to initialize and set basic configuration info
        configs.forEach(one->{
            String paramKey = one.getParamKey();
            paramKey = StrUtil.toCamelCase(paramKey);
            String paramValue = one.getParamValue();
            try{
                Field declaredField = constants.getClass().getDeclaredField(paramKey);
                declaredField.set(constants,paramValue);
            }catch (Exception e){
                log.error("Exception", e);
            }
        });
        //here is to temporarily store images
        new File(imageFolder).mkdirs();

    }



}
