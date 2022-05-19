package com.example;

import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    private Channel channel;
    private static PanelClientController ClientRC;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        channel = Network.getChannel();
        ClientRC = (PanelClientController) rightClientFilePanel.getProperties().get("ctrl");
    }

    @FXML
    VBox  leftServerFilePanel,rightClientFilePanel;


public static void updateTable(){
    ClientRC.updateListFiles(Paths.get(ClientRC.getCurrentPath()));
}
    @FXML
    public static void switchToAuth() throws IOException {
        MainApp.setRoot("/authStage.fxml");
    }


    public void btnExitAction(ActionEvent actionEvent) {
        Network.exitAction();
        Platform.exit();
    }

    public void btnCopyAction(ActionEvent actionEvent) {
    PanelServerController serverPC = (PanelServerController) leftServerFilePanel.getProperties().get("ctrl");
    PanelClientController clientPC = (PanelClientController) rightClientFilePanel.getProperties().get("ctrl");
    if(serverPC.getSelectedFileName() == null && clientPC.getSelectedFileName() == null){
        Alert alert = new Alert(Alert.AlertType.ERROR,"file not selected",ButtonType.OK);
        alert.showAndWait();
        return;
    }
    if(serverPC.getSelectedFileName() != null){
        Path srcPath = Paths.get(serverPC.getCurrentPath(), serverPC.getSelectedFileName());
        Path dstPath = Paths.get(clientPC.getCurrentPath()).resolve(srcPath.getFileName().toString());
        Network.fileUpload(srcPath.toString(),dstPath.toString(),serverPC.getSelectedFileName());
    }
    if(clientPC.getSelectedFileName() != null){
        Path srcPath = Paths.get(clientPC.getCurrentPath(), clientPC.getSelectedFileName());
        Path dstPath = Paths.get(serverPC.getCurrentPath()).resolve(srcPath.getFileName().toString());
        Network.fileDownload(srcPath.toString(),dstPath.toString(),clientPC.getSelectedFileName());
    }

}

    public void btnDeleteAction(ActionEvent actionEvent) {

        PanelServerController serverPC = (PanelServerController) leftServerFilePanel.getProperties().get("ctrl");
        PanelClientController clientPC = (PanelClientController) rightClientFilePanel.getProperties().get("ctrl");
        if(serverPC.getSelectedFileName() == null && clientPC.getSelectedFileName() == null){
                Alert alert = new Alert(Alert.AlertType.ERROR,"file not selected",ButtonType.OK);
                alert.showAndWait();
                return;

        }
        if(serverPC.getSelectedFileName() != null){
            Path srcPath = Paths.get(serverPC.getCurrentPath(), serverPC.getSelectedFileName());
            Network.deleteFile(srcPath.toString());
        }
        if(clientPC.getSelectedFileName() != null){
            Path srcPath = Paths.get(clientPC.getCurrentPath(), clientPC.getSelectedFileName());
            if(Files.isDirectory(srcPath)){
                try {
                    List<FileInfo> list = Files.list(srcPath).map(FileInfo::new).collect(Collectors.toList());
                    for (FileInfo o: list) {
                        Files.delete(srcPath.resolve(o.getFileName()));
                    }
                    Files.delete(srcPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    Files.delete(srcPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
              updateTable();
        }
    }
}