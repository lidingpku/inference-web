<?php
/**
sample code

$cls = new ProcessSolution();
$cls->init(
	dirname(__FILE__)."/..",
	"http://inference-web.org/test/combine",
	"http://inference-web.org/proofs/tptp/Solutions/",
	dirname(__FILE__),
	"/PUZ/PUZ001-1" );
$cls->run;

*/
class ProcessSolution{
	var $urls_pml = array();
	var $urls_pml_eq = array();
	var $urls_pml_plus = array();
	var $map_g1 = array();
	var $map_g2 = array();

	var $url_pml_root;
	var $url_root;
	var $dir_my_root ;
	var $url_my_root ;
	var $dir_sparql_root ;



	
	public function init($root_dir, $root_url, $root_pml, $root_sparql_dir,$path_test){
		$this->url_root = $root_url;
		$this->dir_my_root= $root_dir.$path_test ;
		$this->url_my_root= $root_url.$path_test;
		$this->url_pml_root = $root_pml.$path_test;
		$this->dir_sparql_root= $root_sparql_dir ;

		$paths =array();
		
		$paths []= "Ayane---1.1";
		$paths []= "EP---1.0";
		$paths []= "Faust---1.0";
		$paths []= "Metis---2.2";
		$paths []= "Otter---3.3";
		$paths []= "SNARK---20080805r005";
		$paths []= "SOS---2.0";
		$paths []= "Vampire---9.0";
		
		foreach ($paths as $path){
			$this->urls_pml [] = $this->url_pml_root."/".$path."/answer.owl";
			$this->urls_pml_eq [] = $this->url_pml_root."/".$path."/equalNS.owl";
		}
	}
	
	public function run(){
		$this->run_g00();
		$this->run_g0();
		$this->run_g1();
		$this->run_g2();
		$this->run_mapping_i();
	}

	
	private function get_local_name($url_pml){
		$ret = substr($url_pml,strlen($this->url_pml_root)+1);
		$ret = str_replace("/","-",$ret);
		return $ret;
	}

	private function build_ws_url($service_url,$params){
		$ret = $service_url;
		foreach($params as $key=>$value)
			$ret.= "$key=$value&";
		return $ret;
	}
	
	public function run_g00(){
		$dir = "g00";
		$dir_root = $this->dir_my_root."/".$dir;
		$url_root = $this->url_my_root."/".$dir;
		
		if (!file_exists($dir_root))
			mkdir($dir_root, 0755, true);
		
		$my_filename = "answer";

		foreach ($this->urls_pml as $url_pml){
			$target_base = $this->get_local_name( $url_pml );
			$file_target_rdf  = $dir_root."/".$target_base.".rdf";
			$url_target_rdf  = $url_root."/".$target_base.".rdf";
			$file_target_sparql  = $dir_root."/".$target_base.".from.sparql";
			$url_target_sparql  = $url_root."/".$target_base.".from.sparql";
			$file_target_sparql_named  = $dir_root."/".$target_base.".from.named.sparql";
			$url_target_sparql_named  = $url_root."/".$target_base.".from.named.sparql";


			$content = file_get_contents($url_pml);
			$filename = $file_target_rdf;
			$content= str_replace($url_pml, $url_target_rdf, $content);
			file_put_contents($filename, $content);
			
			$content = "FROM  <$url_target_rdf>";
			$filename = $file_target_sparql;
			file_put_contents($filename, $content);	

			$content = "FROM NAMED <$url_target_rdf>";
			$filename = $file_target_sparql_named;
			file_put_contents($filename, $content);	

			$this->urls_pml_plus [] = $url_target_rdf;
		}
	}	
	
	public function run_g0(){
		$dir = "g0";
		$dir_root = $this->dir_my_root."/".$dir;
		$url_root = $this->url_my_root."/".$dir;
		
		if (!file_exists($dir_root))
			mkdir($dir_root, 0755, true);
		
		$my_filename = "answer";

		foreach ($this->urls_pml as $url_pml){
			$target_base = $this->get_local_name( $url_pml );
			$file_target_rdf  = $dir_root."/".$target_base.".rdf";
			$url_target_rdf  = $url_root."/".$target_base.".rdf";
			$file_target_sparql  = $dir_root."/".$target_base.".from.sparql";
			$url_target_sparql  = $url_root."/".$target_base.".from.sparql";
			$file_target_sparql_named  = $dir_root."/".$target_base.".from.named.sparql";
			$url_target_sparql_named  = $url_root."/".$target_base.".from.named.sparql";

			$params["option"] = "sign";
			$params["url"] = urlencode ( $url_pml );
			$url = $this->build_ws_url(	"http://onto.rpi.edu/sw4j/norm?", $params );

			$content = file_get_contents($url);
			$filename = $file_target_rdf;
			$content= str_replace($url_pml, $url_target_rdf, $content);
			file_put_contents($filename, $content);
			
			$content = "FROM  <$url_target_rdf>";
			$filename = $file_target_sparql;
			file_put_contents($filename, $content);	

			$content = "FROM NAMED <$url_target_rdf>";
			$filename = $file_target_sparql_named;
			file_put_contents($filename, $content);	

			$this->urls_pml_plus [] = $url_target_rdf;
		}
	}
		
	public function run_g1(){
		$dir = "g1";
		$dir_root = $this->dir_my_root."/".$dir;
		$url_root = $this->url_my_root."/".$dir;
		
		if (!file_exists($dir_root))
			mkdir($dir_root, 0755, true);
		
		$my_filename = "answer";

		foreach ($this->urls_pml as $url_pml){
			$target_base = $this->get_local_name( $url_pml );
			$file_target_rdf  = $dir_root."/".$target_base.".rdf";
			$url_target_rdf  = $url_root."/".$target_base.".rdf";
			$file_target_sparql  = $dir_root."/".$target_base.".from.sparql";
			$url_target_sparql  = $url_root."/".$target_base.".from.sparql";
			$file_target_sparql_named  = $dir_root."/".$target_base.".from.named.sparql";
			$url_target_sparql_named  = $url_root."/".$target_base.".from.named.sparql";

			$params["option"] = "sign,dlist";
			$params["url"] = urlencode ( $url_pml );
			$url = $this->build_ws_url(	"http://onto.rpi.edu/sw4j/norm?", $params );

			$content = file_get_contents($url);
			$filename = $file_target_rdf;
			$content= str_replace($url_pml, $url_target_rdf, $content);
			file_put_contents($filename, $content);
			
			$content = "FROM  <$url_target_rdf>";
			$filename = $file_target_sparql;
			file_put_contents($filename, $content);	

			$content = "FROM NAMED <$url_target_rdf>";
			$filename = $file_target_sparql_named;
			file_put_contents($filename, $content);	

			$this->urls_pml_plus [] = $url_target_rdf;
		}
	}
	
	public function run_g2(){
		$dir = "g2";
		$dir_root = $this->dir_my_root."/".$dir;
		$url_root = $this->url_my_root."/".$dir;
		
		if (!file_exists($dir_root))
			mkdir($dir_root, 0755, true);
		
		$my_filename = "answer";

		foreach ($this->urls_pml_plus as $url_pml_plus){
			$pos1 = strrpos($url_pml_plus, "/")+1;
			$pos2 = strrpos($url_pml_plus, ".");
			$target_base = substr($url_pml_plus,$pos1,$pos2-$pos1);
			$url_input_from_sparql  =  substr($url_pml_plus,0,$pos2).".from.named.sparql";

			$file_target_rdf  = $dir_root."/".$target_base.".rdf";
			$url_target_rdf  = $url_root."/".$target_base.".rdf";
			$file_target_sparql  = $dir_root."/".$target_base.".from.sparql";
			$url_target_sparql  = $url_root."/".$target_base.".from.sparql";
			$file_target_sparql_named  = $dir_root."/".$target_base.".from.named.sparql";
			$url_target_sparql_named  = $url_root."/".$target_base.".from.named.sparql";

			$params["step"] =  3;
			$params["output"] =  "xml";
			$params["query-uri"] =  urlencode ($this->dir_sparql_root."/pml2hg.sparql");
			$params["from-uri"] =  urlencode ( $url_input_from_sparql );

			$url = $this->build_ws_url(	$this->url_root. "/demo/stat.php?", $params );
	

			$content = file_get_contents($url);
			$filename = $file_target_rdf;
			file_put_contents($filename, $content);
			
			$content = "FROM <$url_target_rdf>";
			$content .= "\nFROM <$url_pml_plus>";
			$filename = $file_target_sparql;
			file_put_contents($filename, $content);	

			$content = "FROM NAMED <$url_target_rdf>";
			$content .= "\nFROM NAMED <$url_pml_plus>";
			$filename = $file_target_sparql_named;
			file_put_contents($filename, $content);	
		}
	}

	public function run_mapping_i(){
		$file_mapping_i_from = $this->dir_my_root ."/mapping_i.from.sparql";
		$url_mapping_i_from = $this->url_my_root ."/mapping_i.from.sparql";
		$url_mapping_i_query = $this->dir_sparql_root ."/mapping_i.query.sparql";
		$file_mapping_i = $this->dir_my_root ."/mapping_i.rdf";
		$url_mapping_i = $this->url_my_root ."/mapping_i.rdf";
		
		$content ="";
		foreach($this->urls_pml_plus as $url){
			$content .= "FROM <$url>\n";
		}
		foreach($this->urls_pml_eq as $url){
			$content .= "FROM <$url>\n";
		}
		file_put_contents($file_mapping_i_from,$content);
		
		//get first pass
		$params["step"] =  3;
		$params["output"] =  "xml";
		$params["query-uri"] =  urlencode ($url_mapping_i_query);
		$params["from-uri"] =  urlencode ( $url_mapping_i_from );		
		$url = $this->build_ws_url(	$this->url_root. "/demo/stat.php?", $params );

		$content = file_get_contents($url);
		$filename = $file_mapping_i;
		file_put_contents($filename, $content);
	}
}
?>