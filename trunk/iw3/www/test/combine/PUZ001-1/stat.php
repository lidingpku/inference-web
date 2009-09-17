<?php
define("URL_DEMO_HOME", "stat.php");
$params = get_params();
$params["message"] = "welcome!";
run_sparql($params);

function run_sparql($params){
	if (empty($params["step"])){
		show_form($params);
	}
	if ($params["step"] >= 2){
		if (empty($params["query-uri"])){
			$params["message"] = "missing query-uri";
			show_form($params);
		}
		if (empty($params["from-uri"])){
			$params["message"] = "missing from-uri";
			show_form($params);
		}
		$params["query"]= file_get_contents($params["query-uri"]);
		if (is_array($params["from-uri"]))
			foreach($params["from-uri"] as $from_uri)
				$params["from"].= file_get_contents($from_uri)."\n";
		else
			$params["from"]= file_get_contents($params["from-uri"]);
	}	
		
	if (empty($params["query"])){
		$params["message"] = "empty sparql query";
		show_form($params);
	}
	if (empty($params["from"])){
		$params["message"] = "empty sparql from clause";
		show_form($params);
	}
	
	$pos = stripos($params["query"],"where");
	if (!$pos){
		$params["message"] = "missing \"where\" in sparql query";
		show_form($params);
	}
	$first = substr($params["query"], 0, $pos);
	$rest  = substr($params["query"], $pos);
	
	$sparql = urlencode($first ."\n" . $params["from"]."\n". $rest);
	if (strcasecmp($params["output"], "xml")==0){
		$url = "http://onto.rpi.edu/joseki/sparql?query=". $sparql ;
	}else if (strcasecmp($params["output"], "json")==0){
		$url = "http://plato.cs.rpi.edu:8080/joseki/sparql?query=". $sparql ."&output=json";
	}else{
		$url = "http://data-gov.tw.rpi.edu/ws/xslt.php?xml=". urlencode("http://onto.rpi.edu/joseki/sparql?query=". $sparql)  ."&xsl=".urlencode("http://onto.rpi.edu/xml-to-html.xsl");
	}

	if ($params["step"] == 3){
		$data = file_get_contents($url);
		echo $data;
	}else{
		//echo $url;
		$params["message"]= file_get_contents($url);
		show_form($params);
	}

}

function get_params(){
	$param_names [] = "step";
	$param_names [] = "message";
	
	$param_names [] = "query";
	$param_names [] = "from";
	$param_names [] = "query-uri";
	$param_names [] = "from-uri";
	$param_names [] = "output";

	$params = array();

	foreach ($param_names as $param_name)
		if ($temp=get_param($param_name)){
			$params[$param_name] = $temp;
		}else
			$params[$param_name] = "";
		
	return $params;	
}

function get_param($name){
	global $_GET, $_POST;
	
	$data = $_GET;
	if (!$data)
		$data = $_POST;

	if (!$data)
		return false;

	if (array_key_exists($name, $data)&& isset ($data[$name]))
		return $data[$name];

	return false;
}

function show_form($params){
?><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <link href="http://inference-web.org/iw.css" rel="stylesheet" type="text/css" /> 
</head>
<body>

<fieldset>
<legend>step 1. Edit SPARQL query here, we will get SPARQL query results for you</legend>
<form Method="get" action="<?php echo URL_DEMO_HOME; ?>" >
 <input type="hidden" name="step"  value="1" />
<table border=1>
<tr>
<td>
 OPTION
</td>
<td>
 INPUT
</td>
<td>
 EXAMPLE
</td>
</tr>
<tr>
<td>
 SPARQL Query
</td>
<td>
 <textarea name="query" cols='80' rows='5'><?php echo $params["query"]; ?></textarea>
</td>
<td>
<textarea readonly="readonly" cols ='60' style="background:#CCC" rows='5'>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX pmlj: <http://inference-web.org/2.0/pml-justification.owl#> 
SELECT ?class count(*)
WHERE {  ?s rdf:type ?class } 
group by ?class
</textarea>
</td>
</tr>
<tr>
<td>
 FROM Clause
</td>
<td>
 <textarea name="from" cols='80' rows='5'><?php echo  $params["from"]; ?></textarea>
</td>
<td>
<textarea readonly="readonly" cols ='60'  style="background:#CCC" rows='5'>
FROM <http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Ayane---1.1-answer.owl.rdf>
</textarea>
</td>
</tr>
</table>
<select name="output">
  <option value="html" selected >html</option>
  <option value="xml">xml</option>
  <option value="json">json</option>
</select>
 <input value="query" type="submit" /><br />
</form>
</fieldset>

<fieldset>
<legend>step 2. canned query</legend>
<form Method="get" action="<?php echo URL_DEMO_HOME; ?>" >
 <input type="hidden" name="step"  value="2" />
<select name="query-uri"  SIZE=5>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/stat-class-count.sparql" selected >class count</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/stat-rule-count.sparql" >inference rule count</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/stat-source-list.sparql" >list of sources</option>
</select>
<select name="from-uri[]" MULTIPLE  SIZE=5>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Ayane---1.1-answer.owl.from.sparql" >data-Ayane---1.1</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/EP---1.0-answer.owl.from.sparql" >data-EP---1.0</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Faust---1.0-answer.owl.from.sparql" >data-Faust---1.0</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Metis---2.2-answer.owl.from.sparql" >data-Metis---2.2</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Otter---3.3-answer.owl.from.sparql" >data-Otter---3.3</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/SNARK---20080805r005-answer.owl.from.sparql" >data-SNARK---20080805r005</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/SOS---2.0-answer.owl.from.sparql" >data-SOS---2.0</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Vampire---9.0-answer.owl.from.sparql" >data-Vampire---9.0</option>
</select>
<select name="output">
  <option value="html" selected >html</option>
  <option value="xml">xml</option>
  <option value="json">json</option>
</select>
 <input value="query" type="submit" /><br />
</form>
</fieldset>

<fieldset>
<legend>step 3. create new graph</legend>
<form Method="get" action="<?php echo URL_DEMO_HOME; ?>" >
 <input type="hidden" name="step"  value="3" />
<select name="query-uri" >
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/pml2hg.sparql" selected >class count</option>
</select>
<select name="from-uri" >
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Ayane---1.1-answer.owl.from.sparql" >Ayane---1.1</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/EP---1.0-answer.owl.from.sparql" >EP---1.0</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Faust---1.0-answer.owl.from.sparql" >Faust---1.0</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Metis---2.2-answer.owl.from.sparql" >Metis---2.2</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Otter---3.3-answer.owl.from.sparql" >Otter---3.3</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/SNARK---20080805r005-answer.owl.from.sparql" >SNARK---20080805r005</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/SOS---2.0-answer.owl.from.sparql" >SOS---2.0</option>
  <option value="http://tw2.tw.rpi.edu/pml/PUZ001-1/g1/Vampire---9.0-answer.owl.from.sparql" >Vampire---9.0</option>
</select>
 <input type="hidden" value="xml" selected />
 <input value="query" type="submit" /><br />
</form>
</fieldset>



<hr style="font-family: sans-serif;">

<fieldset style="background:#EEE">
<legend>Message/Results</legend>
<?php echo $params["message"]; ?>
</fieldset>

<hr style="font-family: sans-serif;">


<p><font color="#008000"><i>This page is maintained by <a href="http://www.cs.rpi.edu/~dingl">Li Ding</a>. Page last updated on&nbsp; Sept 1, 2009

</i></font></p>
</body>
</html>

<?php
die();
}
?>