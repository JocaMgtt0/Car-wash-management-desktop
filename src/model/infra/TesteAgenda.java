package model.infra;

import java.time.LocalDate;

import model.Agenda;
import model.Automovel;

public class TesteAgenda {

	public static void main(String[] args) {
		
		Agenda hoje = new Agenda("Segunda de Movimento", LocalDate.now());
        
        // 2. Criamos alguns carros para essa agenda
        Automovel carro1 = new Automovel("Civic", "ABC-1234", 50.0, hoje);
        Automovel carro2 = new Automovel("HB20", "XYZ-9876", 40.0, hoje);
        
        // 3. Adicionamos os carros à agenda (usando seu método que já soma o valor)
        hoje.adicionarAutomovel(carro1);
        hoje.adicionarAutomovel(carro2);
        
        // 4. Usamos o DAO para salvar tudo de uma vez (graças ao Cascade!)
        AgendaDAO dao = new AgendaDAO();
        dao.incluirAtomico(hoje);
        
        System.out.println("Sucesso! Verifique seu MySQL.");
        dao.fechar();
		
	}
	
}
