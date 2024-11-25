package Classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PessoaJuridica")
public class PessoaJuridica extends Cliente {

    @Column(name = "cnpj")
    private String cnpj;

    protected PessoaJuridica() {
        // Construtor vazio necessário para JPA
    }

    public PessoaJuridica(String nomeCliente) {
        super(nomeCliente); // Chama o construtor da classe base Cliente
        this.cnpj = ""; // Inicializa CNPJ como vazio
    }

    public String getCnpj() {
        return cnpj; // Retorna o CNPJ da pessoa jurídica
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj; // Define o CNPJ da pessoa jurídica
    }
}
