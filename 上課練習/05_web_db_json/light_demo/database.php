<?php

  //user information
  $host = "127.0.0.1";
  $user = "liao";
  $pass = "liao123";

  //database information
  $databaseName = "lightdb";
  $tableName = "light";


  //Connect to mysql database
  $con = mysql_connect($host,$user,$pass);
  $dbs = mysql_select_db($databaseName, $con);


  //Query database for data
  $result = mysql_query("SELECT * FROM $tableName");

  //store matrix
  $i=0;
  while ($row = mysql_fetch_array($result)){
    $employee[$i]=$row;
    $i++;
  }

  //echo result as json 
    echo json_encode($employee);
?>
