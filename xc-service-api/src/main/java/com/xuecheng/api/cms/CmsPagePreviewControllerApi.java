package com.xuecheng.api.cms;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="cms页面预览接口",description = "cms页面预览接口，提供数据展示")
public interface CmsPagePreviewControllerApi {
    @ApiOperation("根据id查询静态页面信息")
    public void preview(String pageId);
}
