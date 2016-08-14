package com.ciadainformatica.vendas.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


import org.omnifaces.util.Messages;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;

import com.ciadainformatica.vendas.dao.ItemVendaDAO;
import com.ciadainformatica.vendas.domain.ItemVenda;





@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class RelatorioItensVendidosBean implements Serializable{

	private List<ItemVenda> itens;
	HorizontalBarChartModel model;
	


	

	public HorizontalBarChartModel getModel() {
		return model;
	}


	public void setModel(HorizontalBarChartModel model) {
		this.model = model;
	}


	public List<ItemVenda> getItens() {
		return itens;
	}


	public void setItens(List<ItemVenda> itens) {
		this.itens = itens;
	}


	//preenche uma lista com registro de estados
	@PostConstruct // essa anotation diz que o metodo tem que disparar no momento em que a tela é criada 
	public void listar(){
		model = new HorizontalBarChartModel();
		try{
			ItemVendaDAO itemVendaDAO =  new ItemVendaDAO();
			itens = itemVendaDAO.listar();
		} catch (RuntimeException erro) {
			Messages.addGlobalError("Ocorreu um erro ao tentar listar os estados");
			erro.printStackTrace();
		}
		initBarModel();
	}
	
	
	
    public void initBarModel() {
        ChartSeries produtos = new ChartSeries();
        
        produtos.setLabel("Produtos");
        
        for(int i = 0; i< itens.size(); i++){
        	produtos.set(itens.get(i).getProduto().getDescricao(), itens.get(i).getQuantidade());
        }
 
        model.addSeries(produtos);
        
        model.setTitle("Gráfico de produtos vendidos");
        model.setLegendPosition("e");
        model.setStacked(true);
        
        Axis xAxis = model.getAxis(AxisType.X);
        xAxis.setLabel("Quantidade");

         
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setLabel("Produto"); 
         
    }
	
	
	
}