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

print(host)
print(password)
print(username)
print(vhost)

exchange = getEnvOrDefault("SMARTHOME_EXCHANGE", "smarthome")

credentials = pika.PlainCredentials(username, password)
connection = pika.BlockingConnection(pika.ConnectionParameters(host=host, credentials=credentials, virtual_host=vhost))
channel = connection.channel()

print("Connection Established")

channel.exchange_declare(exchange=exchange, exchange_type='topic')
result = channel.queue_declare('', exclusive=True)
queue_name = result.method.queue
channel.queue_bind(exchange=exchange, queue=queue_name, routing_key="#")

def callback(ch, method, properties, body):
    print(" RK: " + str(method.routing_key) +  " [x] Received %r" % body, flush=True)


channel.basic_consume(
    queue=queue_name, on_message_callback=callback, auto_ack=True)

print(' [*] Waiting for messages. To exit press CTRL+C')
channel.start_consuming()
