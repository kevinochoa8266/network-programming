# ryanTemp branch - Read this:
/*
 * 
 * PeerProcess.java is the entry point for the program.
 * 
 * 1. It starts the server for the current peer. (The one that is running this program)
 * 2. It connects to earlier peers, and stores the successful 
 * connections in the connectedPeers list.
 *
 * 3. It listens for new connections, and creates a new thread to handle each new connection.
 * 
 * The main function calls peerProcess.start()
 * 
 * There are two main objects to be aware of. The PeerInfo object, and the Peer object.
 * Pearinfo comes from PeerInfoParser, and hold data like peerID, hostName, portNumber.
 * (and also a list of all peers from the PeerInfo.cfg file, (this list must be populated by PeerInfoParser.readFile() first)
 * 
 * The second main object is the Peer object. Every peer that this peer connects to will become a Peer object.
 * You control the connection to another Peer by calling it's Peer object functions.
 * 
 * Current State:
 * 
 * The program is capable of connecting to other peers based off my testing with ServerTestClient.java 
 * (which is a barebones simulation of a peer). Handshaking is working, and I am currently working on
 * getting peers (Copies of this code) to automatically connect to each other.
 * 
 * 
 */
 # Issues
 A lot of the code from main is mixed in with the new code, which may make this somewhat confusing. I need to refactor and modify the old code to work with this structure.


# CNT4007 - network-programming

 Group Project Members:
  - Kevin Ochoa
  - Bryan Torreblanca
  - Ryan Rodriguez

### Using openjdk-21
