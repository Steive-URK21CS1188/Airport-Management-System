package com.airport_management_system.AMS.models.dao.serviceImpl;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component 
public class JwtService { 
 private static final String SECRET="5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437"; 
  
 
 public  String generateToken(String email,String role) 
 { 
 Map<String,Object> claims=new HashMap<String, Object>(); 
 claims.put("usertype", role.toUpperCase()); 
 String S= CreateToken(claims,email); 
 System.out.println(S); 
 return S; 
 } 
  
 private String CreateToken(Map<String,Object> claims,String email) 
 { 
  return Jwts.builder() 
    .setClaims(claims) 
    .setSubject(email) 
    .setIssuedAt(new Date(System.currentTimeMillis())) 
    .setExpiration(new Date(System.currentTimeMillis()+1000*60*15)) 
    .signWith(getSignedKey(),SignatureAlgorithm.HS256) 
    .compact(); 
 } 
  
     
 private Key getSignedKey() 
 { 
  byte[] keyBytes=Decoders.BASE64.decode(SECRET); 
  return Keys.hmacShaKeyFor(keyBytes); 
 } 
 public boolean validatetoken(String token,UserDetails userDetails) 
 { 
  String username=extractUserName(token); 
  return (username.equals(userDetails.getUsername()) && 
!isTokenExpired(token)); 
 } 
  
  
  
  
 private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) { 
        final Claims claims = extractAllClaims(token); 
        return claimsResolvers.apply(claims); 
    } 
 
 private Claims extractAllClaims(String token) { 
        return 
Jwts.parserBuilder().setSigningKey(getSignedKey()).build().parseClaimsJws(token) 
                .getBody(); 
    } 
 public String extractUserName(String token) { 
        return extractClaim(token, Claims::getSubject); 
    } 
 private Date extractExpiration(String token) { 
        return extractClaim(token, Claims::getExpiration); 
    } 
 public String extractRoleFromToken(String token) { 
        Claims claims = Jwts.parserBuilder() 
                .setSigningKey(getSignedKey()) 
                .build() 
                .parseClaimsJws(token) 
                .getBody(); 
        return claims.get("usertype", String.class);  
    } 
 public boolean isTokenValid(String token, UserDetails userDetails) { 
        final String userName = extractUserName(token); 
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token); 
    } 
  private boolean isTokenExpired(String token) { 
         return extractExpiration(token).before(new Date()); 
     }

  public boolean validateToken(final String token) {
		//it will return claim when Parsed token claim and currently logged claim is matching
	  System.out.println(token);
		Jwts.parserBuilder().setSigningKey(getSignedKey()).build().parseClaimsJws(token);
		System.out.println("Token Validated");
		return true;
	}
  
  
}


/*import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

    private static final String SECRET = "6A784E6252655468576D5A71347437626533377848792F423F4528482B4D6251";

    /**
     * Generate JWT with username and role as claims.
     * Role is now a string (ADMIN / MANAGER)
     */
    /*public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        // store usertype in uppercase for consistency
        claims.put("usertype", role != null ? role.toUpperCase() : "");
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // 15 minutes expiration
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(final String token) {
		//it will return claim when Parsed token claim and currently logged claim is matching 
		Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
		return true;
	}

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract the role (usertype) from the token as a String.
     */
    /*public String extractRoleFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("usertype", String.class);
    }

    public boolean validateTokenSignature(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
        return true;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}*/

