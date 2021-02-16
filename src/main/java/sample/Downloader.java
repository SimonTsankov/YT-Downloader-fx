package sample;

import com.github.kiulian.downloader.OnYoutubeDownloadListener;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.VideoDetails;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.kiulian.downloader.model.formats.AudioFormat;
import com.github.kiulian.downloader.model.formats.AudioVideoFormat;
import com.github.kiulian.downloader.model.formats.Format;
import javafx.scene.text.Text;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public class Downloader {
    public static void main(String[] args) throws YoutubeException {
        Downloader.getDwnLink("https://www.youtube.com/watch?v=AesNXAM5byU&ab_channel=TheWarOwl", FormatEnum.mp3);
    }

    public static void downloadVideo(FormatEnum format, String link, String path) throws YoutubeException, IOException, InterruptedException, ExecutionException, TimeoutException {
        String videoId = link.substring(32);
        YoutubeDownloader downloader = new YoutubeDownloader();
        downloader.setParserRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        downloader.setParserRetryOnFailure(1);
        YoutubeVideo video = downloader.getVideo(videoId);
        File outputDir = new File(path);
        Format videoFormat;
        switch (format) {
            case mp3 -> {
                videoFormat = video.findFormatByItag(141);//m4a 256k
                if (videoFormat == null)
                    videoFormat = video.findFormatByItag(140);// if video deosnt have 256k audio, Dont know if its even possible tbh xd
            }
            case mp4_High -> {
                videoFormat = video.findFormatByItag(37);// 1080p
                if (videoFormat == null)
                    videoFormat = video.findFormatByItag(22);//if 1080p not available 720 p
                else System.out.println("1080p");
            }
            case mp4_Low -> videoFormat = video.findFormatByItag(18);
            default -> videoFormat = video.findFormatByItag(141); // default 720p video
        }

        Future<File> future = video.downloadAsync(videoFormat, outputDir, new OnYoutubeDownloadListener() {
            @Override
            public void onDownloading(int progress) {
                System.out.printf("Downloaded %d%%\n", progress);
            }

            @Override
            public void onFinished(File file) {
                System.out.println("Finished file: " + file);
                JOptionPane.showMessageDialog(null, "Download succsesful!");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: " + throwable.getLocalizedMessage());
            }
        });
    }


    public static String getVideoName(String link) throws YoutubeException {
        String videoId = link.substring(32);
        YoutubeDownloader downloader = new YoutubeDownloader();
        VideoDetails details = downloader.getVideo(videoId).details();
        return details.title();
    }

    public static String getDwnLink(String link, FormatEnum format) throws YoutubeException {
        String videoID = link.substring(32);
        YoutubeDownloader downloader = new YoutubeDownloader();
        YoutubeVideo video = downloader.getVideo(videoID);
        switch (format) {
            case mp4_High -> {
                List<AudioVideoFormat> videoWithAudioFormats = video.videoWithAudioFormats();
                return videoWithAudioFormats.get(1).url();
            }
            case mp3 -> {
                List<AudioFormat> audioFormats = video.audioFormats();
                return audioFormats.get(1).url();
            }
            case mp4_Low -> {
                List<AudioVideoFormat> videoWithAudioFormats = video.videoWithAudioFormats();
                return videoWithAudioFormats.get(0).url();
            }
        }
        return videoID;
    }

    public void getFormats(String link) throws YoutubeException {
        String videoId = link.substring(32);
        System.out.println(videoId);
        YoutubeDownloader downloader = new YoutubeDownloader();
        downloader.setParserRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        downloader.setParserRetryOnFailure(1);
        YoutubeVideo video = downloader.getVideo(videoId);
        Format format;
        for (int i = 5; i < 47; i++) {
            format = video.findFormatByItag(i);
            if (format != null)
                System.out.println(format.type());
        }

    }
}
