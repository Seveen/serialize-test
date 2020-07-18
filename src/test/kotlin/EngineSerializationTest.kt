import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.SerializersModule
import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.amethyst.api.system.System
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class EngineSerializationTest {

    lateinit var target: TestEngine<GameContext>

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
        target = testEngine
    }

    @Test
    @ImplicitReflectionSerializer
    fun given_an_engine_it_should_serialize_correctly() {
        val jsonData = json.stringify(EntitySerializer<EntityType, GameContext>().list, testEngine.entities)

        println(jsonData)

        val result = TestEngine<GameContext>().also { engine ->
            json.parse(EntitySerializer<EntityType, GameContext>().list, jsonData).forEach {
                engine.addEntity(it)
            }
        }

        assertEquals(target.entities.size, result.entities.size)
    }
}