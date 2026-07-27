package view;


import controller.TelaInicialController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
	
	private TelaInicialController controladorTelaInicial;
	
    @Override
    public void start(Stage primaryStage) {
    	try {
            // 1. Carrega o novo arquivo FXML principal (TelaInicial)
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("TelaInicial.fxml"));
    		Parent root = loader.load();
    		controladorTelaInicial = loader.getController();
            
            // 2. Cria a "Cena" (Scene) colocando o layout dentro dela
            Scene scene = new Scene(root);
            
            // 3. Configura o "Palco" (Stage), que é a janela do sistema operacional
            primaryStage.setTitle("Sistema de Gestão - Lava Jato");
            primaryStage.setScene(scene);
            
            // Define um tamanho mínimo para a janela não ficar espremida
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            
            // 4. Exibe a janela na tela
            primaryStage.show();
            
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar a tela principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // O método main serve apenas para dar o "start" inicial no framework do JavaFX
        launch(args);
    }
    
    
    @Override
    public void stop() throws Exception {
    	controladorTelaInicial.getDAOAgenda().fechar();
    	super.stop();
    }
}
