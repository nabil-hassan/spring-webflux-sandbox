package net.nh.ch1.basicendpoints;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

/**
 * Example of how to use the traditional imperative programming style to create a Reactive.
 * </p>
 * Some notes:
 * <ul>
 *     <li>In general, this approach is exactly the same as writing standard REST controllers. </li>
 *     <li>Endpoints must however all return a Mono which wraps the final result.</></li>
 * </ul>
 */
@RestController
@RequestMapping("/imperative")
@Slf4j
public class ImperativeController {

    public static final String CORRELATION_ID = "correlation-id";

    // ===========================================================================================================================
    //     GET endpoint which demonstrates using Project Reactor Context
    // ===========================================================================================================================
    /**
     * Illustrates how to write a simple GET endpoint, which accepts optional query params, and returns a custom HTTP header.
     * @param nameParam the optional query param
     * @return a String-based ResponseEntity containing a custom header
     */
    @GetMapping
    public Mono<ResponseEntity<String>> greeting(@RequestParam(value = "nameParam", required = false) Optional<String> nameParam){
        var name = nameParam.orElse("anon");

        log.info("Handle greeting request with name: {}", name);

        return Mono.deferContextual(ctx -> buildResponseEntity(ctx, name))
                .transformDeferredContextual(this::logCorrelationId)
                .contextWrite(ctx -> ctx.put(CORRELATION_ID, UUID.randomUUID()));

        // alternative form, which shows how to access the context in flatMap()
        //return Mono.just(name)
        //        .flatMap((n) -> Mono.deferContextual(ctx -> buildResponseEntity(ctx, name))
        //        .transformDeferredContextual(this::logCorrelationId)
        //        .contextWrite(ctx -> ctx.put(CORRELATION_ID, UUID.randomUUID()));

    }

    private Publisher<ResponseEntity<String>> logCorrelationId(Mono<ResponseEntity<String>> responseEntityMono,
                                                               ContextView contextView) {
        log.info("The correlation id is: {}", (UUID) contextView.get(CORRELATION_ID));
        return responseEntityMono;
    }

    private Mono<ResponseEntity<String>> buildResponseEntity(ContextView ctx, String name) {
        var correlationId = ctx.get(CORRELATION_ID);
        var message = "Hi there: " + name;

        log.info("Building response entity with name: {} and correlation id: {}", name, correlationId);

        var headers = new HttpHeaders();
        headers.set(CORRELATION_ID, correlationId.toString());

        return Mono.just(new ResponseEntity<>(message, headers, OK));
    }
}
