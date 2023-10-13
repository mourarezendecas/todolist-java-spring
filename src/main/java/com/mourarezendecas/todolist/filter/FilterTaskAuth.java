package com.mourarezendecas.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.mourarezendecas.todolist.user.UserModel;
import com.mourarezendecas.todolist.user.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if(servletPath.startsWith("/tasks/")){
            String auth = request.getHeader("Authorization");
            String encodedAuth = auth.substring("Basic".length()).trim();
            String decodedAuth = new String(Base64.getDecoder().decode(encodedAuth));
            String username = decodedAuth.split(":")[0];
            String password = decodedAuth.split(":")[1];

            UserModel user = this.userRepository.findByUsername(username);
            if(user == null){
                response.sendError(401, "Unauthorized user");
            }
            else{
                BCrypt.Result passwordVerify = BCrypt.verifyer().verify(password.toCharArray(),user.getPassword());
                if(passwordVerify.verified){
                    request.setAttribute("idUser", user.getId());
                    chain.doFilter(request,response);
                }else{
                    response.sendError(401, "Unauthorized user");
                }
            }
        }else{
            chain.doFilter(request,response);
        }

    }
}
