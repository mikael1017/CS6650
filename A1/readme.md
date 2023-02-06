# Jaewoo Cho

# Assignment 1

## Client design

I created a Client Thread object that takes a CountDownLatch, requestCounter, latencyCounter as an input. Countdownlatch is used to keep the synchronization of threads. It allows other threads to wait until a set of operations being performed in other threads completes. basic structure of this class is that it goes into for loop that iterates the given number of requests then for each request, it generates a corresponding data according to the assignment spec.

Then I created another class called a clientMultiThreaded class that creates a given number of threads and for each thread it creates a ClientThread to send the request to the server.

### Request Counter

Request counter object is used to keep track of successful and unsuccessful requests sent to the server.

### Latency Counter

Latency counter object is used to keep track of all latency after each request is sent to the server.

### ClientThread

Client Thread class extends the thread class. It overrides a run function and this thread object send the given amount of post requests to the server. For each request inside this thread, it calculates the latency, stores the result into CSV file.

### ClientMultiThreaded

ClientMultiThreaded class creates a given number of threads and run the threads. After all the threads have ran, it calculates the requested statistics
