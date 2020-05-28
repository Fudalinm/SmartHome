#!/usr/bin/env python
import pika
import os

def getEnvOrDefault(en, default):
    e = os.environ.get(en)
    if e is None:
        return default
    else:
        return e

host = getEnvOrDefault("RABBIT_HOST", "localhost")
password = getEnvOrDefault("RABBIT_PASSWORD", "rabbitmq")
username = getEnvOrDefault("RABBIT_USERNAME", "rabbitmq")
vhost = getEnvOrDefault("RABBIT_VHOST", "smarthome")

env_topic = getEnvOrDefault("MOVE_SENSOR_ENV", "room1.move.env")
info_topic = getEnvOrDefault("MOVE_SENSOR_ENV_INFO", "room1.move.info")

print(host)
print(password)
print(username)
print(vhost)

exchange = getEnvOrDefault("SMARTHOME_EXCHANGE", "smarthome")

credentials = pika.PlainCredentials(username, password)
connection = pika.BlockingConnection(pika.ConnectionParameters(host=host, credentials=credentials, virtual_host=vhost))
channel = connection.channel()

connection2 = pika.BlockingConnection(pika.ConnectionParameters(host=host, credentials=credentials, virtual_host=vhost))
channel2 = connection.channel()

print("Connection Established")

channel.exchange_declare(exchange=exchange, exchange_type='topic')
result = channel.queue_declare('', exclusive=True)
queue_name = result.method.queue
channel.queue_bind(exchange=exchange, queue=queue_name, routing_key=env_topic)

channel2.exchange_declare(exchange=exchange, exchange_type='topic')
result2 = channel2.queue_declare('', exclusive=True)
queue_name2 = result2.method.queue
channel2.queue_bind(exchange=exchange, queue=queue_name2, routing_key=info_topic)

def callback(ch, method, properties, body):
    print("Move_sensor:  RK: " + str(method.routing_key) +  " [x] Received %r" % body, flush=True)
    channel2.basic_publish(exchange=exchange, routing_key=info_topic, body="x")


channel.basic_consume(
    queue=queue_name, on_message_callback=callback, auto_ack=True)

print(' [*] Waiting for messages. To exit press CTRL+C')
channel.start_consuming()
