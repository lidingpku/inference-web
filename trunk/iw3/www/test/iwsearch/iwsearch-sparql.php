<?php

$name= "IWSearch";
$url_home = "iwsearch-sparql.php?";
$url_remote = "http://plato.cs.rpi.edu:8080/joseki/sparql/tdb-iwsearch?";
$url_service =  "http://".$_SERVER["HTTP_HOST"].$_SERVER["REQUEST_URI"];

if (array_key_exists("query",$_GET)){

	$params = array();
	$params["query"]=$_GET["query"];
	foreach($params as $key =>$value)
		$url_remote .= sprintf("%s=%s&",$key, urlencode($value));

	$output = $_GET["output"];
	switch ($output){
		case "html":
			$params = array();
			$params ["xml"] = $url_remote;
			$params ["xsl"] = "http://onto.rpi.edu/xml-to-html.xsl";
			$url_remote = "http://tw.rpi.edu/ws/xslt.php?";
			foreach($params as $key =>$value)
				$url_remote .= sprintf("%s=%s&",$key, urlencode($value));

			header("Content-Type: html");
			break;
		case "xml":
		default:
			header("Content-Type: application/xml");
	}
	echo file_get_contents($url_remote);
	die();
}
else
{

?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <link href="http://inference-web.org/iw.css" rel="stylesheet" type="text/css" /> 
  <title>SPARQL Query Endpoint - <?php echo $name; ?></title>
</head>
<body>

<h3>SPARQL Query Endpoint - <?php echo $name; ?></h3>
WARNING: please do not run expensive sparql query via this interface.


<fieldset>
<legend>Run SPARQL query </legend>
<form Method="get" action="<?php echo $url_home; ?>" >
  <p>General SPARQL query : input query, set any options and
press "Get Results"</p>
  <textarea style="background-color:#EEE;"
 name="query" cols="70" rows="20">PREFIX rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; 
PREFIX foaf: &lt;http://xmlns.com/foaf/0.1/&gt; 
SELECT ?class count(*) 
WHERE {GRAPH ?g
  { ?s rdf:type ?class}
}
GROUP BY ?class
</textarea>
  <br />
output format:
<select name="output">
  <option value="html" selected>html</option>
  <option value="xml">xml</option>
</select>
  <br />
 <input value="query" type="submit" /><br />
</form>
</fieldset>

<h3>REST Service Interface Description</h3>
Base service URI: &nbsp; <?php echo $url_service; ?><br />
<table style="text-align: left; width: 699px; height: 91px;"
 border="1" cellpadding="2" cellspacing="2">
  <tbody>
    <tr>
      <td style="width: 111px; font-weight: bold;">parameter</td>
      <td style="width: 378px; font-weight: bold;">meaning</td>
    </tr>
    <tr>
      <td style="width: 111px;">query</td>
      <td style="width: 378px;">the SPARQL query string (URL encoded)</td>
    </tr>
    <tr>
      <td style="width: 111px;">output</td>
      <td style="width: 378px;">expected output. possible values: "html", "xml"(default)</td>
    </tr>
  </tbody>
</table>

<hr style="font-family: sans-serif;">

<p><font color="#008000"><i>This page is maintained by <a href="http://www.cs.rpi.edu/~dingl">Li Ding</a>. Page last updated on&nbsp; Sept 10, 2009

</i></font></p>
</body>
</html>

<?php
}
?>