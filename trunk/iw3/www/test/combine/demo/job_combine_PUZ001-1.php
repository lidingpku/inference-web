<?php

include_once("cls_solution.php");

$cls = new ProcessSolution();
$cls->init(
	dirname(__FILE__)."/..",
	"http://inference-web.org/test/combine",
	"http://inference-web.org/proofs/tptp/Solutions/",
	dirname(__FILE__),
	"/PUZ/PUZ001-1" );
$cls->run;

?>