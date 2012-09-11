import os
from flask import Flask
from flask import request, abort, jsonify
import pymongo
import json

app = Flask(__name__)
app.debug=True

def getDb():
    connection = pymongo.Connection(["mongodb://lostdog:53521916@alex.mongohq.com:10018/app7485482"])
    connection.app7485482.set_lasterror_options(w=1)
    return connection.app7485482

@app.route('/')
def hello():
    return 'Lost Dog!'

class Post(object):
    owner=''
    species=''
    name=''
    breed=''
    reward=''
    gender=''
    color=''
    where=None
    photos=None
    founded=False
    def dict(self):
        dic={}
        for k in self.__dict__:
            dic[k]=self.__dict__[k]
        return dic
    
@app.route('/post/create')
def create_post():
    if request.method == 'POST':
        post=Post()
        post.owner=request.form['owner']
        post.species=request.form['species']
        post.name=request.form['name']
        post.breed=request.form['breed']
        post.reward=request.form['reward']
        post.gender=request.form['gender']
        post.color=request.form['color']
        post.founded=request.form['founded']
        db=getDb()
        db.post.save(post.dict())
    else:
        abort(400)

@app.route('/post/list')
def list_post():
    db=getDb()
    cursor=db.post.find()
    lstPosts=[]
    for dic in cursor:
        if '_id' in dic:
            del dic['_id']
        lstPosts.append(dic)
    return jsonify(posts=lstPosts) 

@app.route('/post/<post_id>')
def show_post(post_id):
    # show the post with the given id, the id is an integer
    db=getDb()
    cursor=db.post.find_one({"":post_id})
    if cursor:
        if '_id' in cursor:
            del cursor['_id']
        return jsonify(post=cursor)
    else:
        return jsonify()
    
if __name__ == '__main__':
    # Bind to PORT if defined, otherwise default to 5000.
    port = int(os.environ.get('PORT', 8080))
    app.run(host='0.0.0.0', port=port)
