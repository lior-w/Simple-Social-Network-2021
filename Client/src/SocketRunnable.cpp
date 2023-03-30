//
// Created by CLONE-PRO on 04-Jan-22.
//

#include "../include/SocketRunnable.h"

SocketRunnable::SocketRunnable(ConnectionHandler& ch) : connection(ch) {
}

void SocketRunnable::run() {
    while (!connection.shouldTerminate()) {
        string answer = connection.decode();
        if (answer.empty()) {
            cout << "Disconnected. Exiting...\n" << endl;
            break;
        }
        cout << answer << endl;
    }
    if(connection.close()) cout << "connection closed" << endl;

}