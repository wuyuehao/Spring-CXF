<html>

<!-- Required Stylesheets -->
<link href="./css/bootstrap.css" rel="stylesheet">

<!-- Required Javascript -->
<script src="./js/jquery-2.1.1.js"></script>
<script src="./js/bootstrap-treeview.min.js"></script>

<body>
	<h2>Hello World!</h2>
	
	<table>
		<tr>
			<td><input type="text" name="clazza"/><button id="b1" name="click" onclick=init()>Get Data</button><br><div id="ltree"></div></td>
			<td><input type="text" name="clazzb"/><button id="b1" name="click" onclick=init()>Get Data</button><br><div id="tree"></div></td>
		</tr>
		<table>
</body>
</html>
<script type="text/javascript">

	function ajax(uri, method, data) {
        var request = {
            url: uri,
            type: method,
            contentType: "application/json",
            accepts: "application/json",
            cache: false,
            dataType: 'json',
            data: JSON.stringify(data),
            error: function(jqXHR) {
                console.log("ajax error " + jqXHR.status);
            }
        };
        return $.ajax(request);
    }

	function init() {
		ajax("http://localhost:8080/rs/inspector/com.paypal.riskprofilechanges.EvaluateFinancialInstrumentChangeRequest", 'GET').done(function(ret) {
			$('#tree').treeview({
	            data : ret
	        });
        });
		
	}
</script>