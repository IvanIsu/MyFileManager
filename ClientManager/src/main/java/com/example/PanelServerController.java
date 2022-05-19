package com.example;


import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;


public class PanelServerController implements Initializable {
    private static PanelServerController panelServerController;

    @FXML
    TableView<FileInfo> filesServer;

    @FXML
    TextField pathFiledServer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        panelServerController = this;


        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>("Type");
        fileTypeColumn.setCellValueFactory(file -> new SimpleStringProperty(file.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(50);

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("File Name");
        fileNameColumn.setCellValueFactory(file -> new SimpleStringProperty(file.getValue().getFileName()));
        fileNameColumn.setPrefWidth(250);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Size");
        fileSizeColumn.setCellValueFactory(file -> new SimpleObjectProperty<>(file.getValue().getFileSize()));
        fileSizeColumn.setPrefWidth(100);

        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>(){
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item == null || empty){
                        setText(null);
                        setStyle("");
                    }else {
                        String text = String.format("%,d bytes", item);
                        if(item == -1L){
                            text = "DIR";
                        }
                        setText(text);
                    }
                }
            };
        });
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");
        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Date");
        fileDateColumn.setCellValueFactory(file -> new SimpleStringProperty(file.getValue().getLastModified().format(dtf)));
        fileDateColumn.setPrefWidth(120);

        filesServer.getColumns().addAll(fileTypeColumn,fileNameColumn,fileSizeColumn, fileDateColumn);
        filesServer.getSortOrder().add(fileTypeColumn);


        filesServer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2){
                    Path path = Paths.get(pathFiledServer.getText()).resolve(filesServer.getSelectionModel().getSelectedItem().getFileName());
                    Network.getFileList(filesServer.getSelectionModel().getSelectedItem().getFileName(), path.toString());
                }
            }
        });

    }


    private void updateListFiles(String path, List<FileInfo> list){
        pathFiledServer.setText(path);
        filesServer.getItems().clear();
        filesServer.getItems().addAll(list);
        filesServer.sort();
    }


    public String getSelectedFileName(){
        if(!filesServer.isFocused()){
            return null;
        }
        return filesServer.getSelectionModel().getSelectedItem().getFileName();
    }

    public String getCurrentPath(){
        return pathFiledServer.getText();
    }

    public void btnPathUpActionServer(ActionEvent actionEvent) {
        String path = pathFiledServer.getText();
        Network.getFileListUP(null, path);
    }
    public static void updateFileList(String path, List<FileInfo> list){
        panelServerController.updateListFiles(path,list);
    }

}
