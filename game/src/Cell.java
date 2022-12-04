public class Cell extends ZeroCell {
    private boolean player;
    public boolean getPlayer() {
        return player;
    }
    public void changePlayer() {
        player = !player;
    }
    public Cell(Boolean player) {
        this.player = player;
    }
    public Cell(Cell other) {
        this(other.getPlayer());
    }

    @Override
    public String toString() {
        if (player) {
            return "1";
        }
        return "2";
    }
}