
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import org.hexworks.amethyst.api.Context
import org.hexworks.amethyst.api.Engine
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import kotlin.coroutines.CoroutineContext

@Serializable
class TestEngine<T : Context>(
    override val coroutineContext: CoroutineContext = Dispatchers.Default
) : Engine<T>, CoroutineScope {

    val entities = mutableListOf<Entity<EntityType, T>>()

    @Synchronized
    override fun update(context: T): Job {
        return launch {
            entities.filter { it.needsUpdate }.map {
                async { it.update(context) }
            }.awaitAll()
        }
    }

    @Synchronized
    override fun addEntity(entity: Entity<EntityType, T>) {
        entities.add(entity)
    }

    @Synchronized
    override fun removeEntity(entity: Entity<EntityType, T>) {
        entities.remove(entity)
    }
}