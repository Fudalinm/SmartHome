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


class Blinds:
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
        self.target_light = 3000.0
        self.room_light = 3000.0
        self.previous_room_light = None
        self.external_light = 3000.0
        self.previous_external_light = None

        self.blinds_usage = 0

    def set_current_internal_light(self, new_light):
        self.previous_room_light = self.room_light
        self.room_light = new_light

    def set_current_external_light(self, new_light):
        self.previous_external_light = self.external_light
        self.external_light = new_light

    def update_blinds_usage(self):
        if self.external_light < 1000:
            self.blinds_usage = 100
        else:
            if self.external_light > self.room_light and self.target_light > self.room_light:
                self.blinds_usage -= 10
            else:
                self.blinds_usage += 10
        if self.blinds_usage < 0:
            self.blinds_usage = 0
        elif self.blinds_usage > 100:
            self.blinds_usage = 100


host = get_env_or_default("RABBIT_HOST", "localhost")
password = get_env_or_default("RABBIT_PASSWORD", "rabbitmq")
username = get_env_or_default("RABBIT_USERNAME", "rabbitmq")
vhost = get_env_or_default("RABBIT_VHOST", "smarthome")
exchange = get_env_or_default("SMARTHOME_EXCHANGE", "smarthome")

blinds = Blinds(host, password, username, vhost, exchange)


def blinds_control_callback(ch, method, properties, body):
    json_received = json.loads(body)
    if 'ControlType' in json_received.keys() and json_received['ControlType'] != "automatic":
        blinds.f_automatic_control = False
        print('Setting blinds usage')
        blinds.blinds_usage = int(json_received['Usage'])
    else:
        blinds.f_automatic_control = True

    if 'TargetLight' in json_received.keys():
        print('Setting blinds target')
        blinds.target_light = float(json_received['TargetLight'])


def light_info_callback(ch, method, properties, body):
    print("My callback info body: " + str(body), flush=True)
    json_received = json.loads(body)
    print("Info received: " + str(json_received), flush=True)
    print("Setting internal light to: " + str(json_received["InternalLight"]), flush=True)
    print("Setting external light to: " + str(json_received["ExternalLight"]), flush=True)

    blinds.set_current_internal_light(json_received["InternalLight"])
    blinds.set_current_external_light(json_received["ExternalLight"])


def alarm_info_callback(ch, method, properties, body):
    # TODO: implement me!
    #  Shutting blinds if alarm on ?
    pass


def prepare_json_to_send():
    json_as_dict = dict()
    json_as_dict['BlindsUsage'] = str(blinds.blinds_usage)
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
        print("Receiver Topic: " + str(topic) + " alive ", flush=True)


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
        r.update_blinds_usage()
        to_send = str(prepare_json_to_send())
        channel.basic_publish(exchange=r.exchange, routing_key=topic, body=to_send)
        print("S: Blinds sending: " + str(to_send), flush=True)
        print("S: Light current: " + str(r.room_light), flush=True)
        time.sleep(4)


control_topic = get_env_or_default("BLINDS_ROOM1_CONTROL", "room1.blinds.control")
info_topic = get_env_or_default("LIGHT_ROOM_1_INFO", "room1.light.info")
alarm_topic = get_env_or_default("LIGHT_ROOM_1_ALARM", "room1.alarm.info")
env_feedback_topic = get_env_or_default("LIGHT_ROOM_1_FEEDBACK", "room1.light.env_feedback")

# Create receiver for cont
t1 = threading.Thread(target=start_receiver, args=(blinds, control_topic, blinds_control_callback))

# Create receiver for info
t2 = threading.Thread(target=start_receiver, args=(blinds, info_topic, light_info_callback))

# Create receiver for info
t3 = threading.Thread(target=start_receiver, args=(blinds, alarm_topic, alarm_info_callback))

#starting threads
# threads are not sleeping in case of failure
t1.start()
t2.start()
# t3.start()

# Send information for env_feedback
start_sender(blinds, env_feedback_topic)
