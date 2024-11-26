package Classes;

import jakarta.persistence.*;

@Entity
@Table(name = "itens_orcamento")
public class ItemOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_orcamento")
    private Long idItemOrcamento;

    @ManyToOne
    @JoinColumn(name = "id_orcamento")
    private Orcamento orcamento;
    
    @ManyToOne
    @JoinColumn(name = "id_produto")
    private Produto produto;
    
    @Column(name = "preco_un")
    private float precoUn;
    
    @Column(name = "quantidade")
    private int quantidade;
    
    @Column(name = "subtotal")
    private float subtotal;

    // Getters e Setters
    public Long getIdItemOrcamento() {
        return idItemOrcamento;
    }

    public void setIdItemOrcamento(Long idItemOrcamento) {
        this.idItemOrcamento = idItemOrcamento;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {
        this.orcamento = orcamento;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
        // Recalcular o preço unitário se o produto for alterado
        if (produto != null) {
            this.precoUn = (float) produto.getPrecoProduto(); // Garantir que o preço unitário seja atualizado
            atualizarSubtotal();  // Recalcular subtotal ao alterar o produto
        }
    }

    public float getPrecoUn() {
        return precoUn;
    }

    public void setPrecoUn(float precoUn) {
        this.precoUn = precoUn;
        // Recalcular o subtotal sempre que o preço unitário for alterado
        atualizarSubtotal();
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
        // Recalcular o subtotal sempre que a quantidade for alterada
        atualizarSubtotal();
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    // Método privado para atualizar o subtotal
    private void atualizarSubtotal() {
        if (precoUn > 0 && quantidade > 0) {
            this.subtotal = this.precoUn * this.quantidade;
        } else {
            this.subtotal = 0;
        }
    }

    // Callback JPA para garantir o cálculo do subtotal antes de persistir ou atualizar
    @PrePersist
    @PreUpdate
    private void calcularSubtotal() {
        atualizarSubtotal();
    }
}
