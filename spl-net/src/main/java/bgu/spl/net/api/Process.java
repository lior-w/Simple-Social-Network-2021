package bgu.spl.net.api;

import bgu.spl.net.impl.BGS.Messages.Message;

public interface Process<T> {

    void process(Message msg);
}
