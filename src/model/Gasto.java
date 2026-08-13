package model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="gastos")
public class Gasto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    private String nome;
    
    private double valor;
    
    private LocalDate data;
    
    
    public Gasto() {}
    
    
    public Gasto(String nome, double valor, LocalDate data) {
        this.data = data;
        setNome(nome);
        setValor(valor);
    }

    
    
    public String getNome() { return nome; }
    
    public double getValor() { return valor; }
    
    public Long getId() { return id; }

    
    
	public void setNome(String nome) {
		if(nome.isBlank() || nome.isEmpty()) {throw new IllegalArgumentException("Nome invalido, adicione um nome valido!");}
		this.nome = nome;
	}
	
	public void setValor(double valor) {
		if(valor < 0) { throw new IllegalArgumentException("Valor invalido, adicione um valor valido! ");}
		this.valor = valor;
	}


	public LocalDate getData() {
		return data;
	}


	public void setData(LocalDate data) {
		this.data = data;
	}
    
    
	
}
