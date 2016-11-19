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
public class SQL {

    public static final String SQL_ = "";
    public static final String GET_LIST_COURSE_OF_ACCOUNT = "SELECT * "
            + " FROM mydb.course C JOIN "
            + "	( SELECT C_A.id_cource AS id_course "
            + "	FROM mydb.account A "
            + "	JOIN mydb.course_account C_A "
            + "	ON A.idaccount = C_A.id_account_create "
            + " WHERE A.idaccount = ? "
            + "	) new_table "
            + " on C.id = new_table.id_course ";
    
    public static final String GET_LIST_COURSE_BY_ADMIN_CREATE = " SELECT DISTINCT * "
            + " FROM mydb.course C JOIN "
            + " ( SELECT S_A.id_course AS id_course "
            + " FROM mydb.account A "
            + " JOIN mydb.share_course S_A "
            + " ON A.idaccount = S_A.id_account_create "
            + " WHERE A.idaccount = ? "
            + " ) new_table "
            + " ON C.idcourse = new_table.id_course ";

    public static final String GET_SHARE_COURSE_BY_IDACCOUNT_AND_IDCOURSE = " SELECT * FROM share_course "
            + " WHERE id_course = :id_course "
            + " AND id_account_create = :id_account_create "
            + " AND id_account_use = :id_account_use "
            + " AND id_account_share = :id_account_share ";

    public static final String GET_LIST_ACCOUNT_NOT_ATTEND_COURSE = " SELECT * "
            + " FROM mydb.account A "
            + " WHERE A.column1 = 'user' "
            + " AND A.column2 = ? "
            + " AND A.idaccount NOT IN "
            + " ( "
            + "	SELECT A.idaccount "
            + "	FROM mydb.account A "
            + "	JOIN mydb.share_course S_C "
            + "	ON A.idaccount = S_C.id_account_use "
            + "	WHERE A.column1 = 'user' "
            + "	AND A.column2 = ? "
            + " AND S_C.id_course = ? "
            + " ) ";

    public static final String GET_LIST_ACCOUNT_ATTENDED_COURSE_NOT_SHARED = "SELECT * "
            + " FROM mydb.account A "
            + " WHERE A.idaccount IN "
            + " ( "
            + " SELECT S_C.id_account_use "
            + " FROM mydb.share_course S_C "
            + " WHERE S_C.id_course = ? "
            + " AND S_C.id_account_create = ? "
            + " AND S_C.id_account_share = ? "
            + " AND S_C.id_account_share <> S_C.id_account_use "
            + " ) ";
    
     public static final String GET_LIST_ACCOUNT_IN_COURSE = "SELECT * "
            + " FROM mydb.account A "
            + " WHERE A.idaccount IN "
            + " ( "
            + " SELECT S_C.id_account_use "
            + " FROM mydb.share_course S_C "
            + " WHERE S_C.id_course = ? "
            + " AND S_C.id_account_use <> S_C.id_account_create "
            + " ) ";
}
