package podlodka.metrics.client

import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Timer
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import reactor.core.publisher.Mono
import kotlin.random.Random

@Component
class SomeBusinessCalculatorClient {

    suspend fun callBusinessCalculator(): String? {
        return wrapInTimerMetric {
            val response = webClient
                .method(HttpMethod.POST)
                .uri("http://localhost:1080/some-business-calculator/handle")
                .exchange()
                .awaitSingle()

            response.awaitBodyOrNull()
        }
    }

    suspend fun <T> wrapInTimerMetric(
        block: suspend () -> T?,
    ): T? {
        val timer = Timer.start()

        val result = block()

        timer.stop(
            Timer.builder("output_traffic_duration")
                .publishPercentiles(0.99, 0.95, 0.90, 0.75, 0.50)
                .description("Duration of ENRICHERS operations")
                .register(Metrics.globalRegistry)
        )

        return result
    }

    companion object {
        private val webClient = WebClient.builder().exchangeFunction {
            val randomValue = Random.nextDouble(0.0, 1.0)
            Mono.just(
                if (randomValue < 0.2) {
                    Thread.sleep(500)
                    ClientResponse.create(HttpStatus.BAD_GATEWAY).body("ERROR").build()
                } else {
                    Thread.sleep(50)
                    ClientResponse.create(HttpStatus.OK).body("GOOD BUSINESS").build()
                }
            )
        }.build()
    }
}
