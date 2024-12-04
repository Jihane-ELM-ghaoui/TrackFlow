package trackflow.taskmanagement2.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

@Service
    public class Auth0Service {

        public String extractEmailFromToken(String authHeader) {
            try {
                String token = authHeader.replace("Bearer ", "");
                DecodedJWT jwt = JWT.decode(token);
                return jwt.getClaim("email").asString();
            } catch (Exception e) {
                throw new RuntimeException("Invalid token", e);
            }
        }
    }

