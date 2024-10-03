package Classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_cliente")
public abstract class Cliente {
    
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;
    
    @Column(name = "nome_cliente")
    //@NotNull
    private String nomeCliente;
    
    @Column(name = "telefone")
    private String telefone;
    
    @Column(name = "email")
    private String email;
    
    protected Cliente() {
        // Construtor vazio para JPA?
    }

    public Cliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public Long getId() {
        return idCliente;
    }

    public void setId(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
