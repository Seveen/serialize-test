import kotlinx.serialization.Serializable
import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Context
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType

@Serializable
data class GameContext(val defender: Entity<EntityType, GameContext>) : Context

@Serializable
data class Human(override val name: String, override val description: String) : EntityType

@Serializable
class LifeStats(var hp: Int) : Attribute

@Serializable
class DmgStats(var dmg: Int) : Attribute

interface TargetedAction<S : EntityType, T : EntityType> : Command<S, GameContext> {
    val target: Entity<EntityType, GameContext>

    operator fun component1() = context
    operator fun component2() = source
    operator fun component3() = target
}

data class Attack(
    override val context: GameContext, override val source: Entity<EntityType, GameContext>,
    override val target: Entity<EntityType, GameContext>, val dmg: Int
) :
    TargetedAction<EntityType, EntityType>

object Hurtable : BaseFacet<GameContext>(LifeStats::class) {
    override suspend fun executeCommand(command: Command<out EntityType, GameContext>) =
        command.responseWhenCommandIs(Attack::class) { (_, attacker, target, dmg) ->
            println("$attacker hits $target for $dmg! It hurts! ")
            val life = target.findAttribute(LifeStats::class).get()
            life.hp -= dmg
            println("${target.name} has ${life.hp} hp left.")
            Consumed
        }
}

class Bully() : BaseBehavior<GameContext>(DmgStats::class) {
    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        val target = context.defender
        val dmgStats = entity.findAttribute(DmgStats::class).get()
        target.sendCommand(Attack(context, entity, target, dmgStats.dmg))

        return true
    }
}
