package core.controller;

import core.filter.NumRequest;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import project.DO.Account;
import project.config.CONFIG;
import project.dao.AccountDAO;

/**
 *
 * @author DA CUOI
 */
@ManagedBean(name = "loginController")
@SessionScoped
public class LoginController {

    private static final String REDIRECT_TO_HOME_ADMIN = "/admin/home.xhtml?faces-redirect=true";
    private static final String REDIRECT_TO_HOME_USER = "/user/home.xhtml?faces-redirect=true";
    private static final String REDIRECT_TO_LOGIN = "/login.xhtml?faces-redirect=true";
    private static final String TO_LOGIN = "/login.xhtml";

    private Account account;
    private boolean loggedIn = false;
    private boolean logInIsAdmin = false;
    private boolean logInIsUser = false;
    private Account result;
    //FacesContext context = null;

    public LoginController() {
        account = new Account();
        //context = FacesContext.getCurrentInstance();
        //loggedIn = false;
        NumRequest f = new NumRequest();
        f.start();
    }

    public String doLogin() {
        if (account.getUsername() == "") {
            return TO_LOGIN;
        }
        if (account.getUsername() != "" && account.getPassword() != "") {
            result = AccountDAO.getAccountByUsername(account);
            if (result == null) {
                return TO_LOGIN;
            }

            loggedIn = true;
            if (result.getColumn1().equals("admin")) {
                logInIsAdmin = true;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getSessionMap().put(CONFIG.SESSION_NAME_OF_ADMIN, result);
            } else {
                logInIsUser = true;
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getSessionMap().put(CONFIG.SESSION_NAME_OF_USER, result);
            }
            String link = result.getColumn1().equals("admin") ? REDIRECT_TO_HOME_ADMIN : REDIRECT_TO_HOME_USER;
            return link;
        }
        loggedIn = false;
        return TO_LOGIN;
    }

    public String doLogout() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().clear();
        loggedIn = false;
        return REDIRECT_TO_LOGIN;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean isLogInIsAdmin() {
        return logInIsAdmin;
    }

    public void setLogInIsAdmin(boolean logInIsAdmin) {
        this.logInIsAdmin = logInIsAdmin;
    }

    public boolean isLogInIsUser() {
        return logInIsUser;
    }

    public void setLogInIsUser(boolean logInIsUser) {
        this.logInIsUser = logInIsUser;
    }

    public Account getResult() {
        return result;
    }

    public void setResult(Account result) {
        this.result = result;
    }

  

}
