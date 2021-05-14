/*
    Uriel Caracuel Barrera - 2� DAM
    VentanaDatosPersonaController.java
*/
package appagenda;

import entidades.Persona;
import entidades.Provincia;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.RollbackException;

/**
 * FXML Controller class
 *
 * @author ucb40
 */
public class VentanaDatosPersonaController implements Initializable {

    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private TextField textFieldTelefono;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldNumHijos;
    @FXML
    private DatePicker datePickerFechaNacimiento;
    @FXML
    private RadioButton radioButtonSoltero;
    @FXML
    private RadioButton radioButtonCasado;
    @FXML
    private RadioButton radioButtonViudo;
    @FXML
    private ImageView imageViewFoto;
    @FXML
    private ComboBox<Provincia> comboBoxProvincia;
    
    @FXML
    private TextField textFieldSalario;
    @FXML
    private CheckBox checkBoxJubilado;
    
    //ELEMENTO QUE RELACIONA LA VENTANA ROOT CON ESTA VENTANA
    private Pane rootAgendaView;
    @FXML
    private AnchorPane rootVentanaDatosPersona;

    private TableView tableViewPrevio;
    private Persona persona;
    private EntityManager entityManager;
    private boolean nuevaPersona;

    
    public static final char CASADO='C';
    public static final char SOLTERO='S';
    public static final char VIUDO='V';

    
    public static final String CARPETA_FOTOS="src/appagenda/Fotos";
    @FXML
    private HBox hbimageViewFoto;
    @FXML
    private Button ButtonExaminar;
    @FXML
    private Button ButtonSuprimir;
    
    
    


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    


    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        
        //GARDAR DATOS   
        boolean errorFormato = false;

        if (!errorFormato) {
            try {

                //almacenar datos en la BASE DE DATOS
                persona.setNombre(textFieldNombre.getText());
                persona.setApellidos(textFieldApellidos.getText());
                persona.setTelefono(textFieldTelefono.getText());
                persona.setEmail(textFieldEmail.getText());
                
                //datos numericos
                if (!textFieldNumHijos.getText().isEmpty()){
                    try {
                    persona.setNumHijos(Short.valueOf(textFieldNumHijos.getText()));
                    } catch(NumberFormatException ex){
                        errorFormato = true;
                        Alert alert = new Alert(AlertType.INFORMATION, "Número de hijos no válido");
                        alert.showAndWait();
                        textFieldNumHijos.requestFocus();
                    }
                }
                
                if (!textFieldSalario.getText().isEmpty()){
                    try {
                        persona.setSalario(BigDecimal.valueOf(Double.valueOf(textFieldSalario.getText()).doubleValue()));
                    } catch(NumberFormatException ex) {
                        errorFormato = true;
                        Alert alert = new Alert(AlertType.INFORMATION, "Salario no válido");
                        alert.showAndWait();
                        textFieldSalario.requestFocus();
                    }
                }
                //Datos booleanos            
                persona.setJubilado(checkBoxJubilado.isSelected());
                
                //Valores de opción múltiple
                if (radioButtonCasado.isSelected()){
                        persona.setEstadoCivil(CASADO);
                    } else if (radioButtonSoltero.isSelected()){
                        persona.setEstadoCivil(SOLTERO);
                    } else if (radioButtonViudo.isSelected()){
                        persona.setEstadoCivil(VIUDO);
                }
                
                //Fechas
                if (datePickerFechaNacimiento.getValue() != null){
                    LocalDate localDate = datePickerFechaNacimiento.getValue();
                    ZonedDateTime zonedDateTime =
                        localDate.atStartOfDay(ZoneId.systemDefault());
                    
                    Instant instant = zonedDateTime.toInstant();
                    Date date = Date.from(instant);
                    persona.setFechaNacimiento(date);
                } else {
                    persona.setFechaNacimiento(null);
                }
                //Objetos de tabla relacionada
                if (comboBoxProvincia.getValue() != null){
                    persona.setProvincia(comboBoxProvincia.getValue());
                } else {
                        Alert alert = new Alert(AlertType.INFORMATION,"Debe indicar unaprovincia");
                        alert.showAndWait();
                        errorFormato = true;
                }
                //IMAGENES
                



                if (nuevaPersona) {
                    //NUEVO DATO INSERT
                    entityManager.persist(persona);
                } else {
                    //EDITAR DATO, UPDATE
                    entityManager.merge(persona);
                }

                entityManager.getTransaction().commit();

                StackPane rootMain = (StackPane) rootVentanaDatosPersona.getScene().getRoot();
                rootMain.getChildren().remove(rootVentanaDatosPersona);
                rootAgendaView.setVisible(true);

                //ACTUALIZAR TABLEVIEW
                int numFilaSeleccionada;
                if (nuevaPersona) {
                    tableViewPrevio.getItems().add(persona);
                    numFilaSeleccionada = tableViewPrevio.getItems().size() - 1;
                    tableViewPrevio.getSelectionModel().select(numFilaSeleccionada);
                    tableViewPrevio.scrollTo(numFilaSeleccionada);
                } else {
                    numFilaSeleccionada
                            = tableViewPrevio.getSelectionModel().getSelectedIndex();
                    tableViewPrevio.getItems().set(numFilaSeleccionada, persona);
                }
                TablePosition pos = new TablePosition(tableViewPrevio,
                        numFilaSeleccionada, null);
                tableViewPrevio.getFocusModel().focus(pos);
            } catch (RollbackException ex) {
                // Los datos introducidos no cumplen requisitos de BD
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText("No se han podido guardar los cambios. "
                        + "Compruebe que los datos cumplen los requisitos");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            }
        }
    }//FIN GUARDAR


    

    @FXML
    private void onActionButtonCancelar(ActionEvent event) {
        
        StackPane rootMain = (StackPane) rootVentanaDatosPersona.getScene().getRoot();
        rootMain.getChildren().remove(rootVentanaDatosPersona);
        rootAgendaView.setVisible(true);
        
        //cancelar acciones
        entityManager.getTransaction().rollback();
        int numFilaSeleccionada =
        tableViewPrevio.getSelectionModel().getSelectedIndex();
        TablePosition pos = new TablePosition(tableViewPrevio,
        numFilaSeleccionada,null);
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();


    }

    
    public void setRootAgendaView(Pane rootAgendaView) {
        this.rootAgendaView = rootAgendaView;
    }

    public void setTableViewPrevio(TableView tableViewPrevio){
    this.tableViewPrevio=tableViewPrevio;
    }
    //COMPROBAR SI EXISTE LA PERSONA, ROLLBACK EN CASO DE CANCELAR
    public void setPersona(EntityManager entityManager, Persona persona,
            Boolean nuevaPersona) {
        this.entityManager = entityManager;
        entityManager.getTransaction().begin();
        if (!nuevaPersona) {
            this.persona = entityManager.find(Persona.class, persona.getId());
        } else {
            this.persona = persona;
        }
        this.nuevaPersona = nuevaPersona;
    }

    public void mostrarDatos(){
        textFieldNombre.setText(persona.getNombre());
        textFieldApellidos.setText(persona.getApellidos());
        textFieldTelefono.setText(persona.getTelefono());
        textFieldEmail.setText(persona.getEmail());
        
        // Falta implementar el código para el resto de controles
        //DATOS NUMERICOS
        if (persona.getNumHijos() != null){
         textFieldNumHijos.setText(persona.getNumHijos().toString());
        }
        if (persona.getSalario() != null){
            textFieldSalario.setText(persona.getSalario().toString());
        }
        //Valores de tipo boolean
        
        if (persona.getJubilado() != null){
            checkBoxJubilado.setSelected(persona.getJubilado());
        }

        //Datos de opción múltiple

        if (persona.getEstadoCivil() != null) {
            switch (persona.getEstadoCivil()) {
                case CASADO:
                    radioButtonCasado.setSelected(true);
                    break;
                case SOLTERO:
                    radioButtonSoltero.setSelected(true);
                    break;
                case VIUDO:
                    radioButtonViudo.setSelected(true);
                    break;
            }
        }
        
        //Datos de tipo fecha
        if (persona.getFechaNacimiento() != null){
            Date date=persona.getFechaNacimiento();
            Instant instant=date.toInstant();
            ZonedDateTime zdt=instant.atZone(ZoneId.systemDefault());
            LocalDate localDate=zdt.toLocalDate();
            datePickerFechaNacimiento.setValue(localDate);
        }
        //Objetos de una tabla relacionada
        Query queryProvinciaFindAll= entityManager.createNamedQuery("Provincia.findAll");
        List listProvincia= queryProvinciaFindAll.getResultList();
        comboBoxProvincia.setItems(FXCollections.observableList(listProvincia));
        
        if (persona.getProvincia() != null){
            comboBoxProvincia.setValue(persona.getProvincia());
        }
        
        //NO ACTIVAR NEVER
        /*
        comboBoxProvincia.setCellFactory(
                (ListView<Provincia> l)-> new ListCell<Provincia>(){
            @Override
            protected void updateItem(Provincia provincia, Boolean empty){
                super.updateItem(provincia, empty);
                if (provincia == null || empty){
                    setText("");
                } else {
                    setText(provincia.getCodigo()+"-"+provincia.getNombre());
                }
            }
        });*/
        
        
        comboBoxProvincia.setConverter(new StringConverter<Provincia>(){
            @Override
            public String toString(Provincia provincia){
                if (provincia == null){
                    return null;
                } else {
                    return provincia.getCodigo()+"-"+provincia.getNombre();
                }
            }

            @Override
            public Provincia fromString(String userId){
                return null;
            }
        });
        
        //Imágenes
        
        if (persona.getFoto() != null){
            
            String imageFileName=persona.getFoto();
            File file = new File(CARPETA_FOTOS+"/"+imageFileName);
            
            if (file.exists()){
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
         } else {
            Alert alert=new Alert(AlertType.INFORMATION,"No se encuentra la imagen en "
                    +file.toURI().toString());
            alert.showAndWait();
        }
}
        
    }//FIN MOSTRAR DATOS

    @FXML
    private void onActionButtonExaminar(ActionEvent event) {
        File carpetaFotos = new File(CARPETA_FOTOS);
        if (!carpetaFotos.exists()) {
            carpetaFotos.mkdir();
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes (jpg, png)", "*.jpg",
                        "*.png"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*"));
        File file = fileChooser.showOpenDialog(
                rootVentanaDatosPersona.getScene().getWindow());
        if (file != null) {
            try {
                Files.copy(file.toPath(), new File(CARPETA_FOTOS+ "/" + file.getName()).toPath());
                persona.setFoto(file.getName());
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            } catch (FileAlreadyExistsException ex) {
                Alert alert = new Alert(AlertType.WARNING,"Nombre de archivo duplicado");
                alert.showAndWait();
            } catch (IOException ex) {
                Alert alert = new Alert(AlertType.WARNING,"No se ha podido guardar la imagen");
                alert.showAndWait();
            }
        }


        
        
    }//FIN EXAMINAR

    @FXML
    private void onActionSuprimirFoto(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar supresión de imagen");
        alert.setHeaderText("¿Desea SUPRIMIR el archivo asociado a la imagen, \n"
                + "quitar la foto pero MANTENER el archivo, \no CANCELAR la operación?");
        alert.setContentText("Elija la opción deseada:");
        ButtonType buttonTypeEliminar = new ButtonType("Suprimir");
        ButtonType buttonTypeMantener = new ButtonType("Mantener");
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeEliminar, buttonTypeMantener, buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeEliminar){
            String imageFileName = persona.getFoto();
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);
            if (file.exists()) {
             file.delete();
            }
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        } else if (result.get() == buttonTypeMantener) {
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        }
    }//FIN BOTTON SUPRIMIR

    
}//FIN CLASE
