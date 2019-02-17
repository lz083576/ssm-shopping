package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.SysDictionaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/dictionary")
public class DictionaryController implements SysDictionaryControllerApi {

    @Autowired
    DictionaryService dictionaryService;

    @Override
    @GetMapping("/get/{type}")
    public SysDictionary getByType(@PathVariable String type) {
        return dictionaryService.findDictionaryByType(type);
    }
}
