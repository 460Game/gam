package game.view;

import game.model.game.model.ClientGameModel;
import game.model.game.model.GameModel;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import util.Const;

import static images.Images.CURSOR_IMAGE;

/** panel in bottem middle with actions user can take
 */
public class ActionPanel extends Region {
    private GameModel model;

    public ActionPanel() {}

    public ActionPanel(ClientGameModel model) {
        this.model = model;
        this.setCursor(new ImageCursor(CURSOR_IMAGE, 0, 0));
        this.setBackground(model.getTeam().getPanelBG());
    }

    public void draw() {
    }
    public void stop() {
        this.model = null;
    }
}
