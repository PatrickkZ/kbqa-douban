"""
处理前端请求，需要启动Fuseki服务器
"""

from query.query_main import Query

from flask import Flask, request
from flask_cors import *
import json

app = Flask(__name__)

query = Query()


@app.route('/question', methods=['GET', 'POST'])
@cross_origin()
def handle_request():
    question_json = json.loads(request.data)
    question = str(question_json['question']).replace('\n', '')
    ans = query.parse(question)
    return ans


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8081, debug=True)
