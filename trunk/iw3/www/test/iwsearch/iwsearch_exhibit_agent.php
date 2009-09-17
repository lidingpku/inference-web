<?php

$str = file_get_contents("http://data-gov.tw.rpi.edu/ws/sparql2json.php?sparql_uri=http%3A%2F%2Finference-web.org%2Fpub%2Fiwsearch%2Fiwsearch_exhibit_agent.sparql&output=exhibit&service_uri=http%3A%2F%2Fonto.rpi.edu%2Fjoseki%2Fsparql&service_uri=http%3A%2F%2Fplato.cs.rpi.edu%3A8080%2Fjoseki%2Fsparql%2Ftdb-iwsearch%3F");
echo $str;

?>