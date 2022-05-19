package com.example;

import com.example.dto.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.java.Log;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;


@Log public class MainServerHandler extends ChannelInboundHandlerAdapter {



    //private static final Map<Class<? extends BasicRequest>, Consumer<ChannelHandlerContext>> REQUEST_HANDLERS = new HashMap<>();

    //static {
    //    REQUEST_HANDLERS.put(AuthRequest.class, channelHandlerContext -> {
    //        BasicResponse loginOkResponse = new BasicResponse("login ok");
    //        channelHandlerContext.writeAndFlush(loginOkResponse);
    //    });
    //
    //    REQUEST_HANDLERS.put(GetFileListRequest.class, channelHandlerContext -> {
    //        BasicResponse basicResponse = new BasicResponse("file list....");
    //        channelHandlerContext.writeAndFlush(basicResponse);
    //    });
    //}


    private static HashMap<String, FileOutputStream> writeFiles = new HashMap<>();
    private String nickName;
    private static final Path PATH_SERVER_ROOT = Paths.get("ServerManager/src/main/java/com/example/server/root/users");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.log(Level.INFO, "Client connection " + ctx.channel());
        System.out.println("Client connection " + ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BasicRequest request = (BasicRequest) msg;

        if (request instanceof AuthRequest) {
            AuthRequest authRequest = (AuthRequest)msg;
            String login = authRequest.getLogin();
            int pass = authRequest.getPassword();
            String nick = AuthService.getNickByLoginAndPassword(login,pass);
            if(nick != null){
                nickName = nick;
                Path path = PATH_SERVER_ROOT.resolve(nickName);
                ctx.writeAndFlush(new AuthResponse("authOk"));
                ctx.writeAndFlush(new UpdateFIleListResponse(nickName, path.getFileName().toString(), fileUpdateList(path)));
            }else {
                ctx.writeAndFlush(new AuthResponse("authFail"));
            }

        }else if(request instanceof RegRequest) {
            RegRequest regRequest = (RegRequest)msg;
            boolean a = AuthService.createNewUserInBase(regRequest.getLogin(),regRequest.getNickName(),regRequest.getPassword());
            if(a == true){
                Files.createDirectory(PATH_SERVER_ROOT.resolve(regRequest.getNickName()));
                ctx.writeAndFlush(new AuthResponse("RegOk"));
            }else {
                ctx.writeAndFlush(new AuthResponse("RegFail"));
            }

        }else if (request instanceof GetFileListRequest) {
            GetFileListRequest getFileListRequest = (GetFileListRequest)msg;
            Path pathGet = PATH_SERVER_ROOT.resolve(Paths.get(getFileListRequest.getPath()));
            if(getFileListRequest.getFileName() != null){
                if(Files.isDirectory(pathGet)){
                    ctx.writeAndFlush(new UpdateFIleListResponse(null, PATH_SERVER_ROOT.relativize(pathGet).toString(), fileUpdateList(pathGet)));
                }
            }else if(getFileListRequest.getFileName() == null){

                if(pathGet.getParent() != null && !pathGet.getFileName().toString().equals(nickName) ){
                    Path updatePath = pathGet.getParent();
                    ctx.writeAndFlush(new UpdateFIleListResponse(null,PATH_SERVER_ROOT.relativize(updatePath).toString(),fileUpdateList(updatePath)));
                }
            }


        }else if(request instanceof FileDownloadRequest){
            FileDownloadRequest fileDownloadRequest = (FileDownloadRequest) request;
            fileUpload(fileDownloadRequest);

        }else if(request instanceof FileUploadRequest) {
            FileUploadRequest fileUploadRequest = (FileUploadRequest)request;
            String srcPath = PATH_SERVER_ROOT.resolve(fileUploadRequest.getSrcPath()).toString();
            String dstPath = fileUploadRequest.getDstPath();
            String fileName = fileUploadRequest.getFileName();
            fileDownload(ctx, srcPath, dstPath, fileName);


        }else if(request instanceof EndFileDownLoadRequest){
            EndFileDownLoadRequest endFileDownLoadRequest = (EndFileDownLoadRequest) request;
            writeFiles.get(endFileDownLoadRequest.getFileName()).close();
            writeFiles.remove(endFileDownLoadRequest.getFileName());
        }else if(request instanceof DeleteFileRequest){
         DeleteFileRequest deleteFileRequest = (DeleteFileRequest) request;
         Path path = PATH_SERVER_ROOT.resolve(deleteFileRequest.getPath());
         deleteFile(path);
         ctx.writeAndFlush(new UpdateFIleListResponse(null, PATH_SERVER_ROOT.relativize(path.getParent()).toString(), fileUpdateList(path.getParent())));
        }else if(request instanceof ExitRequest){
            ctx.close();
    }

    }

    public static void fileUpload(FileDownloadRequest fileDownloadRequest) throws IOException {

        FileOutputStream fileOutputStream = null;
        FileChannel fileChannel;
        if(writeFiles.isEmpty()){
            Path path = PATH_SERVER_ROOT.resolve(fileDownloadRequest.getDstPath());
            fileOutputStream = new FileOutputStream(path.toFile(),true);
            fileChannel = fileOutputStream.getChannel();
            writeFiles.put(fileDownloadRequest.getFileName(),fileOutputStream);
            ByteBuffer buffer = ByteBuffer.allocate(15*1_000_000);
            buffer.put(fileDownloadRequest.getBytes());
            buffer.flip();
            while (buffer.hasRemaining()){
                fileChannel.write(buffer);
            }
            buffer.clear();
        }else {
            fileChannel = writeFiles.get(fileDownloadRequest.getFileName()).getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(15*1_000_000);
            buffer.put(fileDownloadRequest.getBytes());
            buffer.flip();
            while (buffer.hasRemaining()){
                fileChannel.write(buffer);
            }

            buffer.clear();

        }

    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.log(Level.INFO, "Client disconnect: " + ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();

        log.log(Level.WARNING, cause.toString());
        ctx.close();
    }

    public static List<FileInfo> fileUpdateList(Path path) throws IOException {
        return Files.list(path).map(FileInfo::new).collect(Collectors.toList());
    }

    public static void deleteFile(Path path){
        if(Files.isDirectory(path)){
            try {
                List<FileInfo> list = fileUpdateList(path);
                for (FileInfo o: list) {
                    Files.delete(path.resolve(o.getFileName()));
                }
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void fileDownload(ChannelHandlerContext channel, String srcPath, String dstPath, String fileName){
        int MB_15 = 15 * 1_000_000;
        FileChannel fileChannel = null;
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(srcPath);
            fileChannel = fileInputStream.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(MB_15);
            while (fileChannel.read(buffer) > 0) {
                buffer.flip();
                if(fileChannel.position() == fileChannel.size()){
                    int lastRead = (int) (fileChannel.size() % MB_15);
                    byte[] bytes = new byte[lastRead];
                    buffer.get(bytes, 0, lastRead);

                    channel.writeAndFlush(new FileDownloadResponse(null,fileName,dstPath, bytes));
                }else{
                    channel.writeAndFlush(new FileDownloadResponse(null, fileName,dstPath, buffer.array()));
                }
                buffer.clear();

            }
            channel.writeAndFlush(new EndFileDownLoadResponse(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fileChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
