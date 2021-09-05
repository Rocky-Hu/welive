package com.welive;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class ConvertVideoPakcet implements ApplicationRunner {
    public Process process;

    public Integer pushVideoAsRTSP(String id, String fileName){
        int flag = -1;
        // ffmpeg位置，最好写在配置文件中
//        String ffmpegPath = "E:\\webset\\ffmpeg\\ffmpeg-20200213-6d37ca8-win64-static\\bin\\";
        String ffmpegPath = "";
        try {
            // 视频切换时，先销毁进程，全局变量Process process，方便进程销毁重启，即切换推流视频
            if(process != null){
                process.destroy();
                System.out.println(">>>>>>>>>>推流视频切换<<<<<<<<<<");
            }
            // cmd命令拼接，注意命令中存在空格
//            String command = ffmpegPath; // ffmpeg位置
//            command += "/usr/local/ffmpeg/bin/ffmpeg "; // ffmpeg开头，-re代表按照帧率发送，在推流时必须有
//            command += " -i \"" + id + "\""; // 指定要推送的视频
//            command += " -q 0 -f mpegts -codec:v mpeg1video -s 240x160 " + fileName; // 指定推送服务器，-f：指定格式

            String command = "/usr/local/bin/ffmpeg -i \"{rtspAddr}\" -vcodec libx264 -acodec copy -f flv \"{rtmpAddr}\"";
            command = command.replace("{rtspAddr}", id).replace("{rtmpAddr}", fileName);

            System.out.println("ffmpeg推流命令：" + command);

            // 运行cmd命令，获取其进程
            process = Runtime.getRuntime().exec(new String[] {"sh", "-c", command});
            // 输出ffmpeg推流日志
            BufferedReader br= new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = "";
            while ((line = br.readLine()) != null) {
                System.out.println("视频推流信息[" + line + "]");
            }
            flag = process.waitFor();
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ConvertVideoPakcet convertVideoPakcet = new ConvertVideoPakcet();
//        convertVideoPakcet.pushVideoAsRTSP("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov", "http://127.0.0.1:8081/rtsp/receive");
        convertVideoPakcet.pushVideoAsRTSP("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov", "rtmp://192.168.56.101:1935/mylive/1");
    }
}
