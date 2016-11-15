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
import org.hibernate.Session;
import org.hibernate.Transaction;
import project.DO.Account;
import project.DO.Document;
import project.config.SQL;
import project.config.SQL_USER;

/**
 *
 * @author DA CUOI
 */
public class DocumentDAO {

    public static int addAccountInfo(Account account) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Integer result = -1;
        try {
            tx = session.beginTransaction();
            result = (Integer) session.save(account);
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

    public static int addDocument(Document document) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Integer result = -1;
        try {
            tx = session.beginTransaction();
            result = (Integer) session.save(document);
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

    public static Document getDocumentById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Document result = null;
        try {
            tx = session.beginTransaction();
            result = (Document) session.get(Document.class, id);
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

    //delete document
    public static boolean deleteDocument(Document document) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean result = false;
        try {
            tx = session.beginTransaction();
            Document d
                    = (Document) session.get(Document.class, document.getIdfile());
            session.delete(d);
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
    //lay danh sach tai lieu cua nguoi dung
    public static List<Document> getListDocumentOfUser(Account accountUser) throws SQLException {
        List<Document> list = null;
        Connection conn = null;
        try {
            list = new ArrayList<>();
            conn = JDBC.connectDB();
            conn.setAutoCommit(false);
            PreparedStatement pstm = conn.prepareStatement(SQL_USER.GET_LIST_DOCUMENT_OF_USER);
            pstm.setInt(1, accountUser.getIdaccount());
            pstm.setInt(2, accountUser.getIdaccount());
            pstm.setInt(3, accountUser.getIdaccount());
            ResultSet rs = pstm.executeQuery();
            System.out.println(SQL_USER.GET_LIST_DOCUMENT_OF_USER);
            while (rs.next()) {
                Document item = new Document();
                item.setIdfile(rs.getInt("idfile"));
                item.setSrc(rs.getString("src"));
                item.setSize(rs.getString("size"));
                item.setTitleFile(rs.getString("title_file"));
                item.setType(rs.getString("type"));
                item.setCreateDate(rs.getString("create_date"));
                item.setIdIndex(rs.getInt("id_index"));
                list.add(item);
            }
            rs.close();
            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }
            Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, "error-getListAccountUserByAdminCreate", ex);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    //user and admin
    //lay danh sach tai lieu duoc chia se boi nguoi dung khac
    public static List<Document> getListDocumentSharedByDiffirentAccount(Account accountCurrent) throws SQLException {
        List<Document> list = null;
        Connection conn = null;
        try {
            list = new ArrayList<>();
            conn = JDBC.connectDB();
            conn.setAutoCommit(false);
            PreparedStatement pstm = conn.prepareStatement(SQL_USER.GET_LIST_DOCUMENT_SHARED_FROM_DIFFIRENT_ACCOUNT);
            System.out.println(SQL_USER.GET_LIST_DOCUMENT_SHARED_FROM_DIFFIRENT_ACCOUNT);
            pstm.setInt(1, accountCurrent.getIdaccount());
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                Document item = new Document();
                item.setIdfile(rs.getInt("idfile"));
                item.setSrc(rs.getString("src"));
                item.setSize(rs.getString("size"));
                item.setTitleFile(rs.getString("title_file"));
                item.setType(rs.getString("type"));
                item.setCreateDate(rs.getString("create_date"));
                item.setIdIndex(rs.getInt("id_index"));
                list.add(item);
            }
            rs.close();
            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }
            Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, "error-getListDocumentSharedByDiffirentAccount", ex);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }
}
