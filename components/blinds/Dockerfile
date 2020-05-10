# Get the GCC preinstalled image from Docker Hub
FROM gcc:5.4

# Copy the current folder which contains C++ source code to the Docker image under /
ADD blinds_main.cpp /
# amqp-cpp
# RUN git clone https://github.com/CopernicaMarketingSoftware/AMQP-CPP.git \
#   && cd AMQP-CPP && make && make install

# Use GCC to compile the Test.cpp source file
RUN g++ -o blinds_main blinds_main.cpp

# Run the program output from the previous step
CMD ["./blinds_main"]