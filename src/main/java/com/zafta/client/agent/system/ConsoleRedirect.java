package com.zafta.client.agent.system;

import javax.swing.*;
import java.io.OutputStream;
import java.io.PrintStream;

public class ConsoleRedirect {

    public void redirect(JTextArea textArea) {
        PrintStream ps = new PrintStream(new OutputStream() {
            private final StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                buffer.append((char) b);
                if (b == '\n') {
                    String text = buffer.toString();
                    buffer.setLength(0);
                    SwingUtilities.invokeLater(() -> textArea.append(text));
                }
            }
        }, true);

        System.setOut(ps);
        System.setErr(ps);
    }
}
