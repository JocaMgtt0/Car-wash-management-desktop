package model.infra;

import java.util.List;
import model.Gasto;

public class GastosDAO extends DAO<Gasto>{

	public GastosDAO() {
		super(Gasto.class);
	}
	
	public DAO<Gasto> atualizarGasto(Gasto gasto){
		this.abrirT()
			.merge(gasto)
			.fecharT();
		return this;
	}
	
	public List<Gasto> buscarPorMesEAno(int mes, int ano) {
	    String jpql = "SELECT g FROM Gasto g WHERE MONTH(g.data) = :mes AND YEAR(g.data) = :ano";
	    return em.createQuery(jpql, Gasto.class)
	             .setParameter("mes", mes)
	             .setParameter("ano", ano)
	             .getResultList();
	}

	// FUNÇÃO QUE FALTAVA: Busca anos únicos na tabela de gastos
	public List<Integer> buscarAnosComGastos() {
	    String jpql = "SELECT DISTINCT YEAR(g.data) FROM Gasto g ORDER BY YEAR(g.data) DESC";
	    return em.createQuery(jpql, Integer.class).getResultList();
	}
}