package com.capstone.texttospeech.Model;

public class CategoriesModel {
    String Category,ImageLink;
    public CategoriesModel(){}
    public CategoriesModel(String Category,String ImageLink){
        this.Category = Category;
        this.ImageLink = ImageLink;

    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
