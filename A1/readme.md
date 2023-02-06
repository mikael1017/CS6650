# Jaewoo Cho

# Assignment 1

## Client design

I created a Client Thread object that takes a CountDownLatch, requestCounter, latencyCounter as an input. Countdownlatch is used to keep the synchronization of threads. It allows other threads to wait until a set of operations being performed in other threads completes. basic structure of this class is that it goes into for loop that iterates the given number of requests then for each request, it generates a corresponding data according to the assignment spec.
then it records the start time, request type, latency, response code and also I made my program to record the number of requests sent until that time. The program saves this data into a csv file called "result.csv" inside the same folder as the project.

Then I created another class called a clientMultiThreaded class that creates a given number of threads and for each thread it creates a ClientThread to send the request to the server.

After sending all 500K requests to the server, ClientMultiThreaded class then read all the data from the saved data file and use HdrHistogram library to collect all data and use this library to get all the statistics values.

### Part 4:

I used the csv file that the program created to plot the graph.
When my program saves the data, it also saved the total number of requests sent to the server so far. I used this column and the start time column to find the throughput. and I made a graph of throughput vs Time elapsed.

### Request Counter

Request counter object is used to keep track of successful and unsuccessful requests sent to the server.

### Latency Counter

Latency counter object is used to keep track of all latency after each request is sent to the server.

### ClientThread

Client Thread class extends the thread class. It overrides a run function and this thread object send the given amount of post requests to the server. For each request inside this thread, it calculates the latency, stores the result into CSV file.

### ClientMultiThreaded

ClientMultiThreaded class creates a given number of threads and run the threads. After all the threads have ran, it calculates the requested statistics
