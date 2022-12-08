import java.util.ArrayList;	// It is one of several classes that inherited the List interface. Unlike the arrangement, the size changes variably.
import java.util.Random;	// Random number generation
import java.util.Timer;		// It allows specific tasks to be repeatedly executed in the background for a specific time or period of time.
import java.util.TimerTask;	// It is a class that becomes a unit of a task when scheduling using a timer

import javafx.application.Application;	// Application class from which JavaFX applications extend.
import javafx.application.Platform;		// Application platform support class.
import javafx.stage.Stage;				// Stage class is the top level JavaFX container. Stage can have one Scene at a time.
import javafx.scene.Parent;				// To create a scene, the root container of the UI, 'javafx.scene.Parent' is required.
import javafx.scene.Scene;				// The container for all content in a scene graph
import javafx.scene.control.Alert;		// Support for a number of pre-built dialog types that can be easily shown to users to prompt for a response.
import javafx.scene.control.Alert.AlertType; // When creating an Alert instance, users must pass in an Alert.AlertType enumeration value
import javafx.scene.control.ButtonType;	// The ButtonType class is used as part of the JavaFX Dialog API to specify which buttons should be shown to users in the dialogs
import javafx.scene.control.Menu; 		// A popup menu of actionable items which is displayed to the user only upon request
import javafx.scene.control.MenuBar; 	// A MenuBar control traditionally is placed at the very top of the user interface, and embedded within it are Menus
import javafx.scene.control.MenuItem; 	// MenuItem is intended to be used in conjunction with Menu to provide options to users
import javafx.scene.image.Image; 		// The Image class represents graphical images and is used for loading images
import javafx.scene.image.ImageView; 	// The ImageView is a Node used for painting images loaded with Image class
import javafx.scene.layout.Pane;		// Base class for layout panes which need to expose the children list as public so that users of the subclass can freely add/remove children
import javafx.scene.layout.VBox;		// VBox lays out its children in a single vertical column
import javafx.scene.paint.Color;		// The Color class is used to encapsulate colors in the default sRGB color space

// The main class must inherit and use the application.
public class Main extends Application {
    private static double bomb = 0.1;	// Easy is the default value
    private static int gridSizeX = 10;	// Number of rows. Default is 10.
    private static int gridSizeY = 10;	// Number of columns. Default is 10.
    private static Tile[][] grid;		// a two-dimensional array
    private static Stage main;			// Stage declaration
    private static VBox vbox = new VBox();	// Create Vbox

    static int numBombs, foundBombs;	// variables related to the number of mines
    private static int secondsPassed;	// Variable required for Timer Usage
    public static Timer timer;			// Create Timer Object
    static Image mine = new Image("mine.png");	// Importing the mine image in workspace

    // The start method is the role of showing windows
    @Override
    public void start(Stage stage) {
        grid = new Tile[gridSizeX][gridSizeY];	// Create Tile
        
        // Create TimerTask
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                secondsPassed++;	// When it runs, the time is ++
            }
        };
        timer = new Timer();		// Declare Timer Object
        timer.scheduleAtFixedRate(task, 1000, 1000);	// 1000 = 1 second. Performing a task every 1 second.
        
        main = stage;
        
        // Press the X button to exit
        main.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        main.getIcons().add(mine);	// Set Mine Icon
        main.setTitle("Minesweeper(지뢰찾기) - By Team3");	// Set Title
        
        // It is placed at the top of the container, allowing you to select various tasks.
        // Menu Item, Check Menu Item, etc. can be added as a menu item. Menu with sub-menu can also be added.
        MenuBar menuBar = new MenuBar();

        Menu menuFile = new Menu("Etc");
        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> {
            Alert aboutAlert = new Alert(Alert.AlertType.NONE,
                    "Created by Team3. \n" + "Java Programming Project \n" + "<팀원> 김민재, 이채린, 박주을, 임지수", ButtonType.CLOSE);
            aboutAlert.setTitle("About");
            aboutAlert.setHeaderText("Minesweeper(지뢰찾기)");
            ((Stage) aboutAlert.getDialogPane().getScene().getWindow()).getIcons().add(mine);
            aboutAlert.showAndWait();
        });
        
        // It's a menu item that tells you how to play
        MenuItem help = new MenuItem("Help");
        help.setOnAction(e -> {
            Alert helpAlert = new Alert(Alert.AlertType.INFORMATION,
                    "The aim of minesweeper is to identify all the sqaures which contain mines.\n\n"
                            + "Left click on a square to reveal a number. This number indicates how many of the adjacent squares contain mines. By using these numbers you can deduce which sqaures contain mines. \n\n"
                            + "Right click on a square to mark it as containing a mine. You can right click the sqaure again to unmark it if you made a mistake.\n\n"
                            + "After all mines have successfully been marked the game is over and you win! Be careful though. Left clicking a square with a mine will result in a game over.");
            helpAlert.setTitle("Help");
            helpAlert.setHeaderText("How to play");
            helpAlert.showAndWait();
        });
        
        menuFile.getItems().addAll(about, help);
        
        // Menu for resizing tiles
        Menu menuSize = new Menu("Size");
        
        MenuItem five = new MenuItem("5x5");
        five.setOnAction(e -> {
            gridSizeX = 5; // 5*5
            gridSizeY = 5;
            reload();
        });
        MenuItem ten = new MenuItem("10x10");
        ten.setOnAction(e-> {
            gridSizeX = 10;	// 10*10
            gridSizeY = 10;
            reload();
        });
        MenuItem fifteen = new MenuItem("15x15");
        fifteen.setOnAction(e -> {
            gridSizeX = 15;	// 15*15
            gridSizeY = 15;
            reload();
        });
        /*
         * MenuItem fiftwen = new MenuItem("15x20");
        fiftwen.setOnAction(e -> {
            gridSizeX = 15;	// 15*20
            gridSizeY = 20;
            reload();
        });
         * */
        MenuItem twenty = new MenuItem("20x20");
        twenty.setOnAction(e -> {
            gridSizeX = 20;	// 20*20
            gridSizeY = 20;
            reload();
        });
        
        menuSize.getItems().addAll(five, ten, fifteen, twenty);
        
        // Menu for controlling game difficulty
        Menu menuDifficulty = new Menu("Difficulty");
        MenuItem easy = new MenuItem("Easy - 10% Bombs");
        easy.setOnAction(e -> {
            bomb = 0.1;	// Set 10% of tile count as mine count
            reload();
        });
        MenuItem medium = new MenuItem("Medium - 15% Bombs");
        medium.setOnAction(e -> {
            bomb = 0.15;	// Set 15% of tile count as mine count
            reload();
        });
        MenuItem hard = new MenuItem("Hard - 20% Bombs");
        hard.setOnAction(e -> {
            bomb = 0.2;	// Set 20% of tile count as mine count
            reload();
        });
        menuDifficulty.getItems().addAll(easy, medium, hard);
        
        menuBar.getMenus().addAll(menuFile, menuSize, menuDifficulty);

        vbox.getChildren().addAll(menuBar, createContent());

        Scene scene = new Scene(vbox);

        scene.getStylesheets().add("style.css");	// Use style.css
        main.setScene(scene);
        main.setResizable(false);
        main.sizeToScene();
        main.show();
    }
    
    // Reload the game
    private static void reload() {
    	// Tile Settings
        grid = new Tile[gridSizeX][gridSizeY];

        secondsPassed = 0;	// Initialize to zero

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                secondsPassed++;
            };
        };
        timer.cancel();
        timer = new Timer();
        timer.schedule(task, 1000, 1000);

        vbox.getChildren().remove(1);
        vbox.getChildren().add(createContent());
        main.sizeToScene();
    }

    // Game contents
    private static Parent createContent() {

        // Initialize to zero the number of mines and the number of mines found
        numBombs = 0;
        foundBombs = 0;

        Pane root = new Pane();
        root.setPrefSize(gridSizeX * 35, gridSizeY * 35);	// Convenience method for overriding the region's computed preferred width and height

        // Creating Tiles and Buttons
        for (int y = 0; y < gridSizeY; y++) {
            for (int x = 0; x < gridSizeX; x++) {

                Tile tile = new Tile(x, y, false);
                grid[x][y] = tile;
                root.getChildren().add(tile);
            }
        }

        // Place mines randomly on tiles
        for(int i = 0; i < gridSizeX*gridSizeY*bomb; i++){
            Random rand = new Random();
            int x = rand.nextInt(gridSizeX);
            int y = rand.nextInt(gridSizeY);
            // If there's a mine in grid[x][y]
            if(grid[x][y].hasBomb){
                if (i == 0) {
                    i = 0;
                } else {
                    i--;
                }
            }
            // If grid[x][y] doesn't have a mine
            else{
                grid[x][y].hasBomb = true;
                numBombs++;
            }
        }

        // Add value & color to tiles for how many mines are in the neighborhood
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {

                int numNeighboursBomb = 0;

                ArrayList<Tile> neighbours = new ArrayList<Tile>();

                int[] neighboursLocs = new int[] { -1, -1, -1, 0, -1, 1, 0, -1, 0, 1, 1, -1, 1, 0, 1, 1 };

                for (int i = 0; i < neighboursLocs.length; i++) {
                    int dx = neighboursLocs[i];
                    int dy = neighboursLocs[++i];

                    int newX = x + dx;
                    int newY = y + dy;

                    if (newX >= 0 && newX < gridSizeX && newY >= 0 && newY < gridSizeY) {
                        neighbours.add(grid[newX][newY]);
                        if (grid[newX][newY].hasBomb) {
                            numNeighboursBomb++;
                        }
                    }
                }

                grid[x][y].numBombs = numNeighboursBomb;
                grid[x][y].neighbours = neighbours;
                // Color Settings
                Color[] colors = { null, Color.BLUE, Color.GREEN, Color.RED, Color.DARKBLUE, Color.DARKRED, Color.CYAN,
                        Color.BLACK, Color.DARKGRAY };
                
                grid[x][y].color = colors[grid[x][y].numBombs];

            }
        }
        return root;
    }

    // When Mines are pressed -> Game Over Processing
    public static void gameOver() {
        for (int y = 0; y < gridSizeY; y++) {
            for (int x = 0; x < gridSizeX; x++) {
                if (grid[x][y].hasBomb) {
                    grid[x][y].btn.setGraphic(new ImageView(mine));
                    grid[x][y].btn.setDisable(true);
                }
            }
        }

        Alert gameOver = new Alert(AlertType.INFORMATION);
        ((Stage) gameOver.getDialogPane().getScene().getWindow()).getIcons().add(mine);
        gameOver.setTitle("Game Over! 게임 오버!");
        gameOver.setGraphic(new ImageView(mine));
        gameOver.setHeaderText("Bomb Exploded!\n"+"지뢰가 터졌습니다!");
        gameOver.setContentText(
                "Oh no! You clicked on a bomb and caused all the bombs to explode! Better luck next time.\n"+"지뢰를 발견하여 모든 지뢰들이 터졌습니다. 다음번엔 더 잘 해보세요.");
        gameOver.showAndWait();

        reload();

    }

    // When all mines are found -> show victory
    public static void win() {
    	
        Image kr_Win = new Image("kr_Win.png");
        ImageView kr_WinView = new ImageView(kr_Win);
        kr_WinView.setSmooth(true);
        kr_WinView.setPreserveRatio(true);
        kr_WinView.setFitHeight(100);

        Alert win = new Alert(AlertType.CONFIRMATION);
        ((Stage) win.getDialogPane().getScene().getWindow()).getIcons().add(mine);
        win.setTitle("Win! 승리!");
        win.setGraphic(kr_WinView);
        win.setHeaderText("Congratulations!\n" + "축하합니다!");
        win.setContentText("You found all the bombs in " + secondsPassed + " seconds.\n" + "당신은 모든 지뢰를 " + secondsPassed + "초만에 찾았습니다.");
        win.showAndWait();
        reload();
    }

    public static void main(String[] args) {
        launch(args);
    }

}