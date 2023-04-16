from flask import Flask, request
import psycopg2
import json

app = Flask(__name__)


def insert_request_to_db(data):
    with psycopg2.connect(database="requests", user="postgres", password="1103", host="212.8.244.157", port="5432") as conn, conn.cursor() as cur:
        print(data)
        cur.execute("INSERT INTO all_req (userID, time, client, path, headers, type, requestTime, requestSize, \
                    responseSize, statusCode, methodName, className, other) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, \
                    %s, %s, %s)", (data["userID"], data["time"], data["client"], data["path"], data["headers"], data["type"], \
                                data["requestTime"], data["requestSize"], data["responseSize"], data["statusCode"], data["methodName"], \
                                    data["className"], data["other"]))
    print("Request inserted into database")

@app.route('/upload', methods=['POST'])
def upload_file():
    if request.method == 'POST':
        data = request.json
        for row in data:
            insert_request_to_db(row)
        return "good"
    return "Need post"

@app.route('/')
def root():
    return "Hello, World!"

if __name__ == '__main__':
    app.run(host = "0.0.0.0")
