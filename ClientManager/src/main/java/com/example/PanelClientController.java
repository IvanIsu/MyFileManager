package com.example;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PanelClientController implements Initializable {

    @FXML
    TableView<FileInfo> filesClient;

    @FXML
    ComboBox<String> disksBox;

    @FXML
    TextField pathFiled;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


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
        filesClient.getColumns().addAll(fileTypeColumn,fileNameColumn,fileSizeColumn, fileDateColumn);
        updateListFiles(Paths.get("."));
//        updateListFiles(Paths.get("ClientManager/src/main/java/com/example/clientdir"));
        filesClient.getSortOrder().add(fileTypeColumn);
        disksBox.getItems().clear();
        for(Path p : FileSystems.getDefault().getRootDirectories()){
            disksBox.getItems().add(p.toString());
        }
        disksBox.getSelectionModel().select(0);

        filesClient.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2){
                    Path path = Paths.get(pathFiled.getText()).resolve(filesClient.getSelectionModel().getSelectedItem().getFileName());
                    if(Files.isDirectory(path)){
                        updateListFiles(path);
                    }


                }
            }
        });
    }

    public void updateListFiles(Path path){
            try {
                pathFiled.setText(path.normalize().toAbsolutePath().toString());
                filesClient.getItems().clear();
                filesClient.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
                filesClient.sort();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR,"Cant update file list", ButtonType.OK);
                alert.showAndWait();
            }
    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathFiled.getText());
        if(upperPath.getParent() != null){
            updateListFiles(Paths.get(upperPath.getParent().toString()));
        }

    }

    public void selectDiskAction(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        updateListFiles(Paths.get(element.getSelectionModel().getSelectedItem()));
    }

    public String getSelectedFileName(){
        if(!filesClient.isFocused()){
            return null;
        }
        return filesClient.getSelectionModel().getSelectedItem().getFileName();
    }

    public String getCurrentPath(){
        return pathFiled.getText();
    }
}
