package com.graduation.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.graduation.common.BaseController;
import com.graduation.entity.ResourceDetail;
import com.graduation.service.ResourceDetailService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/resourceDetail")
public class ResourceDetailController extends BaseController<ResourceDetailService, ResourceDetail> {

    // 获取某个资源的详情
    @GetMapping("/getByResourceId/{id}")
    public ResourceDetail getByResourceId(@PathVariable Long id) {
        return service.getOne(new QueryWrapper<ResourceDetail>().eq("learn_resource_id", id));
    }

    // 文件上传接口
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) {
        if (file.isEmpty()) return "error";

        // 确定上传目录: springboot/upload/image 或 video
        String folder = "image";
        if ("video".equals(type)) {
            folder = "video";
        }

        // 获取项目根目录下的 upload 文件夹路径
        String rootPath = System.getProperty("user.dir") + "/upload/" + folder + "/";
        File dir = new File(rootPath);
        if (!dir.exists()) dir.mkdirs();

        // 生成新文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffix;

        try {
            file.transferTo(new File(rootPath + newFileName));
            // 返回文件名，前端保存到数据库
            return newFileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    // 模拟点赞接口 (供App使用)
    @PostMapping("/like/{userId}/{resourceId}")
    public boolean like(@PathVariable Long userId, @PathVariable Long resourceId) {
        service.toggleLike(userId, resourceId);
        return true;
    }
}