

var ldquery = require('./LDquery')();


ldquery.postsNew({ 'owner': '123456', 'name': 'coffee', 'breed': 'Shina Inu', 'gender': 'M', 'color': 'brown', 'place': {'lat': 123456, 'lon': 789012}, 'photos': []}, function() {

    ldquery.postsListAll(function(err, objs) {

        for (var o in objs) {
            console.dir(objs[o]);
            console.log("==============");
        }

    });

});
