package com.capstone.texttospeech.Model;

public class AudioModel {
    String Name,FilePath,FileName,FileLink,ImageLink;

    public AudioModel(){}

    public AudioModel(String Name, String FilePath, String FileName, String FileLink,String ImageLink){
        this.Name = Name;
        this.FilePath = FilePath;
        this.FileName = FileName;
        this.FileLink = FileLink;
        this.ImageLink = ImageLink;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileLink() {
        return FileLink;
    }

    public void setFileLink(String fileLink) {
        FileLink = fileLink;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }
}
