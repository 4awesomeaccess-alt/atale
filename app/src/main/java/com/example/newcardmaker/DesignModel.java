package com.example.newcardmaker;

public class DesignModel {
    private String fileName;
    private String filePath;

    public DesignModel(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
}