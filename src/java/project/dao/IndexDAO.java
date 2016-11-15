/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dao;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import project.DO.Account;
import project.DO.Index;
import project.DO.ShareCourse;

/**
 *
 * @author DA CUOI
 */
public class IndexDAO {

    public static int addIndex(Index index) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        int indexID = -1;
        try {
            tx = session.beginTransaction();
            indexID = (Integer) session.save(index);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return indexID;
    }

    public static Index getIndexById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Index result = null;
        try {
            tx = session.beginTransaction();
            result = (Index) session.get(Index.class, id);
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

    public static boolean deleteIndexById(Index index) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean result = false;
        try {
            tx = session.beginTransaction();
            Index ind = (Index) session.get(Index.class, index.getIdindex());
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

    public static List<Index> getIndexByIdShareCourse(ShareCourse shareCourse) {
        List<Index> list = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        //Session session = new Configuration().configure().buildSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM Index WHERE id_share_course = :id_share_course ORDER BY level ASC");
            query.setParameter("id_share_course", shareCourse.getIdshareCourse());
            list = query.list();
            tx.commit();
        } catch (NullPointerException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
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

    public static boolean cloneCourse(List<Index> list, ShareCourse shareCourse) {
        boolean result = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        List<Integer> listId = new ArrayList<>();
        //List<Index> listCopy = list;
        try {
            Integer indexID;
            tx = session.beginTransaction();
            for (Index item : list) {
                //Index index = listCopy.get(list.indexOf(item));
                Index index = new Index();
                index.setName(item.getName());
                index.setContent(item.getContent());
                index.setTitle(item.getTitle());
                index.setCreateDate(item.getCreateDate());
                index.setLevel(item.getLevel());
                index.setIdShareCourse(shareCourse.getIdshareCourse());
                index.setColumn1(item.getColumn1());
                index.setColumn2(item.getIdindex().toString());
                if (item.getLevel() > 0) {
                    int parent = item.getIdParent();
                    for (Index i : list) {//tim chi muc cua id cha trong list
                        if (i.getIdindex() == parent) {
                            int a = list.indexOf(i);
                            index.setIdParent(listId.get(a));
                            break;
                        }
                    }
                }
                indexID = (Integer) session.save(index);
                listId.add(indexID);
            }
            tx.commit();
            result = true;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            result = false;
        } finally {
            session.close();
        }
        return result;
    }
}
