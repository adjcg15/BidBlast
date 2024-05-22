package com.bidblast.gRPC;

import android.util.Log;

import com.proto.video.Video.VideoRequest;
import com.proto.video.Video.VideoChunkResponse;
import com.proto.video.VideoServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
public class Client {
    private ManagedChannel channel;
    private VideoServiceGrpc.VideoServiceStub videoServiceStub;

    public Client(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        videoServiceStub = VideoServiceGrpc.newStub(channel);
    }

    public void streamVideo(int videoId) {
        VideoRequest request = VideoRequest.newBuilder().setVideoId(videoId).build();
        Log.d("ENTRE", "SI ENTREEEEEEE");

        videoServiceStub.streamVideo(request, new StreamObserver<VideoChunkResponse>() {
            @Override
            public void onNext(VideoChunkResponse value) {
                byte[] videoChunk = value.getData().toByteArray();
                Log.d("STREAM", "Received video chunk with size: " + videoChunk.length);
            }

            @Override
            public void onError(Throwable t) {
                Log.e("STREAM", "Error streaming video: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                Log.d("STREAM", "Video streaming completed");
            }
        });
    }

    public void shutdown() {
        channel.shutdown();
    }
}
