package model.infra;

import java.util.List;

import model.Automovel;

public class AutomovelDAO extends DAO<Automovel>{

	public AutomovelDAO() {
		super(Automovel.class);
	}
	
	// Busca todos os carros vinculados a uma agenda específica
	public List<Automovel> buscarPorAgenda(Long agendaId) {
        String jpql = "SELECT a FROM Automovel a WHERE a.agenda.id = :agendaId";
        return em.createQuery(jpql, Automovel.class)
                 .setParameter("agendaId", agendaId)
                 .getResultList();
    }
	
	
}
