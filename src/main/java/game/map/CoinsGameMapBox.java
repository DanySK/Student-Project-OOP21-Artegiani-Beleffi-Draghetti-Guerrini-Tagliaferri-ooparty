package game.map;

import java.util.Random;

import game.player.Player;

public class CoinsGameMapBox extends GameMapBoxImpl {
    private int coinsNumber;

    public CoinsGameMapBox() {
        super();
        this.generateNewCoins();
    }

    @Override
    public final int getCoinsNumber() {
        return this.coinsNumber;
    }

    @Override
    public final void receiveCoins(final Player p) {
        p.earnCoins(this.coinsNumber);
        this.generateNewCoins();
    }

    private void generateNewCoins() {
        this.coinsNumber = new Random().nextInt(GameMapBoxImpl.MAX_COINS);
    }
}
