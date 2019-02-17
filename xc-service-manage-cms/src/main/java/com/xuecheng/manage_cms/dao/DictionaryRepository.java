package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DictionaryRepository extends MongoRepository<SysDictionary,String> {

    //根据字典type查询字典数据
    SysDictionary findByDType(String type);
}
