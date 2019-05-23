package com.pinyougou.shop.controller;

import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.QiniuUtil;

@RestController
public class UploadController {

    private QiniuUtil qiniuUtil;

    public void setQiniuUtil(QiniuUtil qiniuUtil) {
        this.qiniuUtil = qiniuUtil;
    }

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){

       try {
           //将文件上传到七牛云
           String fileId = qiniuUtil.uploadFile(file.getOriginalFilename(), null, null);
           return new Result(true,fileId);
       }catch (Exception e){
           e.printStackTrace();
           return new Result(false,"上传失败！！！");
       }

    }

}
