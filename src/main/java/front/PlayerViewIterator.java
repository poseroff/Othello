package front;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PlayerViewIterator implements Iterator<PlayerView> {

    private Collection<PlayerView> players;
    private Iterator<PlayerView> playerIterator;

    public PlayerViewIterator(Collection<PlayerView> players){
        this.players = players;
        this.playerIterator = players.iterator();
    }

    @Override
    public boolean hasNext() {
        return !players.isEmpty();
    }

    @Override
    public PlayerView next() {
        if (!hasNext())
            throw new NoSuchElementException();
        if (!playerIterator.hasNext())
            playerIterator = players.iterator();
        return playerIterator.next();
    }
}
