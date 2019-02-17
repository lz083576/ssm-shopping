package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.DictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DictionaryService {

    @Autowired
    DictionaryRepository dictionaryRepository;
    public SysDictionary findDictionaryByType(String type){
        return dictionaryRepository.findByDType(type);
    }
}
