package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Automovel;

public class FormularioLavagemController {

    @FXML 
    private TextField txtModelo;
    
    @FXML 
    private TextField txtPlaca;

    @FXML 
    private TextField txtValor;
    
    @FXML 
    private Button btnSalvar;
    
    @FXML 
    private Button btnCancelar;

    private Automovel lavagemCriada;

    @FXML
    public void initialize() {
        btnSalvar.setOnAction(event -> salvarLavagem());
        btnCancelar.setOnAction(event -> fecharTela());
    }

    private void salvarLavagem() {
    		String modelo = txtModelo.getText();
    		String placa = txtPlaca.getText();
    		String valor = txtValor.getText();
    	
    	try {
    		
    		if(!modelo.isBlank() 
    			&& !placa.isBlank() 
    			&& !valor.isBlank()) {
    			
    			valor = valor.replace(",", ".");
    			double valorFinal = Double.parseDouble(valor);  
    			
    			if(valorFinal < 0) {
    				mostrarAlerta("Valor Invalido", "Valor nao pode ser negativo");
    				return;
    			}
    			
    			if (lavagemCriada != null) {
    				
    				lavagemCriada.setModelo(modelo);
    				lavagemCriada.setValorLavagem(valorFinal);
    				lavagemCriada.setPlaca(placa);
    				
				} else {
    				this.lavagemCriada = new Automovel(modelo, placa, valorFinal, null);
    			}
    			
    			fecharTela();
    		}else {
    			mostrarAlerta("Campos Incompletos", "Por favor, preencha todos os campos.");
    			return;
    		}
    		
		} catch (Exception e) {
			mostrarAlerta("Valor inválido", "Por Favor digite um valor válido");
		}
    }
    
    

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setTitle("Atenção");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void fecharTela() {
        Stage stageAtual = (Stage) btnCancelar.getScene().getWindow();
        stageAtual.close();
    }

    public Automovel getLavagemCriada() {
        return lavagemCriada;
    }
    
    public void setLavagemParaEdicao(Automovel auto){
    	
    	this.lavagemCriada = auto;
    	txtModelo.setText(auto.getModelo());
    	txtPlaca.setText(auto.getPlaca());
    	txtValor.setText(String.valueOf(auto.getValorLavagem()));
    	
    }
    
    
}