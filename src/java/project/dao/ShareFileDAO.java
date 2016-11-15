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
import project.DO.Account;
import project.DO.Document;
import project.DO.ShareFile;
import project.config.SQL_USER;

/**
 *
 * @author DA CUOI
 */
public class ShareFileDAO {

    public static int addShareFile(ShareFile shareFile) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        int shareFileID = -1;
        try {
            tx = session.beginTransaction();
            shareFileID = (Integer) session.save(shareFile);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return shareFileID;
    }

    //delete document follow document and account create it
    public static boolean deleteShareFile(Document document, Account account) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean result = false;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM ShareFile "
                    + " WHERE id_account_use = :id_account_use "
                    + " AND id_account_share = :id_account_share "
                    + " AND id_account_create = :id_account_create "
                    + " AND id_file = :id_file ");
            query.setParameter("id_account_use", account.getIdaccount());
            query.setParameter("id_account_create", account.getIdaccount());
            query.setParameter("id_account_share", account.getIdaccount());
            query.setParameter("id_file", document.getIdfile());
            List list = query.list();
            ShareFile sf = null;
            if (list != null && list.size() > 0) {
                sf = (ShareFile) list.get(0);
            }
            session.delete(sf);
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

    public static boolean deleteShareFileById(ShareFile shareFile) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean result = false;
        try {
            tx = session.beginTransaction();
            ShareFile sf = (ShareFile) session.get(ShareFile.class, shareFile.getIdshareFile());
            session.delete(sf);
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

//    public static ShareFile getShareFileById(ShareFile shareFile) {
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Transaction tx = null;
//        int shareFileID = -1;
//        try {
//            tx = session.beginTransaction();
//            shareFileID = (Integer) session.save(shareFile);
//            tx.commit();
//        } catch (HibernateException e) {
//            if (tx != null) {
//                tx.rollback();
//            }
//            e.printStackTrace();
//        } finally {
//            session.close();
//        }
//        return shareFileID;
//    }
    public static List<ShareFile> getListShareFileByIdAccountUse(Account accountUse) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<ShareFile> result = null;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery(" FROM ShareFile "
                    + " WHERE id_account_use = :id_account_use "
                    + " AND id_account_share <> id_account_use ");
            query.setParameter("id_account_use", accountUse.getIdaccount());
            result = query.list();
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

    //my share file follow document and account
    public static ShareFile shareFileOfMy(Document document, Account account) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        ShareFile sf = null;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM ShareFile "
                    + " WHERE id_account_use = :id_account_use "
                    + " AND id_account_share = :id_account_share "
                    + " AND id_account_create = :id_account_create "
                    + " AND id_file = :id_file ");
            query.setParameter("id_account_use", account.getIdaccount());
            query.setParameter("id_account_create", account.getIdaccount());
            query.setParameter("id_account_share", account.getIdaccount());
            query.setParameter("id_file", document.getIdfile());
            List list = query.list();
            if (list != null && list.size() > 0) {
                sf = (ShareFile) list.get(0);
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
        return sf;
    }

    //admin and user
    //share tai lieu den cac tai khoan khac dua vao accountCreate, accountShare, idFile
    public static boolean shareFileToDiffirentAccount(String[] arrayAccount, ShareFile sf, int accountShare, int idFile) {
        boolean result = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for (String item : arrayAccount) {
                ShareFile shareFile = new ShareFile();
                shareFile.setIdAccountCreate(sf.getIdAccountCreate());
                shareFile.setIdAccountShare(accountShare);
                shareFile.setIdAccountUse(Integer.valueOf(item));
                shareFile.setIdFile(idFile);
                //shareFile.setColumn1(String.valueOf(sf.getIdFile()));
                session.save(shareFile);
            }
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

    //user
    //param Account and Document
    public static ShareFile getShareFileByAccountUseAndIdFile(Account accountUses, Document document) {
        ShareFile shareFile = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM ShareFile WHERE id_account_use = :id_account_use AND id_file = :id_file");
            System.out.println("ShareFileDAO: " + query.getQueryString());
            query.setParameter("id_account_use", accountUses.getIdaccount());
            query.setParameter("id_file", document.getIdfile());
            List list = query.list();
            if (list != null && list.size() > 0) {
                shareFile = (ShareFile) list.get(0);
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
        return shareFile;
    }

    //param Account and int
    public static ShareFile getShareFileByAccountUseAndIdFile(Account accountUses, int document) {
        ShareFile shareFile = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM ShareFile WHERE id_account_use = :id_account_use AND id_file = :id_file AND id_account_use <> id_account_share ");
            System.out.println("ShareFileDAO: " + query.getQueryString());
            query.setParameter("id_account_use", accountUses.getIdaccount());
            query.setParameter("id_file", document);
            List list = query.list();
            if (list != null && list.size() > 0) {
                shareFile = (ShareFile) list.get(0);
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
        return shareFile;
    }

}
