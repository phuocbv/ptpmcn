/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.controllerAdmin;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import project.DO.Account;
import project.DO.Course;
import project.DO.Index;
import project.DO.ShareCourse;
import project.config.CONFIG;
import project.dao.CourseDAO;
import project.dao.IndexDAO;
import project.dao.ShareCourseDAO;

/**
 *
 * @author DA CUOI
 */
@ManagedBean(name = "homeControllerAdmin")
@ViewScoped
public class HomeControllerAdmin {

    List<Course> listCourseByAdminCreate;
    private Account account;
    private Course course;
    private Date courseCreateDate;

    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    FacesContext message;

    public HomeControllerAdmin() {
        FacesContext context = FacesContext.getCurrentInstance();
        account = (Account) context.getExternalContext().getSessionMap().get(CONFIG.SESSION_NAME_OF_ADMIN);
        try {
            listCourseByAdminCreate = CourseDAO.getListCourseByAdminCreate(account);
        } catch (SQLException ex) {
            Logger.getLogger(HomeControllerAdmin.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void addCourseByAdmin() {
        course.setImage("resources/image/coach-icon-12.png");
        course.setCreateDate(format.format(courseCreateDate));

        int idCourse = CourseDAO.addCourse(course);
        if (idCourse > 0) {
            ShareCourse shareCourse = new ShareCourse();
            shareCourse.setCloned("yes");
            shareCourse.setIdAccountUse(account.getIdaccount());
            shareCourse.setIdAccountShare(account.getIdaccount());
            shareCourse.setIdAccountCreate(account.getIdaccount());
            shareCourse.setIdCourse(idCourse);
            int idShareCourse = ShareCourseDAO.addShareCourse(shareCourse);

            if (idShareCourse > 0) {
                Index index = new Index();
                index.setName(course.getCourseName());
                index.setTitle(course.getCourseName());
                index.setContent("content");
                index.setCreateDate(format.format(courseCreateDate));
                index.setIdShareCourse(idShareCourse);
                index.setIdParent(0);
                index.setLevel(0);
                index.setColumn1("root");
                int idIndex = IndexDAO.addIndex(index);
                
                index.setIdindex(idIndex);
                shareCourse.setIdshareCourse(idShareCourse);
                listCourseByAdminCreate.add(course);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Thêm thành công."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Thêm không thành công."));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Thêm không thành công."));
        }
    }

    public List<Course> getListCourseByAdminCreate() {
        if (listCourseByAdminCreate == null) {
            listCourseByAdminCreate = new ArrayList<>();
        }
        return listCourseByAdminCreate;
    }

    public void setListCourseByAdminCreate(List<Course> listCourseByAdminCreate) {
        this.listCourseByAdminCreate = listCourseByAdminCreate;
    }

    public Date getCourseCreateDate() {
        if (courseCreateDate == null) {
            courseCreateDate = new Date();
        }
        return courseCreateDate;
    }

    public void setCourseCreateDate(Date courseCreateDate) {
        this.courseCreateDate = courseCreateDate;
    }

    public Course getCourse() {
        if (course == null) {
            course = new Course();
        }
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course resetCourse() {
        return new Course();
    }
    
    
}
