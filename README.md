# [JSON Database](https://hyperskill.org/projects/65)

## About

Acquire skills of working with JSON to use this tool as part of your work as a software developer. You will create a
functional server that exchanges data with a web browser and handles multiple requests using parallelization.

---

### Stage 1: [Create a database](https://hyperskill.org/projects/65/stages/348/implement)

#### _Summary_

Create a simple array-based database that can store up to a thousand cells of data.

#### _Description_

A JSON database is a single-file database that stores information in JSON format. It is typically accessed remotely
over the internet.

In this stage, you will simulate a database that can store text information in a string array of fixed size 1000.
Initially, each cell (or element) in the database contains an empty string. Users will be able to perform three
primary actions: saving strings to cells, reading information from cells, and deleting information from cells. When
a string is deleted, the corresponding cell should revert to containing an empty string.

Users can interact with the database using the following commands: `set`, `get` and `delete`. A fourth command
`exit` is used when the user is finished.

---

### Stage 2: [Connect it to a server](https://hyperskill.org/projects/65/stages/349/implement)

#### _Summary_

Create a server that allows clients to connect and send messages.

#### _Description_

Time to implement a simple connection between a server and client. The client should send the server a message along
the lines of `Give me a record # N` where N is an arbitrary integer number. The server should reply `A record # N was 
sent!` to the client. Both the client and server should print the received and sent messages to the console.

---

### Stage 3: [Add new functionalities](https://hyperskill.org/projects/65/stages/350/implement)

#### _Summary_

Make your server functional and add the option to exit the program.

#### _Description_

Now that we have communication between the client and server, let's build upon the functionality from the first stage.
The server should be able to receive messages with the operations `get`, `set`, and `delete`, each with an index of the
cell.

For now, there is no need to save the database to a file on the hard drive, so if the server reboots, all the data in
the database will be lost. The server should serve one client at a time in a loop, and the client should only send
one request to the server, get one reply, and exit. After that, the server should wait for another connection from a
client.

To send a request to the server, the client should get all the information through command-line arguments in the
following format (there is a useful library called `JCommander` to help parsing the arguments):

`java Main -t <type> -i <index> [-m <message>]`

- `-t` is the type of the request
- `-i` is the index of the cell
- `-m` is the value/message to save in the database (only needed for `set` requests)

---

### Stage 4: [Start work with JSON](https://hyperskill.org/projects/65/stages/351/implement)

#### _Summary_

Convert requests and responses to JSON format.

#### _Description_

In this stage, we will store the database in JSON format. The database will still be in memory and not saved as a file.
We will use the [GSON library](https://google.github.io/gson/) from Google to work with JSON.

The database will now be stored as Java JSON objects. The keys will now be strings (not limited to integer indexes 
like the previous stages). We will still use command-line arguments, but the client will have to send JSON to the 
server and receive JSON from the server. Similarly, the server should process the received JSON and respond with JSON.

The command-line format will now be as follows:
`java Main -t <type> -k <key> [-v <value>]`

- `-t` specifies the type of the request (`get`, `set`, or `delete`)
- `-k` specifies the key
- `-v` specifies the value (only needed for `set` requests)

---

### Stage 5: [Manage multiple requests](https://hyperskill.org/projects/65/stages/352/implement)

#### _Summary_

Allow your server to handle multiple requests at the same time.

#### _Description_

For this stage, we will improve the client and server by adding the ability to work with files. The server should
store (persist) the database as a file on the hard drive, updating it only when setting a new  value or deleting one.
This functionality is crucial for maintaining data persistence and ensuring that the database state is saved even if
the server is restarted.

We will also parallelize the server's work using executors to handle multiple requests efficiently. Each request  
will be parsed and handled in a separate executor's task which will enhance the performance and scalability so it
can handle a higher load.

Implementing synchronization is essential to maintain the integrity of the database when multiple threads access the
same file. Using the `ReentrantReadWriteLock` class, we can allow multiple threads to read the file concurrently
while ensuring that only one thread can write to the file at a time. This will prevent data corruption and ensure
consistent access to the database.

Also, we will implement the ability for the client to read a request from a file. If the `-in` argument is followed
by a file name, the client should read the request from that file. The file will be stored in the `/client/data`
directory. This feature allows the client to directly send pre-formatted JSON requests to the server, bypassing the
need to first convert command-line arguments into JSON format and then send that JSON to the server.

---

### Stage 6: [Store JSON objects in your database](https://hyperskill.org/projects/65/stages/353/implement)

#### _Summary_

Process operations with complex keys.

#### _Description_
