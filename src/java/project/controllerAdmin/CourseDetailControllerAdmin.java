/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.controllerAdmin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import project.DO.Account;
import project.DO.CommentCourse;
import project.DO.CommentFile;
import project.DO.Course;
import project.DO.Document;
import project.DO.Index;
import project.DO.ShareCourse;
import project.config.CONFIG;
import project.controller.CourseDetailController;
import project.dao.AccountDAO;
import project.dao.CommentCourseDAO;
import project.dao.CommentFileDAO;
import project.dao.CourseDAO;
import project.dao.FileDAO;
import project.dao.IndexDAO;
import project.dao.ShareCourseDAO;
import project.util.FileUtil;
import project.util.Tree;
import project.util.Util;

/**
 *
 * @author DA CUOI
 */
@ManagedBean(name = "courseDetailControllerAdmin")
@ViewScoped
public class CourseDetailControllerAdmin {

    public int courseIdCurrent;
    private Account account;
    private Course courseCurrent;
    private ShareCourse shareCourse;
    private TreeNode root;
    private TreeNode selectNode;
    private Tree treeUtil;
    private boolean displayChoseFile;
    private Index index;
    private Part file;
    private StreamedContent fileDownload;
    private String uploadFileName;
    private String urlFile;
    private String typeFile = "pdf";
    private boolean video = false;
    private boolean editor = false;
    private boolean mp3 = false;
    private boolean pdf = false;
    private boolean image = false;
    private String content = "";
    private String content_file = "";
    private boolean myComment = false;
    private CommentCourse selectedCommentCourse;
    private CommentFile selectedCommentFile;
    private List<CommentCourse> listGoodCommentCourse;
    private List<CommentCourse> listNotGoodCommentCourse;
    private List<CommentFile> listGoodCommentFile;
    private List<CommentFile> listNotGoodCommentFile;
    private String nameApp = "/WebApplication2";
    private String selectTypeComment = "1";
    private String selectTypeCommentFile = "1";

    private List<String> listIdAccountByAdminCreate;
    private List<String> listIdAccountAttendedNotShared;
    private List<Account> listAccountAttendedNotShared;
    private List<Account> listAccountManageByAdmin;

    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private String STATE_CURRENT = "";
    private String STATE_CURRENT_FILE = "";

    public CourseDetailControllerAdmin() {
        FacesContext context = FacesContext.getCurrentInstance();
        account = (Account) context.getExternalContext().getSessionMap().get(CONFIG.SESSION_NAME_OF_ADMIN);

        treeUtil = new Tree();
        urlFile = "/file/demo.pdf";

        listIdAccountByAdminCreate = new ArrayList<>();
        listIdAccountAttendedNotShared = new ArrayList<>();
        selectedCommentCourse = new CommentCourse();
        selectedCommentFile = new CommentFile();
    }

    public int getCourseIdCurrent() {
        return courseIdCurrent;
    }

    public void setCourseIdCurrent(int courseIdCurrent) {
        this.courseIdCurrent = courseIdCurrent;
        if (courseIdCurrent > 0 && account != null) {
            System.err.print(courseIdCurrent);
            courseCurrent = CourseDAO.getCourseById(courseIdCurrent);
            shareCourse = ShareCourseDAO.getShareCouseByIdAccountAndIdCourseAdmin(account.getIdaccount(), courseIdCurrent);
            resetTree();
            shareCourse.setColumn1(String.valueOf(shareCourse.getIdshareCourse()));
            listGoodCommentCourse = CommentCourseDAO
                    .getCommentCourseByAccountAndShareCourse(shareCourse, CONFIG.STATE_GOOT);
            listNotGoodCommentCourse = CommentCourseDAO
                    .getCommentCourseByAccountAndShareCourse(shareCourse, CONFIG.STATE_NOT_GOOT);
        }

    }

    public Index indexFromSelectElement() {
        Index index = null;
        if (selectNode != null) {
            index = (Index) selectNode.getData();
        }
        return index;
    }

    public String returnAccountById(int idAccount) {
        if (idAccount == account.getIdaccount()) {
            myComment = true;
            return "Tôi";
        }
        myComment = false;
        return AccountDAO.getAccountById(idAccount).getUsername();
    }

    public void addCommentCourse() {
        if (!content.equals("")) {
            if (selectTypeComment.equals("1")) {
                STATE_CURRENT = CONFIG.STATE_GOOT;
            } else {
                STATE_CURRENT = CONFIG.STATE_NOT_GOOT;
            }
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            CommentCourse cc = new CommentCourse();
            cc.setContent(content);
            cc.setIdShareCreate(Integer.parseInt(shareCourse.getColumn1()));
            cc.setIdAccount(account.getIdaccount());
            cc.setCreateDate(dt.format(new Date()));
            cc.setColumn1(STATE_CURRENT);
            int result = CommentCourseDAO.addCommentCourse(cc);
            if (result > 0) {
                content = "";
                listGoodCommentCourse = CommentCourseDAO
                        .getCommentCourseByAccountAndShareCourse(shareCourse, CONFIG.STATE_GOOT);
                listNotGoodCommentCourse = CommentCourseDAO
                        .getCommentCourseByAccountAndShareCourse(shareCourse, CONFIG.STATE_NOT_GOOT);
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Bình luận thành công."));
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Lỗi."));
            }
        }
    }
    
     public void addCommentFile() {
        System.out.print("vao");
        if (!content_file.equals("") && selectNode != null) {
            System.out.print("in");
            Index indexData = (Index) selectNode.getData();
            if (selectTypeCommentFile.equals("1")) {
                STATE_CURRENT_FILE = CONFIG.STATE_GOOT_FILE;
            } else {
                STATE_CURRENT_FILE = CONFIG.STATE_NOT_GOOT_FILE;
            }
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            CommentFile cc = new CommentFile();
            cc.setContent(content_file);
            cc.setIdFile(indexData.getIdindex());
            cc.setIdAccount(account.getIdaccount());
            cc.setCreateDate(dt.format(new Date()));
            cc.setColumn1(STATE_CURRENT_FILE);
            int result = CommentFileDAO.addCommentFile(cc);
            if (result > 0) {
                content_file = "";
                listGoodCommentFile = CommentFileDAO
                        .getCommentFileByIndex(indexData.getIdindex(), CONFIG.STATE_GOOT_FILE);
                listNotGoodCommentFile = CommentFileDAO
                        .getCommentFileByIndex(indexData.getIdindex(), CONFIG.STATE_NOT_GOOT_FILE);
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Bình luận thành công."));
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Lỗi."));
            }
        }
    }

    public void deleteComment() {
        System.out.print("vao");
        System.out.println(selectedCommentCourse.getIdcommentCourse());
        if (selectedCommentCourse != null) {
            boolean b = CommentCourseDAO.deleteCommentCourseById(selectedCommentCourse);
            if (b) {
                listGoodCommentCourse = CommentCourseDAO
                        .getCommentCourseByAccountAndShareCourse(shareCourse, CONFIG.STATE_GOOT);
                listNotGoodCommentCourse = CommentCourseDAO
                        .getCommentCourseByAccountAndShareCourse(shareCourse, CONFIG.STATE_NOT_GOOT);
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Xóa thành công."));
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Lỗi."));
            }
        }
    }
    
    public void deleteCommentFile() {
        System.out.print("vao");
        
        if (selectedCommentFile != null) {
            boolean b = CommentFileDAO.deleteCommentFileById(selectedCommentFile);
            if (b) {
                listGoodCommentCourse = CommentCourseDAO
                        .getCommentCourseByAccountAndShareCourse(shareCourse, CONFIG.STATE_GOOT);
                listNotGoodCommentCourse = CommentCourseDAO
                        .getCommentCourseByAccountAndShareCourse(shareCourse, CONFIG.STATE_NOT_GOOT);
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Xóa thành công."));
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Lỗi."));
            }
        }
    }

    public void editForder() {
        if (index != null) {
            if (index.getColumn1().equals("root")) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Đây là thư mục gốc.Không thể đổi được."));
                return;
            }
            if (!index.getColumn1().equals("application/foder")) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Đây không phải thư mục.Không thể đổi được."));
                return;
            }
            boolean b = IndexDAO.updateIndex(index);
            if (b) {
                resetTree();
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Thay đổi thành công."));
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Lỗi."));
            }
        } else {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Chưa thay đổi được."));
        }
    }

    public void uploadFile(FileUploadEvent event) {
        if (file != null) {
            FileUtil fileUtil = new FileUtil();
            try {
                fileUtil.uploadFile(file);
            } catch (IOException ex) {
                Logger.getLogger(CourseDetailControllerAdmin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String deleteCourse() {
        if (courseCurrent != null && shareCourse != null) {
            boolean b1 = IndexDAO.deleteIndexByIdShareCourse(shareCourse);
            boolean b2 = ShareCourseDAO.deleteShareCourseById(shareCourse);
            boolean b3 = false;
            if (b1 && b2) {
                b3 = CourseDAO.deleteCourseById(courseCurrent.getIdcourse());
            }
            if (b3) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Xóa thành công."));
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Lỗi."));
            }
        }
        return "/admin/home.xhtml?faces-redirect=true";
    }

    public void addIndex() {
        if (index != null && selectNode != null) {
            Index s = (Index) selectNode.getData();
            System.out.println(s.getColumn1());
            if (!s.getColumn1().equals("application/foder") && !s.getColumn1().equals("root")) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Bạn phải chọn thư mục."));
                return;
            }
            if (s.getLevel() >= 2) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Không được thêm thư mục bậc 3."));
                return;
            }
            Index select = (Index) selectNode.getData();
            index.setTitle(index.getName());
            index.setContent("content");
            index.setCreateDate(format.format(new Date()));
            index.setIdShareCourse(shareCourse.getIdshareCourse());
            index.setIdParent(select.getIdindex());
            index.setLevel(select.getLevel() + 1);
            index.setColumn1("application/foder");
            index.setColumn2("0");
            int idIndex = IndexDAO.addIndex(index);
            resetTree();
            if (idIndex > 0) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Thêm thư mục thành công."));
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Thêm không thành công."));
            }
        } else {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Chưa chọn thư mục gốc."));
        }
    }

    public void resetTree() {
        List<Index> list = IndexDAO.getIndexByIdShareCourse(shareCourse);
        if (list != null) {
            treeUtil.setListIndex(list);
            root = treeUtil.createTreeNodeIndex();
        }
    }

    public String addFile() throws IOException {
        if (file != null && selectNode != null) {
            String fileType = file.getContentType();
            FileUtil fileUtil = new FileUtil();
            try {
                uploadFileName = fileUtil.uploadFile(file);
            } catch (IOException ex) {
                Logger.getLogger(CourseDetailControllerAdmin.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (uploadFileName != null) {
                if (index != null && file != null) {
                    Index select = (Index) selectNode.getData();
                    if (!select.getColumn1().equals("application/foder")
                            && !select.getColumn1().equals("root")) {
                        FacesContext.getCurrentInstance()
                                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Bạn phải chọn thư mục."));
                        return "";
                    }
                    index.setName(uploadFileName.trim());
                    index.setTitle(uploadFileName.trim());
                    index.setContent("content");
                    index.setCreateDate(format.format(new Date()));
                    index.setIdShareCourse(shareCourse.getIdshareCourse());
                    index.setIdParent(select.getIdindex());
                    index.setLevel(select.getLevel() + 1);
                    index.setColumn1(fileType);
                    index.setColumn2("0");
                    int idIndex = IndexDAO.addIndex(index);
                    if (idIndex > 0) {
                        Document f = new Document();
                        f.setSrc(uploadFileName.trim());
                        f.setSize(String.valueOf(file.getSize()));
                        f.setTitleFile(uploadFileName.trim());
                        f.setType(fileType);
                        f.setCreateDate(format.format(new Date()));
                        f.setIdIndex(idIndex);
                        int idfile = FileDAO.addFile(f);
                    }
                }
            }
            file = null;
            uploadFileName = null;
            resetTree();
        }
        return "/admin/detailCourse.xhtml";
    }

    public void addMemberInCourse() {
        if (listIdAccountByAdminCreate != null && listIdAccountByAdminCreate.size() > 0) {
            boolean result = ShareCourseDAO.insertMemberInCourse(listIdAccountByAdminCreate, account, courseCurrent, shareCourse);
            if (result) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Thêm thành công."));
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Thêm không thành công."));
            }
        } else {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Không có tài khoản nào được chọn."));
        }
    }

    public List<Account> getAccountAttenedNotShare() {
        List<Account> list = null;
        if (courseCurrent != null && account != null) {
            try {
                list = AccountDAO.getListAccountAttendedCourseNotShared(account, courseCurrent);
                System.out.println("list account not share coure" + list.size());
            } catch (SQLException ex) {
                Logger.getLogger(CourseDetailControllerAdmin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return list;
    }

    public void shareCourseToMember() {
        if (listIdAccountAttendedNotShared != null && listIdAccountAttendedNotShared.size() > 0) {
            boolean b = ShareCourseDAO.shareCourseToMember(listIdAccountAttendedNotShared, account, courseCurrent);
            if (b) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Chia sẻ thành công."));
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Warn", "Chia sẻ không thành công."));
            }
        } else {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Warn", "Chưa chọn tài khoản chia sẻ."));
        }
    }

    public void viewFile() {
        if (selectNode != null) {
            Index indexData = (Index) selectNode.getData();
            if (indexData.getColumn1().equals("application/foder")
                    || indexData.getColumn1().equals("root")) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Đây là thư mục. Không xem được."));
                return;
            }
            if (indexData != null) {
                Document document = FileDAO.getDocumentByIdIndex(indexData.getIdindex());
                FileUtil fileUtil = new FileUtil();
                listGoodCommentFile = CommentFileDAO.getCommentFileByIndex(indexData.getIdindex(), CONFIG.STATE_GOOT_FILE);
                listNotGoodCommentFile = CommentFileDAO.getCommentFileByIndex(indexData.getIdindex(), CONFIG.STATE_NOT_GOOT_FILE);
                
                if (document != null) {
                    urlFile = fileUtil.getLinkFile(document.getSrc());

                    video = false;
                    mp3 = false;
                    editor = false;
                    pdf = false;
                    image = false;
                    if (document.getType().contains("image")) {
                        image = true;
                        return;
                    }
                    switch (document.getType()) {
                        case "video/mp4":
                            typeFile = "flash";
                            urlFile = nameApp + urlFile;
                            video = true;
                            break;
                        case "audio/mp3":
                            typeFile = "mp3";
                            mp3 = true;
                            break;
                        case "application/pdf":
                            typeFile = "pdf";
                            pdf = true;
                            break;
                        default:
                            editor = true;
                    }
                }
            }
        }
    }

    public void deleteFile() {
        if (selectNode != null) {
            Index s = (Index) selectNode.getData();
            if (!s.getColumn1().equals("application/foder") && !s.getColumn1().equals("root")) {
                boolean b = IndexDAO.deleteIndexById(s);
                if (b) {
                    resetTree();
                    FacesContext.getCurrentInstance()
                            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Xóa thành công."));
                } else {
                    FacesContext.getCurrentInstance()
                            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Xóa lỗi."));
                }
            } else if (s.getColumn1().equals("application/foder")) {
                boolean b = IndexDAO.getIndexByIdParent(s);
                if (!b) {
                    IndexDAO.deleteIndexById(s);
                    resetTree();
                    FacesContext.getCurrentInstance()
                            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Xóa thư mục thành công"));
                } else {
                    FacesContext.getCurrentInstance()
                            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Thư mục thư mục hoặc file con. Không xóa được"));
                }
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Bạn không được xóa thư mục gốc"));
            }
        }
    }

    public void downloadFile() {
        if (selectNode != null) {
            Index indexData = (Index) selectNode.getData();
            if (indexData.getColumn1().equals("application/foder")
                    || indexData.getColumn1().equals("root")) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Đây là thư mục. Không tải được."));
                return;
            }
            if (indexData != null) {
                Document document = FileDAO.getDocumentByIdIndex(indexData.getIdindex());
                FileUtil fileUtil = new FileUtil();
                if (document != null) {
                    urlFile = fileUtil.getLinkFile(document.getSrc());
                    urlFile = nameApp + urlFile;
                    System.out.println("urlFile: " + urlFile);
                    try {
                        FacesContext.getCurrentInstance().getExternalContext().redirect(urlFile);
                    } catch (IOException ex) {
                        Logger.getLogger(CourseDetailController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Chưa chọn."));
        }
    }

    public void shareFile() {

    }

    public void onDragDrop(TreeDragDropEvent event) {
        TreeNode dragNode = event.getDragNode();
        TreeNode dropNode = event.getDropNode();
        Index drag = (Index) dragNode.getData();//keo
        Index drop = (Index) dropNode.getData();//tha
        if (drag.getColumn1().equals("application/foder")
                || drag.getColumn1().equals("root")) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Đây là thư mục. Không thay đổi được."));
        } else if (!drop.getColumn1().equals("application/foder")
                && !drop.getColumn1().equals("root")) {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Vị trí phải nằm trong thư mục"));
        } else {
            boolean b = IndexDAO.changeIndex(drag, drop);
            if (b) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Thay đổi thành công."));
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Lỗi."));
            }
        }
        resetTree();
    }

    public List<Account> listAccountUserByAdminCreate() {
        List<Account> list = null;
        try {
            list = AccountDAO.getListAccountUserByAdminCreate(account, courseCurrent);
            System.out.println("controller " + list.size());
        } catch (SQLException ex) {
            Logger.getLogger(CourseDetailControllerAdmin.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public void demo() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("abc", new FacesMessage("Successful", "Your message: " + "adsd"));
    }

    public List<String> resetListIdAccountAttendedNotShared() {
        return new ArrayList<>();
    }

    public List<Account> resetListAccountByAdminCreate() {
        return new ArrayList<Account>();
    }

    public List<String> getListIdAccountByAdminCreate() {
        if (listIdAccountByAdminCreate == null) {
            listIdAccountByAdminCreate = new ArrayList<>();
        }
        return listIdAccountByAdminCreate;
    }

    public void setListIdAccountByAdminCreate(List<String> listIdAccountByAdminCreate) {
        this.listIdAccountByAdminCreate = listIdAccountByAdminCreate;
    }

    public List<String> getListIdAccountAttendedNotShared() {
        if (listIdAccountAttendedNotShared == null) {
            listIdAccountAttendedNotShared = new ArrayList<>();
        }
        return listIdAccountAttendedNotShared;
    }

    public void setListIdAccountAttendedNotShared(List<String> listIdAccountAttendedNotShared) {
        this.listIdAccountAttendedNotShared = listIdAccountAttendedNotShared;
    }

    public List<Account> getListAccountAttendedNotShared() {
        return listAccountAttendedNotShared;
    }

    public void setListAccountAttendedNotShared(List<Account> listAccountAttendedNotShared) {
        this.listAccountAttendedNotShared = listAccountAttendedNotShared;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public Course getCourseCurrent() {
        return courseCurrent;
    }

    public void setCourseCurrent(Course courseCurrent) {
        this.courseCurrent = courseCurrent;
    }

    public ShareCourse getShareCourse() {
        return shareCourse;
    }

    public void setShareCourse(ShareCourse shareCourse) {
        this.shareCourse = shareCourse;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getSelectNode() {
        return selectNode;
    }

    public void setSelectNode(TreeNode selectNode) {
        this.selectNode = selectNode;
    }

    public Part resetFile() {
        file = null;
        return file;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public boolean isDisplayChoseFile() {
        return displayChoseFile;
    }

    public void setDisplayChoseFile(boolean displayChoseFile) {
        this.displayChoseFile = displayChoseFile;
    }

    public Index resetIndex() {
        return new Index();
    }

    public Index getIndex() {
        if (index == null) {
            index = new Index();
        }
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public List<Account> getListAccountManageByAdmin() {
        return listAccountManageByAdmin;
    }

    public void setListAccountManageByAdmin(List<Account> listAccountManageByAdmin) {
        this.listAccountManageByAdmin = listAccountManageByAdmin;
    }

    public String getTypeFile() {
        return typeFile;
    }

    public void setTypeFile(String typeFile) {
        this.typeFile = typeFile;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public boolean isEditor() {
        return editor;
    }

    public void setEditor(boolean editor) {
        this.editor = editor;
    }

    public boolean isMp3() {
        return mp3;
    }

    public void setMp3(boolean mp3) {
        this.mp3 = mp3;
    }

    public boolean isPdf() {
        return pdf;
    }

    public void setPdf(boolean pdf) {
        this.pdf = pdf;
    }

    public boolean isImage() {
        return image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isMyComment() {
        return myComment;
    }

    public void setMyComment(boolean myComment) {
        this.myComment = myComment;
    }

    public List<CommentCourse> getListGoodCommentCourse() {
        return listGoodCommentCourse;
    }

    public void setListGoodCommentCourse(List<CommentCourse> listGoodCommentCourse) {
        this.listGoodCommentCourse = listGoodCommentCourse;
    }

    public List<CommentCourse> getListNotGoodCommentCourse() {
        return listNotGoodCommentCourse;
    }

    public void setListNotGoodCommentCourse(List<CommentCourse> listNotGoodCommentCourse) {
        this.listNotGoodCommentCourse = listNotGoodCommentCourse;
    }

    public CommentCourse getSelectedCommentCourse() {
        if (selectedCommentCourse == null) {
            selectedCommentCourse = new CommentCourse();
        }
        return selectedCommentCourse;
    }

    public void setSelectedCommentCourse(CommentCourse selectedCommentCourse) {
        this.selectedCommentCourse = selectedCommentCourse;
    }

    public String getSelectTypeComment() {
        return selectTypeComment;
    }

    public void setSelectTypeComment(String selectTypeComment) {
        this.selectTypeComment = selectTypeComment;
    }

    public String getContent_file() {
        return content_file;
    }

    public void setContent_file(String content_file) {
        this.content_file = content_file;
    }

    public List<CommentFile> getListGoodCommentFile() {
        return listGoodCommentFile;
    }

    public void setListGoodCommentFile(List<CommentFile> listGoodCommentFile) {
        this.listGoodCommentFile = listGoodCommentFile;
    }

    public List<CommentFile> getListNotGoodCommentFile() {
        return listNotGoodCommentFile;
    }

    public void setListNotGoodCommentFile(List<CommentFile> listNotGoodCommentFile) {
        this.listNotGoodCommentFile = listNotGoodCommentFile;
    }

    public String getSelectTypeCommentFile() {
        return selectTypeCommentFile;
    }

    public void setSelectTypeCommentFile(String selectTypeCommentFile) {
        this.selectTypeCommentFile = selectTypeCommentFile;
    }

    public CommentFile getSelectedCommentFile() {
        if(selectedCommentFile == null) selectedCommentFile = new CommentFile();
        return selectedCommentFile;
    }

    public void setSelectedCommentFile(CommentFile selectedCommentFile) {
        this.selectedCommentFile = selectedCommentFile;
    }

}
