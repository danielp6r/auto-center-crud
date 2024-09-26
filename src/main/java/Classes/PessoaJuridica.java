package Classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("PessoaJuridica")
public class PessoaJuridica extends Cliente{
    
    @Column(name = "cnpj")
    private String cnpj;
    
    protected PessoaJuridica() {
        // Construtor vazio para JPA
    }

    public PessoaJuridica(String nomeCliente) {
        super(nomeCliente);
        this.cnpj = "";
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
    
}
