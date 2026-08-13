package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="automoveis")
public class Automovel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String modelo;
	private String placa;
	private double valorLavagem;
	
	@ManyToOne
	@JoinColumn(name="agenda_id")
	private Agenda agenda;
	
	
	public Automovel() {}

	public Automovel(String modelo, String placa, double valorLavagem, Agenda agenda) {
		super();
		setModelo(modelo);
		setPlaca(placa);
		setValorLavagem(valorLavagem);
		this.agenda = agenda;
	}

	public Agenda getAgenda() {
		return agenda;
	}

	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getModelo() {
		return modelo;
	}


	public void setModelo(String modelo) {
		if(modelo.isEmpty() || modelo.isBlank()) { throw new IllegalArgumentException("Esse modelo de carro é incompativel");}
		this.modelo = modelo; 
	}
	
	public String getPlaca() {
		return placa;
	}


	public void setPlaca(String placa) {
		if(placa.isEmpty() || placa.isBlank()) {throw new IllegalArgumentException("Essa placa é incompativel"); }
		this.placa = placa; 
	}


	public double getValorLavagem() {
		return valorLavagem;
	}


	public void setValorLavagem(double valorLavagem) {
		if(valorLavagem < 0) { throw new IllegalArgumentException("Valor da Lavagen Incompativel"); }
		this.valorLavagem = valorLavagem;
	}
	
}
	
	
	
	
	
	
	
	
	
	
