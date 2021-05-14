/*
    Uriel Caracuel Barrera - 2� DAM
    AgendaViewController.java
*/
package appagenda;

import entidades.Persona;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * FXML Controller class
 *
 * @author ucb40
 */
public class AgendaViewController implements Initializable {
    
    private EntityManager entityManager;
    @FXML
    private TableView<Persona> tableViewAgenda;
    @FXML
    private TableColumn<Persona, String> columnNombre;
    @FXML
    private TableColumn<Persona, String> columnApellidos;
    @FXML
    private TableColumn<Persona, String> columnEmail;
    @FXML
    private TableColumn<Persona, String> columnProvincia;
    @FXML
    private Button buttonGuardar;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private TextField textFieldNombre;
    
    private Persona personaSeleccionada;
    @FXML
    private AnchorPane rootAgendaView;


    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void cargarTodasPersonas() {
        Query queryPersonaFindAll
                = entityManager.createNamedQuery("Persona.findAll");
        List<Persona> listPersona = queryPersonaFindAll.getResultList();
        tableViewAgenda.setItems(FXCollections.observableArrayList(listPersona)
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
       //añadir un tipo de objeto referencia
        columnProvincia.setCellValueFactory(cellData->{
        SimpleStringProperty property=new SimpleStringProperty();
        if (cellData.getValue().getProvincia()!=null){
            property.setValue(cellData.getValue().getProvincia().getNombre());
        }
        
        return property;
        });
        
        
        //GUARDAR EL VALOR EN UNA VARIABLE AL SELECCIONAR ELEMENTO DE LA TABLA
        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    personaSeleccionada = newValue;
                    
                    //cambiar texto de los textfields
                    if (personaSeleccionada != null) {
                        textFieldNombre.setText(personaSeleccionada.getNombre());
                        textFieldApellidos.setText(personaSeleccionada.getApellidos());
                    } else {
                        textFieldNombre.setText("");
                        textFieldApellidos.setText("");
                    }
        });

        
        //FIN INIT
    }

    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        
        if (personaSeleccionada != null){
            
            personaSeleccionada.setNombre(textFieldNombre.getText());
            personaSeleccionada.setApellidos(textFieldApellidos.getText());
        }
        entityManager.getTransaction().begin();
        entityManager.merge(personaSeleccionada);
        entityManager.getTransaction().commit();
        
        int numFilaSeleccionada
                = tableViewAgenda.getSelectionModel().getSelectedIndex();
        
        tableViewAgenda.getItems().set(numFilaSeleccionada, personaSeleccionada);

        TablePosition pos = new TablePosition(tableViewAgenda, numFilaSeleccionada, null);
        tableViewAgenda.getFocusModel().focus(pos);
        tableViewAgenda.requestFocus();

        
    }

    @FXML
    private void onActionButtonNuevo(ActionEvent event) {

        try {

            // Cargar la vista de detalle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VentanaDatosPersona.fxml"));
            Parent rootDetalleView = fxmlLoader.load();

            // Pasar la vista de lista al detalle 
            VentanaDatosPersonaController ventanaDatosPersonaController
                    = (VentanaDatosPersonaController) fxmlLoader.getController();

            ventanaDatosPersonaController.setRootAgendaView(rootAgendaView);
             
            //Intercambio de datos funcionales con el detalle
            ventanaDatosPersonaController.setTableViewPrevio(tableViewAgenda);


            // Ocultar la vista de la lista
            rootAgendaView.setVisible(false);
            

            //Añadir la vista detalle al StackPane principal para que se muestre
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();

            rootMain.getChildren().add(rootDetalleView);
            
            
            // Para el botón Nuevo:
            personaSeleccionada = new Persona();
            ventanaDatosPersonaController.setPersona(entityManager,
            personaSeleccionada,true);

            ventanaDatosPersonaController.mostrarDatos();


        } catch (IOException ex) {
            Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE, null, ex
            );
        }

    }

    @FXML
    private void onActionButtonEditar(ActionEvent event) {

        try {

            // Cargar la vista de detalle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VentanaDatosPersona.fxml"));
            Parent rootDetalleView = fxmlLoader.load();

            // Pasar la vista de lista al detalle 
            VentanaDatosPersonaController ventanaDatosPersonaController
                    = (VentanaDatosPersonaController) fxmlLoader.getController();

            ventanaDatosPersonaController.setRootAgendaView(rootAgendaView);
            //Intercambio de datos funcionales con el detalle
            ventanaDatosPersonaController.setTableViewPrevio(tableViewAgenda);

            // Ocultar la vista de la lista
            rootAgendaView.setVisible(false);

            //Añadir la vista detalle al StackPane principal para que se muestre
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();

            rootMain.getChildren().add(rootDetalleView);
            
            
            // Para el botón Editar
            ventanaDatosPersonaController.setPersona(entityManager,
            personaSeleccionada,false);
            
            
            ventanaDatosPersonaController.mostrarDatos();


        } catch (IOException ex) {
            Logger.getLogger(AgendaViewController.class.getName()).log(Level.SEVERE, null, ex
            );
        }

        
    }

    @FXML
    private void onActionButtonSuprimir(ActionEvent event) {
        
        //Para mostrar esa ventana de confirmación
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("¿Desea suprimir el siguiente registro?");
        alert.setContentText(personaSeleccionada.getNombre() + " "
                            + personaSeleccionada.getApellidos());
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
         
            // Acciones a realizar si el usuario acepta
            entityManager.getTransaction().begin();
            entityManager.merge(personaSeleccionada);
            entityManager.remove(personaSeleccionada);
            entityManager.getTransaction().commit();
            tableViewAgenda.getItems().remove(personaSeleccionada);
            tableViewAgenda.getFocusModel().focus(null);
            tableViewAgenda.requestFocus();

         
         
        } else {
         // Acciones a realizar si el usuario cancela
         
         //dejar seleccionado en el TableView la misma fila
            int numFilaSeleccionada=
            tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);
            TablePosition pos = new TablePosition(tableViewAgenda,
            numFilaSeleccionada,null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }    
    }//FIN SUPRIMIR
    
    

}//FIN CLASE