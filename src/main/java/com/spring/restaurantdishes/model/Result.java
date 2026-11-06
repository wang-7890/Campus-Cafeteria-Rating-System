package com.spring.restaurantdishes.model;

public class Result <T>{
    private Integer code;
    private String message;
    private T data;

    public Result(){}
    public Result(Integer code,String message,T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static <T> Result<T> success(T data){
        return new Result<>(200,"success",data);
    }
    public static Result success(){
        return new Result(200,"success",null);
    }
    public static Result error400(){
        return new Result(400,"参数错误或参数校验失败",null);
    }
    public static Result error401(){
        return new Result(401,"登录信息已过期",null);
    }
    public static Result error403(){
        return new Result(403,"没有操作权限",null);
    }
    public static Result error404(){
        return new Result(404,"请求的数据不存在",null);
    }
    public static Result error409(){
        return new Result(409,"数据冲突",null);
    }
    public static Result error500(){
        return new Result(500,"服务器出错",null);
    }
}
