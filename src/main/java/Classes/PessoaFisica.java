package Classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("PessoaFisica")
public class PessoaFisica extends Cliente{
    
    @Column(name = "cpf")
    private String cpf;
    
    protected PessoaFisica() {
        // Construtor vazio para JPA
    }

    public PessoaFisica(String nomeCliente) {
        super(nomeCliente);
        this.cpf = "";
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
}
