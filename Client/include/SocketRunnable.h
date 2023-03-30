//
// Created by CLONE-PRO on 04-Jan-22.
//

#ifndef CLIENT_SOCKETRUNNABLE_H
#define CLIENT_SOCKETRUNNABLE_H
#include "../include/ConnectionHandler.h"


class SocketRunnable {
private:
    ConnectionHandler& connection;
public:
    SocketRunnable(ConnectionHandler& ch);
    void run();
};


#endif //CLIENT_SOCKETRUNNABLE_H
