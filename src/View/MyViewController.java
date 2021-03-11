package View;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;


public class MyViewController implements IView, Initializable, Observer {




    @FXML
    TextField maze_rows;
    @FXML
    TextField maze_col;
    @FXML
    public SolDisplayer solutionDisplayer;
    @FXML
    public MazeDisplayer mazeDisplayer;
    @FXML
    public Label lbl_player_row;
    @FXML
    public Label lbl_player_column;
    @FXML
    public Button solver, saveGameButton;
    @FXML
    public MenuItem menuSave, menuExit ;
    @FXML
    public javafx.scene.control.ChoiceBox<String> choose_char;
    @FXML
    public AnchorPane Apane;



    StringProperty update_player_position_row = new SimpleStringProperty();
    StringProperty update_player_position_col = new SimpleStringProperty();

    private MyViewModel viewModel;


    public Stage stage_two = new Stage();
    @FXML
    private Button Exit2;
    @FXML
    private BorderPane pane;


    private Maze maze;




    public void closeStage2() {
        //System.out.println("cancel");
        Stage stage = (Stage) Exit2.getScene().getWindow();
        // do what you have to do
        stage.close();

    }

    public void generateMaze(ActionEvent event) throws IOException {
        if(this.maze_rows.getText().equals("")||this.maze_col.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Must enter both fields");
            alert.show();
            event.consume();
        }
        else if (!this.maze_rows.getText().chars().unordered().parallel().allMatch((i -> '0' <= i && '9' >= i)) || !(this.maze_col.getText().chars().unordered().parallel().allMatch((i -> '0' <= i && '9' >= i)))) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Rows and Cols must be of Numeric Value!!!");
            alert.show();
            event.consume();
        }
        else if(Integer.valueOf(this.maze_rows.getText()) < 3 || Integer.valueOf(this.maze_col.getText()) < 3 ){
            Alert alert = new Alert(Alert.AlertType.WARNING, "THIS IS WAYYYY TOO EASY FOR U!!!\nChoose a bigger size...");
            alert.show();
            event.consume();
        }
        else if(Integer.valueOf(this.maze_rows.getText()) > 500 || Integer.valueOf(this.maze_col.getText()) > 500 ){
            Alert alert = new Alert(Alert.AlertType.WARNING, "W O W!!!\nU R a brave person\nUnfortunatlly, a maze this size is too BIG to play \n try a smaller size");
            alert.show();
            event.consume();
        }
        else {

            int rows = Integer.valueOf(this.maze_rows.getText());
            int cols = Integer.valueOf(this.maze_col.getText());
            viewModel.generateMaze(rows, cols);
            mazeDisplayer.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    keyPressed(event);
                }
            });

        }
        solver.setDisable(false);
        saveGameButton.setDisable(false);
        menuSave.setDisable(false);
    }

    public void keyPressed(KeyEvent keyEvent) {

        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();

    }

   public void mouseMove(MouseEvent mouseEvent){
        if(maze != null){
            double xPos = mouseEvent.getX()/(mazeDisplayer.getWidth()/maze.getNumOfCols());
            double yPos = mouseEvent.getY()/(mazeDisplayer.getHeight()/maze.getNumOfRows());

            if(xPos > viewModel.getColChar()+1){
                viewModel.moveCharacter(KeyCode.NUMPAD6);
            }
            else if(xPos < viewModel.getColChar()){
                viewModel.moveCharacter(KeyCode.NUMPAD4);
            }
            else if(yPos < viewModel.getRowChar()){
                viewModel.moveCharacter(KeyCode.NUMPAD8);
            }
            else if(yPos > viewModel.getRowChar()+1){
                viewModel.moveCharacter(KeyCode.NUMPAD2);
            }
        }
   }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbl_player_row.textProperty().bind(update_player_position_row);
        lbl_player_column.textProperty().bind(update_player_position_col);
    }


    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }


    public String get_update_player_position_row() {
        return update_player_position_row.get();
    }

    public void set_update_player_position_row(String update_player_position_row) {
        this.update_player_position_row.set(update_player_position_row);
    }

    public String get_update_player_position_col() {
        return update_player_position_col.get();
    }

    public void set_update_player_position_col(String update_player_position_col) {
        this.update_player_position_col.set(update_player_position_col);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }


    public void drawMaze() {
        mazeDisplayer.drawMaze(maze);
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MyViewModel) {
            if (arg.equals(1) || arg.equals(4)) //generate maze
            {
                this.maze = viewModel.getMaze();
                mazeDisplayer.set_player_position_without_draw(viewModel.getRowChar(), viewModel.getColChar());
                /*mazeDisplayer.setHeight(685.0);
                mazeDisplayer.setWidth(700.0);
                solutionDisplayer.setHeight(685.0);
                solutionDisplayer.setWidth(700.0);*/
                solutionDisplayer.setVisible(false);
                mazeDisplayer.drawMaze(this.maze);

            }
            else if (arg.equals(2)||arg.equals(5)) //move char
            {
                if(arg.equals(5))
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION,"This button is not used for movement. \n You can check out the Help menu to see how to move in the maze");
                    alert1.show();
                }
                else{
                    int rowFromViewModel = viewModel.getRowChar();
                    int colFromViewModel = viewModel.getColChar();
                    set_update_player_position_row(rowFromViewModel + "");
                    set_update_player_position_col(colFromViewModel + "");
                    this.mazeDisplayer.set_player_position(rowFromViewModel, colFromViewModel);
                    if (this.mazeDisplayer.getCol_player() == this.mazeDisplayer.getM().getGoalPosition().getColumnIndex() && this.mazeDisplayer.getRow_player() == this.mazeDisplayer.getM().getGoalPosition().getRowIndex()) {
                        playEndMusic(1);
                        //mediaPlayer.pause();
                        endmediaPlayer.play();

                        try {
                            this.Win_window();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }
            }
            else if (arg.equals(3)) //get solution
            {
                solutionDisplayer.setMaze(this.maze);
                Solution sol = viewModel.getSolution();
                solutionDisplayer.setSolution(sol.getSolutionPath());
            }
        }

    }



    @FXML
    Button check;
    static MediaPlayer mediaPlayer;
    static int flag = 0;

    public void playMusic() {
        if (mediaPlayer == null) {
            String ssound = getClass().getResource("..//Clips/Super Mario Bros. Theme Song.mp3").toExternalForm();
            Media sound = new Media(ssound);
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setVolume(0.1);
        }

        if (flag == 0) {
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
            flag = 1;
        } else {
            mediaPlayer.pause();
            flag = 0;
        }
    }

    public void SolveMaze(ActionEvent actionEvent) {
        //do debug on this function and see what happenes after u press the button

        if (actionEvent.getEventType() == ActionEvent.ACTION) {
            if (!solutionDisplayer.isVisible()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Solving maze..");
                viewModel.solveMaze(this.maze);
                solutionDisplayer.setVisible(true);

                alert.show();
            } else {
                solutionDisplayer.setVisible(false);
            }
        }
    }

    public void SaveMaze() {
        if (viewModel.getMaze() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("No File To Save");
            alert.show();
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Game");
            File f = fileChooser.showSaveDialog(new Stage());
            if (f != null) {
                viewModel.saveMaze(f);
            }
        }

    }

    public void LoadMaze() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Game");
        File f = fileChooser.showOpenDialog(new Stage());
        if (f != null) {
            boolean x =viewModel.LoadMaze(f);
            if(x == false){
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please choose a Maze File");
                alert.show();
            }
        }
        mazeDisplayer.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                keyPressed(event);
            }
        });
        solver.setDisable(false);
        menuSave.setDisable(false);
        saveGameButton.setDisable(false);

    }


    int incrementx = 0, incrementy = 0;
    public void Win_window() throws IOException {

        //public ButtonType("HARDER CHALLENGE");
        viewModel.setPlayer(maze.getStartPosition().getRowIndex(),maze.getStartPosition().getColumnIndex());
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "You have won the game!!! \n Would you like to try a Harder challenge?!",
                ButtonType.YES,
                ButtonType.NO
                /*ButtonType.*/);

        alert.setHeaderText("congratulations!!!");
        alert.setTitle("Maze Game 2020");
        //alert.setOnCloseRequest(event -> alertClose());

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.YES) {
/*            incrementx+=3;
            incrementy+=3;*/
            int rows=viewModel.getMaze().getNumOfRows();
            int cols = viewModel.getMaze().getNumOfCols();
/*            int rows = Integer.valueOf(this.maze_rows.getText());
            int cols = Integer.valueOf(this.maze_col.getText());*/
            viewModel.generateMaze(rows+3/*incrementx*/,cols+3/*incrementy*/);
        }
        else{
            alert.close();
            incrementx = 0;
            incrementy = 0;
        }
        //alert.showAndWait();
    }

    public void Zoom(ScrollEvent scrollEvent) {
        mazeDisplayer.zoom(scrollEvent);
        solutionDisplayer.zoom(scrollEvent);
    }


@FXML
MenuItem properties;
    public void properties(ActionEvent actionEvent){

        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Properties.fxml").openStream());
            stage.setTitle("Properties");
            stage.setScene(new Scene(root, 800, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    MenuItem help;
    public void help(ActionEvent actionEvent){

        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Help.fxml").openStream());
            stage.setTitle("Help");
            stage.setScene(new Scene(root, 800, 600));
            stage.initModality(Modality.APPLICATION_MODAL);

            /*String image = getClass().getResource("/Resources/Images/properties").toExternalForm();
            String style = "-fx-background-image: url('" + image + "');";
            (root).setStyle(style);*/

            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    MenuItem about;
    public void about(ActionEvent actionEvent){

        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("about.fxml").openStream());
            stage.setTitle("About");
            stage.setScene(new Scene(root, 800, 600));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void CloseApp() throws IOException {
        Alert alert= new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you do not want to play again?");
        Optional<ButtonType> answer=alert.showAndWait();
        if(answer.get()==ButtonType.CANCEL)
        {
          //  actionEvent.consume();
            Alert alert1=new Alert(Alert.AlertType.INFORMATION,"Great!! here is a new 20x20 maze");
            alert1.show();
            viewModel.generateMaze(20,20);

        }
        else if (answer.get()==ButtonType.OK)
        {
            viewModel.CloseApp();
            Platform.exit();
            System.exit(0);
        }
    }

    public void chooseChar(ActionEvent actionEvent) {
        switch (choose_char.getValue()) {
            case "Mario":
                mazeDisplayer.setImageFileNameCharacter("./Resources/Images/mario2.png");
                break;
            case "Luigi":
                mazeDisplayer.setImageFileNameCharacter("./Resources/Images/Luigi.png");
                break;
            case "Princess Peach":
                mazeDisplayer.setImageFileNameCharacter("./Resources/Images/Princess_Peach.png");
                break;
            case "Yoshi":
                mazeDisplayer.setImageFileNameCharacter("./Resources/Images/Yoshi.png");
                break;
            case "Bowser":
                mazeDisplayer.setImageFileNameCharacter("./Resources/Images/Bowser.png");
                break;
        }
    }

    public void setResizeEvent(Scene s) {
        s.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mazeDisplayer.widthProperty().bind(Apane.widthProperty());
                solutionDisplayer.widthProperty().bind(Apane.widthProperty());
            }
        });
        s.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mazeDisplayer.heightProperty().bind(Apane.heightProperty());
                solutionDisplayer.heightProperty().bind(Apane.heightProperty());

            }
        });

    }

    static MediaPlayer endmediaPlayer;
    public void playEndMusic(int i) {
        if(i==1){
            endmediaPlayer=null;
            if (endmediaPlayer == null) {
                String ssound = getClass().getResource("..//Clips/EndMazeSound.mp3").toExternalForm();
                Media sound = new Media(ssound);
                endmediaPlayer = new MediaPlayer(sound);
                endmediaPlayer.setVolume(0.1);
                //endmediaPlayer.setStopTime(endmediaPlayer.getStopTime());
                //     endmediaPlayer.stopTimeProperty();
            }
            else if(endmediaPlayer!=null)
            {

                endmediaPlayer.play();
            }

        }

    }

}








