package Classes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "orcamentos")
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orcamento_")
    private Long idOrcamento;

    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente")
    private Cliente cliente;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Column(name = "val_mercadorias")
    private Float valMercadorias;

    @Column(name = "valor_servicos")
    private Float valServicos;

    @Column(name = "val_total")
    private Float valTotal;

    @Column(name = "carro")
    private String carro;

    @Column(name = "placa")
    private String placa;

    // Construtor vazio para JPA
    protected Orcamento() {
    }

    // Construtor com Cliente como parâmetro
    public Orcamento(Cliente cliente) {
        this.cliente = cliente;
        this.valMercadorias = 0.0f; // Valor padrão
        this.valServicos = 0.0f;   // Valor padrão
        this.valTotal = this.valMercadorias + this.valServicos; // Valor inicial calculado
        this.dataHora = LocalDateTime.now(); // Data e hora atual
    }

    // Getters e Setters omitidos para brevidade

    public Long getIdOrcamento() {
        return idOrcamento;
    }

    public void setIdOrcamento(Long idOrcamento) {
        this.idOrcamento = idOrcamento;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Float getValMercadorias() {
        return valMercadorias;
    }

    public void setValMercadorias(Float valMercadorias) {
        this.valMercadorias = valMercadorias;
    }

    public Float getValServicos() {
        return valServicos;
    }

    public void setValServicos(Float valServicos) {
        this.valServicos = valServicos;
    }

    public Float getValTotal() {
        return valTotal;
    }

    public void setValTotal(Float valTotal) {
        this.valTotal = valTotal;
    }

    public String getCarro() {
        return carro;
    }

    public void setCarro(String carro) {
        this.carro = carro;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
}
