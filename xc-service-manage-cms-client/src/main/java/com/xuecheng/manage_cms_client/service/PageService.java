package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
@Service
public class PageService {
    private static final Logger LOGGER= LoggerFactory.getLogger(PageService.class);

    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    //保存htnl页面到服务器物理路径
    public void savePageToServerPath(String pageId){
        //根据pageId查询cmspage
        CmsPage cmsPage = this.findCmsPageById(pageId);

        //得到文件的Id，从cmsPage中获取htmlPageFile
        String htmlFileId = cmsPage.getHtmlFileId();
        //fridfs查询文件
        InputStream inputStream = this.getFileById(htmlFileId);
        if (inputStream==null){
            LOGGER.error("getFileById inputStream is null,htmlFileId:{}",htmlFileId);
            return;
        }
        //得到站点的物理路径
        String siteId = cmsPage.getSiteId();
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();
        String pagePhysicalPath = cmsPage.getPagePhysicalPath();
        //得到页面的路径
        String pagePath=sitePhysicalPath+pagePhysicalPath+cmsPage.getPageName();

        //将html文件保存到服务器物理文件中
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //根据页面ID查询页面信息
    public CmsPage findCmsPageById(String id){
        Optional<CmsPage> cmsPage = cmsPageRepository.findById(id);
        return cmsPage.orElse(null);
    }

    //根据文件id从gridfs中查询内容
    public InputStream getFileById(String id){
        //文件对象
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //定义
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //根据页面ID查询页面信息
    public CmsSite findCmsSiteById(String id){
        Optional<CmsSite> cmsSite = cmsSiteRepository.findById(id);

        return cmsSite.orElse(null);
    }
}
