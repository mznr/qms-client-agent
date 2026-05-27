package com.zafta.client.agent.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.dispatcher.VoidDispatchService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.zafta.client.agent.MyGuiApp;
import com.zafta.client.agent.feign.QMSClient;
import com.zafta.client.agent.interfaces.StatusCodes;
import com.zafta.client.agent.models.*;
import com.zafta.client.agent.models.counter.Counter;
import com.zafta.client.agent.models.counter.DisplayType;
import com.zafta.client.agent.schedules.TimeoutSchedule;
import com.zafta.client.agent.ui.AppNotifier;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class QMSAgentService implements NativeKeyListener, StatusCodes {
    private static final Logger LOGGER = LoggerFactory.getLogger(QMSAgentService.class);

    private final CounterHIDDisplayService hidLedCounterDisplayService;

    static {
        System.setProperty("java.awt.headless", "false");
    }
    private final QMSClient qmsClient;

    @Value("${zafta.counter.id}")
    private int counterId;

    @Value("${info.app.version}")
    private String appVersion;


    /*
    Group 1 of Combined functional keys:
        1) CTRL + ->            = Invoke Next
        2) CTRL + UP            = Recall Last Invoked Number
        3) CTRL + DOWN          = Toggle Waiting State for the counter
        4) CTRL + LEFT          = Get Number of Waiting People In Queue
    */
    private static boolean MASK_CONTROL = false;
    private static boolean MASK_ARROW_RIGHT = false;
    private static boolean MASK_ARROW_LEFT = false;
    private static boolean MASK_ARROW_UP = false;
    private static boolean MASK_ARROW_DOWN = false;



    /*
    Group 2 of Combined functional keys:
        1) ALT + ->            =
        2) ALT + UP            = Apply Dot-matrix Counter Display settings
        3) ALT + DOWN + xxx    = Invoke a specific Number (xxx)
        4) ALT + LEFT          =
        5) ALT + F1            =
        6) ALT + F2            =
    */
    private static boolean MASK_ALT = false;
    /*private static boolean MASK_F1 = false;
    private static boolean MASK_F2 = false;*/

    private boolean startGettingNumber;
    private String numberStr = "";

    private TimeoutSchedule timeoutSchedule;

    @EventListener(ApplicationReadyEvent.class)
    public void performSomeInitializingStuff() throws NativeHookException {

        GlobalScreen.setEventDispatcher(new VoidDispatchService());
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(this);

        timeoutSchedule = new TimeoutSchedule(this::resetAllKeys);
        timeoutSchedule.restart();

        startHeartbeat();

        LOGGER.info("Zafta QMS Agent is UP and Running");
    }

    public void startHeartbeat() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        // Schedule the heartbeat task to run every 10 seconds (adjust as needed)
        executorService.scheduleAtFixedRate(this::sendHeartbeat, 0, 5, TimeUnit.SECONDS);
    }

    private void sendHeartbeat() {
        try {
            qmsClient.updateHeartbeat(counterId);
        }catch (Exception ignored){
        }
    }


    public void resetAllKeys(){
        //LOGGER.info("keys released");
        MASK_CONTROL = false;
        MASK_ALT = false;
        MASK_ARROW_RIGHT = false;
        MASK_ARROW_LEFT = false;
        MASK_ARROW_UP = false;
        MASK_ARROW_DOWN = false;
        /*MASK_F1 = false;
        MASK_F2 = false;*/
    }


    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        timeoutSchedule.restart();
        switch (e.getKeyCode()) {
            case NativeKeyEvent.VC_CONTROL -> MASK_CONTROL = true;
            case NativeKeyEvent.VC_ALT -> MASK_ALT = true;
            case NativeKeyEvent.VC_RIGHT -> MASK_ARROW_RIGHT = true;
            case NativeKeyEvent.VC_LEFT -> MASK_ARROW_LEFT = true;
            case NativeKeyEvent.VC_UP -> MASK_ARROW_UP = true;
            case NativeKeyEvent.VC_DOWN -> MASK_ARROW_DOWN = true;
            /*case NativeKeyEvent.VC_F1 -> MASK_F1 = true;
            case NativeKeyEvent.VC_F2 -> MASK_F2 = true;*/
        }


        //ToDo: do it in a better way
        if (MASK_CONTROL & MASK_ARROW_RIGHT) {
            try{
                ResponseMessage responseMessage = qmsClient.invokeNextFromQueue(counterId);
                processResponse(responseMessage);
            }catch (Exception ex){
                if(ex instanceof RetryableException){
                    showNotification( "امکان ارتباط با سرور سامانه وجود ندارد.",TrayIcon.MessageType.ERROR);
                }else {
                    LOGGER.info(ex.getMessage());
                }
            }

        }else if (MASK_CONTROL & MASK_ARROW_LEFT) {
            ResponseMessage responseMessage = qmsClient.getWaitingInQueueByCounterId(counterId);
            if (!responseMessage.isSucceed()){
                //ToDO: error code and error description can be customized by overriding them before returning (may be useful for hiding details from caller)
                LOGGER.error(responseMessage.getContent());
                //ToDO: show proper notifications based on error codes
                return;
            }

            ObjectMapper mapper = new ObjectMapper();

            int waitingInQueue =
                    mapper.convertValue(
                            responseMessage.getPayload(),
                            Integer.class);

            showNotification( "تعداد افراد منتظر در صف: "+ waitingInQueue,TrayIcon.MessageType.INFO);
        }else if (MASK_CONTROL & MASK_ARROW_UP) {
            ResponseMessage responseMessage = qmsClient.recallFromQueue(counterId);
            processResponse(responseMessage);
        }else if (MASK_CONTROL & MASK_ARROW_DOWN) {
            ResponseMessage responseMessage = qmsClient.toggleWaitingModeForCounter(counterId);
            if (!responseMessage.isSucceed()){
                //ToDO: error code and error description can be customized by overriding them before returning (may be useful for hiding details from caller)
                LOGGER.error(responseMessage.getContent());
                //ToDO: show proper notifications based on error codes
                if(responseMessage.getStatusCode() == STATUS_CODE_ERROR_WAITING_MODE_NOT_ALLOWED){
                    showNotification( "به دلیل وجود صف، فعال کردن حالت انتظار ممکن نیست.",TrayIcon.MessageType.WARNING);
                }else if(responseMessage.getStatusCode() == STATUS_CODE_ERROR_NO_QUEUE_IS_ASSIGNED_TO_COUNTER){
                    showNotification("صفی به این باجه تخصیص داده نشده است.",TrayIcon.MessageType.ERROR);
                }
                return;
            }

            ObjectMapper mapper = new ObjectMapper();

            boolean waitingState =
                    mapper.convertValue(
                            responseMessage.getPayload(),
                            Boolean.class);
            if(waitingState){
                showNotification( "حالت انتظار برای این باجه فعال شد",TrayIcon.MessageType.INFO);
            }else {
                showNotification( "حالت انتظار برای این باجه لغو شد",TrayIcon.MessageType.WARNING);
            }
        }else if(MASK_ALT & MASK_ARROW_RIGHT){
            getQMSServerInfo();
        }else if(MASK_ALT & MASK_ARROW_UP){
            if(hidLedCounterDisplayService.applyDefaultSettingsOnCounterHIDDotMatrixDisplay()){
                showNotification( "تنظیمات پیش فرض بر روی نمایشگر باجه اعمال شد",TrayIcon.MessageType.INFO);
            }
        }else if (MASK_ALT & MASK_ARROW_DOWN) {
            startGettingNumber = true;
        }

    }

    public void getQMSServerInfo(){
        ResponseMessage responseMessage = qmsClient.getApplicationInfo();

        String notificationMessage;
        if (!responseMessage.isSucceed()){
            LOGGER.info(responseMessage.getContent());
                notificationMessage = "اطلاعاتی از سرور دریافت نشد.";
                showNotification(notificationMessage,TrayIcon.MessageType.WARNING);
            return;
        }
        ObjectMapper mapper = new ObjectMapper();

        ApplicationInfo applicationInfo =
                mapper.convertValue(
                        responseMessage.getPayload(),
                        ApplicationInfo.class);

        notificationMessage = "سامانه نوبت دهی نسخه " + applicationInfo.getVersion() + "\n" +
                "نرم افزار فراخوان باجه نسخه " + appVersion + "\n"
                + applicationInfo.getDescription();
        LOGGER.info("QMS Server Info: {}", applicationInfo);

        showNotification(notificationMessage,TrayIcon.MessageType.INFO);
    }

    public void processResponse(ResponseMessage responseMessage){
        String notificationMessage = "";
        if (!responseMessage.isSucceed()){
            LOGGER.info(responseMessage.getContent());
            if(responseMessage.getStatusCode() == STATUS_CODE_ERROR_NO_QUEUE_IS_ASSIGNED_TO_COUNTER){
                notificationMessage = notificationMessage + "صفی به این باجه تخصیص داده نشده است.";
                showNotification(notificationMessage,TrayIcon.MessageType.ERROR);
            }else if(responseMessage.getStatusCode() == STATUS_CODE_ERROR_NO_ONE_IN_QUEUE_TO_INVOKE){
               notificationMessage = notificationMessage + "هیچ فردی در صف نیست که فراخوانی شود.";
               showNotification(notificationMessage,TrayIcon.MessageType.WARNING);
           }else if(responseMessage.getStatusCode() == STATUS_CODE_ERROR_REDIS_NOT_AVAIABLE){
                notificationMessage = notificationMessage + "اجزای سرور به طور کامل اجرا نشده اند.";
                showNotification(notificationMessage,TrayIcon.MessageType.ERROR);
            }else if(responseMessage.getStatusCode() == STATUS_CODE_ERROR_COUNTER_NOT_FOUND){
                notificationMessage = notificationMessage + "باجه با شناسه "+ counterId +" در سمت سامانه تعریف نشده است.";
                showNotification(notificationMessage,TrayIcon.MessageType.ERROR);
            }else {
               notificationMessage = notificationMessage + "بروز خطای ناشناحته";
               showNotification(notificationMessage,TrayIcon.MessageType.ERROR);
           }
            return;
        }
        ObjectMapper mapper = new ObjectMapper();

        InvokeNextResponse invokeNextResponse =
                mapper.convertValue(
                        responseMessage.getPayload(),
                        InvokeNextResponse.class);

        notificationMessage = notificationMessage + "شماره " + invokeNextResponse.getInvokedNumber() + " فراخوانی شد. ";
        LOGGER.info("Number {} invoked", invokeNextResponse.getInvokedNumber());

        Counter counter = hidLedCounterDisplayService.getCounterInfo();
        if(counter != null){
            if (counter.getDisplayType().equals(DisplayType.LED_BLINKING)){
                hidLedCounterDisplayService.startBlinking();
            } else if(counter.getDisplayType().equals(DisplayType.DOT_MATRIX_HID)) {
                hidLedCounterDisplayService.displayNumberOnMatrix(counter.getDisplayId(),invokeNextResponse.getInvokedNumber());
            }else if(counter.getDisplayType().equals(DisplayType.SEVEN_SEGMENT_HID)){
                hidLedCounterDisplayService.displayNumberOn7Segment(counter.getDisplayId(), invokeNextResponse.getInvokedNumber());
            }
        }

        int waitingInQueue = invokeNextResponse.getWaitingInQueue();
        if(waitingInQueue == 0){
            LOGGER.info("No one left waiting in queue.");
            notificationMessage = notificationMessage + "\n\nفرد دیگری در صف باقی نمانده است.";
        } else if(waitingInQueue == 1){
            LOGGER.info("There is {} left waiting in queue.", waitingInQueue);
            notificationMessage = notificationMessage + "\n\nتنها یک نفر دیگر در صف باقی مانده است.";
        } else if(waitingInQueue > 1){
            notificationMessage = notificationMessage + "\n\nتعداد " + waitingInQueue + " نفر دیگر در صف باقی مانده است.";
        }

        showNotification(notificationMessage,TrayIcon.MessageType.INFO);
    }

    public void showNotification(String notificationMessage, TrayIcon.MessageType notificationType){
        showNotification(
                "فراخوان باجه " + counterId + "\n",
                notificationMessage,
                notificationType
        );
    }

    public void showNotification(String caption, String message, TrayIcon.MessageType type) {
        AppNotifier.notify(caption, message, type);
    }

    /*public void showNotification(String caption, String notificationMessage, TrayIcon.MessageType notificationType){
        if (SystemTray.isSupported()) {
            MyGuiApp.displayTray(caption,
                    notificationMessage,
                    notificationType);
        } else {
            LOGGER.error("System tray not supported!");
        }
    }*/



    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        switch (e.getKeyCode()) {
            case NativeKeyEvent.VC_CONTROL -> MASK_CONTROL = false;
            case NativeKeyEvent.VC_ALT -> MASK_ALT = false;
            case NativeKeyEvent.VC_RIGHT -> MASK_ARROW_RIGHT = false;
            case NativeKeyEvent.VC_LEFT -> MASK_ARROW_LEFT = false;
            case NativeKeyEvent.VC_UP -> MASK_ARROW_UP = false;
            case NativeKeyEvent.VC_DOWN -> MASK_ARROW_DOWN = false;
            /*case NativeKeyEvent.VC_F1 -> MASK_F1 = false;
            case NativeKeyEvent.VC_F2 -> MASK_F2 = false;*/
        }
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        if(startGettingNumber){
            // Just Numbers
            if (e.getRawCode() >= 48 && e.getRawCode() <= 57){
                numberStr = numberStr + e.getKeyChar();
            }
            if(numberStr.length() == 3){
                startGettingNumber = false;
                //ToDo: Handle Response as 301

                Counter counter = hidLedCounterDisplayService.getCounterInfo();
                if(counter != null){
                    ResponseMessage responseMessage = qmsClient.invokeFromQueue(new InvocationRequest_new(
                            counterId,
                            counter.getDisplayId(),
                            Integer.parseInt(numberStr)));
                    if (!responseMessage.isSucceed()){
                        LOGGER.info(responseMessage.getContent());
                        if(responseMessage.getStatusCode() == STATUS_CODE_ERROR_OUT_OF_RANGE_INVOKED_NUMBER){
                            showNotification("شماره فراخوانی شده خارج از رنج مجاز است.",TrayIcon.MessageType.WARNING);
                        }else if(responseMessage.getStatusCode() == STATUS_CODE_ERROR_INVOKING_NUMBER_NOT_ALLOWED){
                            showNotification("فراخوانی خارج از نوبت مجاز نیست.",TrayIcon.MessageType.WARNING);
                        }
                    }else {
                        LOGGER.info("Number {} invoked.", numberStr);
                        showNotification(  "شماره "+ numberStr + " فراخوانی شد.",TrayIcon.MessageType.INFO);
                    }
                }

                numberStr = "";
                startGettingNumber = false;

            }
        }
    }

}
