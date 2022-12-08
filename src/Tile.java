import javafx.scene.paint.Color;		// The Color class is used to encapsulate colors in the default sRGB color space
import java.util.ArrayList;				// It is one of several classes that inherited the List interface. Unlike the arrangement, the size changes variably.
import javafx.scene.control.Button;		// Button control. When a button is pressed and released a ActionEvent is sent.
import javafx.scene.image.Image;		// The Image class represents graphical images and is used for loading images
import javafx.scene.image.ImageView;	// The ImageView is a Node used for painting images loaded with Image class
import javafx.scene.input.MouseButton;	// Mapping for Button Names
import javafx.scene.input.MouseEvent;	// When mouse event occurs, the top-most node under cursor is picked and the event is delivered to it through capturing and bubbling phases described at EventDispatcher
import javafx.scene.layout.StackPane;	// StackPane lays out its children in a back-to-front stack

// Tile class inherited StackPane
class Tile extends StackPane {
    Button btn = new Button();
    boolean hasBomb;	// Boolean variable to distinguish whether there is a mine or not
    int numBombs = 0;	// numBombs initialize to zero
    Color color = null;
    private boolean flagged = false;	// Boolean variable for determining whether to install a flag
    ArrayList<Tile> neighbours = new ArrayList<Tile>();	// Use generic programming
    private boolean active = true;

    static Image flag = new Image("kr_flag.png");	// Importing the flag image in workspace
    
    Tile(int x, int y, boolean hasBomb) {
        this.hasBomb = hasBomb;
        
        if (hasBomb) {
            Main.numBombs++;
        }

        btn.setMinHeight(35);
        btn.setMinWidth(35);

        btn.setOnMouseClicked(this::onClick);

        getChildren().addAll(btn);

        setTranslateX(x * 35);
        setTranslateY(y * 35);

    }

    private void onClick(MouseEvent e) {

        // Mouse left click
        if (e.getButton() == MouseButton.PRIMARY) {
            if(!flagged) {

                btn.setBackground(null);
                btn.setDisable(true);
                active = false;

                if (hasBomb) {
                    Main.gameOver();
                } else {
                    if (this.numBombs == 0) {
                        blankClick(this);
                    } else {
                        btn.setText(Integer.toString(numBombs));
                        btn.setTextFill(color);
                    }
                }
            }
        }
        // Mouse right click
        else {
            if (!flagged) {
                flagged = true;
                btn.setGraphic(new ImageView(flag));
                if (this.hasBomb) {
                    Main.foundBombs++;
                    if (Main.foundBombs == Main.numBombs) Main.win();
                }
            } else {
                if (hasBomb) {
                    Main.foundBombs--;
                }
                btn.setGraphic(null);
                flagged = false;
            }
        }
    }
    
    // If you click blank,
    private void blankClick(Tile tile) {
        for (int i = 0; i < tile.neighbours.size(); i++) {
            if (tile.neighbours.get(i).active) {
                tile.neighbours.get(i).btn.setDisable(true);
                tile.neighbours.get(i).btn.setGraphic(null);
                tile.neighbours.get(i).btn.setText(Integer.toString(tile.neighbours.get(i).numBombs));
                tile.neighbours.get(i).btn.setTextFill(tile.neighbours.get(i).color);
                tile.neighbours.get(i).active = false;
                if (tile.neighbours.get(i).numBombs == 0) {
                    blankClick(tile.neighbours.get(i));
                }

            }
        }
    }

}