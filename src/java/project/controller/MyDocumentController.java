/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import project.DO.Account;
import project.DO.Document;
import project.DO.Index;
import project.DO.ShareFile;
import project.config.CONFIG;
import project.controllerAdmin.CourseDetailControllerAdmin;
import project.dao.AccountDAO;
import project.dao.DocumentDAO;
import project.dao.FileDAO;
import project.dao.ShareFileDAO;
import project.util.FileUtil;

/**
 *
 * @author DA CUOI
 */
@ManagedBean(name = "myDocumentController")
@ViewScoped
public class MyDocumentController {

    private List<Document> listDocument;
    private List<Account> listAllAccount;
    private Account account;
    private String[] accountSelected;
    private Document selectedDocument;
    private Part file;
    private String url_document = "/WebApplication2/user/my_document_1.xhtml";
    private String urlFile = "";
    private String typeFile = "pdf";
    private String nameApp = "/WebApplication2";
    private Document document;
    private Account ac;
    private boolean video = false;
    private boolean editor = false;
    private boolean mp3 = false;
    private boolean pdf = false;
    private boolean image = false;

    //private ShareFile selectedShareFile;
    FileUtil fileUtil = new FileUtil();
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    public MyDocumentController() {
        FacesContext context = FacesContext.getCurrentInstance();
        account = (Account) context.getExternalContext().getSessionMap().get(CONFIG.SESSION_NAME_OF_USER);

        try {
            listDocument = DocumentDAO.getListDocumentOfUser(account);
        } catch (SQLException ex) {
            Logger.getLogger(MyDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadFile() {
        if (file != null) {
            FileUtil fileUtil = new FileUtil();
            String uploadFileName = "";
            try {
                uploadFileName = fileUtil.uploadFile(file);
            } catch (IOException ex) {
                Logger.getLogger(CourseDetailControllerAdmin.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!uploadFileName.equals("")) {
                Document d = new Document();
                d.setSrc(uploadFileName.trim());
                d.setSize(String.valueOf(file.getSize()));
                d.setTitleFile(uploadFileName.trim());
                d.setType(file.getContentType());
                d.setCreateDate(format.format(new Date()));
                d.setIdIndex(0);
                int id = FileDAO.addFile(d);
                if (id > 0) {
                    ShareFile sf = new ShareFile();
                    sf.setIdAccountUse(account.getIdaccount());
                    sf.setIdAccountShare(account.getIdaccount());
                    sf.setIdAccountCreate(account.getIdaccount());
                    sf.setIdFile(id);
                    ShareFileDAO.addShareFile(sf);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Thêm file thành công."));
                    try {
                        FacesContext.getCurrentInstance().getExternalContext().redirect(url_document);
                    } catch (IOException ex) {
                        Logger.getLogger(MyDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Chưa chọn file."));
        }
    }

    public void shareFile() {
        if (selectedDocument != null && accountSelected != null && accountSelected.length > 0) {
            ShareFile shareFile = ShareFileDAO.getShareFileByAccountUseAndIdFile(account, selectedDocument);
            boolean b = ShareFileDAO.shareFileToDiffirentAccount(accountSelected, shareFile,
                    account.getIdaccount(), selectedDocument.getIdfile());
            if (b) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Chia sẻ thành công."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Có lỗi."));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Lỗi."));
        }
    }

    public void viewFile() {
        if (selectedDocument != null) {
            urlFile = fileUtil.getLinkFile(selectedDocument.getSrc());
            video = false;
            mp3 = false;
            editor = false;
            pdf = false;
            image = false;
            if (selectedDocument.getType().contains("image")) {
                image = true;
                return;
            }
            switch (selectedDocument.getType()) {
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

    public void deleteDocument() {
        if (selectedDocument != null) {
            boolean b = ShareFileDAO.deleteShareFile(selectedDocument, account);
            if (b) {
                try {
                    listDocument = DocumentDAO.getListDocumentOfUser(account);
                } catch (SQLException ex) {
                    Logger.getLogger(MyDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Xóa thành công."));
            } else {
                FacesContext.getCurrentInstance()
                        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Xóa lỗi."));
            }
        } else {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Chưa chọn tài liệu."));
        }
    }

    public void downloadFile() {
        if (selectedDocument != null) {
            urlFile = fileUtil.getLinkFile(selectedDocument.getSrc());
            urlFile = nameApp + urlFile;
            System.out.println("urlFile: " + urlFile);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(urlFile);
            } catch (IOException ex) {

            }
        } else {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Chưa chọn."));
        }
    }

    public List<Account> listAccountNotShareFile() {
        List<Account> list = null;
        if (selectedDocument != null) {
            list = AccountDAO.getAllAccount(account);
        }
        return list;
    }

    public String[] getAccountSelected() {
        return accountSelected;
    }

    public void setAccountSelected(String[] accountSelected) {
        this.accountSelected = accountSelected;
    }

    public List<Account> getListAllAccount() {
        return listAllAccount;
    }

    public void setListAllAccount(List<Account> listAllAccount) {
        this.listAllAccount = listAllAccount;
    }

    public List<Document> getListDocument() {
        return listDocument;
    }

    public void setListDocument(List<Document> listDocument) {
        this.listDocument = listDocument;
    }

    public Document getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument(Document selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
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

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Account getAc() {
        return ac;
    }

    public void setAc(Account ac) {
        this.ac = ac;
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

}
