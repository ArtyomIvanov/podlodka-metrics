package podlodka.metrics.service

import mu.KLogging
import org.springframework.stereotype.Service
import podlodka.metrics.client.SomeBusinessCalculatorClient
import kotlin.random.Random


@Service
class SomeBusinessImpl(
    private val someBusinessCalculatorClient: SomeBusinessCalculatorClient,
) : SomeBusiness {

    override suspend fun makeBusiness(): String? {
        return try {
            failOnSomeStrongLogic()

            someBusinessCalculatorClient.callBusinessCalculator()
        } catch (ex: RuntimeException) {
            logger.error { "SOME ERROR" }
            throw ex
        }
    }






    companion object : KLogging()

    private fun failOnSomeStrongLogic() {
        if (Random.nextDouble(0.0, 1.0) < 0.1)
            throw RuntimeException("FIASKO")
    }
}
