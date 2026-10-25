package com.mridnal.TaskMaster.projects;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class InvitationTokenService {

    private static final String ISSUER = "taskmaster";
    private static final String PURPOSE = "PROJECT_INVITATION";

    private final SecretKey signingKey;
    private final long expirationMillis;

    public InvitationTokenService(
            @Value("${app.invitation.secret}") String secret,
            @Value("${app.invitation.expiration-hours:48}")
            long expirationHours
    ) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "Invitation JWT secret must be at least 32 bytes"
            );
        }

        if (expirationHours <= 0) {
            throw new IllegalArgumentException(
                    "Invitation expiration must be positive"
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = Math.multiplyExact(
                expirationHours, 3_600_000L
        );
    }

    public String generateToken(Long inviteeId, Long projectId) {
        if (inviteeId == null || projectId == null) {
            throw new IllegalArgumentException(
                    "Invitee ID and project ID are required"
            );
        }

        Date issuedAt = new Date();
        Date expiresAt = new Date(
                Math.addExact(issuedAt.getTime(), expirationMillis)
        );

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(inviteeId.toString())
                .claim("projectId", projectId)
                .claim("purpose", PURPOSE)
                .id(UUID.randomUUID().toString())
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(signingKey)
                .compact();
    }

    public Claims validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Invitation token is required"
            );
        }

        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!PURPOSE.equals(claims.get("purpose", String.class))) {
            throw new IllegalArgumentException(
                    "Token is not a project invitation"
            );
        }

        if (claims.getSubject() == null
                || claims.getId() == null
                || claims.get("projectId") == null
                || claims.getIssuedAt() == null
                || claims.getExpiration() == null) {
            throw new IllegalArgumentException(
                    "Required invitation claims are missing"
            );
        }

        try {
            Long.parseLong(claims.getSubject());
            UUID.fromString(claims.getId());

            Object projectClaim = claims.get("projectId");
            if (!(projectClaim instanceof Number)) {
                throw new IllegalArgumentException(
                        "Invalid project ID claim"
                );
            }

            if (((Number) projectClaim).longValue() <= 0) {
                throw new IllegalArgumentException(
                        "Invalid project ID claim"
                );
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid invitation token claims", ex
            );
        }

        return claims;
    }
}