package Classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("Servico")
public class Servico extends Produto{
    
    @Column(name = "cod_servico")
    private String codServico;
    
    
    protected Servico() {
        // Construtor vazio para JPA
    }

    public Servico(String nomeProduto, Double precoProduto) {
        super(nomeProduto, precoProduto);
        this.codServico = "";
    }

    public String getCodServico() {
        return codServico;
    }

    public void setCodServico(String codServico) {
        this.codServico = codServico;
    }
  
}