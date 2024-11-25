package Classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PessoaFisica")
public class PessoaFisica extends Cliente {

    @Column(name = "cpf")
    private String cpf;

    protected PessoaFisica() {
        // Construtor vazio necessário para JPA
    }

    public PessoaFisica(String nomeCliente) {
        super(nomeCliente); // Chama o construtor da classe base Cliente
        this.cpf = ""; // Inicializa CPF como vazio
    }

    public String getCpf() {
        return cpf; // Retorna o CPF da pessoa física
    }

    public void setCpf(String cpf) {
        this.cpf = cpf; // Define o CPF da pessoa física
    }
}
