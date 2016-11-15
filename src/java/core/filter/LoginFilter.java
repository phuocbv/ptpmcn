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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jboss.logging.Logger;
import project.DO.Account;
import project.config.CONFIG;

/**
 *
 * @author DA CUOI
 */
public class LoginFilter implements Filter {

    /**
     * Checks if user is logged in. If not it redirects to the login.xhtml page.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
       
        String contextPath = ((HttpServletRequest) request).getContextPath();
        String reqURI = ((HttpServletRequest) request).getRequestURI();

        FacesContext context = FacesContext.getCurrentInstance();
        Account accountAdmin = (Account) context.getExternalContext().getSessionMap().get(CONFIG.SESSION_NAME_OF_ADMIN);
        Account accountUser = (Account) context.getExternalContext().getSessionMap().get(CONFIG.SESSION_NAME_OF_USER);
        
        if (accountAdmin == null && accountUser == null) {
            ((HttpServletResponse) response).sendRedirect(contextPath + "/login.xhtml");
        }
        
        LoginController loginBean = (LoginController) ((HttpServletRequest) request).getSession().getAttribute("loginController");
        Account accountCurrent = loginBean.getResult();
        if (reqURI.contains("admin") && accountCurrent.getColumn1().equals("admin")) {
            chain.doFilter(request, response);
        } else if (reqURI.contains("user") && accountCurrent.getColumn1().equals("user")) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).sendRedirect(contextPath + "/login.xhtml");
        }
        
//        if (loginBean == null || !loginBean.isLoggedIn()) {
//            //String contextPath = ((HttpServletRequest)request).getContextPath();
//            ((HttpServletResponse) response).sendRedirect(contextPath + "/login.xhtml");
//        }
//        if (contextPath.contains("admin") && loginBean.isLogInIsUser()) {
//            ((HttpServletResponse) response).sendRedirect(contextPath + "/login.xhtml");
//        }
//        if (contextPath.contains("user") && loginBean.isLogInIsAdmin()) {
//            ((HttpServletResponse) response).sendRedirect(contextPath + "/login.xhtml");
//        }
        //loginBean.getAccount().getColumn1()
        //chain.doFilter(request, response);
    }

    public void init(FilterConfig config) throws ServletException {
        // Nothing to do here!
    }

    public void destroy() {
        // Nothing to do here!
    }

}
