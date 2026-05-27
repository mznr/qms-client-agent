package com.zafta.client.agent.schedules;

import java.util.Timer;
import java.util.TimerTask;

public class BlinkTimeoutSchedule {
    private Timer timer;

    private TimeoutCallBack timeoutCallBack;


    public BlinkTimeoutSchedule(TimeoutCallBack timeoutCallBack) {
        this.timeoutCallBack = timeoutCallBack;
        this.timer = new Timer();
    }

    public void restart(int stayOnDuration){
        timer.cancel();
        timer = new Timer();
        timer.schedule(new RemindTask(), stayOnDuration,2000);
    }

    class RemindTask extends TimerTask {
        public void run() {
            timeoutCallBack.timesup();
        }
    }
}
