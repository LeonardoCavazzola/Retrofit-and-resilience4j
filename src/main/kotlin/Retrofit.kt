import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.retrofit.CircuitBreakerCallAdapter
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.create
import retrofit2.http.GET

val objectMapper: ObjectMapper = ObjectMapper()
    .findAndRegisterModules()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

val objectMapperConverterFactory: JacksonConverterFactory = JacksonConverterFactory.create(objectMapper)

val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()

val circuitBreaker: CircuitBreaker = buildCircuitBreaker("publicapis")

interface EntryGateway {
    @GET("/entries")
    fun listEntries(): Call<Map<String, Any>>

    @GET("/entries")
    suspend fun listEntriesWithoutCall(): Map<String, Any>
}

fun retrofit(): Retrofit = Retrofit.Builder()
    .baseUrl("https://api.publicapis.org")
    .addConverterFactory(objectMapperConverterFactory)
    .addCallAdapterFactory(CircuitBreakerCallAdapter.of(circuitBreaker))
    .client(okHttpClient)
    .build()


suspend fun main() {
    val client = retrofit().create<EntryGateway>()
    client.listEntriesWithoutCall().let(::println)
}
