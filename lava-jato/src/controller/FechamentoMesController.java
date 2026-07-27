package controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Agenda;
import model.Gasto;
import model.infra.AgendaDAO;
import model.infra.GastosDAO;


public class FechamentoMesController implements Initializable {

    @FXML private ComboBox<String> comboFiltroMes;
    @FXML private ComboBox<Integer> comboFiltroAno;

    @FXML private Label labelReceitaMes; 
    @FXML private Label labelTotalGastos; 
    @FXML private Label labelLucroLiquido; 
    
    @FXML private TableView<Agenda> tabelaAgendasFiltradas;
    @FXML private TableColumn<Agenda, LocalDate> colunaData;
    @FXML private TableColumn<Agenda, String> colunaNome;
    @FXML private TableColumn<Agenda, Double> colunaValor;
    
    @FXML private Button btnVoltar;
    @FXML private Button btnNovoGasto;
    
    private AgendaDAO agendaDao = new AgendaDAO();
    private GastosDAO gastoDao = new GastosDAO();

    private ObservableList<Agenda> listaFiltrada = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    		configurarTabela();
        
        // CHAMADA DIRETA AO BANCO
        preencherFiltrosIniciais();

        comboFiltroMes.setOnAction(e -> atualizarRelatorio());
        comboFiltroAno.setOnAction(e -> atualizarRelatorio());
        btnVoltar.setOnAction(event -> voltarParaTelaInicial());
        btnNovoGasto.setOnAction(event -> abrirModalGasto());
        atualizarRelatorio();
        
    }
   
    //Adiciona todas os anos de agendas e de gastos no combobox
    //Caso nao tiver nenhum, adiciona o ano atual
    private void preencherFiltrosIniciais() {
        // 1. Preenche Meses (Estatico)
        comboFiltroMes.setItems(FXCollections.observableArrayList(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", 
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        ));
       
        Set<Integer> todosAnos = new HashSet<>();
        
        agendaDao.buscarAnosExistentes().forEach(todosAnos::add);
    	
    	gastoDao.buscarAnosComGastos().forEach(todosAnos::add);
    	
        todosAnos.add(LocalDate.now().getYear());
        
        List<Integer> anosTotais = new ArrayList<>(todosAnos);
        anosTotais.sort(Comparator.reverseOrder());

        ObservableList<Integer> listaAnos = FXCollections.observableArrayList(anosTotais);
        
        comboFiltroAno.setItems(listaAnos);

        // 3. Seleções iniciais
        comboFiltroMes.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
        comboFiltroAno.setValue(LocalDate.now().getYear());
    }

    private void configurarTabela() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        
        colunaData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colunaData.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        });

        colunaValor.setCellValueFactory(new PropertyValueFactory<>("totalArrecadado"));
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
        
        tabelaAgendasFiltradas.setItems(listaFiltrada);
    }

    private void atualizarRelatorio() {
        int mes = comboFiltroMes.getSelectionModel().getSelectedIndex() + 1;
        Integer ano = comboFiltroAno.getValue();

        if (ano != null) {
            List<Agenda> agendas = agendaDao.buscarAgendasPorMesEAno(mes, ano);
            List<Gasto> gastos = gastoDao.buscarPorMesEAno(mes, ano);

            listaFiltrada.setAll(agendas);

            double receita = agendas.stream().mapToDouble(Agenda::getTotalArrecadado).sum();
            double despesas = gastos.stream().mapToDouble(Gasto::getValor).sum();
            double lucro = receita - despesas;

            labelReceitaMes.setText(String.format("R$ %.2f", receita));
            labelTotalGastos.setText(String.format("R$ %.2f", despesas));
            labelLucroLiquido.setText(String.format("R$ %.2f", lucro));

            // Feedback visual de lucro
            if (lucro < 0) {
                labelLucroLiquido.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 28px; -fx-font-weight: bold;");
            } else {
                labelLucroLiquido.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
            }
        }
    }

    private void abrirModalGasto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GerenciarGastos.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Gerenciar Gastos");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            stage.showAndWait();
            
            agendaDao = new AgendaDAO();
            gastoDao = new GastosDAO();
            atualizarRelatorio();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void voltarParaTelaInicial() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaInicial.fxml"));
            Parent root = loader.load();
            Stage janelaAtual = (Stage) btnVoltar.getScene().getWindow();
            
            //fechar transições com banco de dados antes de trocas as cenas
        	agendaDao.fechar();
        	gastoDao.fechar();
            janelaAtual.setScene(new Scene(root));
           
        } 
        catch (Exception e) { e.printStackTrace(); }
    }
}
    
