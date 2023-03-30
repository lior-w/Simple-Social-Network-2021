//
// Created by CLONE-PRO on 04-Jan-22.
//
#include <iostream>
#include <boost/asio.hpp>
#include "../include/ConnectionHandler.h"
#include "../include/KeyboardRunnable.h"
#include "../include/SocketRunnable.h"
#include <thread>


using namespace std;

int main (int argc, char *argv[]) {
    //if (argc < 3) {
    //    std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
    //    return -1;
    //}
    //std::string host = argv[1];
    //short port = atoi(argv[2]);
    std::string host = "127.0.0.1";
    short port = 7777;
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    KeyboardRunnable kbr(connectionHandler);
    SocketRunnable sr(connectionHandler);

    std::thread keyboard(&KeyboardRunnable::run, &kbr);
    std::thread socket(&SocketRunnable::run, &sr);

    keyboard.join();
    socket.join();
    return 0;
}
