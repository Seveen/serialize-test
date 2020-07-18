import kotlinx.serialization.*
import kotlinx.serialization.builtins.list
import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.Context
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.amethyst.api.newEntityOfType
import org.hexworks.amethyst.api.system.Behavior
import org.hexworks.amethyst.api.system.Facet
import org.hexworks.amethyst.api.system.System

@Suppress("UNCHECKED_CAST")
@ImplicitReflectionSerializer
class EntitySerializer<T : EntityType, C : Context> : KSerializer<Entity<T, C>> {
    override val descriptor: SerialDescriptor = SerialDescriptor("Entity") {
        element<String>("type")
        element<String>("attributes")
        element<String>("facets")
        element<String>("behaviors")
    }

    override fun deserialize(decoder: Decoder): Entity<T, C> {
        val dec = decoder.beginStructure(descriptor)
        var type: T? = null
        var attributes: List<Attribute> = listOf()
        var facets: List<Facet<C>> = listOf()
        var behaviors: List<Behavior<C>> = listOf()

        loop@ while (true) {
            when (val i = dec.decodeElementIndex(descriptor)) {
                CompositeDecoder.READ_DONE -> break@loop
                0 -> type = dec.decodeSerializableElement(descriptor, i, PolymorphicSerializer(Attribute::class)) as T
                1 -> attributes =
                    dec.decodeSerializableElement(descriptor, i, PolymorphicSerializer(Attribute::class).list)
                2 -> facets = dec.decodeSerializableElement(descriptor, i, PolymorphicSerializer(System::class).list) as List<Facet<C>>
                3 -> behaviors = dec.decodeSerializableElement(descriptor, i, PolymorphicSerializer(System::class).list) as List<Behavior<C>>
                else -> throw SerializationException("Unknown index $i")
            }
        }

        dec.endStructure(descriptor)
        return newEntityOfType<T, C>(type ?: throw MissingFieldException("type")) {
            attributes(*attributes.toTypedArray())
            facets(*facets.toTypedArray())
            behaviors(*behaviors.toTypedArray())
        }
    }

    override fun serialize(encoder: Encoder, value: Entity<T, C>) {
        val compositeOutput = encoder.beginStructure(descriptor)
        compositeOutput.encodeSerializableElement(descriptor, 0, PolymorphicSerializer(Attribute::class), value.type)
        compositeOutput.encodeSerializableElement(
            descriptor,
            1,
            PolymorphicSerializer(Attribute::class).list,
            value.attributes.toList()
        )
        compositeOutput.encodeSerializableElement(
            descriptor,
            2,
            PolymorphicSerializer(System::class).list,
            value.facets.toList()
        )
        compositeOutput.encodeSerializableElement(
            descriptor,
            3,
            PolymorphicSerializer(System::class).list,
            value.behaviors.toList()
        )
        compositeOutput.endStructure(descriptor)
    }
}