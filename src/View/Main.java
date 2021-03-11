
package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import View.MyViewController;

import java.io.IOException;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("../View/MazeStage.fxml"));
        Parent root = fxmlLoader.load();
        //primaryStage.setTitle("Maze Game 2020");
       // primaryStage.setScene(new Scene(root, 1100, 750));
        Scene s= new Scene(root,1100,750);
        primaryStage.setTitle("Maze Game 2020");
        primaryStage.setScene(s);

        /*String image = getClass().getResource("/Resources/Images/backround1.jpg").toExternalForm();
        String style = "-fx-background-image: url('"+image+"');";
        (root).setStyle(style);*/
        primaryStage.show();

        IModel model= new MyModel();
        MyViewModel viewModel=new MyViewModel(model);
        MyViewController viewController= fxmlLoader.getController();
        viewController.setViewModel(viewModel);
        viewModel.addObserver(viewController);
        model.startServers();
        primaryStage.setOnCloseRequest(e->viewModel.CloseApp());
        viewController.setResizeEvent(s);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
