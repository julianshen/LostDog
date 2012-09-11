var cons = require('consolidate');
var express = require('express');
var util = require('sys');
var dbQuery = require('./LDquery')();

//Initialize upload folder
var uploadFolder = __dirname + '/uploads'
var fs = require('fs');

try {
    var stat = fs.statSync(uploadFolder);
} catch(e) {
    //Assume folder not created
    fs.mkdirSync(uploadFolder);
}

//Init server
var app = express();
app.use(express.logger());
app.use(express.static(__dirname + '/public'));
app.use(express.bodyParser({uploadDir: uploadFolder}));
app.use(express.cookieParser());
  // set this to a secret value to encrypt session cookies
app.use(express.session({ secret: process.env.SESSION_SECRET || 'mysecret11' }));
app.use(require('faceplate').middleware({
    app_id: process.env.FACEBOOK_APPID,
    secret: process.env.FACEBOOK_SECRET
  })
);


// assign the swig engine to .ejs files
app.engine('ejs', cons.qejs);
app.set('view engine', 'ejs');
app.set('views', __dirname + '/views');

//Routing
//Index
app.get('/', function(req,res) {

    dbQuery.postsListAll(function(err, records) {
        res.render('index.ejs', {
            req: req,
            app: app,
            app_id: process.env.FACEBOOK_APPID,
            records: records
        });
    });
});

app.get('/show', function(req, res) {

    dbQuery.postsGet(req['id'], function (err, records) {
        
        res.render('show.ejs', {
            website_url: 'http://heroku.com/',
            req: req,
            app: app,
            app_id: process.env.FACEBOOK_APPID,
            record: records[0]
        });
    });
});

app.get('/picpicker', function(req, res) {
    res.render('picpicker.ejs', {
        req:req,
        app:app,
    });
});

app.post('/_create', function(req, res) {
    console.log('call create: ' + util.inspect(req.body.owner));
        // req.owner: string (facebook ID)
        // req.facebook_id: string
        // req.species: string
        // req.name: string
        // req.breed: string
        // req.gender: 'M' or 'F'
        // req.color: string
        // req.place.lat req.place.long
        // req.photos: an array of string (facebook ID)
    var data = {
        owner: req.body.owner,
        species: req.body.species,
        name: req.body.name,
        breed: req.body.breed,
        gender: req.body.gender,
        color: req.body.color,
        photos: [req.body.photos]};
 
    console.log(util.inspect(data)); 
    dbQuery.postsNew(data, function(err, obj) {
        console.log('callback');
        if(err == null) {
            res.send(util.inspect(obj));
        } else { 
            res.send('[]');
        }
    }); 

});

//Facebook channel files
app.get('/_channel', function(req, res) {
  var cache_expire = 60*60*24*365;
  res.header('Pragma: public');
  res.header('Cache-Control: max-age='+cache_expire);
  res.header('Content-type: text/html');
  res.send('<script src="//connect.facebook.net/en_US/all.js"></script>');
});

//File upload
app.get('/upload', function(req, res) {
  res.render('upload.ejs', {
        req:       req,
        app:       app,
      });
});

app.post('/_upload', function(req, res) {
    if(req.files.upload) {
        //TODO: add file info to database?
    }

    res.send('{}');
});

//For test
app.get('/__events', function(req, res) {
  req.facebook.get('/me/events', { limit: 4 }, function(events) {
    res.send('events: ' + require('util').inspect(events));
  });
});



// listen to the PORT given to us in the environment
var port = process.env.PORT || 3000;
app.listen(port);
