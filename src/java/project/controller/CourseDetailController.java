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
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import project.DO.Account;
import project.DO.Course;
import project.DO.Document;
import project.DO.Index;
import project.DO.ShareCourse;
import project.config.CONFIG;
import project.dao.AccountDAO;
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
    private boolean video = false;
    private boolean mp3 = false;
    private boolean editor = false;
    private boolean pdf = false;
    private boolean image = false;
    private String nameApp = "/WebApplication2";
    private Index index;
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

            //cloned = shareCourseCurrent.getCloned().equals("yes") ? true : false;
            if (shareCourseCurrent.getCloned().equals(CONFIG.CONFIG_CLONED)) {
                resetTree(shareCourseCurrent);
                cloned = true;
            } else {
                ShareCourse shareCourse = ShareCourseDAO.
                        getShareCouseByIdAccountAndIdCourseAdmin(shareCourseCurrent.getIdAccountCreate(), courseId);
                cloned = false;
                resetTree(shareCourse);
            }

            Account account = new Account();
            account.setIdaccount(shareCourseCurrent.getIdAccountCreate());
            Course course = new Course();
            course.setIdcourse(shareCourseCurrent.getIdCourse());
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
                if (index != null && index.getName() != null && file != null) {
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
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Bạn không được xóa thư mục. Chỉ được sửa thư mục"));
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
}
