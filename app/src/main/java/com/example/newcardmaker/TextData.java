package com.example.newcardmaker;

public class TextData {
    String text;
    float xPercent; // X coordinate in %
    float yPercent; // Y coordinate in %
    int color;
    float size;
    int bgColor;
    String alignment;

    public TextData(String text, float xP, float yP, int color, float size, int bgColor, String align) {
        this.text = text;
        this.xPercent = xP;
        this.yPercent = yP;
        this.color = color;
        this.size = size;
        this.bgColor = bgColor;
        this.alignment = align;
    }
}