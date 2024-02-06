package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.chatdto.ApiKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApikeyMapper extends BaseMapper<ApiKey> {
}
