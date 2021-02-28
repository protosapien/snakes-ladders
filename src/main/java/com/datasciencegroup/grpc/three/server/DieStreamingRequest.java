package com.datasciencegroup.grpc.three.server;

import com.datasciencegroup.grpc.three.models.Die;
import com.datasciencegroup.grpc.three.models.GameState;
import com.datasciencegroup.grpc.three.models.Player;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ThreadLocalRandom;

public class DieStreamingRequest implements StreamObserver<Die> {

    private Player client;
    private Player server;
    private StreamObserver<GameState> gameStateStreamObserver;

    public DieStreamingRequest(Player client, Player server, StreamObserver<GameState> gameStateStreamObserver) {
        this.client = client;
        this.server = server;
        this.gameStateStreamObserver = gameStateStreamObserver;
    }

    @Override
    public void onNext(Die die) {
        // when we get a Die value it means it's the client player
        // we are updating the client player
        this.client = this.getNewPlayerPosition(this.client, die.getValue());

        if (this.client.getPosition() < 100) {
            // the server can throw the die now
            this.server = this.getNewPlayerPosition(this.server, ThreadLocalRandom.current().nextInt(1, 7));
        }

        // now we transmit the game state; the client doesn't send a Die value
        // until it gets this game state from the server
        // the GameState contains both players names and their position
        this.gameStateStreamObserver.onNext( this.getGameState() );
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        this.gameStateStreamObserver.onCompleted();
    }

    private GameState getGameState() {
        return GameState.newBuilder()
                .addPlayers(this.client)
                .addPlayers(this.server)
                .build();
    }

    private Player getNewPlayerPosition(Player player, int dieValue) {

        int position = player.getPosition() + dieValue;

        // apply the game rules from the MAP
        position = SnakesAndLaddersMap.getPosition(position);

        // if the player is at position 100, they cannot move any further
        // so simply return the player without updating its position
        // also there are no setPosition() setter in the player object so we need to get the builder
        // for the player so we can set the position
        if (position <= 100) {
            player = player.toBuilder()
                    .setPosition(position)
                    .build();
        }
        // finally we return the player with the updated position
        return player;
    }
}
