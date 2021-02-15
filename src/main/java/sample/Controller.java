package sample;


import com.github.kiulian.downloader.YoutubeException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

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
    private CheckBox mp4_HighCB;

    @FXML
    private CheckBox mp3_CB;

    @FXML
    private CheckBox mp4_LowCB;

    @FXML
    private TextField linkField;

    @FXML
    private TextField pathField;

    @FXML
    private Text Errors_Confirmatio;

    public void initialize() {
        System.out.println("test");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        formatColumn.setCellValueFactory(new PropertyValueFactory<>("format"));
        linkColumn.setCellValueFactory(new PropertyValueFactory<>("link"));
        linkColumn.setPrefWidth(200);
        nameColumn.setPrefWidth(300);
        formatColumn.setPrefWidth(70);
        videoTable.setItems(videosList);
        videoTable.getColumns().addAll(nameColumn, formatColumn, linkColumn);
        System.out.println("test");
    }

    public void addVideo() throws YoutubeException {//TODO check if link is valid, check if any format is selected!
        //TODO make a function that gives the videos name and takes the link as a parameter!
        Boolean added = false;
        FormatEnum[] format = new FormatEnum[3];
        format[0] = mp4_HighCB.isSelected() ? FormatEnum.mp4_High : null;
        format[1] = mp3_CB.isSelected() ? FormatEnum.mp3 : null;
        format[2] = mp4_LowCB.isSelected() ? FormatEnum.mp4_Low : null;
        System.out.println((linkField.getText().substring(32)));
        if(linkField.getText().substring(0,32).equals("https://www.youtube.com/watch?v=")) {
            for (FormatEnum form : format)
                if (form != null) {
                    videosList.add(new YTVideo(Downloader.getVideoName(linkField.getText()), form, linkField.getText()));
                    added = true;
                }
            if (added) display("Videos added!", true);
            else display("Nothing was added", false);
            videoTable.setItems(videosList);
        }else display("Not a valid youtube link!", false);
    }

    public void removeVideo() {
        try {
            videosList.remove(videoTable.getSelectionModel().getSelectedItem());
            display("Removed", true);
        } catch (Exception e) {
            display("Nothing selected!", false);
        }
    }

    public void display(String text, boolean positive) {
        Errors_Confirmatio.setFill(positive ? Color.GREEN : Color.RED);
        Errors_Confirmatio.setText(text);
    }

    public void getClipBoard() {
        String myString = "This text will be copied into clipboard";
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public void downloadVid() throws IOException, YoutubeException, InterruptedException, ExecutionException, TimeoutException {
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
        System.out.println("done");
    }

    public void downloadList() throws InterruptedException, ExecutionException, YoutubeException, TimeoutException, IOException {
        for (YTVideo video :
                videosList) {
            Downloader.downloadVideo(video.getFormat(), video.getLink(), pathField.getText());
        }
    }

    public void ChoosePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        System.out.println(selectedDirectory.getPath());
        pathField.setText(selectedDirectory.getPath());

    }

}
