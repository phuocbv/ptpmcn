/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.controller;

import java.io.IOException;
import java.net.URL;
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
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import project.DO.Account;
import project.DO.CommentCourse;
import project.DO.Course;
import project.DO.Document;
import project.DO.Index;
import project.DO.ShareCourse;
import project.config.CONFIG;
import project.dao.AccountDAO;
import project.dao.CommentCourseDAO;
import project.dao.CourseDAO;
import project.dao.FileDAO;
import project.dao.IndexDAO;
import project.dao.ShareCourseDAO;
import project.util.FileUtil;
import project.util.Tree;

/**
 *
 * @author DA CUOI
 */
@ManagedBean(name = "courseDetailController")
@ViewScoped
public class CourseDetailController {

    public int courseId;
    private Account account;
    private boolean cloned;
    private TreeNode root;
    private Tree treeUtil;
    private TreeNode selectedNode;
    private Part file;
    private String urlFile;
    private String typeFile = "pdf";
    private String uploadFileName;
    private List<Account> listMemberNotShareCourse;
    private List<String> listIdMemberNotShareCouse;
    private ShareCourse shareCourseCurrent;
    private List<Index> listItem;
    private List<CommentCourse> listGoodCommentCourse;
    private List<CommentCourse> listNotGoodCommentCourse;
    private boolean video = false;
    private boolean mp3 = false;
    private boolean editor = false;
    private boolean pdf = false;
    private boolean image = false;
    private String nameApp = "/WebApplication2";
    private String selectTypeComment = "1";
    private String STATE_CURRENT = "";
    private Index index;
    private String content;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    public CourseDetailController() {
        FacesContext context = FacesContext.getCurrentInstance();
        account = (Account) context.getExternalContext().getSessionMap().get(CONFIG.SESSION_NAME_OF_USER);

        treeUtil = new Tree();

    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
        if (courseId > 0) {
            shareCourseCurrent = ShareCourseDAO.getShareCourseByIdAccountAndIdCourse(account.getIdaccount(), courseId);
            if (shareCourseCurrent.getCloned().equals(CONFIG.CONFIG_CLONED)) {
                resetTree(shareCourseCurrent);
                cloned = true;
            } else {
                ShareCourse shareCourse = ShareCourseDAO.
                        getShareCouseByIdAccountAndIdCourseAdmin(shareCourseCurrent.getIdAccountCreate(), courseId);
                cloned = false;
                resetTree(shareCourse);
            }
            listGoodCommentCourse = CommentCourseDAO
                    .getCommentCourseByAccountAndShareCourse(shareCourseCurrent, CONFIG.STATE_GOOT);
            listNotGoodCommentCourse = CommentCourseDAO
                    .getCommentCourseByAccountAndShareCourse(shareCourseCurrent, CONFIG.STATE_NOT_GOOT);
            Account account = new Account();
            account.setIdaccount(shareCourseCurrent.getIdAccountCreate());
            Course course = new Course();
            course.setIdcourse(shareCourseCurrent.getIdCourse());

        }
    }

    public Index indexFromSelectElement() {
        Index index = null;
        if (selectedNode != null) {
            index = (Index) selectedNode.getData();
        }
        return index;
    }

    public String returnAccountById(int idAccount) {
        if (idAccount == account.getIdaccount()) {
            return "Tôi";
        }
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
            cc.setIdShareCreate(Integer.parseInt(shareCourseCurrent.getColumn1()));
            cc.setIdAccount(account.getIdaccount());
            cc.setCreateDate(dt.format(new Date()));
            cc.setColumn1(STATE_CURRENT);
            int result = CommentCourseDAO.addCommentCourse(cc);
            if (result > 0) {
                content = "";
                listGoodCommentCourse = CommentCourseDAO
                        .getCommentCourseByAccountAndShareCourse(shareCourseCurrent, CONFIG.STATE_GOOT);
                listNotGoodCommentCourse = CommentCourseDAO
                        .getCommentCourseByAccountAndShareCourse(shareCourseCurrent, CONFIG.STATE_NOT_GOOT);
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Bình luận thành công."));
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
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Đât là thư mục gốc.Không thể đổi được."));
                return;
            }
            if (!index.getColumn1().equals("application/foder")) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Đât không phải thư mục.Không thể đổi được."));
                return;
            }
            boolean b = IndexDAO.updateIndex(index);
            if (b) {
                resetTree(shareCourseCurrent);
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

    public void resetTree(ShareCourse shareCourse) {
        listItem = IndexDAO.getIndexByIdShareCourse(shareCourse);
        if (listItem != null && listItem.size() > 0) {
            treeUtil.setListIndex(listItem);
            root = treeUtil.createTreeNodeIndex();
        }
    }

    public void cloneCourse() {
        if (shareCourseCurrent.getCloned().equals(CONFIG.CONFIG_NOT_CLONE) && listItem != null) {
            //shareCourseCurrent listItem
            boolean b = IndexDAO.cloneCourse(listItem, shareCourseCurrent);//tao bản sao khóa học
            boolean result = false;
            if (b) {
                result = ShareCourseDAO.updateStatusClone(shareCourseCurrent);
            }
            if (result) {
                cloned = true;
                resetTree(shareCourseCurrent);
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Clone khóa học thành công."));
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Clone không thành công."));
            }
        }
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
        resetTree(shareCourseCurrent);
    }

    public String deleteCourse() {
        if (cloned && shareCourseCurrent != null) {
            boolean b1 = IndexDAO.deleteIndexByIdShareCourse(shareCourseCurrent);
            boolean b2 = ShareCourseDAO.deleteShareCourseById(shareCourseCurrent);
            boolean b3 = false;
            if (b1 && b2) {
                b3 = CourseDAO.deleteCourseById(courseId);
            }
            if (b3) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Xóa thành công."));
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Lỗi."));
            }
        }
        return "/user/home.xhtml?faces-redirect=true";
    }

    public void addIndex() {
        if (index != null && selectedNode != null) {
            Index s = (Index) selectedNode.getData();
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
            Index select = (Index) selectedNode.getData();
            index.setTitle(index.getName());
            index.setContent("content");
            index.setCreateDate(format.format(new Date()));
            index.setIdShareCourse(shareCourseCurrent.getIdshareCourse());
            index.setIdParent(select.getIdindex());
            index.setLevel(select.getLevel() + 1);
            index.setColumn1("application/foder");
            index.setColumn2("0");
            int idIndex = IndexDAO.addIndex(index);
            resetTree(shareCourseCurrent);
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

    public void viewFile() {
        if (selectedNode != null) {
            Index indexData = (Index) selectedNode.getData();
            if (indexData.getColumn1().equals("application/foder")
                    || indexData.getColumn1().equals("root")) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Đây là thư mục. Không xem được."));
                return;
            }
            if (indexData != null) {//&& indexData.getColumn1().equals("application/pdf")
                Document document = null;
                if (cloned) {
                    if (indexData.getColumn2().equals("0")) {
                        document = FileDAO.getDocumentByIdIndex(indexData.getIdindex());
                    } else {
                        document = FileDAO.getDocumentByIdIndex(Integer.parseInt(indexData.getColumn2()));
                    }
                } else {
                    document = FileDAO.getDocumentByIdIndex(indexData.getIdindex());
                }
                FileUtil fileUtil = new FileUtil();
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

    public String addFile() throws IOException {
        if (file != null && selectedNode != null) {
            String fileType = file.getContentType();
            FileUtil fileUtil = new FileUtil();
            try {
                uploadFileName = fileUtil.uploadFile(file);
            } catch (IOException ex) {
            }
            if (uploadFileName != null) {
                if (index != null && file != null) {
                    Index select = (Index) selectedNode.getData();
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
                    index.setIdShareCourse(shareCourseCurrent.getIdshareCourse());
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
            resetTree(shareCourseCurrent);
        }
        return "/user/detailCourse.xhtml";
    }

    public void deleteFile() {
        if (selectedNode != null) {
            Index s = (Index) selectedNode.getData();
            if (!s.getColumn1().equals("application/foder") && !s.getColumn1().equals("root")) {
                boolean b = IndexDAO.deleteIndexById(s);
                if (b) {
                    resetTree(shareCourseCurrent);
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
                    resetTree(shareCourseCurrent);
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
        if (selectedNode != null) {
            Index indexData = (Index) selectedNode.getData();
            if (indexData.getColumn1().equals("application/foder")
                    || indexData.getColumn1().equals("root")) {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Đây là thư mục. Không tải được."));
                return;
            }
            if (indexData != null) {
                Document document = null;
                if (cloned) {
                    if (indexData.getColumn2().equals("0")) {
                        document = FileDAO.getDocumentByIdIndex(indexData.getIdindex());
                    } else {
                        document = FileDAO.getDocumentByIdIndex(Integer.parseInt(indexData.getColumn2()));
                    }
                } else {
                    document = FileDAO.getDocumentByIdIndex(indexData.getIdindex());
                }
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

    public List<Account> getListMemberNotShareCourse() {
        if (listMemberNotShareCourse == null) {
            listMemberNotShareCourse = new ArrayList<>();
        }
        return listMemberNotShareCourse;
    }

    public void setListMemberNotShareCourse(List<Account> listMemberNotShareCourse) {
        this.listMemberNotShareCourse = listMemberNotShareCourse;
    }

    public List<String> getListIdMemberNotShareCouse() {
        if (listIdMemberNotShareCouse == null) {
            listIdMemberNotShareCouse = new ArrayList<>();
        }
        return listIdMemberNotShareCouse;
    }

    public void setListIdMemberNotShareCouse(List<String> listIdMemberNotShareCouse) {
        this.listIdMemberNotShareCouse = listIdMemberNotShareCouse;
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

    public boolean isCloned() {
        return cloned;
    }

    public void setCloned(boolean cloned) {
        this.cloned = cloned;
    }

    public TreeNode getSelectedNode() {
        if (selectedNode == null) {
            selectedNode = new DefaultTreeNode();
        }
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
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

    public boolean isMp3() {
        return mp3;
    }

    public void setMp3(boolean mp3) {
        this.mp3 = mp3;
    }

    public boolean isEditor() {
        return editor;
    }

    public void setEditor(boolean editor) {
        this.editor = editor;
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

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSelectTypeComment() {
        return selectTypeComment;
    }

    public void setSelectTypeComment(String selectTypeComment) {
        this.selectTypeComment = selectTypeComment;
    }
}
