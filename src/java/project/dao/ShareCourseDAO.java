/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.dao;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import project.DO.Account;
import project.DO.Course;
import project.DO.ShareCourse;
import project.config.*;

/**
 *
 * @author DA CUOI
 */
public class ShareCourseDAO {

    public static int addShareCourse(ShareCourse shareCourse) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        int shareCourseID = -1;
        try {
            tx = session.beginTransaction();
            shareCourseID = (Integer) session.save(shareCourse);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return shareCourseID;
    }

    public static boolean deleteShareCourseById(ShareCourse shareCourse) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean result = false;
        try {
            tx = session.beginTransaction();
            ShareCourse s = (ShareCourse) session.get(ShareCourse.class, shareCourse.getIdshareCourse());
            session.delete(s);
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

    /**
     * admin and user
     *
     * @param idaccount
     * @param idcourse
     * @return
     */
    //lay share course theo id course và id account admin create course
    public static ShareCourse getShareCouseByIdAccountAndIdCourseAdmin(int idaccount, int idcourse) {
        ShareCourse result = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        //Session session = new Configuration().configure().buildSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            SQLQuery query = session.createSQLQuery(SQL.GET_SHARE_COURSE_BY_IDACCOUNT_AND_IDCOURSE);
            query.addEntity(ShareCourse.class);
            query.setParameter("id_course", idcourse);
            query.setParameter("id_account_share", idaccount);
            query.setParameter("id_account_use", idaccount);
            query.setParameter("id_account_create", idaccount);
            List list = query.list();
            if (list != null && list.size() > 0) {
                result = (ShareCourse) list.get(0);
            }
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
        return result;
    }

    public static boolean insertMemberInCourse(List<String> listAccount, Account accountCreate, Course course) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        boolean result = false;
        try {
            tx = session.beginTransaction();
            for (String item : listAccount) {
                ShareCourse shareCourse = new ShareCourse();
                shareCourse.setIdAccountUse(Integer.parseInt(item));
                shareCourse.setIdCourse(course.getIdcourse());
                shareCourse.setIdAccountShare(0);
                shareCourse.setIdAccountCreate(accountCreate.getIdaccount());
                shareCourse.setCloned(CONFIG.CONFIG_NOT_CLONE);
                session.save(shareCourse);
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

//admin
    public static boolean shareCourseToMember(List<String> listIdMember, Account adminCurrent, Course courseCurrent) {
        boolean result = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for (String item : listIdMember) {
                Query query = session.createQuery("FROM ShareCourse WHERE id_account_use = :id_account_use "
                        + " AND id_course = :id_course AND id_account_create = :id_account_create");
                query.setParameter("id_account_use", item);
                query.setParameter("id_course", courseCurrent.getIdcourse());
                query.setParameter("id_account_create", adminCurrent.getIdaccount());
                List list = query.list();
                if (list != null && list.size() > 0) {
                    ShareCourse s = (ShareCourse) list.get(0);
                    s.setIdAccountShare(adminCurrent.getIdaccount());
                    session.update(s);
                }
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

//fun user
    //tim share_couese theo id nguoi dung hien tai va id khoa hoc
    public static ShareCourse getShareCourseByIdAccountAndIdCourse(int idAccountCurrent, int idCourseCurrent) {
        ShareCourse result = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM ShareCourse WHERE id_account_use = :id_account_use "
                    + " AND id_course = :id_course AND id_account_share <> 0 ");
            query.setParameter("id_account_use", idAccountCurrent);
            query.setParameter("id_course", idCourseCurrent);
            List<ShareCourse> list = query.list();
            if (list != null && list.size() > 0) {
                result = list.get(0);
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

//fun user
    //lay danh sach thanh vien trong khoa hoc nhung chua duoc chia se khoa hoc 
    public static List<ShareCourse> getListMemberNotShareCourse(ShareCourse shareCourse) {
        List<ShareCourse> list = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM ShareCourse WHERE id_course = :id_course "
                    + " AND id_account_create = :id_account_create AND id_account_share = 0 ");
            query.setParameter("id_course", shareCourse.getIdCourse());
            query.setParameter("id_account_create", shareCourse.getIdAccountCreate());
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

    //user
    //tim sharecourse dua vao id nguoi dung va id khoa hoc 
    //vi khong bao gio co 1 mguoi ma co 2 lan tham gia cung 1 khoa hoc
    public static ShareCourse getShareCourseByIdAccountUserAndIdCourse(Account accountUser, Course courseCurrent) {
        ShareCourse shareCourse = null;
        List<ShareCourse> list = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("FROM ShareCourse WHERE id_course = :id_course "
                    + " AND id_account_use = :id_account_use ");
            query.setParameter("id_course", courseCurrent.getIdcourse());
            query.setParameter("id_account_use", accountUser.getIdaccount());
            list = query.list();
            if (list != null && list.size() > 0) {
                shareCourse = list.get(0);
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
        return shareCourse;
    }

    //user
    //update trang thai da clone khoa hoc cua nguoi dung
    public static boolean updateStatusClone(ShareCourse shareCourse) {
        boolean result = false;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ShareCourse sc = (ShareCourse) session.get(ShareCourse.class, shareCourse.getIdshareCourse());
            sc.setCloned(CONFIG.CONFIG_CLONED);
            session.update(sc);
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
