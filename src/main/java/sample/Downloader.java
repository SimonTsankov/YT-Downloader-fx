package sample;

import com.github.kiulian.downloader.OnYoutubeDownloadListener;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.VideoDetails;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.kiulian.downloader.model.formats.AudioVideoFormat;
import com.github.kiulian.downloader.model.formats.Format;
import com.github.kiulian.downloader.model.formats.VideoFormat;
import com.github.kiulian.downloader.model.quality.VideoQuality;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Downloader {
    public static void main(String[] args) throws YoutubeException, IOException {
//        YoutubeDownloader downloader = new YoutubeDownloader();
//        downloader.setParserRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
//        downloader.setParserRetryOnFailure(1);
//        String videoId = "STKSpaXqrus&ab_channel=Wave"; // for url https://www.youtube.com/watch?v=abc12345
//        YoutubeVideo video = downloader.getVideo(videoId);
//        VideoDetails details = video.details();
//
//        List<AudioVideoFormat> videoWithAudioFormats = video.videoWithAudioFormats();
//        videoWithAudioFormats.forEach(it -> {
//            System.out.println(it.audioQuality() + " : " + it.url());
//        });
//
//        File outputDir = new File("D:\\Pictures\\Camera Roll");
//        Format format = videoWithAudioFormats.get(0);
//        File myAwesomeFile = video.download(format, outputDir, "myAwesomeName");
        Downloader dwn= new Downloader();
       //dwn.downloadVideo(FormatEnum.mp4_High,"https://www.youtube.com/watch?v=fKa7FTxgiA4&ab_channel=DarkViperAUClips","D:\\\\Pictures\\\\Camera Roll");
        dwn.getVideoName("https://www.youtube.com/watch?v=STKSpaXqrus&ab_channel=Wave");
        //dwn.getFormats("https://www.youtube.com/watch?v=STKSpaXqrus&ab_channel=Wave");
    }

    public static void downloadVideo(FormatEnum format, String link,String path) throws YoutubeException, IOException, InterruptedException, ExecutionException, TimeoutException {
        long startTime= System.currentTimeMillis();
        String videoId=link.substring(32);
        System.out.println(videoId);
        YoutubeDownloader downloader = new YoutubeDownloader();
        downloader.setParserRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        downloader.setParserRetryOnFailure(1);
        YoutubeVideo video = downloader.getVideo(videoId);
        File outputDir = new File(path);
        Format videoFormat;
        switch (format){
            case mp3 -> {
                    videoFormat= video.findFormatByItag(141);//m4a 256k
                    if(videoFormat==null) videoFormat= video.findFormatByItag(140);// if video deosnt have 256k audio, Dont know if its even possible tbh xd
            }
            case mp4_High ->{
                    videoFormat= video.findFormatByItag(37);// 1080p
                    if(videoFormat==null)
                        videoFormat= video.findFormatByItag(22);//if 1080p not available 720 p
                else System.out.println("1080p");
            }
            case mp4_Low ->
                    videoFormat = video.findFormatByItag(18);
            default -> videoFormat= video.findFormatByItag(141); // default 720p video
        }
        Future<File> future = video.downloadAsync(videoFormat, outputDir, new OnYoutubeDownloadListener() {
            @Override
            public void onDownloading(int progress) {
                System.out.printf("Downloaded %d%%\n", progress);
            }

            @Override
            public void onFinished(File file) {
                System.out.println("Finished file: " + file);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: " + throwable.getLocalizedMessage());
            }
        });
        System.out.println("DONE in: "+ ( System.currentTimeMillis()-startTime)+"ms");
    }

    public String getVideoName(String link) throws YoutubeException {
        String videoId = link.substring(32);
        YoutubeDownloader downloader = new YoutubeDownloader();
        VideoDetails details = downloader.getVideo(videoId).details();
        return  details.title();
    }
    public void getFormats(String link) throws YoutubeException {
        String videoId=link.substring(32);
        System.out.println(videoId);
        YoutubeDownloader downloader = new YoutubeDownloader();
        downloader.setParserRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        downloader.setParserRetryOnFailure(1);
        YoutubeVideo video = downloader.getVideo(videoId);

        Format format;
        for (int i = 5; i <47 ; i++) {
            format= video.findFormatByItag(i);
            if(format!=null)
                System.out.println(format.type());
        }

    }
}
