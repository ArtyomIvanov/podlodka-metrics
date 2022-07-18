package podlodka.metrics.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import podlodka.metrics.service.SomeBusiness


@RestController
class SomeBusinessController(
    private val someBusiness: SomeBusiness,
) {

    @GetMapping("/task")
    suspend fun requestBusinessLogic(): String? {
        return someBusiness.makeBusiness()
    }
}
