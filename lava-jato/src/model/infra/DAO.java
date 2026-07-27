package model.infra;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

// Classe usada para facilitar CRUD no banco de dados
// Aumentar niveis de abstração
public class DAO<E> {

	private static EntityManagerFactory emf;
	protected EntityManager em;
	private Class<E> classe;
	
	
	
	// primeiro Start da classe
	static {
		try {
			emf = Persistence.createEntityManagerFactory("lava-jato", carregarCredenciais());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Lê usuário/senha do banco de src/db.properties (fora do git).
	// Copie db.properties.example para db.properties e preencha com suas credenciais locais.
	private static Map<String, String> carregarCredenciais() {
		Properties props = new Properties();
		try (InputStream in = DAO.class.getResourceAsStream("/db.properties")) {
			if (in == null) {
				throw new IllegalStateException(
					"db.properties não encontrado. Copie src/db.properties.example para src/db.properties e preencha suas credenciais.");
			}
			props.load(in);
		} catch (IOException e) {
			throw new IllegalStateException("Falha ao ler db.properties", e);
		}

		Map<String, String> overrides = new HashMap<>();
		overrides.put("javax.persistence.jdbc.user", props.getProperty("db.user"));
		overrides.put("javax.persistence.jdbc.password", props.getProperty("db.password"));
		return overrides;
	}
	
	
	public DAO() {
		this(null);
	}

	
	
	// set uma classe e cria o EMF
	public DAO(Class<E> classe) {
		this.classe = classe;
		em = emf.createEntityManager();
	}
	
	
	
	// Abre uma transação
	public DAO<E> abrirT(){
		em.getTransaction().begin();
		return this;
	}
	
	
	
	// Fecha uma transação
	public DAO<E> fecharT(){
		em.getTransaction().commit();
		return this;
	}
	
	
	
	//Inclui uma entidade ao banco
	public DAO<E> incluir(E entidade){
		em.persist(entidade);
		return this;
	}
	
	
	
	//abre, inclui e fecha uma transação. Incluindo uma entidade ao banco
	public DAO<E> incluirAtomico(E entidade){
		return this.abrirT().incluir(entidade).fecharT();
	}
	
	
	
	//remove um obj do banco
	public DAO<E> remover(E entidade) {
        if (entidade != null) {
            E remover = em.contains(entidade) ? entidade : em.merge(entidade);
            em.remove(remover);
        }
        return this;
    }
	
	
	
	// Retorna todos os objetos
	public List<E> obterTodos(){
		return obterTodos(10, 0);
	}
	
	
	
	//retorna uma lista de Objetos do banco. Conforme parametros passado
	public List<E> obterTodos(int qtd, int deslocamento){
		if(classe == null) {
			throw new UnsupportedOperationException("Classe nula. ");
		}
		String jpql = "Select e from " + classe.getName() + " e";
		TypedQuery<E> query = em.createQuery(jpql, classe);
		query.setMaxResults(qtd);
		query.setFirstResult(deslocamento);
		return query.getResultList();
	}
	
	
	
	// retorna um objeto do banco de dados
	public E obterPorID(Object id){ 
		
		return em.find(classe, id); 
	}
	
	
	
	//fecha transação
	public void fechar(){
		em.close();
	}
	
	
	
	// "merge" no banco de dados
	public DAO<E> merge(E entidade){
		em.merge(entidade);
		return this;
	}
}
