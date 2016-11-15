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
import project.DO.Course;
import project.dao.CourseDAO;

/**
 *
 * @author DA CUOI
 */
@FacesValidator("project.validate.CourseValidate")
public class CourseValidate implements Validator {

    @Override
    public void validate(FacesContext fc, UIComponent uic, Object o) throws ValidatorException {
        Course course = null;
        String courseName = o.toString();
        if(!courseName.equals("")){
           course = CourseDAO.getCourseByCourseName(courseName);
        }
        if(course != null){
             FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "Tên khóa học đã tồn tại");
            throw new ValidatorException(msg);
        }
    }

}
