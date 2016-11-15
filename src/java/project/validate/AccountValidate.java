/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.validate;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import project.DO.Account;
import project.dao.AccountDAO;

/**
 *
 * @author DA CUOI
 */
@FacesValidator("project.validate.AccountValidate")
public class AccountValidate implements Validator {

    @Override
    public void validate(FacesContext fc, UIComponent uic, Object o) throws ValidatorException {
        Account account = null;
        String username = o.toString();
        if (!username.equals("")) {
            account = AccountDAO.getAccountByUsername(username);
        }
        if (account != null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "Tên tài khoản đã tồn tại");
            throw new ValidatorException(msg);
        }
    }
}
