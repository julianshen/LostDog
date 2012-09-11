var mongo = require('mongodb');


var db_server = "alex.mongohq.com";
var db_port = 10018;
var db_name = "app7485482";
var db_account = "lostdog";
var db_password = "53521916";
var collection_name = 'post';

module.exports = function() {
    var dbContext = null; 

    function _connect( callback ) {

        dbContext = new mongo.Db(db_name, new mongo.Server(db_server, db_port, {auto_reconnect: true}));
        dbContext.open(function(err, dbContext) {
            dbContext.authenticate(db_account, db_password, function() {
                if (err) {
                    return callback(err, "failed to connect");
                }

                var collection = new mongo.Collection(dbContext, collection_name);

                callback(null, collection);
            });
        });
            
    }

    function _postsQuery(query, callback) {
    
        _connect( function(err, collection) {

            if (err !== null) {
                console.log("fail to query db " + err);
                throw new Error(err);
            }
        
            collection.find(query).toArray(callback);
            
        });

    }

    function postsListAll(callback) {

        // callback: function(err, records)
        _postsQuery( {}, function(err, objs) {
            dbContext.close();  
            callback(err, objs);
        });

    }

    function postsNew(req, callback) {
        // req.owner: string (facebook ID)
        // req.species: string
        // req.name: string
        // req.breed: string
        // req.gender: 'M' or 'F'
        // req.color: string
        // req.place.lat req.place.long
        // req.photos: an array of string (facebook ID)     

        if (!req.owner) 
            return false;

        if (!req.name)
            return false;
    
        var newRecord = {
            owner: req.owner,
            species: req.species || 'Unicorn',
            name: req.name,
            breed: req.breed || '',
            reward: req.reward || '',
            geneder: req.gender || '',
            color: req.color || '',
            where: req.place || {lat: -1, lon:-1},
            photos: req.photos || [],
            founded: false
        };

        _connect( function (err, obj) {
            if (err !== null) {
                throw new Error(err);
            }

            var collection = obj;

            collection.insert(newRecord, {save: true}, function(err, objs) {
                dbContext.close();
                if (callback)
                    callback(err, objs);
            });
        });
        
    }

    function searchNearby(loc, callback) {
        // loc.lat
        // loc.lon

        postsListAll(callback);

    }


    return {
        postsListAll: postsListAll,
        postsNew: postsNew,
        searchNearby: searchNearby,
        close: function() {
            if (dbContext !== null)
                dbContext.close();
        }
    };
};

