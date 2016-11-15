/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.filter;

import core.controller.LoginController;
import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jboss.logging.Logger;
import project.DO.Account;
import project.config.CONFIG;

/**
 *
 * @author DA CUOI
 */
@WebFilter("/user/*")
public class LoginFilter implements Filter {

    /**
     * Checks if user is logged in. If not it redirects to the login.xhtml page.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        NumRequest.add_request();
        String contextPath = ((HttpServletRequest) request).getContextPath();
        String reqURI = ((HttpServletRequest) request).getRequestURI();
        Account account = null;
        try {
            account = (Account) ((HttpServletRequest) request).getSession().getAttribute(CONFIG.SESSION_NAME_OF_USER);
        } catch (Exception e) {
            ((HttpServletResponse) response).sendRedirect(contextPath + "/login.xhtml");
        }
        if (account == null) {
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
