public class ArmeriaConfiguration {

    public String getString() {
        if (isFeatureEnabled("use-new-string")) {
            return "bar";
        }
        return "foo";
    }
    
    @Bean
    public WebClient webClient(MeterRegistry meterRegistry, HttpTracing tracing) {
        return WebClient.builder("http://xxxx:8081/")
                        .decorator(circuitBreakerDecorator(meterRegistry))
                        .build();
    }

    private static Function<? super HttpClient, CircuitBreakerClient> circuitBreakerDecorator(
            MeterRegistry meterRegistry) {
        final CircuitBreakerRule rule =
                CircuitBreakerRule.builder()
                                  // A failure if the response is 5xx.
                                  .onServerErrorStatus()
                                  // A failure if an Exception is raised.
                                  // ResponseTimeoutException is thrown when response is not received within timeout.
                                  .onException()
                                  .thenFailure();
        final CircuitBreaker circuitBreaker =
                CircuitBreaker.builder("BackendApiClient")
                              .listener(CircuitBreakerListener.metricCollecting(meterRegistry))
                              // The threshold that changes CircuitBreaker's state to OPEN.
                              .failureRateThreshold(0.5)
                              .build();
        return CircuitBreakerClient.newDecorator(circuitBreaker, rule);
    }
}
