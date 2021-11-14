public class ArmeriaConfiguration {
    @Bean
    public WebClient webClient(MeterRegistry meterRegistry, HttpTracing tracing) {
        return WebClient.builder("http://xxxx:8081/")
                        .decorator(circuitBreakerDecorator(meterRegistry))
                        .build();
    }

    private static Function<? super HttpClient, CircuitBreakerClient> circuitBreakerDecorator(
            MeterRegistry meterRegistry) {
        final CircuitBreakerRule rule = CircuitBreakerRule.builder()
                                                          .onServerErrorStatus()
                                                          .onException()
                                                          .thenFailure();
        final CircuitBreaker circuitBreaker =
                CircuitBreaker.builder("BackendApiClient")
                              .listener(CircuitBreakerListener.metricCollecting(meterRegistry))
                              .build();
        return CircuitBreakerClient.newDecorator(circuitBreaker, rule);
    }
}
