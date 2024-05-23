package com.bidblast.gRPC;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.proto.video.Video.VideoRequest;
import com.proto.video.Video.VideoChunkResponse;
import com.proto.video.VideoServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import okio.ByteString;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {
    private ManagedChannel channel;
    private VideoServiceGrpc.VideoServiceStub videoServiceStub;
    private Handler handler;
    private List<byte[]> videoFragments;

    public Client(String host, int port, Handler handler) {
        this.handler = handler;
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        videoServiceStub = VideoServiceGrpc.newStub(channel);
        videoFragments = new ArrayList<>();
    }

    public void streamVideo(int videoId) {
        VideoRequest request = VideoRequest.newBuilder().setVideoId(videoId).build();

        videoServiceStub.streamVideo(request, new StreamObserver<VideoChunkResponse>() {
            @Override
            public void onNext(VideoChunkResponse value) {
                byte[] videoChunk = value.getData().toByteArray();
                if (videoChunk.length > 0) {
                    Log.d("CLIENT", "Received video chunk: " + Arrays.toString(videoChunk) + " " + videoChunk.length);
                    videoFragments.add(videoChunk);
                } else {
                    Log.d("CLIENT", "Received video chunk is empty");
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e("CLIENT", "Error fetching video: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                Log.d("CLIENT", "Video fetching completed");
                Log.d("CLIENT", "Total fragments received: " + videoFragments.size());
                for (byte[] fragment : videoFragments) {
                    Log.d("CLIENT", "Fragment size: " + fragment.length);
                }
                sendVideoToFragment();
            }

        });
    }

    private void sendVideoToFragment() {
        Message message = handler.obtainMessage(1, videoFragments);
        message.sendToTarget();
    }

    public void shutdown() {
        channel.shutdown();
    }
}