/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.university.stcav.eva.model;

/**
 *
 * @author johan
 */
public class AudioElement {
    private String codec;
    private String samplingRate;// format: xxxxhz
    private String channel;// format: mono, stereo
    private String bitrate;// format: xxxxk

    public AudioElement() {
    }

    public AudioElement(String codec, String samplingRate, String channel, String bitrate) {
        this.codec = codec;
        this.samplingRate = samplingRate;
        this.channel = channel;
        this.bitrate = bitrate;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(String samplingRate) {
        this.samplingRate = samplingRate;
    }

    
}
