import java.util.ArrayList;	// List 인터페이스를 상속받은 여러 클래스 중 하나. 배열과 달리 크기가 가변적으로 변함.
import java.util.Random;	// 난수 발생
import java.util.Timer;		// 실제 타이머의 기능을 수행하는 클래스
import java.util.TimerTask;	// "Timer" 클래스가 수행되어야 할 내용을 작성하는 클래스

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;	// Stage는 한 번에 하나의 Scene 가질 수 있음.
import javafx.scene.Parent;	// Scene을 생성하려면 UI의 루트 컨테이너인 javafx.scene.Parent가 필요함.
import javafx.scene.Scene;	// Scene
import javafx.scene.control.Alert;	// 경고창
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;	// 버튼
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;	
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;	// Pane : 레이아웃. 스테이지에 올려놓는 타일을 말한다. Pane을 Scene 안에 등록함.
import javafx.scene.layout.VBox;	// VBox : 수직으로 배치
import javafx.scene.paint.Color;

// 메인클래스는 반드시 Application을 상속을 해서 사용해야 함.
public class Main extends Application {
    private static double bomb = 0.1;	// easy가 디폴트 값
    private static int gridSize = 10;	// 10*10이 디폴트 값
    private static Tile[][] grid;	// 타일 2차원 배열
    private static Stage main;
    private static VBox vbox = new VBox();	// vbox 생성

    static int numBombs, foundBombs;
    private static int secondsPassed;
    public static Timer timer;
    static Image mine = new Image("mine.png");

    // start 메소드는 윈도우를 보여주는 역할임.
    @Override
    public void start(Stage stage) {

        grid = new Tile[gridSize][gridSize];	// 타일 생성

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                secondsPassed++;
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 1000, 1000);	// 1000 = 1초. 1초마다 task 수행
        
        main = stage;
        
        // X(닫기) 버튼 누르면 나가지게 함
        main.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        main.getIcons().add(mine);
        main.setTitle("Minesweeper(지뢰찾기) - By Team3");
        
        // 컨테이너 상단에 배치되어, 다양한 작업을 선택하도록 해줌.
        // 메뉴 아이템으로 MenuItem, CheckMenuItem 등 추가 가능. 서브 메뉴 갖는 Menu도 추가 가능.
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
        
        // 어떻게 플레이하는지 알려주는 메뉴 아이템
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
        
        // 타일 사이즈 조정을 위한 메뉴
        Menu menuSize = new Menu("Size");
        MenuItem ten = new MenuItem("10x10");
        ten.setOnAction(e -> {
            gridSize = 10;	// 10*10
            reload();
        });
        MenuItem fifteen = new MenuItem("15x15");
        fifteen.setOnAction(e -> {
            gridSize = 15;	// 15*15
            reload();
        });
        MenuItem twenty = new MenuItem("20x20");
        twenty.setOnAction(e -> {
            gridSize = 20;	// 20*20
            reload();
        });
        menuSize.getItems().addAll(ten, fifteen, twenty);
        
        // 게임 난이도 조절을 위한 메뉴
        Menu menuDifficulty = new Menu("Difficulty");
        MenuItem easy = new MenuItem("Easy - 10% Bombs");
        easy.setOnAction(e -> {
            bomb = 0.1;	// 타일 수의 10%가 지뢰
            reload();
        });
        MenuItem medium = new MenuItem("Medium - 15% Bombs");
        medium.setOnAction(e -> {
            bomb = 0.15;	// 타일 수의 15%가 지뢰
            reload();
        });
        MenuItem hard = new MenuItem("Hard - 20% Bombs");
        hard.setOnAction(e -> {
            bomb = 0.2;	// 타일 수의 20%가 지뢰
            reload();
        });
        menuDifficulty.getItems().addAll(easy, medium, hard);
        /*
         * Menu menuHint = new Menu("Item");
        MenuItem small = new MenuItem("Small Item");
        small.setOnAction(e -> {
        	numBombs--;
        	foundBombs--;
        });
        MenuItem big = new MenuItem("Big Item");
        big.setOnAction(e -> {
        	
        });
        menuHint.getItems().addAll(small, big);
         */
        
        
        menuBar.getMenus().addAll(menuFile, menuSize, menuDifficulty);

        vbox.getChildren().addAll(menuBar, createContent());

        Scene scene = new Scene(vbox);

        scene.getStylesheets().add("style.css");
        main.setScene(scene);
        main.setResizable(false);
        main.sizeToScene();
        main.show();
    }
    
    // 게임 다시 로드
    private static void reload() {
    	// 타일 설정
        grid = new Tile[gridSize][gridSize];

        secondsPassed = 0;

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

    // 게임 내용
    private static Parent createContent() {

        // 지뢰의 수와 찾은 지뢰의 수 0으로 초기화
        numBombs = 0;
        foundBombs = 0;

        Pane root = new Pane();
        root.setPrefSize(gridSize * 35, gridSize * 35);

        // 타일과 버튼 생성
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {

                Tile tile = new Tile(x, y, false);
                grid[x][y] = tile;
                root.getChildren().add(tile);
            }
        }

        // 지뢰를 타일에 랜덤으로 배치
        for(int i = 0; i < gridSize*gridSize*bomb; i++){
            Random rand = new Random();
            int x = rand.nextInt(gridSize);
            int y = rand.nextInt(gridSize);

            if(grid[x][y].hasBomb){
                if (i == 0) {
                    i = 0;
                } else {
                    i--;
                }
            }
            else{
                grid[x][y].hasBomb = true;
                numBombs++;
            }
        }

        // 이웃에 지뢰가 얼마나 있는지 타일에 값 추가 & 색 설정
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

                    if (newX >= 0 && newX < gridSize && newY >= 0 && newY < gridSize) {
                        neighbours.add(grid[newX][newY]);
                        if (grid[newX][newY].hasBomb) {
                            numNeighboursBomb++;
                        }
                    }
                }

                grid[x][y].numBombs = numNeighboursBomb;
                grid[x][y].neighbours = neighbours;

                Color[] colors = { null, Color.BLUE, Color.GREEN, Color.RED, Color.DARKBLUE, Color.DARKRED, Color.CYAN,
                        Color.BLACK, Color.DARKGRAY };

                grid[x][y].color = colors[grid[x][y].numBombs];

            }
        }
        return root;
    }

    // 지뢰를 눌렀을 때 -> 게임오버 처리
    public static void gameOver() {
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
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

    // 모든 지뢰를 다 찾았을 때 -> 승리 표시
    public static void win() {

        Image winTrophy = new Image("kr_Win.png");
        ImageView winTrophyView = new ImageView(winTrophy);
        winTrophyView.setSmooth(true);
        winTrophyView.setPreserveRatio(true);
        winTrophyView.setFitHeight(100);

        Alert win = new Alert(AlertType.CONFIRMATION);
        ((Stage) win.getDialogPane().getScene().getWindow()).getIcons().add(mine);
        win.setTitle("Win! 승리!");
        win.setGraphic(winTrophyView);
        win.setHeaderText("Congratulations!\n" + "축하합니다!");
        win.setContentText("You found all the bombs in " + secondsPassed + " seconds.\n" + "당신은 모든 지뢰를 " + secondsPassed + "초만에 찾았습니다.");
        win.showAndWait();
        reload();
    }

    public static void main(String[] args) {
        launch(args);
    }

}