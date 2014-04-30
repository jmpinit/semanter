var express = require("express");
var app = express();
var fs = require("fs");

var corpusData = {};
var imageNames = fs.readdirSync("./public/corpus");

function progress() {
    var total = imageNames.length;
    var completed = 0;
    
    for(var i in imageNames) {
        if(imageNames[i] in corpusData) {
            completed++;
        }
    }

    return 100*completed/total;
}

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

app.get('/random', function (req, res) {
    var name = imageNames[Math.floor(Math.random()*imageNames.length)]
    res.send({name: name});
});

app.get('/progress', function (req, res) {
    res.send({progress: progress()});
});

app.post('/submit/:image', function (req, res) {
    var image = req.params.image;
    corpusData[image] = req.body;
    console.log(JSON.stringify(corpusData));
});

var port = Number(process.env.PORT || 5000);
app.listen(port, function() {
    console.log("Listening on " + port);
});
