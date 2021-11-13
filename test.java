public class ArmeriaConfiguration {
    @Bean
    public MeterRegistry meterRegistry(){
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }
}
