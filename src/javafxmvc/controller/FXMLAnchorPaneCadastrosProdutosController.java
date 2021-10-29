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
import javafxmvc.model.dao.ProdutoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Produto;

/**
 * FXML Controller class
 *
 * @author anxi_
 */
public class FXMLAnchorPaneCadastrosProdutosController implements Initializable {

    @FXML
    private TableView<Produto> tableViewProdutos;

    @FXML
    private TableColumn<Produto, String> tableColumnProdutosCodigo;

    @FXML
    private TableColumn<Produto, String>  tableColumnProdutosNome;

    @FXML
    private TableColumn<Produto, String>  tableColumnProdutosPreco;

    @FXML
    private TableColumn<Produto, String>  tableColumnProdutosQuantidade;
    
    @FXML
    private TableColumn<Produto, String> tableColumnProdutosCategoria;

    @FXML
    private Label labelProdutoCodigo;

    @FXML
    private Label labelProdutoNome;

    @FXML
    private Label labelProdutoPreco;

    @FXML
    private Label labelProdutoQuantidade;
    
    @FXML
    private Label labelProdutoCategoria;

    @FXML
    private Button buttonInserir;

    @FXML
    private Button buttonAlterar;

    @FXML
    private Button buttonRemover;
    
    private List<Produto> listProdutos;
    private ObservableList<Produto> observableListProdutos;

    //Atributos para manipulação de Banco de Dados
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        produtoDAO.setConnection(connection);
        carregarTableViewProdutos();
        selecionarItemTableViewProdutos(null);
        
         // Listen acionado diante de quaisquer alterações na seleção de itens do TableView
        tableViewProdutos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableViewProdutos(newValue));
    }

public void carregarTableViewProdutos() {
        tableColumnProdutosCodigo.setCellValueFactory(new PropertyValueFactory<>("cdProduto"));
        tableColumnProdutosNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        tableColumnProdutosPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        tableColumnProdutosQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        tableColumnProdutosCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        listProdutos = produtoDAO.listar();

        observableListProdutos = FXCollections.observableArrayList(listProdutos);
        tableViewProdutos.setItems(observableListProdutos);
    }   

public void selecionarItemTableViewProdutos(Produto produto){
        if (produto != null) {
            labelProdutoCodigo.setText(String.valueOf(produto.getCdProduto()));
            labelProdutoNome.setText(produto.getNome());
            labelProdutoPreco.setText(String.valueOf(produto.getPreco()));
            labelProdutoQuantidade.setText(String.valueOf(produto.getQuantidade()));
            labelProdutoCategoria.setText(produto.getCategoria().getDescricao());
        } else {
            labelProdutoCodigo.setText("");
            labelProdutoNome.setText("");
            labelProdutoPreco.setText("");
            labelProdutoQuantidade.setText("");
            labelProdutoCategoria.setText("");
        }

    }

@FXML
    public void handleButtonInserir() throws IOException {
        Produto produto = new Produto();
        boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosProdutosDialog(produto);
        if (buttonConfirmarClicked) {
            produtoDAO.inserir(produto);
            carregarTableViewProdutos();
        }
    }
    
        @FXML
    public void handleButtonAlterar() throws IOException {
        Produto produto = tableViewProdutos.getSelectionModel().getSelectedItem();
        if (produto != null) {
            boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosProdutosDialog(produto);
            if (buttonConfirmarClicked) {
                produtoDAO.alterar(produto);
                carregarTableViewProdutos();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um produto na Tabela!");
            alert.show();
        }
    }
    
     @FXML
    public void handleButtonRemover() throws IOException {
        Produto produto = tableViewProdutos.getSelectionModel().getSelectedItem();
        if (produto != null) {
            produtoDAO.remover(produto);
            carregarTableViewProdutos();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um produto na Tabela!");
            alert.show();
        }
    }
    
    public boolean showFXMLAnchorPaneCadastrosProdutosDialog(Produto produto) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCadastrosClientesDialogController.class.getResource("/javafxmvc/view/FXMLAnchorPaneCadastrosProdutosDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        // Criando um Estágio de Diálogo (Stage Dialog)
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Produtos");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        // Setando o prdouto no Controller.
        FXMLAnchorPaneCadastrosProdutosDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setProduto(produto);

        // Mostra o Dialog e espera até que o usuário o feche
        dialogStage.showAndWait();

        return controller.isButtonConfirmarClicked();

    }
    
}
