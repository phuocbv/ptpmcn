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
import project.DO.CommentFile;
import project.DO.Index;
import project.DO.ShareCourse;

/**
 *
 * @author dacuoi
 */
public class CommentFileDAO {
    public static int addCommentFile(CommentFile commentFile) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Integer result = -1;
        try {
            tx = session.beginTransaction();
            result = (Integer) session.save(commentFile);
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

//    public static boolean deleteCommentCourse(CommentCourse commentCourse) {
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Transaction tx = null;
//        boolean result = false;
//        try {
//            tx = session.beginTransaction();
//            CommentCourse cc = (CommentCourse) session.get(CommentCourse.class, commentCourse.getIdcommentCourse());
//            session.delete(cc);
//            tx.commit();
//            result = true;
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
    
    public static boolean deleteCommentFileById(CommentFile cc) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean result = false;
        try {
            tx = session.beginTransaction();
            CommentFile ind = (CommentFile) session.get(CommentFile.class,
                    cc.getIdcommentFile());
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

    public static List<CommentFile> getCommentFileByIndex(int idIndex, String states) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<CommentFile> list = null;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM CommentFile "
                    + " WHERE id_file = :id_file "
                    + " AND column1 = :column1 "
                    + " ORDER BY create_date DESC");
            query.setParameter("id_file", idIndex);
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
