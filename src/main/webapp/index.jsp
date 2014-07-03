<html>

<!-- Required Stylesheets -->
<link href="./css/bootstrap.css" rel="stylesheet">

<!-- Required Javascript -->
<script src="./js/jquery-2.1.1.js"></script>
<script src="./js/bootstrap-treeview.min.js"></script>

<body>
	<h2>PPaas-ASF Mapping Viewer</h2>
	<div class="container-fluid">
		<div class="row">
			<div class="col-lg-5">
				<table width="100%">
					<tr>
						<td><input class="form-control" type="text" id="ltext"
							value="com.paypal.api.platform.riskprofileapi.IdentityProfileDecisionRequest" /></td>
						<td><button class="btn btn-info" id="bl" name="click">Get
								Data</button></td>
					</tr>
				</table>
			</div>
			<div class="col-lg-2">
				<p align="center">
					<button class="btn btn-info" id="bm">Get Mapping</button>
				</p>
			</div>
			<div class="col-lg-5">
				<table width="100%">
					<tr>
						<td><input class="form-control" type="text" id="rtext"
							value="com.paypal.riskprofilechanges.EvaluateIdentityProfileChangeRequest" /></td>
						<td><button class="btn btn-info" id="br" name="click">Get
								Data</button></td>
					</tr>
				</table>
			</div>
		</div>

		<div class="row">
			<div class="col-lg-5">
				<div id="ltree"></div>
			</div>
			<div class="col-lg-2">
				<canvas id="canvas" width="300" height="200"> 
                    Fallback content, in case the browser does not support Canvas. 
                </canvas>
			</div>
			<div class="col-lg-5">
				<div id="rtree"></div>
			</div>
		</div>
	</div>
</body>
<footer id="footer"></footer>
</html>
<script type="text/javascript">
	var canvas = document.getElementById('canvas');
	var context = canvas.getContext('2d');

	$('#bm')
			.click(
					function() {

						ajax("http://localhost:8080/rs/mapping/1", 'GET')
								.done(function(ret) {
									$.each(ret, function(i, o) {
										var start = $('#canvas').offset().top;
				                        canvas.height = $('#footer').offset().top;
				                        alert(o.key);
				                        alert(o.value);
				                        context.beginPath();
				                        context.moveTo(0, $("span:contains('"+o.key+"')")
				                                .offset().top
				                                - start);
				                        context.lineTo(canvas.width, $(
				                                "span:contains('"+o.value+"')")
				                                .offset().top
				                                - start);
				                        context.stroke();
									});
								});
						
					});

	$('#bl').click(function() {
		call($('#ltext').val(), '#ltree');
	});

	$('#br').click(function() {
		call($('#rtext').val(), '#rtree');
	});

	function ajax(uri, method, data) {
		var request = {
			url : uri,
			type : method,
			contentType : "application/json",
			accepts : "application/json",
			cache : false,
			dataType : 'json',
			data : JSON.stringify(data),
			error : function(jqXHR) {
				console.log("ajax error " + jqXHR.status);
			}
		};
		return $.ajax(request);
	}

	function call(classname, view) {
		ajax("http://localhost:8080/rs/inspector/" + classname, 'GET').done(
				function(ret) {
					$(view).treeview({
						data : ret
					});
				});

	}
</script>