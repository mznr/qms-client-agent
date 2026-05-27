package com.zafta.client.agent.schedules;

import java.util.Timer;
import java.util.TimerTask;

public class TimeoutSchedule {
    private Timer timer;

    private TimeoutCallBack timeoutCallBack;


    public TimeoutSchedule(TimeoutCallBack timeoutCallBack) {
        this.timeoutCallBack = timeoutCallBack;
        this.timer = new Timer();
    }

    public void restart(){
        timer.cancel();
        timer = new Timer();
        timer.schedule(new RemindTask(), 3000,2000);
    }

    class RemindTask extends TimerTask {
        public void run() {
            timeoutCallBack.timesup();
        }
    }
}
