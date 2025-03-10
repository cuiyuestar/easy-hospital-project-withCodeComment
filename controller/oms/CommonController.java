package com.easy.hospital.controller.oms;

import com.easy.hospital.common.response.RespResult;
import com.easy.hospital.common.response.RespSystemCode;
import com.easy.hospital.common.response.RespUtils;
import com.easy.hospital.common.utils.AliOSSUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/common")
public class CommonController {
    @Resource
    private AliOSSUtils aliOSSUtils;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/file/upload")
    public RespResult<String> uploadFile(@RequestParam("file") MultipartFile file){
        log.info("文件上传：{}", file);
        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.isBlank(originalFilename)){
                return RespUtils.fail(RespSystemCode.PARAM_ERROR, "文件名为空");
            }
            //截取原始文件名的后缀   dfdfdf.png
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称
            String objectName = UUID.randomUUID().toString() + extension;
            String filePath = aliOSSUtils.upload(file.getBytes(), objectName);
            return RespUtils.success(filePath);
        } catch (IOException e){
            return RespUtils.fail(RespSystemCode.SYSTEM_ERROR, "文件上传失败");
        }
    }
}
