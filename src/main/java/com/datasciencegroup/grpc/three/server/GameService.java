package com.datasciencegroup.grpc.three.server;

import com.datasciencegroup.grpc.three.models.Die;
import com.datasciencegroup.grpc.three.models.GameServiceGrpc;
import com.datasciencegroup.grpc.three.models.GameState;
import com.datasciencegroup.grpc.three.models.Player;
import io.grpc.stub.StreamObserver;

public class GameService extends GameServiceGrpc.GameServiceImplBase{

    @Override
    public StreamObserver<Die> roll(StreamObserver<GameState> responseObserver) {

        Player client = Player.newBuilder()
                .setName("client")
                .setPosition(0)
                .build();

        Player server = Player.newBuilder()
                .setName("server")
                .setPosition(0)
                .build();

        return new DieStreamingRequest(client, server, responseObserver);
    }
}
