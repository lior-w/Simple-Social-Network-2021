//
// Created by CLONE-PRO on 04-Jan-22.
//

#include "../include/KeyboardRunnable.h"

KeyboardRunnable::KeyboardRunnable(ConnectionHandler& ch) : connection(ch) {
}

void KeyboardRunnable::run() {
    while (!connection.shouldTerminate()) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        int len=line.length();
        if (!connection.sendLine(line)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }

        if (connection.isLogoutSent()) {
            while (!connection.shouldTerminate() && !connection.isLogoutFailed()) {
            }
            connection.resetLogout();
        }
    }
}