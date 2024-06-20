package com.bidblast.grpc;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.proto.video.Video.VideoRequest;
import com.proto.video.Video.VideoChunkResponse;
import com.proto.video.VideoServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private static ManagedChannel channel = null;
    private final VideoServiceGrpc.VideoServiceStub videoServiceStub;
    private static final String GRPC_URL = "192.168.100.164";
    private static final int GRPC_PORT = 3001;
    private final Handler handler;

    public Client(Handler handler) {
        this.handler = handler;
        channel = ManagedChannelBuilder.forAddress(GRPC_URL, GRPC_PORT)
                .usePlaintext()
                .build();
        videoServiceStub = VideoServiceGrpc.newStub(channel);
    }

    public void streamVideo(int videoId) {
        VideoRequest request = VideoRequest.newBuilder().setVideoId(videoId).build();

        videoServiceStub.streamVideo(request, new StreamObserver<VideoChunkResponse>() {
            @Override
            public void onNext(VideoChunkResponse value) {
                byte[] videoChunk = value.getData().toByteArray();
                List<byte[]> videoFragments = new ArrayList<>();
                videoFragments.add(videoChunk);
                sendVideoChunksToHandler(videoFragments);
            }

            @Override
            public void onError(Throwable t) {
                Log.e("CLIENT", "Error fetching video: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                Log.d("CLIENT", "Video fetching completed");
            }
        });
    }

    private void sendVideoChunksToHandler(List<byte[]> videoFragments) {
        Message message = handler.obtainMessage(1, videoFragments);
        message.sendToTarget();
    }

    public static boolean getChannelStatus() {
        return channel != null;
    }

    public void shutdown() {
        channel.shutdown();
    }
}