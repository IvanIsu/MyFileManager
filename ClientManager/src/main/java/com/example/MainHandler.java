package com.example;

import com.example.dto.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class MainHandler extends ChannelInboundHandlerAdapter {


    private static HashMap<String, FileOutputStream> writeFiles = new HashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        BasicResponse response = (BasicResponse) msg;

        if(response instanceof AuthResponse) {
            AuthResponse authResponse = (AuthResponse) msg;
            if (authResponse.getResponse().equals("authOk")) {
                AuthController.switchToMain();
            } else if (authResponse.getResponse().equals("authFail")) {
                AuthController.showAlertMsg("authFail");
            } else if (authResponse.getResponse().equals("RegOk")) {
                AuthController.showDoneMsg("RegOK");
            } else if (authResponse.getResponse().equals("RegFail")) {
                AuthController.showAlertMsg("RegFail");
            }
        }else if (response instanceof UpdateFIleListResponse){
            UpdateFIleListResponse updateFIleListResponse = (UpdateFIleListResponse)msg;
            PanelServerController.updateFileList(updateFIleListResponse.getPath(), updateFIleListResponse.getFileInfoList());
        }else if (response instanceof FileDownloadResponse){
            FileDownloadResponse fileDownloadResponse = (FileDownloadResponse) response;
            copyFileOnClient(fileDownloadResponse);
        }else if(response instanceof EndFileDownLoadResponse){
            EndFileDownLoadResponse endFileDownLoadResponse = (EndFileDownLoadResponse)response;
            writeFiles.get(endFileDownLoadResponse.getResponse()).close();
            writeFiles.remove(endFileDownLoadResponse.getResponse());
            MainController.updateTable();
        }


    }
    public static void copyFileOnClient(FileDownloadResponse fileDownloadResponse) throws IOException {
        FileOutputStream fileOutputStream = null;
        FileChannel fileChannel;
        if(writeFiles.isEmpty()){
            Path path = Paths.get(fileDownloadResponse.getDstPath());
            fileOutputStream = new FileOutputStream(path.toFile(),true);
            fileChannel = fileOutputStream.getChannel();
            writeFiles.put(fileDownloadResponse.getFileName(),fileOutputStream);
            ByteBuffer buffer = ByteBuffer.allocate(15*1_000_000);
            buffer.put(fileDownloadResponse.getBytes());
            buffer.flip();
            while (buffer.hasRemaining()){
                fileChannel.write(buffer);
            }
            buffer.clear();
        }else {
            fileChannel = writeFiles.get(fileDownloadResponse.getFileName()).getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(15*1_000_000);
            buffer.put(fileDownloadResponse.getBytes());
            buffer.flip();
            while (buffer.hasRemaining()){
                fileChannel.write(buffer);
            }

            buffer.clear();

        }
    }
}
