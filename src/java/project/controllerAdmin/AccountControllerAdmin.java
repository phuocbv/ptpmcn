/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.controllerAdmin;

import core.util.MD5;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import project.DO.Account;
import project.DO.AccountInfo;
import project.config.CONFIG;
import project.dao.AccountDAO;
import project.dao.AccountInfoDAO;

/**
 *
 * @author DA CUOI
 */
@ManagedBean(name = "accountControllerAdmin")
@ViewScoped
public class AccountControllerAdmin {

    private Account account;
    private List<Account> listAccountManageByAdmin;
    private Account accountSelected;
    private Account accountSelectInDataTable;
    private AccountInfo accountInfo;
    private String confirmPassword;

    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    //danh sach tai khoan duoc tao boi nguoi quan tri voi id tuong ung
    private List<Account> listAccountCreatedByAdmin;

    public AccountControllerAdmin() {
        FacesContext context = FacesContext.getCurrentInstance();
        account = (Account) context.getExternalContext().getSessionMap().get(CONFIG.SESSION_NAME_OF_ADMIN);
        accountSelectInDataTable = new Account();
        listAccountCreatedByAdmin = AccountDAO.getListAccountCreatedByAdmin(account);
    }

    public void addAccount() {
        if (accountSelected != null && accountInfo != null) {
            accountSelected.setAccountName(accountInfo.getFirstName() + " " + accountInfo.getLastName());
            accountSelected.setCreateDate(format.format(new Date()));
            accountSelected.setColumn1("user");
            accountSelected.setColumn2(account.getIdaccount().toString());
            int idAccount = AccountDAO.addAccountInfo(accountSelected);
            if (idAccount > 0) {
                accountInfo.setSchool("school");
                accountInfo.setIdAccount(idAccount);
                int idAccountInfo = AccountInfoDAO.addAccountInfo(accountInfo);
                if (idAccountInfo > 0) {
                    listAccountCreatedByAdmin.add(accountSelected);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Thêm thành công."));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Thêm không thành công."));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Thêm không thành công."));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Thông tin chưa đầy đủ."));
        }
    }

    public void deleteAccount() {
        if (accountSelectInDataTable != null) {
            boolean b = AccountDAO.deleteAccountById(accountSelectInDataTable.getIdaccount());
            if (b) {
                listAccountCreatedByAdmin = AccountDAO.getListAccountCreatedByAdmin(account);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Xóa thành công."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Xóa không thành công."));
            }
        }
    }

    public List<Account> getListAccountManageByAdmin() {
        if (listAccountManageByAdmin == null) {
            listAccountManageByAdmin = new ArrayList<Account>();
        }
        return listAccountManageByAdmin;
    }

    public void setListAccountManageByAdmin(List<Account> listAccountManageByAdmin) {
        this.listAccountManageByAdmin = listAccountManageByAdmin;
    }

    public List<Account> getListAccountCreatedByAdmin() {
        return listAccountCreatedByAdmin;
    }

    public void setListAccountCreatedByAdmin(List<Account> listAccountCreatedByAdmin) {
        this.listAccountCreatedByAdmin = listAccountCreatedByAdmin;
    }

    public Account resetAccountSelect() {
        return new Account();
    }

    public Account getAccountSelected() {
        if (accountSelected == null) {
            accountSelected = new Account();
        }
        return accountSelected;
    }

    public void setAccountSelected(Account accountSelected) {
        this.accountSelected = accountSelected;
    }

    public AccountInfo resetAccountInfo() {
        return new AccountInfo();
    }

    public AccountInfo getAccountInfo() {
        if (accountInfo == null) {
            accountInfo = new AccountInfo();
        }
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Account getAccountSelectInDataTable() {
        return accountSelectInDataTable;
    }

    public void setAccountSelectInDataTable(Account accountSelectInDataTable) {
        this.accountSelectInDataTable = accountSelectInDataTable;
    }

}
