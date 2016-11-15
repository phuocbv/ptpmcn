package core.controller;

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
    private boolean loggedIn;
    private boolean logInIsAdmin = false;
    private boolean logInIsUser = false;
    private Account result;
    FacesContext context;
    public LoginController(){
        account = new Account();
        context = FacesContext.getCurrentInstance();
    }
    
    public String doLogin(){
        if(account.getUsername() == ""){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", ""));
            return TO_LOGIN;
        }
        if(account.getUsername() != "" && account.getPassword() != ""){
            
            result = AccountDAO.getAccountByUsername(account);
            //Account result = AccountDAO.getAccount(account);
            if(result == null) {
                //context.addMessage(null, new FacesMessage("Error", "Đăng nhập không thành công"));
                return TO_LOGIN;
            }
            loggedIn = true;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().put(CONFIG.SESSION_NAME_OF_ADMIN, result);
            //context.addMessage(null, new FacesMessage("Error", "Đăng nhập thành công"));
            if( result.getColumn1().equals("admin")){
                logInIsAdmin = true;
            }else{
                logInIsUser = true;
            }
            String link = result.getColumn1().equals("admin") ? REDIRECT_TO_HOME_ADMIN : REDIRECT_TO_HOME_USER;
            return link;
        }
        loggedIn = false;
        context.addMessage(null, new FacesMessage("Error", "Đăng nhập không thành công"));
        return TO_LOGIN;
    }
    
    public String doLogout(){
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().clear(); //remove(CONFIG.SESSION_NAME_OF_ADMIN);
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