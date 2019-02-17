package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;


import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Test
    public void testFindAll(){
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }

    @Test
    public void testFindPage(){
        int page=1;
        int size=10;
        Pageable pageable= PageRequest.of(page,size );
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }

    //添加
    @Test
    public void testInsert(){
        //定义实体类
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("s01");
        cmsPage.setTemplateId("t01");
        cmsPage.setPageName("测试页面");
        cmsPage.setPageCreateTime(new Date());
        List<CmsPageParam> cmsPageParams = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        cmsPageParams.add(cmsPageParam);
        cmsPage.setPageParams(cmsPageParams);
        cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage);
    }
    //删除
    @Test
    public void testDelete() {
        cmsPageRepository.deleteById("5b17a2c511fe5e0c409e5eb3");
    }


    @Test
    public void testUpdate(){
        Optional<CmsPage> optional = cmsPageRepository.findById("5c2dfd529a878a3f6095c11d");

        if (optional.isPresent()){
            CmsPage cmsPage = optional.get();
            cmsPage.setPageAliase("liangzhi");
            cmsPageRepository.save(cmsPage);
        }
    }

    @Test
    public void testFindBypageName(){
        CmsPage cmsPage = cmsPageRepository.findByPageName("测试页面");
        System.out.println(cmsPage);
    }

    @Test
    public void testFindAll2(){
        //条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        //一定要返回该对象
        exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //页面别名模糊查询，需要自定义字符串的匹配器实现模糊查询
        //ExampleMatcher.GenericPropertyMatchers.contains()包含
        //ExampleMatcher.GenericPropertyMatchers.startsWith()开头匹配
        //条件值
        CmsPage cmsPage = new CmsPage();
        //站点ID
//        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        //模板ID
//        cmsPage.setTemplateId("5a962c16b00ffc514038fafd");
        cmsPage.setPageAliase("分类");
        //创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        Pageable pageable = new PageRequest(0, 10);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        System.out.println(all);

    }
}
