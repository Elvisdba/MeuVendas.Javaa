package com.ciadainformatica.vendas.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Messages;

import com.ciadainformatica.vendas.dao.ItemVendaDAO;
import com.ciadainformatica.vendas.dao.ProdutoDAO;
import com.ciadainformatica.vendas.dao.UsuarioDAO;
import com.ciadainformatica.vendas.dao.VendaDAO;
import com.ciadainformatica.vendas.domain.Cliente;
import com.ciadainformatica.vendas.domain.Funcionario;
import com.ciadainformatica.vendas.domain.ItemVenda;
import com.ciadainformatica.vendas.domain.Produto;
import com.ciadainformatica.vendas.domain.Usuario;
import com.ciadainformatica.vendas.domain.Venda;






@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class VendaBean implements Serializable{

	private ItemVenda item;
	private List<ItemVenda> itens;
	private Venda venda;
	private Produto produto;
	List<Produto> produtos;
	public String codigo;
	public short quantidade = 1;
	public BigDecimal valorTotal;
	public Funcionario funcionario;
	public Cliente cliente;
	private BigDecimal dinheiro;
	private BigDecimal troco;
	
	
	
	public BigDecimal getDinheiro() {
		return dinheiro;
	}
	public void setDinheiro(BigDecimal dinheiro) {
		this.dinheiro = dinheiro;
	}
	public BigDecimal getTroco() {
		return troco;
	}
	public void setTroco(BigDecimal troco) {
		this.troco = troco;
	}
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	public ItemVenda getItem() {
		return item;
	}
	public void setItem(ItemVenda item) {
		this.item = item;
	}
	public List<ItemVenda> getItens() {
		return itens;
	}
	public void setItens(List<ItemVenda> itens) {
		this.itens = itens;
	}
	public Venda getVenda() {
		return venda;
	}
	public void setVenda(Venda venda) {
		this.venda = venda;
	}
	
	public Produto getProduto() {
		return produto;
	}
	public void setProduto(Produto produto) {
		this.produto = produto;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	
	public short getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(short quantidade) {
		this.quantidade = quantidade;
	}
	
	public Funcionario getFuncionario() {
		return funcionario;
	}
	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}	
	
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	
	
	
	@PostConstruct // essa anotation diz que o metodo tem que disparar no momento em que a tela é criada 
	public void listar(){
		itens = new ArrayList<ItemVenda>();
		item = new ItemVenda();
		produto = new Produto();
		funcionario = new Funcionario();
		cliente = new Cliente();
		venda = new Venda();
		dinheiro = new BigDecimal("0");
		
	}
	
	public void adicionarItem(){
		
		if(funcionario != null && funcionario.getCodigo().toString() != ""){
			
			produtos = new ArrayList<Produto>();
			produto = new Produto();
			if(codigo == null || codigo.trim().equals("")){
				Messages.addGlobalError("A pesquisa está vazia");
			}else{
				
				//capturando dados para pesquisa
				try{
				    produto.setCodigo(Long.parseLong(codigo));
				}catch(NumberFormatException e){
					//System.out.println(codigo);
					produto.setDescricao(codigo);
				}
				
				try{
					codigo = "";
					ProdutoDAO produtoDAO = new ProdutoDAO();
					produtos = produtoDAO.pesquisar(produto);
					
				}catch(Exception erro){
					Messages.addGlobalError("Nenhum produto encontrado!");
				}
				
				
				if(produtos.size() == 1){//quando produto é encontrado
					item.setProduto(produtos.get(0));
					item.setFuncionario(funcionario);
					
					
					if(quantidade > 0){
						item.setQuantidade(quantidade);					
						quantidade = 1;
					}else{
						Messages.addGlobalError("não é permitido quantidade menor que 1, logo foi atribuido 1 a quantidade!");
						quantidade = 1;
						item.setQuantidade(quantidade);
					}
					
					for(int i = 0; i<itens.size(); i++){
						if(item.getProduto().getCodigo() == itens.get(i).getProduto().getCodigo()){
							quantidade = (short) (item.getQuantidade() + itens.get(i).getQuantidade());
							item.setQuantidade(quantidade);
							itens.remove(i);
							quantidade = 1;
						}
					}
					item.setValorParcial(item.getProduto().getPreco().multiply(new BigDecimal(item.getQuantidade())));
					//até a linha anterior o item já armazenou os valores que necessita menos venda
					
					itens.add(0,item);	
					item = new ItemVenda();
					valorTotal();
					
					
					
					
				}else if(produtos.size() == 0){
					Messages.addGlobalError("Nenhum produto encontrado!");
				}else{
					Messages.addGlobalError("Erro!");
				}
			}
		}else{
			Messages.addGlobalError("Adicione um funcionario para a venda!");
		}
	}
	
	
	public void excluir(ActionEvent evento){
		try{
				item =(ItemVenda) evento.getComponent().getAttributes().get("itemSelecionado");
				
				
				for(int i = 0; i<itens.size(); i++){
					if(item.getProduto().getCodigo() == itens.get(i).getProduto().getCodigo()){
						itens.remove(i);
						Messages.addGlobalError(item.getProduto().getDescricao()+" exlcuido com sucesso!");
						valorTotal();
					}
				}
				

			}catch(Exception erro){
				Messages.addGlobalError("Ouve um erro na hora de apagar o usuario");
				erro.printStackTrace();
			}
	}
	
	public void valorTotal(){
		valorTotal = new BigDecimal(0);	
		for(int i = 0; i < itens.size(); i++){
			
			valorTotal = valorTotal.add(itens.get(i).getValorParcial());	
		}
		


	}

	
	public void calcularTroco(){
		if(dinheiro.compareTo(new BigDecimal(0)) == 1 && dinheiro != null){
			try{
				troco = dinheiro.subtract(valorTotal);
			}catch(Exception erro){
				Messages.addGlobalError("ocorreu um erro a calcular o troco");
			}
		}
	}
	
	public void salvarVenda(){
		if(valorTotal.compareTo(new BigDecimal(0)) == 1){
			java.util.Date data = new java.util.Date();
			java.sql.Timestamp timestamp = new java.sql.Timestamp(data.getTime());
			VendaDAO vendaDAO = new VendaDAO();
			ItemVendaDAO itemVendaDAO = new ItemVendaDAO();
			ProdutoDAO produtoDAO = new ProdutoDAO();
			produto = new Produto();
			
			try{
				
				System.out.println("entrou");
				venda.setValorTotal(valorTotal);
				venda.setHorario(timestamp);
				venda.setCliente(cliente);
				
				vendaDAO.salvar(venda);
				
				for(int i = 0; i < itens.size(); i++){
					System.out.println("entrou2");
					item = itens.get(i);
					item.setVenda(venda);
					itemVendaDAO.salvar(item);
					
					//atualizar estoque
					produto = produtoDAO.buscar(item.getProduto().getCodigo());
					short aux = (short) (produto.getQuantidade()- item.getQuantidade());
					produto.setQuantidade(aux);
					produtoDAO.merge(produto);
					
					
					produto = new Produto();
					aux = 0;
					
					
				}
		
				
				
				valorTotal = new BigDecimal(0);
				item = new ItemVenda();
				funcionario = new Funcionario();
				cliente = new Cliente();
				itens.clear();	
				
				
			}catch(Exception erro){
				Messages.addGlobalError("Ouve um erro ao tentar salvar a venda!");			
			}
		}else{
			Messages.addGlobalError("valor total não deve estar zerado");
		}
	}


}