package models.authentication;

import dal.jpa.JPATokenRepository;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import play.libs.Json;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class JwtEncoder {

    private static final String key = "bjJyNXU4eC9BP0QoRytLYVBkU2dWa1lwM3M2djl5JEImRSlIQE1jUWVUaFdtWnE0dDd3IXolQypGLUphTmRSZw==";

    public static String toJWT(AuthenticationToken token) {
        JwtBuilder builder = Jwts.builder();
        builder.setSubject(token.getUser().getUuid());
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("roles", Json.toJson(token.getUser().getRoles()).asText());
        claims.put("tokenId", token.getId());
        claims.put("refreshKey", token.getRefreshKey());
        builder.setClaims(claims);
        builder.setIssuedAt(token.getStart());
        Date start = token.getStart();
        start.setTime(start.getTime() + 3600);
        builder.setExpiration(start);
        return builder.signWith(SignatureAlgorithm.HS512, key).compact();
    }

    public static AuthenticationToken fromJWT(String token, JPATokenRepository repository) {

        String tokenId = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().get("tokenId", String.class);

        try {
            return repository.getToken(tokenId).toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
