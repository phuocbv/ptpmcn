/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dao;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import project.DO.Account;
import project.DO.CommentCourse;
import project.DO.ShareCourse;

/**
 *
 * @author dacuoi
 */
public class CommentCourseDAO {

    //them tai khoan nguoi dung
    public static int addCommentCourse(CommentCourse commentCourse) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Integer result = -1;
        try {
            tx = session.beginTransaction();
            result = (Integer) session.save(commentCourse);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result.intValue();
    }

    public static boolean deleteCommentCourse(CommentCourse commentCourse) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean result = false;
        try {
            tx = session.beginTransaction();
            CommentCourse cc = (CommentCourse) session.get(CommentCourse.class, commentCourse.getIdcommentCourse());
            session.delete(cc);
            tx.commit();
            result = true;
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
    
    public static boolean deleteCommentCourseById(CommentCourse cc) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean result = false;
        try {
            tx = session.beginTransaction();
            CommentCourse ind = (CommentCourse) session.get(CommentCourse.class,
                    cc.getIdcommentCourse());
            session.delete(ind);
            tx.commit();
            result = true;
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

    public static List<CommentCourse> getCommentCourseByAccountAndShareCourse(ShareCourse shareCourse, String states) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<CommentCourse> list = null;
        System.out.print(shareCourse.getIdshareCourse());
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM CommentCourse "
                    + " WHERE id_share_create = :id_share_create "
                    + " AND column1 = :column1 "
                    + " ORDER BY create_date DESC");
            query.setParameter("id_share_create", shareCourse.getColumn1());
            query.setParameter("column1", states);
            query.setMaxResults(10);
            list = query.list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return list;
    }
    
    
}
