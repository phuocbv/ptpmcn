/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import project.DO.Account;
import project.DO.Document;
import project.DO.ShareFile;
import project.config.CONFIG;
import project.dao.AccountDAO;
import project.dao.DocumentDAO;
import project.dao.ShareCourseDAO;
import project.dao.ShareFileDAO;
import project.util.FileUtil;

/**
 *
 * @author DA CUOI
 */
@ManagedBean(name = "sharedDocumentController")
@ViewScoped
public class SharedDocumentController {

    private Account account;
    private Document selectedDocument;
    private List<Document> listDocument;
    private List<Account> listAccount;
    private List<ShareFile> listShareFile;
    private String[] accountSelected;
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
    private ShareFile selectedShareFile;
    FileUtil fileUtil = new FileUtil();

    public SharedDocumentController() {
        FacesContext context = FacesContext.getCurrentInstance();
        account = (Account) context.getExternalContext()
                .getSessionMap().get(CONFIG.SESSION_NAME_OF_ADMIN);
        try {
            listDocument = DocumentDAO.getListDocumentSharedByDiffirentAccount(account);
            listShareFile = ShareFileDAO.getListShareFileByIdAccountUse(account);
        } catch (SQLException ex) {
            Logger.getLogger(SharedDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Account> listAccountNotShareFile() {
        List<Account> list = null;
        if (selectedShareFile != null) {
            //list = AccountDAO.getListAccountNotShareFile(account, selectedDocument);
            list = AccountDAO.getAllAccount(account);
        }
        return list;
    }

    public Account getAccountById(int idAccount) {
        ac = AccountDAO.getAccountById(idAccount);
        return ac;
    }

    public Document getDocumentById(int idDocument) {
        document = DocumentDAO.getDocumentById(idDocument);
        return document;
    }

    public void shareFile() {
        if (selectedShareFile != null && accountSelected != null && accountSelected.length > 0) {
            //ShareFile shareFile = ShareFileDAO.getShareFileByAccountUseAndIdFile(account, selectedDocument);
            boolean b = ShareFileDAO.shareFileToDiffirentAccount(accountSelected, selectedShareFile,
                    account.getIdaccount(), selectedShareFile.getIdFile());
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
        if (selectedShareFile != null) {
            Document document = getDocumentById(selectedShareFile.getIdFile());
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

    public void deleteFile() {
        if (selectedShareFile != null) {
            boolean b = ShareFileDAO.deleteShareFileById(selectedShareFile);
            if (b) {
                listShareFile.remove(selectedShareFile);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Xóa thành công."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Xóa lỗi."));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Chưa chọn tài liệu"));
        }
    }

    public void downloadFile() {
        if (selectedShareFile != null) {
            Document document = getDocumentById(selectedShareFile.getIdFile());
            if (document != null) {
                urlFile = fileUtil.getLinkFile(document.getSrc());
                urlFile = nameApp + urlFile;
                System.out.println("urlFile: " + urlFile);
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect(urlFile);
                } catch (IOException ex) {
                }
            }
        } else {
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Chưa chọn."));
        }
    }

    public List<Document> getListDocument() {
        return listDocument;
    }

    public void setListDocument(List<Document> listDocument) {
        this.listDocument = listDocument;
    }

    public Document getSelectedDocument() {
        //if(selectedDocument == null) selectedDocument = new Document();
        return selectedDocument;
    }

    public void setSelectedDocument(Document selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public List<Account> getListAccount() {
        return listAccount;
    }

    public void setListAccount(List<Account> listAccount) {
        this.listAccount = listAccount;
    }

    public String[] getAccountSelected() {
        return accountSelected;
    }

    public void setAccountSelected(String[] accountSelected) {
        this.accountSelected = accountSelected;
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

    public List<ShareFile> getListShareFile() {
        return listShareFile;
    }

    public void setListShareFile(List<ShareFile> listShareFile) {
        this.listShareFile = listShareFile;
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

    public ShareFile getSelectedShareFile() {
        return selectedShareFile;
    }

    public void setSelectedShareFile(ShareFile selectedShareFile) {
        this.selectedShareFile = selectedShareFile;
    }

}
