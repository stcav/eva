/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.university.stcav.eva.processor;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.university.stcav.eva.model.AudioElement;
import org.university.stcav.eva.model.FontElement;
import org.university.stcav.eva.model.MediaElement;
import org.university.stcav.eva.model.ProcessorResponse;
import org.university.stcav.eva.model.VideoElement;

/**
 *
 * @author johan
 */
public class Processor {

    private static int HDWIDTH = 1280;
    private static int HDHEIGHT = 720;
    private static String HDDAR = "16:9";

    public static MediaElement get_mediaElement(String name, String path, boolean log) throws InterruptedException, IOException {
        String comando = "ffmpeg -i " + name;
        ProcessorResponse pr = ProcessExecutor.execute_process(comando.split(" "), path, log);

        AudioElement ae;
        VideoElement ve;
        MediaElement me = new MediaElement();

        String line;

        Iterator iterStdOut = pr.getStdout().iterator();
        Iterator iterStdError = pr.getStderror().iterator();

        while (iterStdError.hasNext()) {
            line = (String) iterStdError.next();
            line = line.trim();
            if (line.startsWith("Input #")) {
                String n = line.split("'")[1];
                //System.out.println("Name: " + n);
                me.setName(n);
            }
            if (line.startsWith("Duration:")) {
                String dur = line.split(": ")[1].split(",")[0];
                //System.out.println("Duration " + dur);
                me.setDuration(dur);
            }
            if (line.startsWith("Stream #")) {
                if (line.split(":")[2].endsWith("Video")) {

                    String vdar = "";
                    String vc = "";
                    String vs = "";
                    String vr = "";
                    String vfps = "";
                    //System.out.println("//**************** Video *****************//");
                    try {
                        vc = line.split(":")[3].split(",")[0].trim();
                        //System.out.println("Codec: " + vc);
                    } catch (Exception e) {
                        System.out.println("No vcodec");
                    }
                    try {
                        vs = line.split(":")[3].split(",")[2].replace('[', ',').split(",")[0].trim();
                        //System.out.println("resolution: " + vs);
                    } catch (Exception e) {
                        System.out.println("No vsize");
                    }

                    try {
                        vdar = line.split(",")[3].replace('[', ',').split(",")[1].split("DAR")[1].replace(']', ',').split(",")[0].trim();
                        //System.out.println("DAR: " + vdar);
                    } catch (Exception e) {
                        System.out.println("No Aspect Ratio");
                    }
                    try {
                        vr = line.split(",")[3].trim();
                        //System.out.println("Bit Rate: " + vr);
                    } catch (Exception e) {
                        System.out.println("No bitrate");
                    }
                    try {
                        vfps = line.split(",")[4].trim();
                        //System.out.println("FPS: " + vfps);
                    } catch (Exception e) {
                        System.out.println("No FPS");
                    }


                    ve = new VideoElement(vc, vs, vdar, vr, vfps);
                    me.setVideoElement(ve);
                    me.setAvaliabeVideo(true);
                    me.setIsMEValidate(true);
                    System.gc();
                }
                if (line.split(":")[2].endsWith("Audio")) {
                    //System.out.println("//**************** Audio *****************//");
                    String ac = "";
                    String asr = "";
                    String ach = "";
                    String ar = "";
                    try {
                        ac = line.split(":")[3].split(",")[0].trim();
                        //System.out.println("Codec: " + ac);
                    } catch (Exception e) {
                        System.out.println("No acodec");
                    }
                    try {
                        asr = line.split(",")[1].trim();
                        //System.out.println("Sampling Rate: " + asr);
                    } catch (Exception e) {
                        System.out.println("No sampling rate");
                    }
                    try {
                        ach = line.split(",")[2].trim();
                        //System.out.println("Channels: " + ach);
                    } catch (Exception e) {
                        System.out.println("No audio channels");
                    }
                    try {
                        ar = line.split(",")[4].trim();
                        //System.out.println("Bit Rate: " + ar);
                    } catch (Exception e) {
                        System.out.println("No bitrate");
                    }


                    ae = new AudioElement(ac, asr, ach, ar);
                    me.setAudioElement(ae);
                    me.setAvaliableAudio(true);
                    me.setIsMEValidate(true);
                    System.gc();
                }
            }


        }
        return me;
    }

    public static String resize_video_hd(MediaElement me, MediaElement meConfig, String path, boolean excuteProcess, boolean log) throws InterruptedException, IOException {
        String command;
        //obtengo los parametros resize del me-> width:height:pad:typepad
        String resizeParameter = get_resize_parameter(me.getVideoElement().get_width(), me.getVideoElement().get_height(), HDWIDTH, HDHEIGHT);
        //Formateo el resize a widthxheight
        String size = resizeParameter.split(":")[0] + "x" + resizeParameter.split(":")[1];
        //establezco el filterpad, si lo hay.
        String filterPad = "";
        if (Integer.parseInt(resizeParameter.split(":")[2]) > 0) {
            if (resizeParameter.split(":")[3].equals("2")) {
                filterPad = "-vf pad=" + HDWIDTH + ":" + HDHEIGHT + ":" + resizeParameter.split(":")[2] + ":0:black";
            }
        }
        command = "ffmpeg -y -i " + me.getName() + " -s " + size + " " + filterPad + " -acodec " + meConfig.getAudioElement().getCodec() + " -ar 44100 -ab " + meConfig.getAudioElement().getBitrate() + " -r " + meConfig.getVideoElement().getFps() + " -force_fps -aspect " + HDDAR + " -vcodec " + meConfig.getVideoElement().getCodec() + " -b " + meConfig.getVideoElement().getBitRate() + " -maxrate " + meConfig.getVideoElement().getBitRate() + " -minrate " + meConfig.getVideoElement().getBitRate() + " -preset veryfast " + meConfig.getName();
        System.out.println(command);
        if (excuteProcess) {
            ProcessExecutor.execute_process(command.split(" "), path, log);
            return command;
        }
        return command;

    }

    public static String transcode_video_PALMPEG2(String origin, String output, String path, boolean excuteProcess, boolean log) throws InterruptedException, IOException {
        //String command = "ffmpeg -async 25 -i " + origin + " -an -s 720x576 -deinterlace -r 25 -force_fps -aspect 4:3 -f yuv4mpegpipe - | yuvdenoise | ffmpeg -i - -an -f mpeg2video -vcodec mpeg2video -b 2600000 -maxrate 2600000 -minrate 2600000 -bf 2 -bufsize 1835008 -y " + output;
        String command = "ffmpeg -async 25 -i " + origin + " -an -s 720x576 -deinterlace -r 25 -force_fps -aspect 4:3 -f mpeg2video -vcodec mpeg2video -b 2600000 -maxrate 2600000 -minrate 2600000 -bf 2 -bufsize 1835008 -y " + output;
        System.out.println(command);
        if (excuteProcess) {
            ProcessExecutor.execute_process(command.split(" "), path, log);
            return command;
        }
        return command;
    }

    public static String transcode_audio_MPEG2(String origin, String output, String path, boolean excuteProcess, boolean log) throws InterruptedException, IOException {
        String command = "ffmpeg -async 25 -vn -i " + origin + " -acodec mp2 -ac 2 -ab 128000 -ar 48000 -f mp2 -y " + output;
        System.out.println(command);
        if (excuteProcess) {
            ProcessExecutor.execute_process(command.split(" "), path, log);
            return command;
        }
        return command;
    }

    private static String get_resize_parameter(int owidth, int oheight, int ewidth, int eheight) {
        //pwidth: Ancho video original
        //pheight: Alto video original
        //ewidth: Ancho estimado dell video
        //eheight: Alto estimado del video

        int cwidth;// Ancho calculado
        int cheight;// Alto calculado
        float factor;//factor de redimensionamiento
        int pad = 0;
        int typePad;//1-> hor 2-> ver
        if (owidth > oheight) {
            if (ewidth > eheight) {
                factor = (float) eheight / (float) oheight;
                cheight = eheight;
                cwidth = Math.round(factor * owidth);
                pad = Math.round((ewidth - (factor * owidth)) / 2);
                typePad = 2;
                return cwidth + ":" + cheight + ":" + pad + ":" + typePad;
            }
        }
        return null;
    }

    public static String cut_video_hd(int home_cut, int end_cut, MediaElement me, MediaElement meConfig, String path, boolean excuteProcess, boolean log) throws InterruptedException, IOException {
        //String command = "ffmpeg -y -sameq -ss " + do_SecondsToTime(home_cut) + " -t " + do_SecondsToTime(end_cut) + " -i " + me.getName() + " -acodec " + meConfig.getAudioElement().getCodec() + " -ab " + meConfig.getAudioElement().getBitrate() + " -r " + meConfig.getVideoElement().getFps() + " -force_fps -aspect " + HDDAR + " -vcodec " + meConfig.getVideoElement().getCodec() + " -b " + meConfig.getVideoElement().getBitRate() + " -maxrate " + meConfig.getVideoElement().getBitRate() + " -minrate " + meConfig.getVideoElement().getBitRate() + " -preset veryfast " + meConfig.getName();
        String command = "ffmpeg -y -i " + me.getName() + " -ss " + do_SecondsToTime(home_cut) + " -t " + do_SecondsToTime(end_cut - home_cut) + " -acodec " + meConfig.getAudioElement().getCodec() + " -ab " + meConfig.getAudioElement().getBitrate() + " -r " + meConfig.getVideoElement().getFps() + " -force_fps -aspect " + HDDAR + " -vcodec " + meConfig.getVideoElement().getCodec() + " -b " + meConfig.getVideoElement().getBitRate() + " -maxrate " + meConfig.getVideoElement().getBitRate() + " -minrate " + meConfig.getVideoElement().getBitRate() + " -preset veryfast " + meConfig.getName();
        System.out.println(command);
        if (excuteProcess) {
            ProcessExecutor.execute_process(command.split(" "), path, log);
            return command;
        }
        return command;
    }

    public static String do_SecondsToTime(int seconds) {
        int hours = seconds / 3600;
        int min = (seconds - (hours * 3600)) / 60;
        int sec = seconds - (hours * 3600) - (min * 60);
        return hours + ":" + min + ":" + sec;
    }

    public static int do_TimeToSeconds(String time) {
        return Math.round(Float.parseFloat(time.split(":")[0]) * 3600 + Float.parseFloat(time.split(":")[1]) * 60 + Float.parseFloat(time.split(":")[2]));
    }

    public static String create_video_from_image(String srcImage, int time, VideoElement ve, String output, String path, boolean excuteProcess, boolean log) throws InterruptedException, IOException {
        String command = "ffmpeg -loop_input -shortest -y -i " + srcImage + " -t " + time + " -r " + ve.getFps() + " -force_fps -vcodec " + ve.getCodec() + " -b " + ve.getBitRate() + " -maxrate " + ve.getBitRate() + " -minrate " + ve.getBitRate() + " -preset veryfast " + output;
        System.out.println(command);
        if (excuteProcess) {
            ProcessExecutor.execute_process(command.split(" "), path, log);
            return command;
        }
        return command;
    }

    public static String insert_silence_to_video(MediaElement me, MediaElement meConfig, String path, boolean excuteProcess, boolean log) throws InterruptedException, IOException {
        String command = "ffmpeg -shortest -i " + me.getName() + " -ar 44100 -ab 224k -f s16le -ac 2 -i /dev/zero -y " + meConfig.getName();
        System.out.println(command);
        if (excuteProcess) {
            ProcessExecutor.execute_process(command.split(" "), path, log);
            return command;
        }
        return command;
    }

    public static String create_image_from_video(MediaElement me, int timeStamp, String output, String path, boolean excuteProcess, boolean log) throws InterruptedException, IOException {
        String command = "ffmpeg -y -i " + me.getName() + " -an -ss " + do_SecondsToTime(timeStamp) + " -r 1 -vframes 1 -f mjpeg " + output;
        System.out.println(command);
        if (excuteProcess) {
            ProcessExecutor.execute_process(command.split(" "), path, log);
            return command;
        }
        return command;
    }

    public static String insert_text_to_video(MediaElement me, MediaElement meConfig, FontElement fe, String path, boolean excuteProcess, boolean log) throws InterruptedException, IOException {
        //String filterDrawText = "-vf drawtext=fontsize=" + fe.getFontSize() + ":fontcolor=" + fe.getFontColor()+ ":shadowcolor=black:shadowx=1:shadowy=1:fix_bounds=true" + ":fontfile=" + fe.getFontFile() + ":text='" + fe.getText() + "':x=" + fe.getX() + ":y=" + fe.getY();
        String filterDrawText = "-vf drawtext=fontsize=" + fe.getFontSize() + ":fontcolor=" + fe.getFontColor()+ ":shadowcolor=black:shadowx=1:shadowy=1" + ":fontfile=" + fe.getFontFile() + ":text='" + fe.getText() + "':x=" + fe.getX() + ":y=" + fe.getY();
        String command = "ffmpeg -y -i " + me.getName() + " " + filterDrawText + " -acodec " + meConfig.getAudioElement().getCodec() + " -ab " + meConfig.getAudioElement().getBitrate() + " -r " + meConfig.getVideoElement().getFps() + " -force_fps -aspect " + HDDAR + " -vcodec " + meConfig.getVideoElement().getCodec() + " -b " + meConfig.getVideoElement().getBitRate() + " -maxrate " + meConfig.getVideoElement().getBitRate() + " -minrate " + meConfig.getVideoElement().getBitRate() + " -preset veryfast " + meConfig.getName();
        System.out.println(command);
        System.out.println(command.split(" ")[5]);
        String[] commandArray = command.split(" ");
        commandArray[5] = commandArray[5].replace("¬", " ");
        if (excuteProcess) {
            ProcessExecutor.execute_process(commandArray, path, log);
            return command;
        }
        return command;
    }

    public static String insert_text_to_video(MediaElement me, MediaElement meConfig, FontElement fe, int homeTime, int endTime, String path, boolean excuteProcess, boolean log) throws InterruptedException, IOException {
        //String filterDrawText = "-vf drawtext=fontsize=" + fe.getFontSize() + ":fontcolor=" + fe.getFontColor() + ":shadowcolor=black:shadowx=1:shadowy=1:fix_bounds=true"+ ":fontfile=" + fe.getFontFile() + ":text=" + fe.getText() + ":x=" + fe.getX() + ":y=" + fe.getY();
        String filterDrawText = "-vf drawtext=fontsize=" + fe.getFontSize() + ":fontcolor=" + fe.getFontColor() + ":shadowcolor=black:shadowx=1:shadowy=1"+ ":fontfile=" + fe.getFontFile() + ":text=" + fe.getText() + ":x=" + fe.getX() + ":y=" + fe.getY();
        String ht = do_SecondsToTime(homeTime);
        String et = do_SecondsToTime(endTime);
        String cutTime = "-ss " + ht + " -t " + et;
        String command = "ffmpeg -y -i " + me.getName() + " " + cutTime + " " + filterDrawText + " -acodec " + meConfig.getAudioElement().getCodec() + " -ab " + meConfig.getAudioElement().getBitrate() + " -r " + meConfig.getVideoElement().getFps() + " -force_fps -aspect " + HDDAR + " -vcodec " + meConfig.getVideoElement().getCodec() + " -b " + meConfig.getVideoElement().getBitRate() + " -maxrate " + meConfig.getVideoElement().getBitRate() + " -minrate " + meConfig.getVideoElement().getBitRate() + " -preset veryfast " + meConfig.getName();
        System.out.println(command);
        System.out.println(command.split(" ")[9]);
        String[] commandArray = command.split(" ");
        commandArray[9] = commandArray[5].replace("¬", " ");
        if (excuteProcess) {
            ProcessExecutor.execute_process(commandArray, path, log);
            return command;
        }
        return command.replace("¬", " ");
    }

    public static String insert_text_from_file_to_video(MediaElement me, MediaElement meConfig, FontElement fe, int homeTime, int endTime, String path, boolean excuteProcess, boolean log) throws InterruptedException, IOException {
        //String filterDrawText = "-vf drawtext=fontsize=" + fe.getFontSize() + ":fontcolor=" + fe.getFontColor() + ":shadowcolor=black:shadowx=1:shadowy=1:fix_bounds=true"+":fontfile=" + fe.getFontFile() + ":textfile=" + fe.getText() + ":x=" + fe.getX() + ":y=" + fe.getY();
        String filterDrawText = "-vf drawtext=fontsize=" + fe.getFontSize() + ":fontcolor=" + fe.getFontColor() + ":shadowcolor=black:shadowx=1:shadowy=1"+":fontfile=" + fe.getFontFile() + ":textfile=" + fe.getText() + ":x=" + fe.getX() + ":y=" + fe.getY();
        String ht = do_SecondsToTime(homeTime);
        String et = do_SecondsToTime(endTime);
        String cutTime = "-ss " + ht + " -t " + et;
        String command = "ffmpeg -y -i " + me.getName() + " " + cutTime + " " + filterDrawText + " -acodec " + meConfig.getAudioElement().getCodec() + " -ab " + meConfig.getAudioElement().getBitrate() + " -r " + meConfig.getVideoElement().getFps() + " -force_fps -aspect " + HDDAR + " -vcodec " + meConfig.getVideoElement().getCodec() + " -b " + meConfig.getVideoElement().getBitRate() + " -maxrate " + meConfig.getVideoElement().getBitRate() + " -minrate " + meConfig.getVideoElement().getBitRate() + " -preset veryfast " + meConfig.getName();
        System.out.println(command);
        System.out.println(command.split(" ")[9]);
        String[] commandArray = command.split(" ");
        commandArray[9] = commandArray[5].replace("¬", " ");
        if (excuteProcess) {
            ProcessExecutor.execute_process(commandArray, path, log);
            return command;
        }
        return command.replace("¬", " ");
    }

    public static String insert_text_from_file_to_video(MediaElement me, MediaElement meConfig, FontElement fe, String path, boolean excuteProcess, boolean log) throws InterruptedException, IOException {
        //String filterDrawText = "-vf drawtext=fontsize=" + fe.getFontSize() + ":fontcolor=" + fe.getFontColor() + ":shadowcolor=black:shadowx=1:shadowy=1:fix_bounds=true"+ ":fontfile=" + fe.getFontFile() + ":textfile='" + fe.getText() + "':x=" + fe.getX() + ":y=" + fe.getY();
        String filterDrawText = "-vf drawtext=fontsize=" + fe.getFontSize() + ":fontcolor=" + fe.getFontColor() + ":shadowcolor=black:shadowx=1:shadowy=1"+ ":fontfile=" + fe.getFontFile() + ":textfile='" + fe.getText() + "':x=" + fe.getX() + ":y=" + fe.getY();
        String command = "ffmpeg -y -i " + me.getName() + " " + filterDrawText + " -acodec " + meConfig.getAudioElement().getCodec() + " -ab " + meConfig.getAudioElement().getBitrate() + " -r " + meConfig.getVideoElement().getFps() + " -force_fps -aspect " + HDDAR + " -vcodec " + meConfig.getVideoElement().getCodec() + " -b " + meConfig.getVideoElement().getBitRate() + " -maxrate " + meConfig.getVideoElement().getBitRate() + " -minrate " + meConfig.getVideoElement().getBitRate() + " -preset veryfast " + meConfig.getName();
        System.out.println(command);
        System.out.println(command.split(" ")[5]);
        String[] commandArray = command.split(" ");
        commandArray[5] = commandArray[5].replace("¬", " ");
        if (excuteProcess) {
            ProcessExecutor.execute_process(commandArray, path, log);
            return command;
        }
        return command;
    }

    public static String do_fade_to_video(MediaElement me, MediaElement meConfig, int fadeInSeconds, int fadeOutSeconds, String path, boolean excuteProcess, boolean log) throws InterruptedException, IOException {
        String filterFade = "";
        String temp;
        boolean isfadein = false;
        if (fadeInSeconds > 0) {
            filterFade = "-vf ";
            temp = "fade=in:0:" + Math.round(fadeInSeconds * meConfig.getVideoElement().getNumberFPS());
            System.out.println(temp);
            filterFade += temp;
            isfadein = true;
        }
        if (fadeOutSeconds > 0) {
            if (isfadein) {
                filterFade += ",";
            } else {
                filterFade = "-vf ";
            }
            //System.out.println(me.getVideoElement().getNumberFPS());
            temp = "fade=out:" + Math.round(((float) do_TimeToSeconds(me.getDuration())) * meConfig.getVideoElement().getNumberFPS() - (float) fadeOutSeconds * meConfig.getVideoElement().getNumberFPS()) + ":" + Math.round(fadeOutSeconds * me.getVideoElement().getNumberFPS());
            System.out.println(temp);
            filterFade += temp;
        }
        String command = "ffmpeg -y -i " + me.getName() + " " + filterFade + " -acodec " + meConfig.getAudioElement().getCodec() + " -ab " + meConfig.getAudioElement().getBitrate() + " -r " + meConfig.getVideoElement().getFps() + " -force_fps -aspect " + HDDAR + " -vcodec " + meConfig.getVideoElement().getCodec() + " -b " + meConfig.getVideoElement().getBitRate() + " -maxrate " + meConfig.getVideoElement().getBitRate() + " -minrate " + meConfig.getVideoElement().getBitRate() + " -preset veryfast " + meConfig.getName();
        System.out.println(command);
        if (excuteProcess) {
            ProcessExecutor.execute_process(command.split(" "), path, log);
            return command;
        }
        return command;

    }

    public static void do_merge_videos_x264(List<MediaElement> mes, String path, String output, boolean excuteProcess, boolean log) throws InterruptedException, IOException {
        String command;
        String concat = "concat:";
        MediaElement me;
        int i = 0;
        Iterator iter = mes.iterator();

        while (iter.hasNext()) {
            me = (MediaElement) iter.next();
            //command = "ffmpeg -y -i " + me.getName() + " -vcodec copy -vbsf h264_mp4toannexb -acodec copy -absf aac_adtstoasc part" + i + ".ts";
            command = "ffmpeg -y -i " + me.getName() + " -c copy -bsf h264_mp4toannexb -absf aac_adtstoasc part" + i + ".ts";
            System.out.println(command);
            ProcessExecutor.execute_process(command.split(" "), path, log);
            concat += "part" + i + ".ts|";
            i++;
        }
        concat = concat.substring(0, concat.length() - 1);
        System.out.println(concat);

        //command = "ffmpeg -y -i " + concat + " -vcodec copy -acodec copy -absf aac_adtstoasc " + output;
        command = "ffmpeg -y -i " + concat + " -absf aac_adtstoasc " + output;
        System.out.println(command);
        ProcessExecutor.execute_process(command.split(" "), path, log);
    }

    public static String do_merge_videos_x264_return(List<MediaElement> mes, String path, String output, boolean excuteProcess, boolean log) throws InterruptedException, IOException {
        String command;
        String concat = "concat:\"";
        MediaElement me;
        int i = 0;
        Iterator iter = mes.iterator();

        while (iter.hasNext()) {
            me = (MediaElement) iter.next();
            //command = "ffmpeg -y -i " + me.getName() + " -vcodec copy -vbsf h264_mp4toannexb -acodec copy -absf aac_adtstoasc part" + i + ".ts";
            command = "ffmpeg -y -i " + me.getName() + " -c copy -bsf h264_mp4toannexb -absf aac_adtstoasc part" + i + ".ts";
            System.out.println(command);
            ProcessExecutor.execute_process(command.split(" "), path, log);
            concat += "part" + i + ".ts|";
            i++;
        }
        concat = concat.substring(0, concat.length() - 1);
        System.out.println(concat);

        //command = "ffmpeg -y -i " + concat + " -vcodec copy -acodec copy -absf aac_adtstoasc " + output;
        command = "ffmpeg -y -i " + concat + "\" -absf aac_adtstoasc " + output;
        System.out.println(command);
        return command;
    }
}
