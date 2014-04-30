var express = require("express");
var app = express();

// static server
app.use(express.static(__dirname + '/public'));
app.use(express.bodyParser());

// jade settings
app.set('views', __dirname + '/templates');
app.set('view engine', "jade");
app.engine('jade', require('jade').__express);
app.get("/", function(req, res){
    res.render("index");
});

app.post('/submit/:image', function (req, res){
    var image = req.params.image;
    console.log(image, req.body);
});

var port = Number(process.env.PORT || 5000);
app.listen(port, function() {
    console.log("Listening on " + port);
});
