<?php


define("OUTPUT_HTML", "html");

define("URL_IWSEARCH_REGEX", "http://inference-web.org/test/iwsearch/iwsearch_regex.php");
define("URL_SPARQL2JSON", "http://data-gov.tw.rpi.edu/ws/sparql2json.php");
define("SPARQL_TDB_IWSEARCH", "http://plato.cs.rpi.edu:8080/joseki/sparql/tdb-iwsearch?");

$output = $_GET["output"];
$query = $_GET["query"];
$class = $_GET["class"];
$source = $_GET["source"];

if (empty($source)){
   $source="";
}

if (empty($class )){
   $class ="";
}

if (!empty($output)){
	$sparql_uri = URL_IWSEARCH_REGEX . "?query=".$query."&source=".$source."&class=".$class;
	
   $url = sprintf("%s?sparql_uri=%s&service_uri=%s&output=%s",
     URL_SPARQL2JSON,
     urlencode( urlencode( URL_IWSEARCH_REGEX . "?query=".$query."&source=".$source."&class=".$class)),
     urlencode( SPARQL_TDB_IWSEARCH ),
     $output   );

   if ($_GET["debug"]){
      echo "<pre>";
      echo file_get_contents($sparql_uri);
      echo "</pre>";
      echo $url;
     }
      
   echo file_get_contents($url);   
   exit();
}

$query =str_replace("\"","",$query);
if (empty($query)){
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
<form Method="get" action="<?php echo URL_IWSEARCH_REGEX; ?>" >
 query  (required): <input name="query" size="60" value="chip"/><br/>
 class (optional): <input name="class" size="60" value="sensor"/><br/>
 source (optional): <input name="source" size="60" value="utep"/><br/>
 <input name="output" checked value="<?php echo OUTPUT_HTML; ?>" type="radio" />HTML
 <input name="output" value="" type="radio" />none

 <input value="go" type="submit" /><br />
</form>
</fieldset>

<hr style="font-family: sans-serif;">

<p><font color="#008000"><i>This page is maintained by <a href="http://www.cs.rpi.edu/~dingl">Li Ding</a>. Page last updated on&nbsp; Sept 1, 2009

</i></font></p>
</body>
</html>

<?php
exit();
}

$source =str_replace("\"","",$source);


?>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> 
PREFIX foaf: <http://xmlns.com/foaf/0.1/> 
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
SELECT distinct ?subject ?p1 ?p2 ?o ?class ?source
WHERE 
{
  GRAPH ?source
  {
        {
  	   ?subject ?p  ?o1 .
          filter( regex(str(?subject), "<?php echo $query; ?>","i"))
        }
        union
        {
	   ?subject ?p1 ?o .
           filter( regex(str(?o), "<?php echo $query; ?>","i"))
        }
	 union
        {
	   ?subject ?p1 _:x .
	   _:x ?p2 ?o . 
          filter( regex(str(?o), "<?php echo $query; ?>","i"))
        }
        filter(isuri(?subject)) .
        <?php if (!empty($class)) echo " ?subject rdf:type ?class .\n filter(regex(str(?class ),\"$class\", \"i\")) .";  
		else echo "optional {?subject rdf:type ?class .}";?>
  }
  filter(regex(str(?source),"<?php echo $source; ?>","i"))
}
