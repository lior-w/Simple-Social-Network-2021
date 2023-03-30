#ifndef CONNECTION_HANDLER__
#define CONNECTION_HANDLER__
                                           
#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include <unordered_map>

using boost::asio::ip::tcp;
using namespace std;

class ConnectionHandler {
private:
	const string host_;
	const short port_;
	boost::asio::io_service io_service_;   // Provides core I/O functionality
	tcp::socket socket_;
    unordered_map<string, short> commandMap;
    bool logoutSent;
    bool logoutFailed;
    bool terminate;
    string getDate();
 
public:
    ConnectionHandler(string host, short port);
    virtual ~ConnectionHandler();
 
    // Connect to the remote machine
    bool connect();
 
    // Read a fixed number of bytes from the server - blocking.
    // Returns false in case the connection is closed before bytesToRead bytes can be read.
    bool getBytes(char bytes[], unsigned int bytesToRead);
 
	// Send a fixed number of bytes from the client - blocking.
    // Returns false in case the connection is closed before all the data is sent.
    bool sendBytes(const char bytes[], int bytesToWrite);
	
    // Read an ascii line from the server
    // Returns false in case connection closed before a newline can be read.
    bool getLine(string& line);
	
	// Send an ascii line from the server
    // Returns false in case connection closed before all the data is sent.
    bool sendLine(string& line);
 
    // Get Ascii data from the server until the delimiter character
    // Returns false in case connection closed before null can be read.
    bool getFrameAscii(string& frame, char delimiter);
 
    // Send a message to the remote host.
    // Returns false in case connection is closed before all the data is sent.
    bool sendFrameAscii(const string& frame, char delimiter);
	
    // Close down the connection properly.
    bool close();

    bool shouldTerminate();

    bool isLogoutSent();

    bool isLogoutFailed();

    void resetLogout();

    string decode();

    string decodeAck();

    string decodeAckFollow();

    string decodeAckLogstat();

    string decodeAckStat();

    string decodeError();

    string decodeNotification();

    string encode(string& s);

    string encodeRegister(vector<string> split);

    string encodeLogin(vector<string> split);

    string encodeLogout(vector<string> split);

    string encodeFollow(vector<string> split);

    string encodePost(vector<string> split);

    string encodePM(vector<string> split);

    string encodeLogstat(vector<string> split);

    string encodeStat(vector<string> split);

    string encodeBlock(vector<string> split);

    void tokenize(string const &str, const char delim, vector<string> &out);

    short bytesToShort(char* bytesArr);

    void shortToBytes(short num, char* bytesArr);



}; //class ConnectionHandler
 
#endif