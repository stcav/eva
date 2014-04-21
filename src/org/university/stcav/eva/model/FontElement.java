/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.university.stcav.eva.model;

/**
 *
 * @author stcav
 */
public class FontElement {
    String Text;
    String x;
    String y;
    String fontSize;
    String fontColor;
    String fontFile;
    

    public FontElement() {
    }

    public FontElement(String Text, String fontSize, String fontColor, String fontFile, String x, String y) {
        this.Text = Text;
        this.fontSize = fontSize;
        this.fontColor = fontColor;
        this.fontFile = fontFile;
        this.x = x;
        this.y = y;
    }

    public String getText() {
        return Text.replace(" ","¬");
    }

    public void setText(String Text) {
        this.Text = Text;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getFontFile() {
        return fontFile;
    }

    public void setFontFile(String fontFile) {
        this.fontFile = fontFile;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }
    


}
