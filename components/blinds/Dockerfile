FROM python:latest
ADD blinds.py /
RUN pip3 install pika
ADD wait-for.sh /
RUN apt-get -q update && apt-get -qy install netcat