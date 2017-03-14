<?php
require_once 'controller/Controller.php';

session_start();
$c = new Controller();
try {
	$c->createEvent($_POST['event_name'], $_POST['event_date'], $_POST['starttime'], $_POST['endtime']);
	$_SESSION["errorEventName"] = "";
	$_SESSION["errorEventDate"] = "";
	$_SESSION["errorEventStart"] = "";
	$_SESSION["errorEventEnd"] = "";
} catch (Exception $e) {
	$_SESSION["errorEventName"] = $e->getMessage();
	$_SESSION["errorEventDate"] = $e->getMessage();
	$_SESSION["errorEventStart"] = $e->getMessage();
	$_SESSION["errorEventEnd"] = $e->getMessage();
}
?>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="refresh" content="0; url=/EventRegistration/" />
	</head>
</html>