<?php
$ME = "svgzoom.php";

$url=$_GET["url"];
if (empty($url))
  die();


$scale=$_GET["scale"];
$cwidth=$_GET["cwidth"];
$cheight=$_GET["cheight"];
if (empty($scale)){
	if (empty($cwidth)){
?>
<script type="text/javascript" >
function alertSize() {
  var myWidth = 0, myHeight = 0;
  if( typeof( window.innerWidth ) == 'number' ) {
    //Non-IE
    myWidth = window.innerWidth;
    myHeight = window.innerHeight;
  } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
    //IE 6+ in 'standards compliant mode'
    myWidth = document.documentElement.clientWidth;
    myHeight = document.documentElement.clientHeight;
  } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
    //IE 4 compatible
    myWidth = document.body.clientWidth;
    myHeight = document.body.clientHeight;
  }
<?php
	$url_new= sprintf("%s?url=%s&cwidth=",$ME, $url);
?>
       window.location="<?php echo $url_new; ?>"+ myWidth+"&cheight="+myHeight;
}
</script>
<body onload="alertSize();">
<?php
		die();
	}

	//load partial data
	$content = file_get_contents($url,FILE_TEXT,null, 0, 1000);

	//compute scale
	$pos1= strpos($content,"svg width");
	$pos1 = strpos($content, "\"", $pos1)+1;
	$pos2 = strpos($content, "pt", $pos1);
	$width = intval(substr($content, $pos1,$pos2));
	$scale_x= round(0.55*$cwidth/$width, 2);

	$pos1= strpos($content,"height",pos1);
	$pos1 = strpos($content, "\"", $pos1)+1;
	$pos2 = strpos($content, "pt", $pos1);
	$height= intval(substr($content, $pos1,$pos2));
	$scale_y= round(0.5*$cheight/$height, 2);

	$scale = min($scale_x, $scale_y);
}


$view=$_GET["view"];
if (!empty($view)){
	$content = file_get_contents($url);
	
	//change scale
	$content = preg_replace("/scale\([^\)]+\)/","scale($scale)",$content);
	
	//output
	header("Content-Type: text/xml");
 	echo $content;
}else{
	//show a form and an iframe
	$url_svg= sprintf("%s?url=%s&scale=%s&view=svg",$ME, $url, $scale);
?><html>
<head>
<script type="text/javascript" src="slider/js/range.js"></script>
<script type="text/javascript" src="slider/js/timer.js"></script>
<script type="text/javascript" src="slider/js/slider.js"></script>
<link type="text/css" rel="StyleSheet" href="slider/css/winclassic.css" />
</head>

<body>
<div style="background:#FFD;border:1px $DDD solid">
<table style="width=100%"><tr>
<td>
<form action="<?php echo $ME; ?>" >
	<input  type="text" id="url" name="url" value="<?php echo $url; ?>" size="100"/>  
	<input type="text" id="scale" name="scale" value="<?php echo $scale; ?>"/> 
	<input type="submit" value="update" />
</form>
</td>
<td>
<form action="<?php echo $ME; ?>">
	<input  type="hidden" id="url" name="url" value="<?php echo $url; ?>" size="100"/>  
	<input type="submit" value="reset" />
</form>
</td>
<td>
Resize SVG Image: <div class="slider" id="slider-1" >
  <input class="slider-input" id="slider-input-1"/ tooltip="resize" > 
</div>	
</td>
</tr></table>
</div>

<object type="image/svg+xml" data="<?php echo $url_svg; ?>" />

<script type="text/javascript">
var s = new Slider(document.getElementById("slider-1"), document.getElementById("slider-input-1") );
s.setMinimum(1);
s.setMaximum(100)
s.setValue(document.getElementById("scale").value *100);

s.onchange = function () {
	var v=s.getValue()/100;
	var old_v = document.getElementById("scale").value;
	
	if (v==old_v)
		return;

	document.getElementById("scale").value = v;
<?php
	$url_new= sprintf("%s?url=%s&scale=",$ME, $url);
?>
       window.location="<?php echo $url_new; ?>"+ v;
};

window.onresize = function () {
	s.recalculate();
};
</script>


</body>
</html>
<?php
}
?>