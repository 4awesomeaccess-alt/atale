package com.example.newcardmaker;

public class DesignModel {
    private String fileName;
    private String filePath;
    private String imagePath; // thumbnail image path

    public DesignModel(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.imagePath = "";
    }

    public DesignModel(String fileName, String filePath, String imagePath) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.imagePath = imagePath;
    }

    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public String getImagePath() { return imagePath != null ? imagePath : ""; }

    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
