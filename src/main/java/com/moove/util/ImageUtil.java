package com.moove.util;

import java.io.File;
import java.io.IOException;

import jakarta.servlet.http.Part;

public class ImageUtil {
    /**
     * Extracts the file name from the given {@link Part} object based on the
     * "content-disposition" header.
     */
    public String getImageNameFromPart(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        String imageName = null;

        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                imageName = s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }

        if (imageName == null || imageName.isEmpty()) {
            imageName = "default.jpg";
        }

        return imageName;
    }

    /**
     * Uploads the image file to the specified directory on the server.
     */
    public boolean uploadImage(Part part, String rootPath, String saveFolder) {
        String savePath = getSavePath(rootPath, saveFolder);
        File fileSaveDir = new File(savePath);

        if (!fileSaveDir.exists()) {
            if (!fileSaveDir.mkdirs()) { // Use mkdirs() to create parent directories if needed
                System.out.println("Failed to create directory: " + savePath);
                return false;
            }
        }
        try {
            String imageName = getImageNameFromPart(part);
            String filePath = savePath + File.separator + imageName;
            System.out.println("Saving image to: " + filePath);
            part.write(filePath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Constructs the save path using the root path and save folder.
     */
    public String getSavePath(String rootPath, String saveFolder) {
        // Ensure rootPath ends with a separator
        if (!rootPath.endsWith(File.separator)) {
            rootPath += File.separator;
        }
        // Construct the path relative to the web app root
        String relativePath = saveFolder;
        return rootPath + relativePath;
    }
}