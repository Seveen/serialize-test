
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.SerializersModule
import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.amethyst.api.system.System
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

class EntitySerializationTest {

    private lateinit var target: Entity<EntityType, GameContext>

    private val module = SerializersModule {
        polymorphic(Attribute::class) {
            Human::class with Human.serializer()
            LifeStats::class with LifeStats.serializer()
            DmgStats::class with DmgStats.serializer()
        }

        polymorphic(System::class) {
            Hurtable::class with SystemSerializer.create()
            Bully::class with SystemSerializer.create()
            TestFacet::class with SystemSerializer.create()
            TestBehavior::class with SystemSerializer.create()
        }
    }

    val json = Json(
        configuration = JsonConfiguration(useArrayPolymorphism = true),
        context = module
    )

    @BeforeTest
    fun setup() {
        target = testEntity
    }

    @Test
    @ImplicitReflectionSerializer
    fun given_an_entity_it_should_serialize_correctly() {

        val jsonData = json.stringify(EntitySerializer(), target)

        println(jsonData)

        val result = json.parse(EntitySerializer<EntityType, GameContext>(), jsonData)

        assertTrue {
            result.type == target.type
                    && result.hasAttributes
                    && result.hasBehaviors
                    && result.hasFacets
        }
    }
}