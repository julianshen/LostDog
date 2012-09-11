

var ldquery = require('./LDquery')();


ldquery.postsNew({ 'owner': '123456', 'species': 'dog', 'name': 'coffee', 'reward': 'a kiss', 'breed': 'Shina Inu', 'gender': 'M', 'color': 'brown', 'place': {'lat': 25.044198, 'lon': 121.551533}, 'photos': []}, function() {

    ldquery.postsListAll(function(err, objs) {

        for (var o in objs) {
            console.dir(objs[o]);
            console.log("==============");
        }

    });

});
