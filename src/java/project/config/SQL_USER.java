/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.config;

/**
 *
 * @author DA CUOI
 */
public class SQL_USER {

    public static String SQL_LIST_COURSE_OF_MEMBER = " SELECT * "
            + " FROM mydb.course C "
            + " JOIN mydb.share_course S_C "
            + " ON S_C.id_course = C.idcourse "
            + " WHERE S_C.id_account_use = ? "
            + " AND S_C.id_account_share <> 0 ";

    public static String GET_LIST_MEMBER_NOT_SHARE_COURSE = "SELECT * "
            + " FROM mydb.account A "
            + " WHERE A.idaccount IN "
            + " ( "
            + " SELECT S_C.id_account_use "
            + " FROM mydb.share_course S_C "
            + " WHERE S_C.id_course = ? "
            + " AND S_C.id_account_create = ? "
            + " AND S_C.id_account_share = 0 "
            + " ) ";
    public static String GET_LIST_DOCUMENT_OF_USER = "select * "
            + " from mydb.document D "
            + " join mydb.share_file S_F "
            + " on D.idfile = S_F.id_file "
            + " where S_F.id_account_use = ? "
            + " and S_F.id_account_share = ? "
            + " and S_F.id_account_create = ? ";
    public static String GET_LIST_ACCOUNT_NOT_SHARE_FILE = "select * "
            + " from mydb.account A "
            + " where A.idaccount NOT IN "
            + " ( "
            + " select S_F.id_account_use "
            + " from mydb.share_file S_F "
            + " where S_F.id_file = ? "
            + " and S_F.id_account_share = ? "
            + " ) ";
    public static String GET_LIST_DOCUMENT_SHARED_FROM_DIFFIRENT_ACCOUNT = " select * "
            + " from mydb.document D "
            + " where D.idfile IN "
            + " ( "
            + " select S_F.id_file "
            + " from mydb.share_file S_F "
            + " where S_F.id_account_use = ? "
            + " and S_F.id_account_use <> S_F.id_account_share "
            + " ) ";
    
    public static String GET_LIST_SHARE_FILE_FROM_DIFFIRENT_ACCOUNT = " select * "
            + " from mydb.share_file S_F "
            + " where S_F.id_account_use = ? "
            + " and S_F.id_account_use <> S_F.id_account_share ";
           
}
