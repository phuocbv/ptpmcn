/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import project.DO.Account;
import project.DO.Course;
import project.config.SQL;
import project.config.SQL_USER;

/**
 *
 * @author DA CUOI
 */
public class CourseDAO {

    public static int addCourse(Course cource) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Integer courseID = -1;
        try {
            tx = session.beginTransaction();
            courseID = (Integer) session.save(cource);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return courseID.intValue();
    }

    //return course by id
    public static Course getCourseById(int idCourse) {
        Course result = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
//        Session session = new Configuration().configure().buildSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            result = (Course) session.get(Course.class, idCourse);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }
    
     //deleteCourse
    public static boolean deleteCourseById(int idCourse) {
        boolean result = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Course course = (Course) session.get(Course.class, idCourse);
            session.delete(course);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }

    //admin
    //tra lai khoa hoc theo ten kho hoc
    public static Course getCourseByCourseName(String courseName) {
        Course course = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<Course> list = null;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM Course WHERE course_name =:course_name");
            query.setParameter("course_name", courseName);
            list = query.list();
            if (list != null && list.size() > 0) {
                course = list.get(0);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return course;
    }

//    public static Course getCourseById(int idCourse) {
//        Course result = null;
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        //Session session = new Configuration().configure().buildSessionFactory().openSession();
//        Transaction tx = null;
//        try {
//            tx = session.beginTransaction();         
//            Query query = session.createQuery("FROM Course WHERE idcourse = :idcourse");
//            query.setParameter("idcourse", idCourse);
//            List list = query.list();
//           
//            System.out.print(list.size());
//            if (list != null && list.size() > 0) {
//                result = (Course) list.get(0);
//            }
//            tx.commit();
//        } catch (HibernateException e) {
//            if (tx != null) {
//                tx.rollback();
//            }
//            e.printStackTrace();
//        } finally {
//            session.close();
//        }
//        return result;
//    }
    public static List<Course> getListCourseOfAccount(Account account) throws SQLException {
        List<Course> result = null;
        Connection conn = null;
        try {
            result = new ArrayList<>();
            conn = JDBC.connectDB();
            conn.setAutoCommit(false);
            PreparedStatement pstm = conn.prepareStatement(SQL.GET_LIST_COURSE_OF_ACCOUNT);
            pstm.setInt(1, account.getIdaccount());
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                Course item = new Course();
                item.setIdcourse(rs.getInt("id"));
                item.setCourseName(rs.getString("cource_name"));
                item.setImage(rs.getString("cource_image"));
                item.setCreateDate(rs.getString("cource_create_date"));
                item.setField("field");
                item.setColumn3(rs.getString("idshare_course"));
                result.add(item);
            }
            rs.close();
            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }
            Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, "error-CourseDAO.getListCourseOfAccount", ex);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return result;
    }

//User
    //get list course of member
    public static List<Course> getListCourseOfMember(Account userCurrent) throws SQLException {
        List<Course> list = null;
        Connection conn = null;
        try {
            list = new ArrayList<>();
            conn = JDBC.connectDB();
            conn.setAutoCommit(false);
            PreparedStatement pstm = conn.prepareStatement(SQL_USER.SQL_LIST_COURSE_OF_MEMBER);
            pstm.setInt(1, userCurrent.getIdaccount());
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                Course item = new Course();
                item.setIdcourse(rs.getInt("idcourse"));
                item.setCourseName(rs.getString("course_name"));
                item.setImage(rs.getString("image"));
                item.setCreateDate(rs.getString("create_date"));
                item.setField("field");
                list.add(item);
            }
            rs.close();
            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }
            Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, "error-CourseDAO.getListCourseOfMember", ex);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

//Admin
    public static List<Course> getListCourseByAdminCreate(Account account) throws SQLException {
        List<Course> result = new ArrayList<>();
        Connection conn = null;
        try {
            conn = JDBC.connectDB();
            conn.setAutoCommit(false);
            PreparedStatement pstm = conn.prepareStatement(SQL.GET_LIST_COURSE_BY_ADMIN_CREATE);
            pstm.setInt(1, account.getIdaccount());
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                Course item = new Course();
                item.setIdcourse(rs.getInt("idcourse"));
                item.setCourseName(rs.getString("course_name"));
                item.setImage(rs.getString("image"));
                item.setCreateDate(rs.getString("create_date"));
                item.setField("field");
                result.add(item);
            }
            rs.close();
            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }
            Logger.getLogger(CourseDAO.class.getName()).log(Level.SEVERE, "error-CourseDAO.getListCourseByAdminCreate", ex);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return result;
    }
}
