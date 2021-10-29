/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxmvc.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafxmvc.model.dao.CategoriaDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Categoria;

/**
 * FXML Controller class
 *
 * @author anxi_
 */
public class FXMLAnchorPaneCadastrosCategoriasController implements Initializable {

    
    @FXML
    private TableView<Categoria> tableViewCategoria;

    @FXML
    private TableColumn<Categoria, String> tableColumnCategoriasCodigo;

    @FXML
    private TableColumn<Categoria, String> tableColumnCategoriasDescricao;

    @FXML
    private Button buttonInserir;

    @FXML
    private Button buttonRemover;
    
    @FXML
    private Label labelCategoriaCodigo;

    @FXML
    private Label labelCategoriaDescricao;
    
    private List<Categoria> listCategoria;
    private ObservableList<Categoria> observableListCategoria;

    //Atributos para manipulação de Banco de Dados
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       categoriaDAO.setConnection(connection);
       carregarTableViewCategoria();
       
       // Listen acionado diante de quaisquer alterações na seleção de itens do TableView
        tableViewCategoria.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableViewCategoria(newValue));
    }


         public void carregarTableViewCategoria() {
        tableColumnCategoriasCodigo.setCellValueFactory(new PropertyValueFactory<>("cdCategoria"));
        tableColumnCategoriasDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        listCategoria = categoriaDAO.listar();

        observableListCategoria = FXCollections.observableArrayList(listCategoria);
        tableViewCategoria.setItems(observableListCategoria);
    }
         
         public void selecionarItemTableViewCategoria(Categoria categoria){
        if (categoria != null) {
            labelCategoriaCodigo.setText(String.valueOf(categoria.getCdCategoria()));
            labelCategoriaDescricao.setText(categoria.getDescricao());            
        } else {
            labelCategoriaCodigo.setText("");
            labelCategoriaDescricao.setText(""); 
        }

    }
         
    @FXML
    public void handleButtonInserir() throws IOException {
        Categoria categoria = new Categoria();
        boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosCategoriasDialog(categoria);
        if (buttonConfirmarClicked) {
            categoriaDAO.inserir(categoria);
            carregarTableViewCategoria();
        }
    }
    
    @FXML
    public void handleButtonRemover() throws IOException {
        Categoria categoria = tableViewCategoria.getSelectionModel().getSelectedItem();
        if (categoria != null) {
            categoriaDAO.remover(categoria);
            carregarTableViewCategoria();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha uma categoria na Tabela!");
            alert.show();
        }
    }
    
    public boolean showFXMLAnchorPaneCadastrosCategoriasDialog(Categoria categoria) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCadastrosCategoriasDialogController.class.getResource("/javafxmvc/view/FXMLAnchorPaneCadastrosCategoriasDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        // Criando um Estágio de Diálogo (Stage Dialog)
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Categorias");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        // Setando o cliente no Controller.
        FXMLAnchorPaneCadastrosCategoriasDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setCategoria(categoria);

        // Mostra o Dialog e espera até que o usuário o feche
        dialogStage.showAndWait();

        return controller.isButtonConfirmarClicked();

    }
    
}


