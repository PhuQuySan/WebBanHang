package com.asm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.asm.models.Account;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	    Account currentUser = (Account) request.getSession().getAttribute("currentUser");

//	    if (currentUser == null) {
//	        response.sendRedirect("/auth/login");
//	        return false;
//	    }

	    // Phân quyền: chỉ cho phép admin vào /admin/**
	    String requestURI = request.getRequestURI();
	    if (requestURI.startsWith("/admin") && !"ADMIN".equals(currentUser.getRole())) {
	        response.sendRedirect("/auth/login");
	        return false;
	    }

	    return true;
	}

}