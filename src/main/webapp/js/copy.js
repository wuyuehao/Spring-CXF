var clippedEl = document.getElementById("copy-button");
var client = new ZeroClipboard(clippedEl);
client.on('ready', function(event) {
	console.log('movie is loaded');

	client.on('copy', function(event) {
		event.clipboardData.setData('text/plain', "test");
		console.log('data is set');
	});

	client.on('aftercopy', function(event) {
		console.log('Copied text to clipboard');
	});
});

client.on('error', function(event) {
	// console.log( 'ZeroClipboard error of type "' + event.name + '": '
	// + event.message );
	ZeroClipboard.destroy();
});