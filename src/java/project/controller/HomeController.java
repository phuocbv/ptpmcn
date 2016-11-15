/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.controller;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import project.DO.*;
import project.config.CONFIG;
import project.dao.CourseDAO;

/**
 *
 * @author DA CUOI
 */
@ManagedBean(name = "homeController")
@SessionScoped
public class HomeController {

    private Account account;
    private String username;
    private List<Course> listCourse;
    private Course course;
    private Date createDate;

    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    public HomeController() {
        FacesContext context = FacesContext.getCurrentInstance();
        account = (Account) context.getExternalContext().getSessionMap().get(CONFIG.SESSION_NAME_OF_USER);
        try {
            listCourse = CourseDAO.getListCourseOfMember(account);
        } catch (SQLException ex) {
        }
        username = account.getUsername();
    }

    public void addCourse() {
        course.setImage("resources/image/coach-icon-12.png");
        course.setCreateDate(format.format(createDate));
        listCourse.add(course);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Course> getListCourse() {
        if (listCourse == null) {
            listCourse = new ArrayList<Course>();
        }
        return listCourse;
    }

    public void setListCourse(List<Course> listCourse) {
        this.listCourse = listCourse;
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
        createDate = new Date();
        return new Course();
    }

    public Date getCreateDate() {
        if (createDate == null) {
            createDate = new Date();
        }
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

}
