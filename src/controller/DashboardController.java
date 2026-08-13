package controller;

import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell; // IMPORT ESSENCIAL
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Agenda;
import model.Automovel;
import model.infra.AgendaDAO;
import model.infra.AutomovelDAO;

public class DashboardController {

    @FXML private Label labelTotal;
    @FXML private Label labelTitulo; 

    @FXML private TableView<Automovel> tabelaLavagens;
    @FXML private TableColumn<Automovel, String> colunaModelo;
    @FXML private TableColumn<Automovel, String> colunaPlaca;
    @FXML private TableColumn<Automovel, Double> colunaValor;

    @FXML private Button btnNovaLavagem;
    @FXML private Button btnVoltar;
    @FXML private Button btnExcluirLavagem; 
    @FXML private Button btnEditarLavagem;

    private Agenda agendaAtual;
    private ObservableList<Automovel> listaLavagens = FXCollections.observableArrayList();
    
    private AutomovelDAO daoAuto = new AutomovelDAO();
    private AgendaDAO daoAgenda = new AgendaDAO(); 

    @FXML
    public void initialize() {
        colunaModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colunaPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valorLavagem"));
        
        // Formatação da coluna de Valor (R$)
        colunaValor.setCellFactory(column -> {
            return new TableCell<Automovel, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle(""); 
                    } else {
                        setText(String.format("R$ %.2f", item));
                        // Alinha o texto à direita (estilo contábil)
                        setStyle("-fx-alignment: CENTER-RIGHT;"); 
                    }
                }
            };
        });
        
        tabelaLavagens.setItems(listaLavagens);

        
        Platform.runLater(()->{
        	Stage stage = (Stage) btnVoltar.getScene().getWindow();
        	stage.setOnHidden(a -> { 
        		daoAuto.fechar(); 
        		daoAgenda.fechar();
        	});
        });
        
        btnVoltar.setOnAction(event -> voltarParaTelaInicial());
        
        
        btnNovaLavagem.setOnAction(event -> abrirTelaNovaLavagem());
        btnExcluirLavagem.setOnAction(event -> excluirLavagem());
        btnEditarLavagem.setOnAction(event -> abrirTelaEdicaoLavagem());
        
        btnExcluirLavagem.disableProperty().bind(
            tabelaLavagens.getSelectionModel().selectedItemProperty().isNull()
        );
        
        btnEditarLavagem.disableProperty().bind(
    		tabelaLavagens.getSelectionModel().selectedItemProperty().isNull()
        );
        
        
    }

    public void setAgenda(Agenda agenda) {
        this.agendaAtual = agenda;
        labelTitulo.setText("Agenda: " + agenda.getNome());
        carregarAutomoveisDoBanco();
    }
    
    private void carregarAutomoveisDoBanco() {
        List<Automovel> listaBanco = daoAuto.buscarPorAgenda(agendaAtual.getId());
        listaLavagens.setAll(listaBanco);
        atualizarTotal(); 
    }

    private void excluirLavagem() {
        Automovel selecionada = tabelaLavagens.getSelectionModel().getSelectedItem();

        if (selecionada != null) {
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Confirmar Exclusão");
            alerta.setHeaderText("Deseja excluir a lavagem do " + selecionada.getModelo() + "?");
            alerta.setContentText("O valor de R$ " + selecionada.getValorLavagem() + " será removido do total.");

            if (alerta.showAndWait().get() == ButtonType.OK) {
                try {
                    double novoTotal = agendaAtual.getTotalArrecadado() - selecionada.getValorLavagem();
                    agendaAtual.setTotalArrecadado(novoTotal);
                    daoAgenda.abrirT().merge(agendaAtual).fecharT();

                    daoAuto.abrirT().remover(selecionada).fecharT();

                    listaLavagens.remove(selecionada);
                    atualizarTotal();

                } catch (Exception e) {
                    System.err.println("Erro ao deletar lavagem do banco.");
                    e.printStackTrace();
                }
            }
        }
    }

    private void atualizarTotal() {
        String textoFormatado = String.format("R$ %.2f", agendaAtual.getTotalArrecadado());
        labelTotal.setText(textoFormatado);
        
        if (textoFormatado.length() >= 14) { 
            labelTotal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        } else if (textoFormatado.length() >= 11) { 
            labelTotal.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        } else {
            labelTotal.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        }
    }

    
    private void abrirTelaEdicaoLavagem() {
    	
    	Automovel selecionado = tabelaLavagens.getSelectionModel().getSelectedItem();
    	
    	if (selecionado != null) {
    		double valorAntigoLavagem = selecionado.getValorLavagem();
			
    		try {
    			
    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FormularioLavagem.fxml"));
    			Parent root = loader.load();
    			FormularioLavagemController controllerModal = loader.getController();
    			
    			Stage stageModal = new Stage();
    			stageModal.setTitle("Editar Lavagem");
    			stageModal.setScene(new Scene(root));
    			stageModal.initModality(Modality.APPLICATION_MODAL);
    			stageModal.setResizable(false);
    			controllerModal.setLavagemParaEdicao(selecionado);
    			stageModal.showAndWait();
    			
    			Automovel atualizacaoLav = controllerModal.getLavagemCriada();
    			
    			if(atualizacaoLav != null) {
    				atualizacaoLav.setAgenda(agendaAtual);
    				daoAuto.abrirT().merge(atualizacaoLav).fecharT();
    				
    				double novoTotal = (agendaAtual.getTotalArrecadado() - valorAntigoLavagem) + selecionado.getValorLavagem();
                	agendaAtual.setTotalArrecadado(novoTotal);
	                daoAgenda.abrirT().merge(agendaAtual).fecharT();
	                carregarAutomoveisDoBanco();
	                atualizarTotal();
    				
    			}
    			
    			    			
    			
    		} catch (Exception e) {e.printStackTrace();}
		}
    }
    
    
    private void abrirTelaNovaLavagem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FormularioLavagem.fxml"));
            Parent root = loader.load();
            FormularioLavagemController controllerModal = loader.getController();

            Stage stageModal = new Stage();
            stageModal.setTitle("Registrar Nova Lavagem");
            stageModal.setScene(new Scene(root));
            stageModal.initModality(Modality.APPLICATION_MODAL);
            stageModal.setResizable(false);
            stageModal.showAndWait();

            Automovel novaLavagem = controllerModal.getLavagemCriada();
            
            if (novaLavagem != null) {
                novaLavagem.setAgenda(agendaAtual);
                daoAuto.incluirAtomico(novaLavagem);
                
                double novoTotal = agendaAtual.getTotalArrecadado() + novaLavagem.getValorLavagem();
                agendaAtual.setTotalArrecadado(novoTotal);
                daoAgenda.abrirT().merge(agendaAtual).fecharT();

                listaLavagens.add(novaLavagem);
                atualizarTotal();
            }
        } catch (Exception e) { 
            e.printStackTrace();
        }
    }

    private void voltarParaTelaInicial() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaInicial.fxml"));
            Parent root = loader.load();
            Stage janelaAtual = (Stage) btnVoltar.getScene().getWindow();
            //Fechar request do banco de dados
            daoAgenda.fechar();
            daoAuto.fechar();
            janelaAtual.setScene(new Scene(root));
            janelaAtual.setTitle("Lava Jato - Início (Minhas Agendas)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}