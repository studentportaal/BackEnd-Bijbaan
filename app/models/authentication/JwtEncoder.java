package models.authentication;

import dal.repository.TokenRepository;
import io.jsonwebtoken.*;
import play.libs.Json;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class JwtEncoder {

    private static final String key = "bjJyNXU4eC9BP0QoRytLYVBkU2dWa1lwM3M2djl5JEImRSlIQE1jUWVUaFdtWnE0dDd3IXolQypGLUphTmRSZw==";

    public static String toJWT(AuthenticationToken token) {
        JwtBuilder builder = Jwts.builder();
        HashMap<String, Object> claims = new HashMap<>();

        claims.put("roles", Json.toJson(token.getUser().getRoles()));
        claims.put("tokenId", token.getId());
        claims.put("refreshKey", token.getRefreshKey());
        builder.setClaims(claims);
        builder.setIssuedAt(token.getStart());
        Date start = token.getStart();
        start.setTime(start.getTime() + 3600000);
        builder.setExpiration(start);
        builder.setSubject(token.getUser().getUuid());
        return builder.signWith(SignatureAlgorithm.HS512, key).compact();
    }

    public static AuthenticationToken fromJWT(String token, TokenRepository repository) {

        String tokenId = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().get("tokenId", String.class);

        try {
            AuthenticationToken authenticationToken = repository.getToken(tokenId).toCompletableFuture().get();
            Jwts.parser().setSigningKey(key).requireSubject(authenticationToken.getUser().getUuid()).parse(token);
            return authenticationToken;
        } catch (InterruptedException | MissingClaimException | IncorrectClaimException | ExecutionException e) {
            return null;
        }
    }
}
