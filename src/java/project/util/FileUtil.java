/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author DA CUOI
 */
public class FileUtil {

    public static final int BUFFER_SIZE = 1024;
    FacesContext context = FacesContext.getCurrentInstance();
    ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
    String PART = servletContext.getRealPath("");    
    String LINK = File.separator + "file" + File.separator;
    
    public String uploadFile(Part file) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String result = "";
        if (file.getSize() > 0) {
            try {   
                String fileName = Util.getFileNameFromPart(file);
                File outputFile = new File(PART + LINK + fileName);
                inputStream = file.getInputStream();
                outputStream = new FileOutputStream(outputFile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                result = fileName;
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        return result;
    }
    
    public void downloadFile(){
        
    }
    
    public String getLinkFile(String fileName){
        if(!fileName.equals("")){
            return "/file/" + fileName;
        }
        return "";
    }
    
    public String getFullLinkFile(String fileName){
        if(!fileName.equals("")){
            return PART + LINK + fileName;
        }
        return "";
    }
}
