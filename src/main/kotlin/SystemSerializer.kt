
import kotlinx.serialization.*
import org.hexworks.amethyst.api.Context
import org.hexworks.amethyst.api.system.System
import kotlin.reflect.KClass

class SystemSerializer <T: System<C>, C: Context> (val klass: KClass<T>): KSerializer<T> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor(klass.simpleName!!, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): T {
        klass.objectInstance?.let {
            return it
        }

        return klass.constructors.first().call()
    }

    override fun serialize(encoder: Encoder, value: T) {
    }

    companion object {
        inline fun <reified T: System<C>, C: Context> create() = SystemSerializer(T::class)
    }
}

