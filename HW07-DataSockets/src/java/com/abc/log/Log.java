package com.abc.log;

public interface Log {
    void out(Object msg);
    void outln(Object msg);
    void out(Exception x);
}
