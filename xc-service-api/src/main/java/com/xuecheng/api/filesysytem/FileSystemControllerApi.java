package com.xuecheng.api.filesysytem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(value="文件管理接口",description = "文件管理接口，提供，文件上传服务")
public interface FileSystemControllerApi {

    //上传文件
    @ApiOperation("上传文件")
    public UploadFileResult upload(MultipartFile file,String filetag,String businesskey,String metadata);
}
