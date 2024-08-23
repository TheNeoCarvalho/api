package com.manolo.api.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.manolo.api.user.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository UserRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

        var authorization = request.getHeader("Authorization");
        var auth_encoded = authorization.substring("Basic".length()).trim();

        byte[] auth_decoded = Base64.getDecoder().decode(auth_encoded);
        var auth = new String(auth_decoded);

        String[] credentials = auth.split(":");

        var username = credentials[0];
        var password = credentials[1];

        var user = this.UserRepository.findByUsername(username);
        
        if (user == null) {
          response.sendError(401, "User not authorized");
        } else {
          var password_verify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
          if (password_verify.verified) {
              response.sendError(401);
            }
        }




        filterChain.doFilter(request, response);

      }

}
