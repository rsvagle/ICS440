package com.abc.log;

import java.awt.*;

import javax.swing.*;

import com.programix.gui.*;
import com.programix.testing.*;
import com.programix.util.*;

public class LogPane extends JPanel implements Log, Output {
    private JTextArea logTA;
    private AppendTask newLineAppendTask;

    public LogPane(String title) {
        logTA = new JTextArea();
        logTA.setEditable(false);

        setLayout(new GridLayout());
        setBorder(GuiTools.createSandwichBorder(
            10, 5, BorderFactory.createTitledBorder(title)));
        add(new JScrollPane(logTA));

        newLineAppendTask = new AppendTask("\n");
    }

    @Override
    public void out(Object msg) {
        SwingUtilities.invokeLater(new AppendTask(String.valueOf(msg)));
    }

    @Override
    public void outln(Object msg) {
        SwingUtilities.invokeLater(new AppendTask(String.valueOf(msg)));
        SwingUtilities.invokeLater(newLineAppendTask);
    }

    @Override
    public void out(Exception x) {
        String[] line = StringTools.stackTraceToStringArray(x);
        for ( int i = 0; i < line.length; i++ ) {
            outln(line[i]);
        }
    }

    @Override
    public void out(String msg) {
        out((Object) msg);
    }

    @Override
    public void outln(String msg) {
        outln((Object) msg);
    }

    private class AppendTask implements Runnable {
        private String text;

        public AppendTask(String text) {
            this.text = text;
        }

        @Override
        public void run() {
            logTA.append(text);
        }
    } // class AppendTask
}
