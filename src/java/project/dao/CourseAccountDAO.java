/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author DA CUOI
 */
public class CourseAccountDAO {

//    public static int addCourseAccount(CourseAccount courseAccount) {
//        Integer result = null;
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Transaction tx = null;
//        Integer courseID = null;
//        try {
//            tx = session.beginTransaction();
//            courseID = (Integer) session.save(courseAccount);
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
}
