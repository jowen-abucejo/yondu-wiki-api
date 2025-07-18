package com.yondu.knowledgebase.config;

import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.UserService;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class RequestFilter extends OncePerRequestFilter {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private UserService userService;

    private final Logger log = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("RequestFilter.doFilterInternal()");

        final String requestTokenHeader = request.getHeader("Authorization");

        String email = null;
        String token = null;

        log.info("requestTokenHeader : " + requestTokenHeader);

        if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer")){
            token = requestTokenHeader.substring("Bearer ".length());

            try{
                Jwt builtToken = tokenUtil.readJwt(token);
                email = (String)builtToken.getHeader().get("email");
            }catch (JwtException jwtException){
                log.error(jwtException.getMessage());
                log.error("JWT Token must be expired.");
            }
        }else{
            log.info("JWT token does not begin with bearer string.");
        }

        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){

            User user = null;
            try{
                user = userService.loadUserByUsername(email);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            userToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(userToken);
        }

        filterChain.doFilter(request, response);
    }
}
