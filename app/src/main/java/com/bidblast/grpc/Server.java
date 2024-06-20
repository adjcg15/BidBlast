package com.bidblast.grpc;

import com.proto.video.Video;
import com.proto.video.VideoServiceGrpc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class Server {

    private final ManagedChannel channel;
    private final VideoServiceGrpc.VideoServiceStub asyncStub;
    private static final String GRPC_BASE_URL = "192.168.1.71";
    private static final int GRPC_PORT = 3001;

    public Server() {
        this.channel = ManagedChannelBuilder.forAddress(GRPC_BASE_URL, GRPC_PORT)
                .usePlaintext()
                .build();
        this.asyncStub = VideoServiceGrpc.newStub(channel);
    }

    public void uploadVideo(File videoFile, int auctionId, String mimeType) throws Exception {
        StreamObserver<Video.VideoUploadRequest> requestObserver = asyncStub.uploadVideo(new StreamObserver<Video.VideoUploadResponse>() {
            @Override
            public void onNext(Video.VideoUploadResponse response) {
                System.out.println("Upload response: " + response.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("Upload completed.");
            }
        });

        try (InputStream inputStream = new FileInputStream(videoFile)) {
            byte[] buffer = new byte[256 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                System.out.println("Sending chunk: " + bytesRead + " bytes");
                Video.VideoUploadRequest request = Video.VideoUploadRequest.newBuilder()
                        .setAuctionId(auctionId)
                        .setMimeType(mimeType)
                        .setContent(com.google.protobuf.ByteString.copyFrom(buffer, 0, bytesRead))
                        .setName(videoFile.getName())
                        .build();
                requestObserver.onNext(request);
            }
        } catch (Exception e) {
            requestObserver.onError(e);
            throw e;
        }

        requestObserver.onCompleted();
    }

    public void shutdown() {
        channel.shutdown();
    }
}
