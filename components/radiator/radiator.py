#!/usr/bin/env python
import pika
import os
import json
import time
import threading


def get_env_or_default(en, default):
    e = os.environ.get(en)
    if e is None:
        return default
    else:
        return e


class Radiator:
    def __init__(self, h, p, u, v, e):
        self.host = h
        self.password = p
        self.username = u
        self.vhost = v
        self.exchange = e

        print(host)
        print(password)
        print(username)
        print(vhost)

        self.plain_credentials = pika.PlainCredentials(self.username, self.password)
        self.connection_parameters = pika.ConnectionParameters(host=self.host, virtual_host=self.vhost,
                                                               credentials=self.plain_credentials)

        self.f_automatic_control = True
        self.target_temperature = 22.0
        self.room_temperature = 22.0
        self.previous_room_temperature = None

        self.radiator_usage = 0

    def set_current_temperature(self, new_temperature):
        self.previous_room_temperature = self.room_temperature
        self.room_temperature = new_temperature

    def update_radiator_usage(self):
        self.radiator_usage = int(self.radiator_usage + (self.target_temperature - self.room_temperature) * 8)
        if self.radiator_usage < 0:
            self.radiator_usage = 0
        elif self.radiator_usage > 100:
            self.radiator_usage = 100


host = get_env_or_default("RABBIT_HOST", "localhost")
password = get_env_or_default("RABBIT_PASSWORD", "rabbitmq")
username = get_env_or_default("RABBIT_USERNAME", "rabbitmq")
vhost = get_env_or_default("RABBIT_VHOST", "smarthome")
exchange = get_env_or_default("SMARTHOME_EXCHANGE", "smarthome")

radiator = Radiator(host, password, username, vhost, exchange)


def radiator_control_callback(ch, method, properties, body):
    json_received = json.loads(body)
    if json_received['ControlType'] != "automatic":
        radiator.f_automatic_control = False
        print('Setting radiator usage')
        radiator.radiator_usage = int(json_received['Usage'])
    else:
        radiator.f_automatic_control = True


def radiator_info_callback(ch, method, properties, body):
    print("My callback info body: " + str(body), flush=True)
    # print(str(body))
    json_received = json.loads(body)
    print("Info received: " + str(json_received), flush=True)
    print("Setting internal temperature to: " + str(json_received["InternalTemperature"]), flush=True)
    radiator.set_current_temperature(json_received["InternalTemperature"])


def prepare_json_to_send():
    json_as_dict = dict()
    json_as_dict['RadiatorUsage'] = str(radiator.radiator_usage)
    json_to_send = json.dumps(json_as_dict)
    return json_to_send


def start_receiver(r, topic, fun):
    while True:
        try:
            connection = pika.BlockingConnection(r.connection_parameters)
            channel = connection.channel()
            print("Receiver connection Established for " + str(topic))
            break
        except :
            time.sleep(2)
            continue
    channel.exchange_declare(exchange=r.exchange, exchange_type='topic')
    result = channel.queue_declare('', exclusive=True, durable=True)
    queue_name = result.method.queue
    channel.queue_bind(exchange=exchange, queue=queue_name, routing_key=topic)

    channel.basic_consume(queue=queue_name, on_message_callback=fun, auto_ack=True)
    channel.start_consuming()

    while True:
        time.sleep(2)
        print("Receiver Topic: " + str(topic) + " alive ",flush=True)


def start_sender(r, topic):
    while True:
        try:
            connection = pika.BlockingConnection(r.connection_parameters)
            channel = connection.channel()
            print("Sender connection Established for " + str(topic))
            break
        except:
            time.sleep(2)
            continue

    while True:
        r.update_radiator_usage()
        to_send = str(prepare_json_to_send())
        channel.basic_publish(exchange=r.exchange, routing_key=topic, body=to_send)
        print("S: Radiator sending: " + str(to_send), flush=True)
        print("S: Temperature current: " + str(r.room_temperature) ,flush=True)
        time.sleep(4)




control_topic = get_env_or_default("RADIATOR_CONTROL_ROOM_1", "room1.temperature.control")
info_topic = get_env_or_default("TEMPERATURE_ROOM_1_INFO", "room1.temperature.info")
env_feedback_topic = get_env_or_default("TEMPERATURE_ROOM_1_FEEDBACK", "room1.temperature.env_feedback")

# Create receiver for cont
t1 = threading.Thread(target=start_receiver, args=(radiator, control_topic, radiator_control_callback))

# Create receiver for info
t2 = threading.Thread(target=start_receiver, args=(radiator, info_topic, radiator_info_callback))

#starting threads
# threads are not sleeping in case of failure
t1.start()
t2.start()

# Send information for env_feedback
start_sender(radiator, env_feedback_topic)
