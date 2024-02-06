package com.example.entity.chatdto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@TableName("db_apikeys")
@AllArgsConstructor
public class ApiKey {
    String apikey;
    int is_valid;
    int is_using;
}
