import dev.xdark.clientapi.event.render.*
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.*

class App : KotlinMod() {

    private var waiting = true

    private val container = rectangle {
        origin = BOTTOM
        align = BOTTOM
        size = V3(75.0, 60.0)
        offset.y -= size.y
        color = Color(0, 0, 0, 0.62)
    }

    private val title = text {
        origin = CENTER
        align = CENTER
        content = "Команда синих"
    }

    private val box = rectangle {
        origin = BOTTOM
        align = BOTTOM
        offset.y += 100
        size = V3(75.0, 20.0)
        color = Color(100, 100, 100, 1.0)
        addChild(title, container)
    }

    override fun onEnable() {
        UIEngine.initialize(this)

        UIEngine.overlayContext.addChild(box)

        registerHandler<HealthRender> { isCancelled = waiting }
        registerHandler<ExpBarRender> { isCancelled = waiting }
        registerHandler<HungerRender> { isCancelled = waiting }
        registerHandler<ArmorRender> { isCancelled = waiting }
        registerHandler<VehicleHealthRender> { isCancelled = waiting }

        registerChannel("box:start") {
            waiting = false
        }
    }
}