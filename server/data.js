var mongoose = require('mongoose');

mongoose.connect('mongodb://localhost/lostdog');

var Schema = mongoose.Schema
  , ObjectId = Schema.ObjectId;

var LostDogSchema = new Schema({
   name : String,
   breed : {type:String, default: 'mix'},
   gender : {type:String, default: 'female'},
   color : {type:String, default: 'unknown'},
   owner : String,
   photo : String,
   founded : {type:Boolean, default: false}
});

var LostDog = mongoose.model('LostDogSchema', LostDogSchema);


