package com.jeeplus.modules.sys.security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jeeplus.config.properties.JeePlusProperites;
import com.jeeplus.modules.sys.utils.UserUtils;
import java.util.Date;

public class JWTUtil {
    public static final String TOKEN = "token";

    public static final String REFRESH_TOKEN = "refreshToken";

    /**
     * 校验token是否正确
     * @param token 密钥
     * @return 是否正确
     */
    public static int verify(String token) {
        try {
            String userName = JWTUtil.getLoginName(token);
            String password = UserUtils.getByLoginName(userName).getId();
            Algorithm algorithm = Algorithm.HMAC256(password);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", userName)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return 0;
        } catch (TokenExpiredException e){
            return 1;
        }
        catch (Exception exception) {
            return 2;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的用户名
     */
    public static String getLoginName(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 生成签名
     * @param username 用户名
     * @param password 用户的密码
     * @return 加密的token
     */
    public static String createAccessToken(String username, String password) {
            Date date = new Date(System.currentTimeMillis() + JeePlusProperites.newInstance().getEXPIRE_TIME());
            Algorithm algorithm = Algorithm.HMAC256(password);
            // 附带username信息
            return JWT.create()
                    .withClaim("username", username)
                    .withExpiresAt(date)
                    .sign(algorithm);
    }

    /**
     * refresh TOKEN 刷新用
     * @param username 用户名
     * @param password 用户的密码
     * @return 加密的token
     */
    public static String createRefreshToken(String username, String password) {
        Date date = new Date(System.currentTimeMillis() + 3*JeePlusProperites.newInstance().getEXPIRE_TIME());
        Algorithm algorithm = Algorithm.HMAC256(password);
        // 附带username信息
        return JWT.create()
                .withClaim("username", username)
                .withExpiresAt(date)
                .sign(algorithm);
    }

}
