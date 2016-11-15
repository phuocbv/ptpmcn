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
import project.DO.Document;
import project.DO.Index;
import project.DO.ShareFile;

/**
 *
 * @author DA CUOI
 */
public class FileDAO {

    public static int addFile(Document document) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        int fileID = -1;
        try {
            tx = session.beginTransaction();
            fileID = (int) session.save(document);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return fileID;
    }

    public static boolean updateShareFileForFile(Document document, ShareFile shareFile) {
        boolean result = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Document doc = (Document) session.get(Document.class, document.getIdfile());
            doc.setColumn1(shareFile.getIdshareFile().toString());
            session.update(doc);
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

    public static Document getDocumentByIdIndex(int idIndex) {
        Document result = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        //Session session = new Configuration().configure().buildSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM Document WHERE id_index = :id_index");
            query.setParameter("id_index", idIndex);
            List list = query.list();
            if (list != null && list.size() > 0) {
                result = (Document) list.get(0);
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
        return result;
    }
}
