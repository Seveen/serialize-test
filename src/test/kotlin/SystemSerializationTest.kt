import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.SerializersModule
import org.hexworks.amethyst.api.system.System
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SystemSerializationTest {
    private lateinit var target: System<GameContext>

    private val module = SerializersModule {
        polymorphic(System::class) {
            Hurtable::class with SystemSerializer.create()
            Bully::class with SystemSerializer.create()
        }
    }

    private val json = Json(
        configuration = JsonConfiguration(useArrayPolymorphism = true),
        context = module
    )

    @Test
    fun given_an_object_system_it_should_serialize_correctly() {
        target = Hurtable

        val jsonData = json.stringify(PolymorphicSerializer(System::class), target)

        println(jsonData)

        val result = json.parse(PolymorphicSerializer(System::class), jsonData)

        assertEquals(target, result)
    }

    @Test
    fun given_a_class_system_it_should_serialize_correctly() {
        target = Bully()

        val jsonData = json.stringify(PolymorphicSerializer(System::class), target)

        println(jsonData)

        val result = json.parse(PolymorphicSerializer(System::class), jsonData)

        assertTrue {
            target::class == result::class
        }
    }
}