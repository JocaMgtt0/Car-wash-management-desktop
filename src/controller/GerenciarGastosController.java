package controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Gasto;
import model.infra.GastosDAO;

public class GerenciarGastosController implements Initializable {

    @FXML private TextField txtNomeGasto;
    @FXML private TextField txtValorGasto;
    @FXML private DatePicker dpDataGasto;

    @FXML private TableView<Gasto> tabelaGastos;
    @FXML private TableColumn<Gasto, String> colunaDescricao; // Batendo com o FXML
    @FXML private TableColumn<Gasto, Double> colunaValor;
    @FXML private TableColumn<Gasto, LocalDate> colunaData;

    @FXML private Button btnSalvar;
    @FXML private Button btnExcluir;
    @FXML private Button btnConcluir; // Adicionado para fechar a janela
    @FXML private Button btnEditarGasto;

    private Gasto gastoCriado;

    private GastosDAO dao = new GastosDAO();
    private ObservableList<Gasto> listaGastos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Configuração das Colunas (Seu código original mantido)
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("nome"));
        
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colunaValor.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("R$ %.2f", item));
                    setStyle("-fx-alignment: CENTER-RIGHT;");
                }
            }
        });

        colunaData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colunaData.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        });


        dpDataGasto.setValue(LocalDate.now());

        atualizarTabelaPorData(dpDataGasto.getValue());

        dpDataGasto.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                atualizarTabelaPorData(newValue);
            }
        });

        tabelaGastos.setItems(listaGastos);

        btnConcluir.setOnAction(event -> {
            Stage stage = (Stage) btnConcluir.getScene().getWindow();
            stage.close();
        });
        
        // fecha a request do banco em qualquer caso
        Platform.runLater(()->{
        	Stage stage = (Stage) btnConcluir.getScene().getWindow();
        	stage.setOnHidden(a -> { dao.fechar(); });
        });
        
        btnSalvar.setOnAction(event -> salvarGasto());
        btnExcluir.setOnAction(event -> excluirGasto());
        btnEditarGasto.setOnAction(event -> setGastoEdicao(tabelaGastos.getSelectionModel().getSelectedItem())); //completar
        
       
        btnEditarGasto.disableProperty().bind(tabelaGastos.getSelectionModel().selectedItemProperty().isNull());
        btnExcluir.disableProperty().bind(tabelaGastos.getSelectionModel().selectedItemProperty().isNull());
    }
    
    
    
    private void atualizarTabelaPorData(LocalDate dataSelecionada) {
        int mes = dataSelecionada.getMonthValue();
        int ano = dataSelecionada.getYear();

        // Busca no banco apenas os gastos daquele período
        List<Gasto> filtrados = dao.buscarPorMesEAno(mes, ano);
        
        // Atualiza a lista da interface
        listaGastos.setAll(filtrados);
    }

    private void salvarGasto() {
        try {
        	String nome = txtNomeGasto.getText();
            double valor = Double.parseDouble(txtValorGasto.getText().replace(",", "."));
            LocalDate data = dpDataGasto.getValue();

            if (nome.trim().isEmpty() || data == null) return;
            
            
            if(gastoCriado != null) {
            	
            	gastoCriado.setData(data);
            	gastoCriado.setNome(nome);
            	gastoCriado.setValor(valor);
            	
            	dao.abrirT().merge(gastoCriado).fecharT();
            	atualizarTabelaPorData(data);
            	limparCampos();
            	gastoCriado = null; 
            	
            }else {
            	
            	Gasto novo = new Gasto(nome, valor, data);
            	dao.incluirAtomico(novo);
            	
            	// Em vez de apenas dar listaGastos.add(novo), 
            	// chamamos a atualização para garantir que o filtro de mês seja respeitado
            	atualizarTabelaPorData(data);
            	
            	limparCampos();
            }
            

        } catch (NumberFormatException e) {
            exibirErro("Valor inválido! Use apenas números e ponto.");
        } catch (Exception e) {
            exibirErro(e.getMessage());
        }
    }
    
    private void setGastoEdicao(Gasto gasto) {
    	this.gastoCriado = gasto;
    	txtNomeGasto.setText(gasto.getNome());
    	txtValorGasto.setText(String.valueOf(gasto.getValor()));
    	dpDataGasto.setValue(gasto.getData());
    }

    private void excluirGasto() {
        Gasto selecionado = tabelaGastos.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Excluir: " + selecionado.getNome() + "?", ButtonType.YES, ButtonType.NO);
            if (confirm.showAndWait().get() == ButtonType.YES) {
                dao.abrirT().remover(selecionado).fecharT();
                listaGastos.remove(selecionado);
            }
        }
    }

    private void limparCampos() {
        txtNomeGasto.clear();
        txtValorGasto.clear();
        dpDataGasto.setValue(LocalDate.now());
        txtNomeGasto.requestFocus();
    }

    private void exibirErro(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setHeaderText(null);
        alert.show();
    }

    public ObservableList<Gasto> getListaFinal() {
        return listaGastos;
    }
}