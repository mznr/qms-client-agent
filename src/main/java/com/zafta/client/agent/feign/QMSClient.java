package com.zafta.client.agent.feign;

import com.zafta.client.agent.models.InvocationRequest_new;
import com.zafta.client.agent.models.ResponseMessage;
import com.zafta.client.agent.models.counter.CounterUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(
        url = "${qms.server.url}",
        name = "QMS-SERVICE",
        path = "/api/v1"
        // configuration = FeignResponseDecoderConfig.class
)
public interface QMSClient {

    @GetMapping("/qms/application/info")
    ResponseMessage getApplicationInfo();

    @GetMapping("/configs/led-blinking/blinkingConfig")
    ResponseMessage getBlinkingConfiguration();

    @GetMapping("/display-configs/get/dot-matrix-Config")
    ResponseMessage getDotMatrixDisplayConfiguration();

    @GetMapping("/counters/getCounter/{counterId}")
    ResponseMessage getCounter(@PathVariable(value="counterId") int counterId);

    @PostMapping("/counters/updateCounter")
    ResponseMessage updateCounter(@RequestBody CounterUpdateRequest request);

    @GetMapping("/turns/getWaitingInQueueByCID/{counterId}")
    ResponseMessage getWaitingInQueueByCounterId(@PathVariable int counterId);

    @GetMapping("/turns/getWaitingInQueueByQID/{queueId}")
    ResponseMessage getWaitingInQueueByQueueId(@PathVariable int queueId);

    @GetMapping("/queues/getQueue/{queueId}")
    ResponseMessage getQueue(@PathVariable int queueId);

    @GetMapping("/turns/invokeNext/{counterId}")
    ResponseMessage invokeNextFromQueue(@PathVariable(value="counterId") int counterId);

    @GetMapping("/turns/recall/{counterId}")
    ResponseMessage recallFromQueue(@PathVariable(value="counterId") int counterId);

    @PostMapping("/turns/invoke")
    ResponseMessage invokeFromQueue(@RequestBody InvocationRequest_new invocationRequest);

    @GetMapping("/counters/toggleWaitingMode/{counterId}")
    ResponseMessage toggleWaitingModeForCounter(@PathVariable int counterId);

    @GetMapping("/qms/heartbeat/{counterId}")
    ResponseMessage updateHeartbeat(@PathVariable(value="counterId") int counterId);
}