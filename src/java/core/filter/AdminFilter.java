/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import project.DO.Account;
import project.config.CONFIG;

/**
 *
 * @author DA CUOI
 */
@WebFilter("/admin/*")
public class AdminFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        NumRequest.add_request();
        String contextPath = ((HttpServletRequest) request).getContextPath();
        String reqURI = ((HttpServletRequest) request).getRequestURI();
        Account accountAdmin = null;
        try {
            accountAdmin = (Account) ((HttpServletRequest) request).getSession().getAttribute(CONFIG.SESSION_NAME_OF_ADMIN);
        } catch (Exception e) {
            ((HttpServletResponse) response).sendRedirect(contextPath + "/login.xhtml");
        }
        if (accountAdmin == null) {
            ((HttpServletResponse) response).sendRedirect(contextPath + "/login.xhtml");
        } else {
            chain.doFilter(request, response);
        }

    }

    public void init(FilterConfig config) throws ServletException {
        // Nothing to do here!
    }

    public void destroy() {
        // Nothing to do here!
    }

}
