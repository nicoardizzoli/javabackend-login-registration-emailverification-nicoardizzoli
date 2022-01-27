package com.nicoardizzoli.javabackendloginregistrationemailverification.email;

public interface EmailSender {

    void send(String to, String email);
}
