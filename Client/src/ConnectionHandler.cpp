#include "../include/ConnectionHandler.h"
 
using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
 
ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_), commandMap(), logoutSent(false), logoutFailed(false), terminate(false) {
    commandMap = {{"REGISTER", 1}, {"LOGIN", 2}, {"LOGOUT", 3}, {"FOLLOW", 4}, {"POST", 5}, {"PM", 6}, {"LOGSTAT", 7}, {"STAT", 8}, {"BLOCK", 12}};
}
    
ConnectionHandler::~ConnectionHandler() {
    close();
}
 
bool ConnectionHandler::connect() {
    std::cout << "Starting connect to " 
        << host_ << ":" << port_ << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);			
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, ';');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(encode(line), ';');
}
 
bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character. 
    // Notice that the null character is not appended to the frame string.
    try {
		do{
			getBytes(&ch, 1);
            frame.append(1, ch);
        }while (delimiter != ch);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
	bool result=sendBytes(frame.c_str(),frame.length());
	if(!result) return false;
	return sendBytes(&delimiter,1);
}
 
// Close down the connection properly.
bool ConnectionHandler::close() {
    try{
        socket_.close();
        return true;
    } catch (...) {
        cout << "closing failed: connection already closed" << endl;
        return false;
    }
}

bool ConnectionHandler::shouldTerminate() {
    return terminate;
}

bool ConnectionHandler::isLogoutSent() {
    return logoutSent;
}

bool ConnectionHandler::isLogoutFailed() {
    return logoutFailed;
}

void ConnectionHandler::resetLogout() {
    if (logoutSent) logoutSent = false;
    if (logoutFailed) logoutFailed = false;
}

string ConnectionHandler::decode() {
    char bytes[2];
    if(!getBytes(bytes, 2)) return NULL;
    short opcode = bytesToShort(bytes);
    if (opcode == 10) {
        return decodeAck();
    } else if (opcode == 11) {
        return decodeError();
    } else return decodeNotification();
}

string ConnectionHandler::decodeAck() {
    char bytes[2];
    if(!getBytes(bytes, 2)) return NULL;
    short msgOpcode = bytesToShort(bytes);
    if (msgOpcode == 4) {
        return decodeAckFollow();
    } else if (msgOpcode == 7) {
        return decodeAckLogstat();
    } else if (msgOpcode == 8) {
        return decodeAckStat();
    } else {
        string s;
        if(!getFrameAscii(s, ';')) return NULL;
        if (msgOpcode == 3) terminate = true;
        return "ACK " + to_string(msgOpcode);
    }
}


string ConnectionHandler::decodeAckFollow() {
    string userName;
    if(!getFrameAscii(userName, ';')) return NULL;
//    string s;
//    if(!getFrameAscii(s, ';')) return NULL;
    return "ACK 4 " + userName.substr(0, userName.size()-1);
}

string ConnectionHandler::decodeAckLogstat() {
    char ageBytes[2];
    char numPostsBytes[2];
    char numFollowersBytes[2];
    char numFollowingBytes[2];
    if(!getBytes(ageBytes, 2)) return NULL;
    if(!getBytes(numPostsBytes, 2)) return NULL;
    if(!getBytes(numFollowersBytes, 2)) return NULL;
    if(!getBytes(numFollowingBytes, 2)) return NULL;
    string s;
    if(!getFrameAscii(s, ';')) return NULL;
    short age = bytesToShort(ageBytes);
    short numPosts = bytesToShort(numPostsBytes);
    short numFollowers = bytesToShort(numFollowersBytes);
    short numFollowing = bytesToShort(numFollowingBytes);
    return "ACK 7 " + to_string(age) + " " + to_string(numPosts) + " " + to_string(numFollowers) + " " + to_string(numFollowing);
}

string ConnectionHandler::decodeAckStat() {
    char ageBytes[2];
    char numPostsBytes[2];
    char numFollowersBytes[2];
    char numFollowingBytes[2];
    if(!getBytes(ageBytes, 2)) return NULL;
    if(!getBytes(numPostsBytes, 2)) return NULL;
    if(!getBytes(numFollowersBytes, 2)) return NULL;
    if(!getBytes(numFollowingBytes, 2)) return NULL;
    string s;
    if(!getFrameAscii(s, ';')) return NULL;
    short age = bytesToShort(ageBytes);
    short numPosts = bytesToShort(numPostsBytes);
    short numFollowers = bytesToShort(numFollowersBytes);
    short numFollowing = bytesToShort(numFollowingBytes);
    return "ACK 8 " + to_string(age) + " " + to_string(numPosts) + " " + to_string(numFollowers) + " " + to_string(numFollowing);
}

string ConnectionHandler::decodeError() {
    char bytes[2];
    if(!getBytes(bytes, 2)) return NULL;
    short msgOpcode = bytesToShort(bytes);
    if (msgOpcode == 3) logoutFailed = true;
    string s;
    if(!getFrameAscii(s, ';')) return NULL;
    return "ERROR " + to_string(msgOpcode);
}

string ConnectionHandler::decodeNotification() {
    char typeByte[1];
    string postingUser;
    string content;
    if (!getBytes(typeByte, 1)) return NULL;
    string type;
    if (typeByte[0] == '0') {
        type = "PM";
    } else type = "Public";
    if (!getFrameAscii(postingUser, '\0')) return NULL;
    if (!getFrameAscii(content, '\0')) return NULL;
    string s;
    if (!getFrameAscii(s, ';')) return NULL;
    return "NOTIFICATION " + type + " " + postingUser.substr(1,postingUser.size() - 2) + " " + content.substr(0, content.size() - 1);
}

string ConnectionHandler::encode(string &s) {
    vector<string> split;
    tokenize(s, ' ', split);
    if (commandMap.find(split.at(0)) != commandMap.end()) {
        short opcode = commandMap.at(split.at(0));
        if (opcode == 1) return encodeRegister(split);
        else if (opcode == 2) return encodeLogin(split);
        else if (opcode == 3) return encodeLogout(split);
        else if (opcode == 4) return encodeFollow(split);
        else if (opcode == 5) return encodePost(split);
        else if (opcode == 6) return encodePM(split);
        else if (opcode == 7) return encodeLogstat(split);
        else if (opcode == 8) return encodeStat(split);
        else return encodeBlock(split);
    } else return "invalid input";
}

string ConnectionHandler::encodeRegister(vector<string> split) {
    string userName = split.at(1);
    string password = split.at(2);
    string birthday = split.at(3);
    char opcode[2];
    shortToBytes(1, opcode);
    string command;
    command = command + opcode[0] + opcode[1] + userName + '\0' + password + '\0' + birthday + '\0';
    return command;
}

string ConnectionHandler::encodeLogin(vector<string> split) {
    string userName = split.at(1);
    string password = split.at(2);
    string captcha = split.at(3);
    char opcode[2];
    shortToBytes(2, opcode);
    string command;
    command = command + opcode[0] + opcode[1] + userName + '\0' + password + '\0' + captcha;
    return command;
}

string ConnectionHandler::encodeLogout(vector<string> split) {
    logoutSent = true;
    char opcode[2];
    shortToBytes(3, opcode);
    string command;
    command = command + opcode[0] + opcode[1];
    return command;
}

string ConnectionHandler::encodeFollow(vector<string> split) {
    string follow = split.at(1);
    string userName = split.at(2);
    char opcode[2];
    shortToBytes(4, opcode);
    string command;
    command = command + opcode[0] + opcode[1] + follow + userName;
    return command;
}

string ConnectionHandler::encodePost(vector<string> split) {
    string content;
    for (int i = 1; i < split.size() - 1; i++) {
        content.append(split.at(i) + ' ');
    }
    content.append(split.at(split.size() - 1));
    char opcode[2];
    shortToBytes(5, opcode);
    string command;
    command = command + opcode[0] + opcode[1] + content + '\0';
    return command;
}

string ConnectionHandler::encodePM(vector<string> split) {
    string userName = split.at(1);
    string content;
    for (int i = 2; i < split.size() - 1; i++) {
        content.append(split.at(i) + ' ');
    }
    content.append(split.at(split.size() - 1));
    string date = getDate();
    char opcode[2];
    shortToBytes(6, opcode);
    string command;
    command = command + opcode[0] + opcode[1] + userName + '\0' + content + '\0' + date + '\0';
    return command;
}

string ConnectionHandler::encodeLogstat(vector<string> split) {
    char opcode[2];
    shortToBytes(7, opcode);
    string command;
    command = command + opcode[0] + opcode[1];
    return command;
}

string ConnectionHandler::encodeStat(vector<string> split) {
    string userList = split.at(1);
    char opcode[2];
    shortToBytes(8, opcode);
    string command;
    command = command + opcode[0] + opcode[1] + userList + '|' + '\0';
    return command;
}

string ConnectionHandler::encodeBlock(vector<string> split) {
    string userName = split.at(1);
    char opcode[2];
    shortToBytes(12, opcode);
    string command;
    command = command + opcode[0] + opcode[1] + userName + '\0';
    return command;
}

void ConnectionHandler::tokenize(string const &str, const char delim, vector<string> &out) {
    size_t start;
    size_t end = 0;
    while ((start = str.find_first_not_of(delim, end)) != string::npos) {
        end = str.find(delim, start);
        out.push_back(str.substr(start, end - start));
    }
}

short ConnectionHandler::bytesToShort(char* bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void ConnectionHandler::shortToBytes(short num, char* bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

string ConnectionHandler::getDate() {
    time_t ttime = time(0);
    tm *local_time = localtime(&ttime);
    int year =  1900 + local_time->tm_year;
    int month = 1 + local_time->tm_mon;
    int day =  local_time->tm_mday;
    int hour =  1 + local_time->tm_hour;
    int minute =  local_time->tm_min;

    string date;
    string dd = to_string(day);
    if (dd.size() == 2) date = date + dd;
    else date = date + '0' + dd;
    //DD
    string mm = to_string(month);
    if (mm.size() == 2) date = date + '-' + mm;
    else date = date + '-' + '0' + mm;
    //DD-MM
    date = date + '-' + to_string(year);
    //DD-MM-YYYY
    string hh = to_string(hour);
    if (hh.size() == 2) date = date + ' ' + hh;
    else date = date + ' ' + '0' + hh;
    //DD-MM-YYYY HH
    string min = to_string(minute);
    if (min.size() == 2) date = date + ':' + min;
    else date = date + ':' + '0' + min;
    //DD-MM-YYYY HH:MM
    return date;
}
