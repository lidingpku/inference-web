package org.inference_web.iwapp.tptp;

import org.inference_web.pml.DataPmlHg;

import com.hp.hpl.jena.rdf.model.Resource;

public class DataPmlHgTptp extends DataPmlHg {

	public DataPmlHgTptp(){
		m_map_color_engine.getColor("other");	
		m_map_color_engine.getColor("EP");
		m_map_color_engine.getColor("SOS");
		m_map_color_engine.getColor("Ayane" );
		m_map_color_engine.getColor("Metis" );
		m_map_color_engine.getColor("Faust" );
		m_map_color_engine.getColor("Otter" );
		m_map_color_engine.getColor("SNARK");
		m_map_color_engine.getColor("Vampire");	
		
		m_map_color_lang.getColor("TPTPCNF");
		m_map_color_lang.getColor("TPTPFOF");
		m_map_color_lang.getColor("other");
	}
	
	@Override
	protected String graphviz_get_languge_color(Resource resLang){
		String name = (null!=resLang)? resLang.getLocalName():"n/a";
		return m_map_color_lang.getColor(name);
	}

	@Override
	protected String graphviz_get_engine_color(Resource resEngine){
		String name = (null!=resEngine)? resEngine.getLocalName():"n/a";
		name =name.replaceAll("[^a-zA-Z]+.+", "");
		return m_map_color_engine.getColor(name);
	}




}
