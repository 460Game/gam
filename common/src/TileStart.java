import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class TileStart extends TileTest {

    public TileStart(int x, int y) {
        super(x, y);
    }

    @Override
    public Paint getColor() {
        return Color.RED;
    }
}
