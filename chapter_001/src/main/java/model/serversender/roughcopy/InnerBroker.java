package model.serversender.roughcopy;

import model.broker.BrokerMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InnerBroker {

    private final ExecutorService service;
    private final BrokerMessage brokerMessage;

    public InnerBroker(BrokerMessage brokerMessage) {
        this.brokerMessage = brokerMessage;
        service = Executors.newCachedThreadPool();
    }

    public void init() {
        while (!this.service.isShutdown()) {
            service.submit(new MyRunnable());
        }
    }

    public void work() {
        while (brokerMessage.distribute()) {
        }
    }

    class MyRunnable implements Runnable {

        @Override
        public void run() {
            InnerBroker.this.work();
        }
    }
}
