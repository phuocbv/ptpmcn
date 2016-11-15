/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import project.DO.AccountInfo;
import project.DO.Course;

/**
 *
 * @author DA CUOI
 */
public class AccountInfoDAO {

    //admin
    //them thong tin tai khoan
    public static int addAccountInfo(AccountInfo accountInfo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Integer result = -1;
        try {
            tx = session.beginTransaction();
            result = (Integer) session.save(accountInfo);
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
    
    
}
