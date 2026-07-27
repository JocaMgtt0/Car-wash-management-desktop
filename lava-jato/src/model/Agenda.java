package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="agendas")
public class Agenda {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nome;
	private LocalDate data;
	private double totalArrecadado;
	
	@OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Automovel> automoveis = new ArrayList<>();
	
	
	public Agenda() {
	}

	public Agenda(String nome, LocalDate data) {
		super();
		this.nome = nome;
		this.data = data;
		this.totalArrecadado = 0.0;
	}
	
	
	public void adicionarAutomovel(Automovel auto) {
        auto.setAgenda(this);
        this.automoveis.add(auto);
        this.totalArrecadado += auto.getValorLavagem();
    }
	

	public double getTotalArrecadado() {
		return totalArrecadado;
	}

	public void setTotalArrecadado(double totalArrecadado) {
		this.totalArrecadado = totalArrecadado;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}
	
	public Long getId() {
		return id;
	}
}
