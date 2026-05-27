package com.zafta.client.agent.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zafta.client.agent.feign.QMSClient;
import com.zafta.client.agent.mappers.GenericMapper;
import com.zafta.client.agent.models.CounterDotMatrixDisplayConfig;
import com.zafta.client.agent.models.LEDBlinkingConfig;
import com.zafta.client.agent.models.ResponseMessage;
import com.zafta.client.agent.models.counter.Counter;
import com.zafta.client.agent.models.counter.CounterDisplayRequest;
import com.zafta.client.agent.models.counter.DisplayType;
import com.zafta.client.agent.protocol.*;
import com.zafta.client.agent.schedules.BlinkTimeoutSchedule;
import com.zafta.client.agent.utilities.BytesUtils;
import lombok.RequiredArgsConstructor;
import org.hid4java.*;
import org.hid4java.event.HidServicesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class CounterHIDDisplayService implements HidServicesListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CounterHIDDisplayService.class);
    private HidDevice handle = null;

    private final GenericMapper mapper;

    private final QMSClient qmsClient;

    @Value("${zafta.counter.id}")
    private int counterId;

    @Value("${zafta.counter.displayId}")
    private int currentDisplayId;

    private BlinkTimeoutSchedule blinkTimeoutSchedule;

    private final int VENDOR_ID = 0xc251;
    private final short PRODUCT_ID = (short) 0x2201;

    @EventListener(ApplicationReadyEvent.class)
    private void executeExample() throws HidException {
        enumerateHIDDevices();
        Counter counter = getCounterInfo();
        if (counter == null) {
            return;
        }
        if (
                !counter.getDisplayType().equals(DisplayType.LED_BLINKING) &&
                        !counter.getDisplayType().equals(DisplayType.DOT_MATRIX_HID) &&
                        !counter.getDisplayType().equals(DisplayType.SEVEN_SEGMENT_HID)
        ) {
            return;
        }

        /* ToDo: check if display type is blinking-led */
        /* it Turns the LED off */
        //if(counter.getDisplayType().equals(DisplayType.LED_BLINKING)) {
            blinkTimeoutSchedule = new BlinkTimeoutSchedule(this::TurnOff);
        //}


        //applyDefaultSettingsOnCounterHIDDotMatrixDisplay();

        //powerOnSelfTest();

    }

    public void enumerateHIDDevices() {
        // Configure to use custom specification
        HidServicesSpecification hidServicesSpecification = new HidServicesSpecification();
        // Use the v0.7.0 manual start feature to get immediate attach events
        hidServicesSpecification.setAutoStart(false);

        // Get HID services using custom specification
        HidServices hidServices = HidManager.getHidServices(hidServicesSpecification);
        hidServices.addHidServicesListener(this);

        // Manually start the services to get attachment event
        hidServices.start();

        // Provide a list of attached devices
        for (HidDevice hidDevice : hidServices.getAttachedHidDevices()) {
            if (hidDevice.isVidPidSerial(VENDOR_ID, PRODUCT_ID, null)) {
                handle = hidDevice;
                handle.open();
            }
        }

        if (handle == null) {
            LOGGER.warn("No Counter HID-type Display found.");
        }
    }

    public void powerOnSelfTest() {
        try {
            Counter counter = getCounterInfo();
            if (counter == null) {
                return;
            }
            if (counter.getDisplayType().equals(DisplayType.LED_BLINKING)) {
                startBlinking();
            }
        } catch (Exception e) {
            LOGGER.error("LED Display Self Test Failed");
        }
    }

    public Counter getCounterInfo() {
        try {
            ResponseMessage responseMessage = qmsClient.getCounter(counterId);
            if (!responseMessage.isSucceed()) {
                LOGGER.info(responseMessage.getContent());
                return null;
            }

            return mapper.convertToDto(responseMessage.getPayload(), Counter.class);
        } catch (Exception ignored) {
            return null;
        }
    }

    public void delay(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
    }


    public boolean applyDefaultSettingsOnCounterHIDDotMatrixDisplay() {
        Counter counter = getCounterInfo();
        if (counter == null) {
            return false;
        }
        if (counter.getDisplayType().equals(DisplayType.DOT_MATRIX_HID)) {
            if (handle == null) {
                LOGGER.error("Matrix Display is not Connected. Settings wouldn't be set on it");
                return false;
            }
            CounterDotMatrixDisplayConfig dotMatrixDisplayConfiguration = getDotMatrixDisplayConfiguration();
            setTextOnCounterDotMatrixDisplay(dotMatrixDisplayConfiguration.getDefaultText());

            delay(200);

            int defaultOnDurationSeconds = dotMatrixDisplayConfiguration.getDefaultOnDurationSeconds();
            setCounterDotMatrixDisplayOnDuration(defaultOnDurationSeconds);


            int newDisplayId = counter.getDisplayId();
            if (newDisplayId != currentDisplayId) {
                delay(1000);
                setIdOnCounterDotMatrixDisplay(currentDisplayId, newDisplayId);
            }

            return true;
        }
        return false;
    }


    public void setTextOnCounterDotMatrixDisplay(String textToBeDisplayed) {
        if (handle == null) {
            LOGGER.error("Matrix Display is not Connected. Text wouldn't be set on it");
            return;
        }
        try {
            byte[] command = CounterDisplaySetTextCMDBuilder.buildCMDSetText(textToBeDisplayed);
            handle.write(command, command.length, (byte) 0x03);
            LOGGER.info("Counter display text successfully set to {}", textToBeDisplayed);
        } catch (Exception e) {
            LOGGER.error("Error in setting text on counter dot-matrix display");
        }
    }


    public void setIdOnCounterDotMatrixDisplay(int oldId, int newId) {
        if (handle == null) {
            LOGGER.error("Matrix Display is not Connected. Id wouldn't be set on it");
            return;
        }

        try {
            for(int i = 0; i<5 ;i++){
                byte[] command = CounterDisplaySetIdCMDBuilder.build(oldId, newId);
                handle.write(command, command.length, (byte) 0x03);
                delay(100);
            }

            LOGGER.info("Counter display id successfully changed from {} to {}", oldId, newId);
        } catch (Exception e) {
            LOGGER.error("Error in setting Id on counter dot-matrix display");
        }
    }


    public void setCounterDotMatrixDisplayOnDuration(int duration) {
        if (handle == null) {
            LOGGER.error("Matrix Display is not Connected. On-Duration wouldn't be set on it");
            return;
        }

        try {
            byte[] command = CounterDisplaySetOnDurationCMDBuilder.build(duration);
            handle.write(command, command.length, (byte) 0x03);
            LOGGER.info("Counter display on-duration (seconds) successfully set tp : {}", duration);
        } catch (Exception e) {
            LOGGER.error("Error in setting on-duration on counter dot-matrix display");
        }
    }

    public void displayNumberOnMatrix(int counterId, int invokedNumber) {
        if (handle == null) {
            LOGGER.warn("Counter Matrix Display is not Connected. ");
            return;
        }
        CounterDisplayRequest counterDisplayRequest = new CounterDisplayRequest(counterId, invokedNumber);
        byte[] bytes = new CounterDisplayCMDBuilder(counterDisplayRequest).build();
        for (int i = 0; i < 5; i++) {
            handle.write(bytes, bytes.length, (byte) 0x03);
        }
    }

    public void displayNumberOn7Segment(int counterId, int invokedNumber) {
        if (handle == null) {
            throw new RuntimeException("7-Segment Display is not Connected.");
        }
        CounterDisplayRequest counterDisplayRequest = new CounterDisplayRequest(counterId, invokedNumber);
        byte[] bytes = new CounterDisplayCMDBuilder_7Segment(counterDisplayRequest).build();
        handle.write(bytes, bytes.length, (byte) 0x03);
    }

    public void TurnOn() {
        if (handle == null) {
            throw new RuntimeException("LED Display is not Connected.");
        }
        byte[] bytes = BytesUtils.toByteArray("315051525354555657");
        handle.write(bytes, bytes.length, (byte) 0x03);
        delay(5);
        handle.write(bytes, bytes.length, (byte) 0x03);
        delay(5);
        handle.write(bytes, bytes.length, (byte) 0x03);
    }


    public void TurnOff() {
        if (handle == null) {
            throw new RuntimeException("LED Display is not Connected.");
        }
        byte[] bytes = BytesUtils.toByteArray("305051525354555657");
        handle.write(bytes, bytes.length, (byte) 0x03);
        delay(5);
        handle.write(bytes, bytes.length, (byte) 0x03);
        delay(5);
        handle.write(bytes, bytes.length, (byte) 0x03);
    }

    public void startBlinking() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        LEDBlinkingRunnable runnable = new LEDBlinkingRunnable();
        executorService.execute(runnable);
        executorService.shutdown();
    }

    public void blink(int numberOfBlinks, int delay) {
        if (handle == null) {
            throw new RuntimeException("LED Display is not Connected.");
        }

        for (int i = 0; i < numberOfBlinks; i++) {
            TurnOn();
            delay(delay);
            TurnOff();
            delay(delay);
        }

        TurnOn();
    }

    public CounterDotMatrixDisplayConfig getDotMatrixDisplayConfiguration() {
        ResponseMessage responseMessage = qmsClient.getDotMatrixDisplayConfiguration();
        if (!responseMessage.isSucceed()) {
            LOGGER.info(responseMessage.getContent());
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();

        return mapper.convertValue(
                responseMessage.getPayload(),
                CounterDotMatrixDisplayConfig.class);
    }


    public LEDBlinkingConfig getBlinkingConfiguration() {
        ResponseMessage responseMessage = qmsClient.getBlinkingConfiguration();
        if (!responseMessage.isSucceed()) {
            LOGGER.info(responseMessage.getContent());
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();

        return mapper.convertValue(
                responseMessage.getPayload(),
                LEDBlinkingConfig.class);
    }

    class LEDBlinkingRunnable implements Runnable {
        @Override
        public void run() {
            try {
                LEDBlinkingConfig ledBlinkingConfig = getBlinkingConfiguration();

                blink(
                        ledBlinkingConfig.getNumberOfBlinks(), //* 2
                        ledBlinkingConfig.getBlinkingInterval());
                blinkTimeoutSchedule.restart(ledBlinkingConfig.getStayOnDuration());
            } catch (RuntimeException e) {
                LOGGER.warn(e.getMessage());
            }
        }
    }


    @Override
    public void hidDeviceAttached(HidServicesEvent hidServicesEvent) {
        HidDevice hidDevice = hidServicesEvent.getHidDevice();
        if (hidDevice.isVidPidSerial(VENDOR_ID, PRODUCT_ID, null)) {
            LOGGER.info("Counter HID Display connected");
            handle = hidDevice;
            handle.open();

            powerOnSelfTest();
            applyDefaultSettingsOnCounterHIDDotMatrixDisplay();
        }
    }

    @Override
    public void hidDeviceDetached(HidServicesEvent hidServicesEvent) {
        HidDevice hidDevice = hidServicesEvent.getHidDevice();
        if (hidDevice.isVidPidSerial(VENDOR_ID, PRODUCT_ID, null)) {
            LOGGER.info("Counter HID Display Detached");
        }
    }

    @Override
    public void hidFailure(HidServicesEvent hidServicesEvent) {

    }

    @Override
    public void hidDataReceived(HidServicesEvent hidServicesEvent) {

    }
}
