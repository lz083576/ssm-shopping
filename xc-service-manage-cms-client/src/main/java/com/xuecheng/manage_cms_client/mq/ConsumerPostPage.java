package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class ConsumerPostPage {

    @Autowired
    private PageService pageService;
    @Autowired
    CmsPageRepository cmsPageRepository;
    private static final Logger LOGGER= LoggerFactory.getLogger(ConsumerPostPage.class);

    //发布页面
    @RabbitListener(queues={"${xuecheng.mq.queue}"})
    public void postPage(String msg){


        //解析消息
        Map map = JSON.parseObject(msg, Map.class);

        //得到消息中的页面id
        String pageId = (String) map.get("pageId");
        //校验页面是否合法
        //查询页面信息
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(!optional.isPresent()){
            LOGGER.error("receive cms post page,cmsPage is null:{}",pageId);
            return ;
        }
        //调用service方法将页面从GridFS中下载到服务器
        pageService.savePageToServerPath(pageId);
    }
}
