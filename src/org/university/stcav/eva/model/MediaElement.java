/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.university.stcav.eva.model;

/**
 *
 * @author johan
 */
public class MediaElement {
    private boolean  isMEValidate;
    private AudioElement audioElement;
    private VideoElement videoElement;
    private String name;
    private String duration;
    private boolean AvaliableAudio;
    private boolean AvaliabeVideo;

    public MediaElement() {
        audioElement = new AudioElement();
        videoElement = new VideoElement();
        AvaliabeVideo = false;
        AvaliableAudio = false;
        isMEValidate = false;
    }

    public MediaElement(AudioElement audioElement, VideoElement videoElement, String name) {
        this.audioElement = audioElement;
        this.videoElement = videoElement;
        this.name = name;
    }

    public MediaElement(AudioElement audioElement, VideoElement videoElement) {
        this.audioElement = audioElement;
        this.videoElement = videoElement;
    }

    public boolean isIsMEValidate() {
        return isMEValidate;
    }

    public void setIsMEValidate(boolean isMEValidate) {
        this.isMEValidate = isMEValidate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvaliabeVideo() {
        return AvaliabeVideo;
    }

    public void setAvaliabeVideo(boolean AvaliabeVideo) {
        this.AvaliabeVideo = AvaliabeVideo;
    }

    public boolean isAvaliableAudio() {
        return AvaliableAudio;
    }

    public void setAvaliableAudio(boolean AvaliableAudio) {
        this.AvaliableAudio = AvaliableAudio;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public AudioElement getAudioElement() {
        return audioElement;
    }

    public void setAudioElement(AudioElement audioElement) {
        this.audioElement = audioElement;
    }

    public VideoElement getVideoElement() {
        return videoElement;
    }

    public void setVideoElement(VideoElement videoElement) {
        this.videoElement = videoElement;
    }
    
}
