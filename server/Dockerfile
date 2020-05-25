FROM python:latest
ADD server.py /
ADD wait-for.sh /
RUN apt-get -q update && apt-get -qy install netcat
RUN pip3 install pika
