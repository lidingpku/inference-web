<?php
define("URL_DEMO_HOME", "http://inference-web.org/test/iwsearch/iwsearch_test1.php");
define("URL_DEMO_SPARQL", "http://inference-web.org/test/iwsearch/iwsearch_sparql_list_instance.php");
define("URL_SPARQL2JSON", "http://data-gov.tw.rpi.edu/ws/sparql2json.php");
define("SPARQL_TDB_IWSEARCH", "http://plato.cs.rpi.edu:8080/joseki/sparql/tdb-iwsearch?");


$uri=$_GET["uri"];
if (empty($uri)){
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <link href="http://inference-web.org/iw.css" rel="stylesheet" type="text/css" /> 
</head>
<body>

<h3>Inference Web DEMO: Search for PML Instances</h3>

<fieldset>
<legend>demo 1:sample sparql queries on registry data</legend>
<form Method="get" action="<?php echo URL_SPARQL2JSON; ?>" >
 <select name="sparql_uri">
  <option value="http://inference-web.org/test/iwsearch/iwsearch_count_triples.sparql">count loaded triples</option>
  <option value="http://inference-web.org/test/iwsearch/iwsearch_list_graph.sparql">list graph and count triples in the graph</option>
  <option value="http://inference-web.org/test/iwsearch/iwsearch_list_class.sparql">list classes and the number of their instances (with uri)</option>
  <option value="http://inference-web.org/test/iwsearch/iwsearch_list_class_all.sparql">list classes and the number of their instances (all,including blank node)</option>
  <option value="http://inference-web.org/test/iwsearch/iwsearch_list_person.sparql">list instance of pmlp:Person</option>
  <option value="http://inference-web.org/test/iwsearch/iwsearch_list_agent.sparql">list instance of pmlp:Agent (with OWL inf, including pmlp:Person, pmlp:Organization,..)</option>
  <option value="http://inference-web.org/test/iwsearch/iwsearch_complex_ref.sparql">list instances referencing an external instance (limit 100)</option>
  <option value="http://inference-web.org/test/iwsearch/iwsearch_regex_pdf.sparql">list instances related to "PDF"</option>
  <option value="http://inference-web.org/test/iwsearch/iwsearch_regex_smith.sparql">list instances related to "smith"</option>
</select>
 <input type="hidden" name="output" value="html">
 <input type="hidden" name="service_uri" value="<?php echo SPARQL_TDB_IWSEARCH; ?>">
 <input value="go" type="submit" /><br />
</form>
</fieldset>



<fieldset>
<legend>demo 2: list instance of pmlp class</legend>
<form Method="get" action="<?php echo URL_SPARQL2JSON; ?>" >
 <select name="sparql_uri">
<?php
$content = file_get_contents("http://data-gov.tw.rpi.edu/ws/sparql2json.php?sparql_uri=http%3A%2F%2Finference-web.org%2Fpub%2Fiwsearch%2Fiwsearch_list_class.sparql&output=sparql&service_uri=http%3A%2F%2Fplato.cs.rpi.edu%3A8080%2Fjoseki%2Fsparql%2Ftdb-iwsearch%3F");
$data = json_decode($content);

foreach($data->results->bindings as $binding){
	echo "<option value=\"".urlencode(URL_DEMO_HOME."?uri=". urlencode($binding->class->value)) ."\">". $binding->class->value. "</option>";
}


	
?>
</select>
 <input type="hidden" name="output" value="html">
 <input type="hidden" name="service_uri" value="<?php echo SPARQL_TDB_IWSEARCH; ?>">
 <input value="go" type="submit" /><br />
</form>
</fieldset>

<fieldset>
<legend>demo 3: faceted browsing on agents</legend>
<a href="http://inference-web.org/test/iwsearch/iwsearch_exhibit_agent.html">http://inference-web.org/test/iwsearch/iwsearch_exhibit_agent.html</a>
</fieldset>

<fieldset>
<legend>demo 4: search for instances by keyword and source</legend>
<a href="http://inference-web.org/test/iwsearch/iwsearch_regex.php">http://inference-web.org/test/iwsearch/iwsearch_regex.php</a>
</fieldset>

<hr style="font-family: sans-serif;">

<p><font color="#008000"><i>This page is maintained by <a href="http://www.cs.rpi.edu/~dingl">Li Ding</a>. Page last updated on&nbsp; Sept 10, 2009

</i></font></p>
</body>
</html>

<?php
}else{

?>PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> 
PREFIX foaf: <http://xmlns.com/foaf/0.1/> 
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
SELECT ?subject ?class ?source
WHERE 
{
  ?class rdfs:subClassOf <<?php echo $uri; ?>> .
  GRAPH ?source
  {
	?subject rdf:type ?class .
  }
}
<?php

}

?>