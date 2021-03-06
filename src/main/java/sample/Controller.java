package sample;


import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.playlist.PlaylistVideoDetails;
import com.github.kiulian.downloader.model.playlist.YoutubePlaylist;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Controller {
    @FXML
    private TableView<YTVideo> videoTable;
    private TableColumn<YTVideo, String> nameColumn = new TableColumn<>("Name:");
    private TableColumn<YTVideo, FormatEnum> formatColumn = new TableColumn<>("Format");
    private TableColumn<YTVideo, String> linkColumn = new TableColumn<>("Link:");

    private ObservableList<YTVideo> videosList = FXCollections.observableArrayList();

    @FXML
    private CheckBox playlist_CB, mp4_LowCB, mp3_CB, mp4_HighCB;

    @FXML
    private TextField linkField, pathField;

    @FXML
    public Text Errors_Confirmation;

    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        formatColumn.setCellValueFactory(new PropertyValueFactory<>("format"));
        linkColumn.setCellValueFactory(new PropertyValueFactory<>("link"));
        linkColumn.setPrefWidth(200);
        nameColumn.setPrefWidth(300);
        formatColumn.setPrefWidth(70);
        videoTable.setItems(videosList);
        videoTable.getColumns().addAll(nameColumn, formatColumn, linkColumn);

    }

    public String getPlaylistId(String link) {
        return  link.substring(link.indexOf("list=") + 5, link.indexOf("channel"));
    }

    public void addPlaylist() throws YoutubeException {
        FormatEnum[] format = new FormatEnum[3];
        format[0] = mp4_HighCB.isSelected() ? FormatEnum.mp4_High : null;
        format[1] = mp3_CB.isSelected() ? FormatEnum.mp3 : null;
        format[2] = mp4_LowCB.isSelected() ? FormatEnum.mp4_Low : null;

        System.out.println((linkField.getText().substring(32)));
        YoutubeDownloader downloader = new YoutubeDownloader();
        YoutubePlaylist playlist = downloader.getPlaylist(getPlaylistId(linkField.getText()));
        for (int i = 0; i < playlist.details().videoCount(); i++) {
            PlaylistVideoDetails details = playlist.videos().get(i);
            System.out.println(details.videoId());
            for (FormatEnum form : format)
                if (form != null)
                    videosList.add(new YTVideo(details.title(), form, "https://www.youtube.com/watch?v=" + details.videoId()));

        }
    }
        public boolean addVideo () throws YoutubeException {
            Boolean added = false;
            FormatEnum[] format = new FormatEnum[3];
            format[0] = mp4_HighCB.isSelected() ? FormatEnum.mp4_High : null;
            format[1] = mp3_CB.isSelected() ? FormatEnum.mp3 : null;
            format[2] = mp4_LowCB.isSelected() ? FormatEnum.mp4_Low : null;
            if(linkField.getText().length()<33) {
                display("Not a valid youtube link",false);
                return false;
            }
            System.out.println((linkField.getText().substring(32)));
            if (playlist_CB.isSelected())
                addPlaylist();
             else {
                    for (FormatEnum form : format)
                        if (form != null) {
                            try {
                                videosList.add(new YTVideo(Downloader.getVideoName(linkField.getText()), form, linkField.getText()));
                            } catch (Exception e) {
                                try {System.out.println("test add");
                                addPlaylist();

                                }catch ( Exception ee){
                                display("This video doesnt exist!", false);
                                }
                                return false;
                            }
                            added = true;
                        }
                    if (added) display("Video\\s added!", true);
                    else display("No format was selected! ", false);
                    videoTable.setItems(videosList);
            }
             return  true;
        }

        public void removeVideo () {
            try {
                videosList.remove(videoTable.getSelectionModel().getSelectedItem());
                display("Removed!", true);
            } catch (Exception e) {
                display("Nothing selected!", false);
            }
        }


        public void display (String text,boolean positive){
            Errors_Confirmation.setFill(positive ? Color.GREEN : Color.RED);
            Errors_Confirmation.setText(text);
        }

        public void getClipBoard () throws YoutubeException {
            FormatEnum format[] = new FormatEnum[3];
            format[0] = mp4_HighCB.isSelected() ? FormatEnum.mp4_High : null;
            format[1] = mp3_CB.isSelected() ? FormatEnum.mp3 : null;
            format[2] = mp4_LowCB.isSelected() ? FormatEnum.mp4_Low : null;
            StringSelection stringSelection = null;
            for (FormatEnum form : format) {
                if (form != null) {
                    stringSelection = new StringSelection(Downloader.getDwnLink(linkField.getText(), form));
                    break;
                }
            }
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (stringSelection != null) {
                clipboard.setContents(stringSelection, null);
                display("Link added to clipboard!", true);
            } else display("Nothing was selected or added!", false);
        }

        public void downloadVid () throws
        IOException, YoutubeException, InterruptedException, ExecutionException, TimeoutException {
            System.out.println("began");
            FormatEnum[] format = new FormatEnum[3];
            format[0] = mp4_HighCB.isSelected() ? FormatEnum.mp4_High : null;
            format[1] = mp3_CB.isSelected() ? FormatEnum.mp3 : null;
            format[2] = mp4_LowCB.isSelected() ? FormatEnum.mp4_Low : null;
            for (FormatEnum form : format) {
                if (form != null) {
                    Downloader.downloadVideo(form, linkField.getText(), pathField.getText());
                }
            }
        }

        public void downloadList () throws
        InterruptedException, ExecutionException, YoutubeException, TimeoutException, IOException {
            for (YTVideo video :
                    videosList) {
                Downloader.downloadVideo(video.getFormat(), video.getLink(), pathField.getText());
            }
        }

        public void ChoosePath () {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(null);
            pathField.setText(selectedDirectory.getPath());
        }

    }
