package controller;

import java.time.LocalDate;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Agenda;
import model.infra.AgendaDAO;

public class FileAgendaController {

	@FXML
	private TextField txtNomeAgenda;

	@FXML
	private DatePicker datePickerData;

	@FXML
	private Button btnCriar;

	@FXML
	private Button btnCancelar;

	// Variável para guardar a agenda que acabamos de criar
	private Agenda agendaCriada;

	@FXML
	public void initialize() {
		btnCriar.setOnAction(event -> salvarNovaAgenda());
		btnCancelar.setOnAction(event -> fecharTela());
	}

	private void salvarNovaAgenda() {
		String nome = txtNomeAgenda.getText();
		LocalDate dataEscolhida = datePickerData.getValue();

		// Validação simples: se estiver vazio, avisa o usuário e para o código
		if (nome == null || nome.isBlank() || dataEscolhida == null) {
			Alert alerta = new Alert(AlertType.WARNING);
			alerta.setTitle("Atenção");
			alerta.setHeaderText("Campos Incompletos");
			alerta.setContentText("Por favor, preencha o nome e escolha uma data para a agenda.");
			alerta.showAndWait();
			return;
		}
		
		AgendaDAO agendaDao = new AgendaDAO();
		Agenda agendaBanco = agendaDao.buscarPorData(dataEscolhida); 
		
		if (agendaBanco != null && agendaCriada == null) {
			Alert alerta = new Alert(AlertType.WARNING);
			alerta.setTitle("Atenção");
			alerta.setHeaderText("Agenda ja existente");
			alerta.setContentText("Por favor, adicione outra data, ja tem uma agenda no banco com essa data");
			alerta.showAndWait();
			agendaDao.fechar();
			return;
		}
		
		if (agendaCriada != null) {

			agendaCriada.setData(dataEscolhida);
			agendaCriada.setNome(nome);

		} else {
			this.agendaCriada = new Agenda(nome, dataEscolhida);
		}
		agendaDao.fechar();
		fecharTela();
	}

	private void fecharTela() {
		Stage stageAtual = (Stage) btnCancelar.getScene().getWindow();
		stageAtual.close();
	}

	// Método importante: a Tela Inicial vai chamar isso para pegar a agenda criada
	public Agenda getAgendaCriada() {
		return agendaCriada;
	}

	public void setAgendaEdicao(Agenda agenda) {
		agendaCriada = agenda;
		txtNomeAgenda.setText(agenda.getNome());
		datePickerData.setValue(agenda.getData());
	}

}