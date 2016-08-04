package com.ciadainformatica.drogaria.bean;

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

import com.ciadainformatica.drogaria.dao.ItemVendaDAO;
import com.ciadainformatica.drogaria.dao.VendaDAO;
import com.ciadainformatica.drogaria.domain.ItemVenda;
import com.ciadainformatica.drogaria.domain.Venda;





@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class RelatorioVendasBean implements Serializable{

	private List<Venda> vendas;
	HorizontalBarChartModel model;
	


	

	public HorizontalBarChartModel getModel() {
		return model;
	}


	public void setModel(HorizontalBarChartModel model) {
		this.model = model;
	}

	public List<Venda> getVendas() {
		return vendas;
	}


	public void setVendas(List<Venda> vendas) {
		this.vendas = vendas;
	}


	//preenche uma lista com registro de estados
	@PostConstruct // essa anotation diz que o metodo tem que disparar no momento em que a tela é criada 
	public void listar(){
		model = new HorizontalBarChartModel();
		try{
			VendaDAO vendaDAO =  new VendaDAO();
			vendas = vendaDAO.listar();
		} catch (RuntimeException erro) {
			Messages.addGlobalError("Ocorreu um erro ao tentar listar as vendas");
			erro.printStackTrace();
		}
		initBarModel();
	}
	
	
	
    public void initBarModel() {
        ChartSeries modelVendas = new ChartSeries();
        
        modelVendas.setLabel("Vendas");
        
        for(int i = 0; i< vendas.size(); i++){
        	modelVendas.set(vendas.get(i).getCodigo(), vendas.get(i).getValorTotal());
        }
 
        model.addSeries(modelVendas);
        
        model.setTitle("Gráfico de vendas");
        model.setLegendPosition("e");
        model.setStacked(true);
        
        Axis xAxis = model.getAxis(AxisType.X);
        xAxis.setLabel("Código");

         
        Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setLabel("Valor"); 
         
    }
	
	
	
}