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

/**
 *
 * @author DA CUOI
 */
@FacesValidator(value="project.validate.EqualsValidator")
public class EqualsValidate implements Validator {

    @Override
    public void validate(FacesContext fc, UIComponent uic, Object o) throws ValidatorException {
        Object otherValue = uic.getAttributes().get("otherValue");

        if (o == null || otherValue == null) {
            return; // Let required="true" handle.
        }

        if (!o.equals(otherValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "Hai mật khẩu không trùng nhau");
            throw new ValidatorException(msg);
        }
        
    }

}
