/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.university.stcav.eva.model;

/**
 *
 * @author johan
 */
public class VideoElement {

    private String codec;
    private String resolution;
    private String dar;
    private String bitRate;
    private String fps;

    public VideoElement() {
    }

    public VideoElement(String codec, String resolution, String dar, String bitRate, String fps) {
        this.codec = codec;
        this.resolution = resolution;
        this.dar = dar;
        this.bitRate = bitRate;
        this.fps = fps;
    }

    public String getBitRate() {
        return bitRate;
    }

    public void setBitRate(String bitRate) {
        this.bitRate = bitRate;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getDar() {
        return dar;
    }

    public void setDar(String dar) {
        this.dar = dar;
    }

    public String getFps() {
        return fps;
    }

    public void setFps(String fps) {
        this.fps = fps;
    }

    public float getNumberFPS(){
        return Float.parseFloat(fps.split(" ")[0]);
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public int get_width() {
        return Integer.parseInt(resolution.split("x")[0]);
    }

    public int get_height() {
        return Integer.parseInt(resolution.split("x")[1]);
    }
}
