/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.controller;

import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import project.DO.Account;
import project.DO.Course;
import project.DO.ShareCourse;
import project.DO.ShareFile;
import project.config.CONFIG;
import project.dao.AccountDAO;
import project.dao.CommentCourseDAO;
import project.dao.CourseDAO;
import project.dao.ShareCourseDAO;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dacuoi
 */
@ManagedBean(name = "memberControllerUser")
@ViewScoped
public class MemberController {

    public int courseIdCurrent;
    public Account account;
    public Account selectedAccount;
    public Course courseCurrent;
    public ShareCourse shareCourse;
    private boolean cloned;
    private List<Account> listAccountInCourse;

    public MemberController() {
        FacesContext context = FacesContext.getCurrentInstance();
        account = (Account) context.getExternalContext().getSessionMap().get(CONFIG.SESSION_NAME_OF_USER);
    }

    public int getCourseIdCurrent() {
        return courseIdCurrent;
    }

    public void setCourseIdCurrent(int courseIdCurrent) {
        this.courseIdCurrent = courseIdCurrent;
        System.out.println("vao1");
        if (courseIdCurrent > 0 && account != null) {
            System.out.println("vao");
            courseCurrent = CourseDAO.getCourseById(courseIdCurrent);
            shareCourse = ShareCourseDAO.getShareCourseByIdAccountAndIdCourse(account.getIdaccount(), courseIdCurrent);
//            if (shareCourse.getCloned().equals(CONFIG.CONFIG_CLONED)) {
//                cloned = true;
//            } else {
//                ShareCourse sc = ShareCourseDAO.
//                        getShareCouseByIdAccountAndIdCourseAdmin(shareCourse.getIdAccountCreate(), courseIdCurrent);
//                cloned = false;
//            }
//            shareCourse.setColumn1(String.valueOf(shareCourse.getIdshareCourse()));
            try {
                listAccountInCourse = AccountDAO.getListAccountInCourse(account, courseCurrent);
            } catch (SQLException ex) {
                Logger.getLogger(MemberController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public List<Account> getListAccountInCourse() {
        return listAccountInCourse;
    }

    public void setListAccountInCourse(List<Account> listAccountInCourse) {
        this.listAccountInCourse = listAccountInCourse;
    }

    public Account getSelectedAccount() {
        return selectedAccount;
    }

    public void setSelectedAccount(Account selectedAccount) {
        this.selectedAccount = selectedAccount;
    }

}
