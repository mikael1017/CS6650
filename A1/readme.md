# Jaewoo Cho

# Assignment 1

## Client design

### Request Counter

Request counter object is used to keep track of successful and unsuccessful requests sent to the server.

### Latency Counter

Latency counter object is used to keep track of all latency after each request is sent to the server.

### ClientThread

Client Thread class extends the thread class. It overrides a run function and this thread object send the given amount of post requests to the server. For each request inside this thread, it calculates the latency, stores the result into CSV file.

### ClientMultiThreaded

ClientMultiThreaded class creates a given number of threads and run the threads. After all the threads have ran, it calculates the requested statistics
