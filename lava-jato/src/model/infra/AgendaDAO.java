package model.infra;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.TypedQuery;

import model.Agenda;

public class AgendaDAO extends DAO<Agenda>{
	
	
	public AgendaDAO() {
		super(Agenda.class);
	}
	
	/**
     * Busca uma agenda específica pela data. 
     * Útil para o Dashboard carregar o dia atual.
     */
	public Agenda buscarPorData(LocalDate data) {
		String jpql = "SELECT a FROM Agenda a Where a.data = :data";
		TypedQuery<Agenda> query = em.createQuery(jpql, Agenda.class);
        query.setParameter("data", data);
        
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null; 
        }
	}
	
	
	/**
     * Busca todas as agendas ordenadas pela data mais recente.
     */
	public List<Agenda> obterTodasOrdenadas(){
		String jpql = "SELECT a FROM Agenda a ORDER BY a.data DESC";
		return em.createQuery(jpql, Agenda.class).getResultList();
	}
	
	public List<Agenda> buscarAgendasPorMesEAno(int mes, int ano) {
	    String jpql = "SELECT a FROM Agenda a WHERE MONTH(a.data) = :mes AND YEAR(a.data) = :ano";
	    return em.createQuery(jpql, Agenda.class)
	             .setParameter("mes", mes)
	             .setParameter("ano", ano)
	             .getResultList();
	}
	
	public List<Integer> buscarAnosExistentes() {
	    // Busca apenas os anos, sem repetir, direto do banco
	    String jpql = "SELECT DISTINCT YEAR(a.data) FROM Agenda a ORDER BY YEAR(a.data) DESC";
	    return em.createQuery(jpql, Integer.class).getResultList();
	}
	
	
	
}
