var mongo = require('mongodb');


var db_name = "lost_dog";
var db_server = "127.0.0.1";
var db_port = 27017;
var collection_name = 'Post';

module.exports = function() {
    var dbContext = null; 

    function _connect( callback ) {

        dbContext = new mongo.Db(db_name, new mongo.Server(db_server, db_port, {auto_reconnect: true}));
        dbContext.open(function(err, dbContext) {
            if (err) {
                return callback(err, "failed to connect");
            }

            var collection = new mongo.Collection(dbContext, collection_name);

            callback(null, collection);
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
        // req.name: string
        // req.breed: string
        // req.gender: string
        // req.color: string
        // req.place.lat req.place.long
        // req.photos: an array of string (facebook ID)     

        if (!req.owner) 
            return false;

        if (!req.name)
            return false;
    
        var newRecord = {
            owner: req.owner,
            name: req.name,
            breed: req.breed || '',
            color: req.color || '',
            place: req.place || {lat: -1, lon:-1},
            photos: req.photos || []
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

