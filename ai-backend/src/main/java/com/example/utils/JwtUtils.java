package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {
    //jwt密钥
    @Value("${spring.security.jwt.key}")
    private String key;

    @Value("${spring.security.jwt.expire}")
    private int expire;

    @Resource
    StringRedisTemplate template;   //相当于黑名单





    //加入黑名单方法
    public boolean invalidateJwt(String headerToken){

        String token = convertToken(headerToken);
        Algorithm algorithm = Algorithm.HMAC256(key);

        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT verify = jwtVerifier.verify(token);
            return deleteToken(verify.getId(), verify.getExpiresAt());
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    /**
     * 根据配置快速计算过期时间
     * @return 过期时间
     */
    public Date expireTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire);
        return calendar.getTime();
    }

    //根据用户信息创建Jwt令牌
    public String createJwt(UserDetails user, String userId,String username) {

            Algorithm algorithm = Algorithm.HMAC256(key);
            Date expire = this.expireTime();
//           template.opsForValue().set("test","1111111");
            return JWT.create()
                    .withJWTId(UUID.randomUUID().toString())
                    .withClaim("id", userId)
                    .withClaim("name", username)
                    .withClaim("authorities", user.getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority).toList())
                    .withExpiresAt(expire)
                    .withIssuedAt(new Date())
                    .sign(algorithm);

    }




    private boolean deleteToken(String uuid,Date time){
        if (this.isInvalidToken(uuid))
            return false;
        Date now = new Date();
        long expire = Math.max(time.getTime()- now.getTime(),0);
        /*
         *.set(Const.JWT_BLACK_List+uuid, "", expire, TimeUnit.MILLISECONDS)：通过 set 方法设置了键值对。
         键是 Const.JWT_BLACK_List + uuid，这里使用了常量 Const.JWT_BLACK_List 和 uuid 来构建键，表示黑名单中的某个 JWT。
         值是空字符串 ""，实际上，对于黑名单，通常只关心键是否存在，而不太关心具体的值。
         *expire 是设置的过期时间，单位为毫秒。
         *TimeUnit.MILLISECONDS 表示过期时间的单位是毫秒。
        */
        template.opsForValue().set(Const.JWT_BLACK_List+uuid,"",expire, TimeUnit.MILLISECONDS);
        return true;
    }

    // 验证uuid是否在黑那名单中
    private  boolean isInvalidToken(String uuid){
        return Boolean.TRUE.equals(template.hasKey(Const.JWT_BLACK_List + uuid));
    }
    //根据Jwt验证并解析用户信息
    public  DecodedJWT resolveJwt(String headerToken){
        String token = convertToken(headerToken);
        if(token==null) return null;

        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT verify = jwtVerifier.verify(token);  //对JWT令牌进行验证，看看是否被修改
//            System.out.println(verify.getId());
//            判断是否存在于黑名单中，如果存在，则返回null表示失效
            if(this.isInvalidToken(verify.getId()))
                return null;

            Date expiresAt = verify.getExpiresAt();
            if(new Date().after(expiresAt)) //如果是过期令牌则返回null
            {
                return null;
            }
            else
               return verify;
        } catch (JWTVerificationException e) {
            System.out.println(e);
            return null;
        }
    }

    // 验证token是否合法
    private String convertToken(String headerToken){
        if(headerToken == null || !headerToken.startsWith("Bearer ")) {

            return null;
        }
        return headerToken.substring(7);
    }
    public String toId(DecodedJWT jwt){
        Map<String,Claim>  claims = jwt.getClaims();
        return claims.get("id").asString();
    }
    /**
     * 将jwt对象中的内容封装为UserDetails
     * @param jwt 已解析的Jwt对象
     * @return UserDetails
     */
    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("******")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }


}
