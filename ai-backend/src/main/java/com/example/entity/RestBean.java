package com.example.entity;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

//<T> 表示 RestBean 是一个泛型类，其中 T 是一个类型参数。这意味着你可以在使用 RestBean 类时，为 T 指定具体的类型
/*Java 中的记录（Record）是在Java 16中引入的一种新的类型，用于简化不可变数据的创建和管理。
记录类是一种特殊的类，它自动生成一些标准的方法，如构造函数、equals 方法、hashCode 方法和 toString 方法。
记录类通常用于表示一些简单的数据载体，例如数据传输对象（DTO）或不可变的值对象。*/
public record RestBean<T> (int code, T data, String message) {
    //写几个工具方法，用于快速创建RestBean对象
    public static <T> RestBean<T> success(T data){
        return new RestBean<>(200, data, "请求成功");
    }
    public static <T> RestBean<T> success(){
        return success(null);
    }

    public static <T> RestBean<T> failure(int code, String message){
        return new RestBean<>(code, null, message);
    }

    public static <T> RestBean<T> failure(int code){
        return failure(code, "请求失败");
    }

    public static <T> RestBean<T> forbidden(String message){
        return failure(403, message);
    }
    //将当前对象转换为JSON格式的字符串用于返回
    public String asJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}