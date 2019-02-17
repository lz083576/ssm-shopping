package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.DictionaryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DictionaryTest {

    @Autowired
    DictionaryRepository dictionaryRepository;

    @Autowired
    DictionaryService dictionaryService;

    @Test
    public void testDic(){
        SysDictionary byDType = dictionaryRepository.findByDType("200");
        System.out.println(byDType);
    }

    @Test
    public void testDiction(){
        SysDictionary byType = dictionaryService.findDictionaryByType("200");
        System.out.println(byType);
    }
}
