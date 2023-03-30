//
// Created by CLONE-PRO on 04-Jan-22.
//

#ifndef CLIENT_KEYBOARDRUNNABLE_H
#define CLIENT_KEYBOARDRUNNABLE_H
#include "../include/ConnectionHandler.h"

class KeyboardRunnable {
private:
    ConnectionHandler& connection;
public:
    KeyboardRunnable(ConnectionHandler& ch);
    void run();
};


#endif //CLIENT_KEYBOARDRUNNABLE_H
