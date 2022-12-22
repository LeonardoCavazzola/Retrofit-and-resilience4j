import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import java.time.Duration

val config: CircuitBreakerConfig = CircuitBreakerConfig.custom()
    .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) // baseado em contagem
    .slidingWindowSize(10) // mede a cada 10 request
    .slowCallRateThreshold(70.0F)  // se 70% for lenta, abre o circuito
    .failureRateThreshold(70.0F) // e se 70% falhar, abre o circuito
    .waitDurationInOpenState(Duration.ofSeconds(5)) // ficar 5 com o circuito aberto, antes de ficar meio aberto
    .slowCallDurationThreshold(Duration.ofSeconds(3))// define o que é uma chamada lenta
    .permittedNumberOfCallsInHalfOpenState(3) // numero de requisições premitida com o circuito meio aberto antes de fechar ou abrir.
    .build()

fun buildCircuitBreaker(name: String): CircuitBreaker = CircuitBreakerRegistry.of(config).circuitBreaker(name)
