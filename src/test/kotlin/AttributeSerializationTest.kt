import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.hexworks.amethyst.api.Attribute
import org.junit.Test
import kotlin.test.assertEquals

class AttributeSerializationTest {
    private lateinit var target: Attribute

    @Test
    fun given_an_attribute_it_should_serialize_correctly() {
        target = Human("A human bully", "A baddie")

        val module = SerializersModule {
            polymorphic(Attribute::class) {
                Human::class with Human.serializer()
                LifeStats::class with LifeStats.serializer()
                DmgStats::class with DmgStats.serializer()
            }
        }

        val json = Json(context = module)

        val jsonData = json.stringify(PolymorphicSerializer(Attribute::class), target)

        println(jsonData)

        val result = json.parse(PolymorphicSerializer(Attribute::class), jsonData)

        assertEquals(target, result)
    }
}