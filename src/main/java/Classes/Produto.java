package Classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "produtos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long idProduto;
    
    @Column(name = "nome_produto")
    private String nomeProduto;
    
    @Column(name = "descricao")
    private String descricao;
    
    @Column(name = "preco_produto")
    private double precoProduto;

    protected Produto() {
        // Construtor vazio para JPA
    }

    // Construtor com parâmetros para inicializar os atributos
    public Produto(String nomeProduto, Double precoProduto) {
        this.nomeProduto = nomeProduto;
        this.precoProduto = precoProduto != null ? precoProduto : 0.0; // Garantir que o preço não seja nulo
    }

    // Getters e Setters
    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPrecoProduto() {
        return precoProduto;
    }

    public void setPrecoProduto(double precoProduto) {
        if (precoProduto >= 0) {  // Garantir que o preço não seja negativo
            this.precoProduto = precoProduto;
        } else {
            throw new IllegalArgumentException("Preço do produto não pode ser negativo.");
        }
    }

    @Override
    public String toString() {
        return "Produto [idProduto=" + idProduto + ", nomeProduto=" + nomeProduto + ", descricao=" + descricao
                + ", precoProduto=" + precoProduto + "]";
    }
}
