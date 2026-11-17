package com.example.login_auth.config;

import java.util.Collections;

import com.example.login_auth.repository.UserRepository;
import com.example.login_auth.service.TokenService;

@Component
public class SecurityFilter extends OncePerRequestFilter{

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Override 
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response){

        var token = this.recoverToken(request);
        var login = tokenService.validateToken(token);

        if(login != null){
            User user = userRepository.findByEmail(login);
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }

}
