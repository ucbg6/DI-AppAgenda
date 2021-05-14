/*
    Uriel Caracuel Barrera - 2º DAM
    Main.java
*/
package appagenda;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main extends Application {
    
    // EMF y EM
    private EntityManagerFactory emf;
    private EntityManager em;

    
    public static void main(String[] args) {
        launch(args);
    }

    //START
    @Override
    public void start(Stage primaryStage) throws IOException {
        
        StackPane rootMain = new StackPane();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AgendaView.fxml"));
        //Parent root = fxmlLoader.load(); ANTES DE AGREGAR OTRA VENTANA
        Pane rootAgendaView=fxmlLoader.load();
        
        rootMain.getChildren().add(rootAgendaView);
        
        //Scene scene = new Scene(root); ANTES DE AGREGAR OTRA VENTANA
        
        Scene scene = new Scene(rootMain,600,400);
        primaryStage.setTitle("App Agenda");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Conexion a la BD
        emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        em = emf.createEntityManager();
        
        //CARGAR CONTROLADOR
        AgendaViewController agendaViewController =
            (AgendaViewController)fxmlLoader.getController();
          
        agendaViewController.setEntityManager(em);
        
        //CARGAR DATOS DE LA BASE DE DATOS
        agendaViewController.cargarTodasPersonas();
    }
    
    //METODO STOP
    @Override
    public void stop(){        
        //CERRAR entitimanager y factory
        em.close();
        emf.close();
        
        try {
            DriverManager.getConnection("jdbc:derby:BDAgenda;shutdown=true");
        } catch (SQLException ex) {
        }
    }

//FIN CLASE
}



