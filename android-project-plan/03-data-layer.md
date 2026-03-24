# Phase 2 — Data Layer (Retrofit, DTOs, Repository Implementations)

Implémentation de la couche data : DTOs pour le mapping JSON, interfaces Retrofit, mappers, et implémentations concrètes des repositories.

> **Source web** : `happyrow-front/src/features/*/services/Http*Repository.ts`

---

## Conventions backend importantes

Le backend utilise des conventions spécifiques qu'il faut respecter :

- **Champs JSON** : `snake_case` (ex: `event_date`, `user_email`, `created_at`)
- **Identifiants** : champ `identifier` (pas `id`)
- **Organisateur** : champ `creator` (pas `organizerId`)
- **Timestamps** : en millisecondes (nombre, pas ISO string)
- **Auth** : Header `Authorization: Bearer <token>`

---

## Tâches

### T-2.1 : DTOs Events

- [ ] Créer `data/remote/dto/EventDto.kt`

```kotlin
@Serializable
data class EventApiRequest(
    val name: String,
    val description: String,
    val event_date: String,
    val location: String,
    val type: String
)

@Serializable
data class EventApiResponse(
    val identifier: String,
    val name: String,
    val description: String,
    val event_date: Long,
    val location: String,
    val type: String,
    val creator: String? = null,
    val creation_date: Long? = null,
    val update_date: Long? = null,
    val members: List<String>? = null
)
```

> **Ref web** : `src/features/events/services/HttpEventRepository.ts` lignes 5-25

### T-2.2 : DTOs Participants

- [ ] Créer `data/remote/dto/ParticipantDto.kt`

```kotlin
@Serializable
data class ParticipantApiRequest(
    val user_email: String,
    val status: String
)

@Serializable
data class ParticipantApiResponse(
    val user_email: String,
    val user_name: String? = null,
    val event_id: String,
    val status: String,
    val joined_at: Long,
    val updated_at: Long? = null
)
```

> **Ref web** : `src/features/participants/services/HttpParticipantRepository.ts` lignes 9-21

### T-2.3 : DTOs Resources

- [ ] Créer `data/remote/dto/ResourceDto.kt`

```kotlin
@Serializable
data class ResourceApiRequest(
    val name: String,
    val category: String,
    val quantity: Int,
    val suggested_quantity: Int? = null
)

@Serializable
data class ContributorApiResponse(
    val user_id: String,
    val quantity: Int,
    val contributed_at: Long
)

@Serializable
data class ResourceApiResponse(
    val identifier: String,
    val event_id: String,
    val name: String,
    val category: String,
    val current_quantity: Int,
    val suggested_quantity: Int? = null,
    val contributors: List<ContributorApiResponse> = emptyList(),
    val version: Int = 0,
    val created_at: Long,
    val updated_at: Long? = null
)
```

> **Ref web** : `src/features/resources/services/HttpResourceRepository.ts` lignes 9-33

### T-2.4 : DTOs Contributions

- [ ] Créer `data/remote/dto/ContributionDto.kt`

```kotlin
@Serializable
data class ContributionApiRequest(
    val quantity: Int
)

@Serializable
data class ContributionApiResponse(
    val identifier: String,
    val participant_id: String,
    val resource_id: String,
    val quantity: Int,
    val created_at: Long,
    val updated_at: Long? = null
)
```

> **Ref web** : `src/features/contributions/services/HttpContributionRepository.ts` lignes 9-21

### T-2.5 : Interface Retrofit — EventApi

- [ ] Créer `data/remote/api/EventApi.kt`

```kotlin
interface EventApi {
    @POST("events")
    suspend fun createEvent(@Body request: EventApiRequest): EventApiResponse

    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: String): EventApiResponse

    @GET("events")
    suspend fun getEvents(): List<EventApiResponse>

    @PUT("events/{id}")
    suspend fun updateEvent(
        @Path("id") id: String,
        @Body request: EventApiRequest
    ): EventApiResponse

    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: String)
}
```

> **Ref web** : Endpoints déduits de `HttpEventRepository.ts`
> - POST `/events`
> - GET `/events/{id}`
> - GET `/events` (liste tous, filtrage côté client)
> - PUT `/events/{id}`
> - DELETE `/events/{id}`

### T-2.6 : Interface Retrofit — ParticipantApi

- [ ] Créer `data/remote/api/ParticipantApi.kt`

```kotlin
interface ParticipantApi {
    @POST("events/{eventId}/participants")
    suspend fun addParticipant(
        @Path("eventId") eventId: String,
        @Body request: ParticipantApiRequest
    ): ParticipantApiResponse

    @GET("events/{eventId}/participants")
    suspend fun getParticipantsByEvent(
        @Path("eventId") eventId: String
    ): List<ParticipantApiResponse>

    @PUT("events/{eventId}/participants/{userEmail}")
    suspend fun updateParticipantStatus(
        @Path("eventId") eventId: String,
        @Path("userEmail") userEmail: String,
        @Body request: ParticipantApiRequest
    ): ParticipantApiResponse

    @DELETE("events/{eventId}/participants/{userEmail}")
    suspend fun removeParticipant(
        @Path("eventId") eventId: String,
        @Path("userEmail") userEmail: String
    )
}
```

> **Ref web** : Endpoints déduits de `HttpParticipantRepository.ts`

### T-2.7 : Interface Retrofit — ResourceApi

- [ ] Créer `data/remote/api/ResourceApi.kt`

```kotlin
interface ResourceApi {
    @POST("events/{eventId}/resources")
    suspend fun createResource(
        @Path("eventId") eventId: String,
        @Body request: ResourceApiRequest
    ): ResourceApiResponse

    @GET("events/{eventId}/resources")
    suspend fun getResourcesByEvent(
        @Path("eventId") eventId: String
    ): List<ResourceApiResponse>

    @GET("resources/{id}")
    suspend fun getResourceById(@Path("id") id: String): ResourceApiResponse

    @PUT("resources/{id}")
    suspend fun updateResource(
        @Path("id") id: String,
        @Body request: ResourceApiRequest
    ): ResourceApiResponse

    @DELETE("resources/{id}")
    suspend fun deleteResource(@Path("id") id: String)
}
```

> **Ref web** : Endpoints déduits de `HttpResourceRepository.ts`

### T-2.8 : Interface Retrofit — ContributionApi

- [ ] Créer `data/remote/api/ContributionApi.kt`

```kotlin
interface ContributionApi {
    @GET("events/{eventId}/resources/{resourceId}/contributions")
    suspend fun getContributionsByResource(
        @Path("eventId") eventId: String,
        @Path("resourceId") resourceId: String
    ): List<ContributionApiResponse>

    @POST("events/{eventId}/resources/{resourceId}/contributions")
    suspend fun createContribution(
        @Path("eventId") eventId: String,
        @Path("resourceId") resourceId: String,
        @Body request: ContributionApiRequest
    ): ContributionApiResponse

    @DELETE("events/{eventId}/resources/{resourceId}/contributions")
    suspend fun deleteContribution(
        @Path("eventId") eventId: String,
        @Path("resourceId") resourceId: String
    )
}
```

> **Ref web** : Endpoints déduits de `HttpContributionRepository.ts`

### T-2.9 : Mappers DTO ↔ Domain

- [ ] Créer `data/remote/mapper/EventMapper.kt`

```kotlin
fun EventApiResponse.toDomain(fallbackOrganizerId: String = ""): Event {
    return Event(
        id = identifier,
        name = name,
        description = description,
        date = event_date,
        location = location,
        type = EventType.valueOf(type.uppercase()),
        organizerId = creator ?: fallbackOrganizerId
    )
}

fun EventCreationRequest.toApiRequest(): EventApiRequest {
    return EventApiRequest(
        name = name,
        description = description,
        event_date = date,
        location = location,
        type = type.name
    )
}
```

- [ ] Créer `data/remote/mapper/ParticipantMapper.kt` — même pattern
- [ ] Créer `data/remote/mapper/ResourceMapper.kt` — même pattern
- [ ] Créer `data/remote/mapper/ContributionMapper.kt` — même pattern

### T-2.10 : OkHttp Interceptor pour l'auth token

- [ ] Créer `data/remote/AuthInterceptor.kt`

```kotlin
class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenProvider()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
```

### T-2.11 : Implémentation `EventRepositoryImpl`

- [ ] Créer `data/repository/EventRepositoryImpl.kt`
- [ ] Injecter `EventApi` via constructeur
- [ ] Implémenter toutes les méthodes de `EventRepository`
- [ ] Utiliser les mappers pour convertir DTO ↔ Domain
- [ ] Gérer les erreurs HTTP (404 → null, autres → exception)

> **Ref web** : `src/features/events/services/HttpEventRepository.ts` — logique complète lignes 43-211

### T-2.12 : Implémentation `ParticipantRepositoryImpl`

- [ ] Créer `data/repository/ParticipantRepositoryImpl.kt`
- [ ] Même pattern que EventRepositoryImpl

> **Ref web** : `src/features/participants/services/HttpParticipantRepository.ts`

### T-2.13 : Implémentation `ResourceRepositoryImpl`

- [ ] Créer `data/repository/ResourceRepositoryImpl.kt`

> **Ref web** : `src/features/resources/services/HttpResourceRepository.ts`

### T-2.14 : Implémentation `ContributionRepositoryImpl`

- [ ] Créer `data/repository/ContributionRepositoryImpl.kt`

> **Ref web** : `src/features/contributions/services/HttpContributionRepository.ts`

### T-2.15 : Module Hilt — NetworkModule

- [ ] Créer `di/NetworkModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides @Singleton
    fun provideEventApi(retrofit: Retrofit): EventApi = retrofit.create(EventApi::class.java)

    @Provides @Singleton
    fun provideParticipantApi(retrofit: Retrofit): ParticipantApi = retrofit.create(ParticipantApi::class.java)

    @Provides @Singleton
    fun provideResourceApi(retrofit: Retrofit): ResourceApi = retrofit.create(ResourceApi::class.java)

    @Provides @Singleton
    fun provideContributionApi(retrofit: Retrofit): ContributionApi = retrofit.create(ContributionApi::class.java)
}
```

### T-2.16 : Module Hilt — RepositoryModule

- [ ] Créer `di/RepositoryModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Binds @Singleton
    abstract fun bindParticipantRepository(impl: ParticipantRepositoryImpl): ParticipantRepository

    @Binds @Singleton
    abstract fun bindResourceRepository(impl: ResourceRepositoryImpl): ResourceRepository

    @Binds @Singleton
    abstract fun bindContributionRepository(impl: ContributionRepositoryImpl): ContributionRepository
}
```

---

## Critères de validation

- Toutes les interfaces Retrofit compilent
- Les DTOs sont annotés `@Serializable`
- Les mappers couvrent tous les champs (attention aux champs optionnels)
- Les implémentations repository gèrent correctement les erreurs HTTP
- Le module Hilt fournit toutes les dépendances
- Le token Bearer est automatiquement ajouté à chaque requête via l'interceptor
