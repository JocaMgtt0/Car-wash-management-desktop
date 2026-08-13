package controller;

import javafx.scene.control.TextField;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell; // IMPORT ADICIONADO
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Agenda;
import model.infra.AgendaDAO;

public class TelaInicialController implements Initializable {

	@FXML
	private TableView<Agenda> tabelaAgendas;
	@FXML
	private TableColumn<Agenda, String> colunaNome;
	@FXML
	private TableColumn<Agenda, LocalDate> colunaData;
	@FXML
	private TableColumn<Agenda, Double> colunaTotal;

	@FXML
	private Button btnCriarAgenda;
	@FXML
	private Button btnFechamento;
	@FXML
	private Label labelTotalGeral;
	@FXML
	private Button btnExcluirAgenda;
	@FXML
	private Button btnEditarAgenda;

	@FXML
	private TextField txtFiltroNome;
	@FXML
	private DatePicker dpFiltroData;
	@FXML
	private Button btnLimparFiltro;

	private AgendaDAO dao = new AgendaDAO();
	private ObservableList<Agenda> listaAgendas;
	private List<Agenda> novaListaFiltradaNome;
	private List<Agenda> novaListaFiltradaData;
	private boolean limpando = false;
	
	//Lista do filtro duplo
	private ObservableList<Agenda> filtroFinal =  FXCollections.observableArrayList();
	
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

		// --- FORMATAÇÃO DA DATA (dd/MM/yyyy) ---
		colunaData.setCellValueFactory(new PropertyValueFactory<>("data"));

		// Limpa os filtro e carrega dados do banco
		btnLimparFiltro.setOnAction(e -> {
			limpando = true;
			novaListaFiltradaData = null;
			novaListaFiltradaNome = null;
			dpFiltroData.setValue(null);
			txtFiltroNome.clear();
			limpando = false;
			carregarDadosDoBanco();
		});

		// escuta filtro data
		dpFiltroData.valueProperty().addListener((observable, oldDate, newDate) -> {
			if (limpando) return;
			LocalDate data = observable.getValue();
			getAgendaComData(data);
			exibeFiltro();
		});

		// escuta o filtro Nome
		txtFiltroNome.textProperty().addListener((observable, oldValue, newValue) -> {
			if (limpando) return;
			String semEspaco = observable.getValue().trim();
			getAgendaList(semEspaco);
			exibeFiltro();
		});

		colunaData.setCellFactory(column -> {
			return new TableCell<Agenda, LocalDate>() {
				private final DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

				@Override
				protected void updateItem(LocalDate item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
					} else {
						setText(formatador.format(item));
					}
				}
			};
		});

		// --- FORMATAÇÃO DO VALOR TOTAL (R$ 0,00) ---
		colunaTotal.setCellValueFactory(new PropertyValueFactory<>("totalArrecadado"));
		colunaTotal.setCellFactory(column -> {
			return new TableCell<Agenda, Double>() {
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						setText(String.format("R$ %.2f", item));
						setStyle("-fx-alignment: CENTER-RIGHT;"); // Alinhado à direita
					}
				}
			};
		});

		btnCriarAgenda.setOnAction(event -> abrirTelaCriarAgenda());
		btnFechamento.setOnAction(event -> abrirTelaFechamento());
		btnExcluirAgenda.setOnAction(event -> excluirAgendaSelecionada());
		btnExcluirAgenda.disableProperty().bind(tabelaAgendas.getSelectionModel().selectedItemProperty().isNull());
		btnEditarAgenda.setOnAction(event -> abrirTelaEdicaoAgenda());
		btnEditarAgenda.disableProperty().bind(tabelaAgendas.getSelectionModel().selectedItemProperty().isNull());

		carregarDadosDoBanco();

		tabelaAgendas.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2 && tabelaAgendas.getSelectionModel().getSelectedItem() != null) {
				Agenda agendaSelecionada = tabelaAgendas.getSelectionModel().getSelectedItem();
				abrirDashboardDaAgenda(agendaSelecionada);
			}
		});
	}
	
	
	private void exibeFiltro(){
		
		if (novaListaFiltradaData != null && novaListaFiltradaNome != null) {
			
			filtroFinal.clear();
			for (int i = 0 ; i < novaListaFiltradaNome.size(); i ++) {
				Agenda obj1 = novaListaFiltradaNome.get(i);
				
				if(novaListaFiltradaData.contains(obj1)) {
					filtroFinal.add(obj1);
				}
			}
			
			tabelaAgendas.setItems(filtroFinal);
		} else if(novaListaFiltradaData != null) {
			tabelaAgendas.setItems(FXCollections.observableArrayList(novaListaFiltradaData));
		} else if(novaListaFiltradaNome != null) {
			tabelaAgendas.setItems(FXCollections.observableArrayList(novaListaFiltradaNome));
		}
	}
	
	private List<Agenda> getAgendaComData(LocalDate data) {
		novaListaFiltradaData = listaAgendas.stream()
							.filter(valor -> valor.getData().equals(data))
							.collect(Collectors.toList());
		return novaListaFiltradaData;
	}

	private List<Agenda> getAgendaList(String letraParaBuscar) {

		novaListaFiltradaNome = listaAgendas.stream()
				.filter(valor -> valor.getNome().toLowerCase().contains(letraParaBuscar.toLowerCase()))
				.collect(Collectors.toList());
		return novaListaFiltradaNome;
	}

	private void excluirAgendaSelecionada() {
		Agenda selecionada = tabelaAgendas.getSelectionModel().getSelectedItem();
		if (selecionada == null)
			return;

		Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
		confirmacao.setTitle("Confirmar Exclusão");
		confirmacao.setHeaderText("Deseja realmente excluir esta agenda?");
		confirmacao.setContentText("Isso apagará permanentemente a agenda: " + selecionada.getNome()
				+ "\ne todos os veículos vinculados a ela.");

		if (confirmacao.showAndWait().get() == ButtonType.OK) {
			try {
				dao.abrirT().remover(selecionada).fecharT();
				listaAgendas.remove(selecionada);
				calcularTotalGeral();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void carregarDadosDoBanco() {
		List<Agenda> agendasDoBanco = dao.obterTodasOrdenadas();
		listaAgendas = FXCollections.observableArrayList(agendasDoBanco);
		tabelaAgendas.setItems(listaAgendas);
		calcularTotalGeral();
	}

	private void abrirTelaCriarAgenda() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FileAgenda.fxml"));
			Parent root = loader.load();
			FileAgendaController controllerModal = loader.getController();
			Stage stageModal = new Stage();
			stageModal.setTitle("Criar Nova Agenda");
			stageModal.setScene(new Scene(root));
			stageModal.initModality(Modality.APPLICATION_MODAL);
			stageModal.setResizable(false);

			stageModal.showAndWait();

			Agenda novaAgenda = controllerModal.getAgendaCriada();
			if (novaAgenda != null) {
				dao.incluirAtomico(novaAgenda);
				listaAgendas.add(novaAgenda);
				calcularTotalGeral();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void abrirTelaEdicaoAgenda() {
		Agenda selecionada = tabelaAgendas.getSelectionModel().getSelectedItem();

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FileAgenda.fxml"));
			Parent root = loader.load();
			FileAgendaController controllerModal = loader.getController();
			Stage stageModal = new Stage();
			stageModal.setTitle("Atualizar Agenda");
			stageModal.setScene(new Scene(root));
			stageModal.initModality(Modality.APPLICATION_MODAL);
			stageModal.setResizable(false);
			controllerModal.setAgendaEdicao(selecionada);
			stageModal.showAndWait();

			Agenda agendaCriada = controllerModal.getAgendaCriada();

			if (agendaCriada != null) {

				dao.abrirT().merge(agendaCriada).fecharT();
				calcularTotalGeral();
				dao.fechar();
				dao = new AgendaDAO();
				carregarDadosDoBanco();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calcularTotalGeral() {
		double somaGeral = 0.0;
		for (Agenda agenda : listaAgendas) {
			somaGeral += agenda.getTotalArrecadado();
		}
		String textoFormatado = String.format("R$ %.2f", somaGeral);
		labelTotalGeral.setText(textoFormatado);

		if (textoFormatado.length() >= 14) {
			labelTotalGeral.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
		} else if (textoFormatado.length() >= 11) {
			labelTotalGeral.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
		} else {
			labelTotalGeral.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
		}
	}

	private void abrirDashboardDaAgenda(Agenda agendaSelecionada) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
			Parent root = loader.load();
			DashboardController controllerDoDashboard = loader.getController();
			controllerDoDashboard.setAgenda(agendaSelecionada);
			Stage janelaAtual = (Stage) tabelaAgendas.getScene().getWindow();
			janelaAtual.setScene(new Scene(root));
			janelaAtual.setTitle("Lava Jato - Gerir Agenda: " + agendaSelecionada.getNome());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void abrirTelaFechamento() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FechamentoMes.fxml"));
			Parent root = loader.load();
			FechamentoMesController controllerFinanceiro = loader.getController();
			Stage janelaAtual = (Stage) btnFechamento.getScene().getWindow();
			janelaAtual.setScene(new Scene(root));
			janelaAtual.setTitle("Lava Jato - Fechamento Mensal");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AgendaDAO getDAOAgenda() {
		return dao;
	}
}