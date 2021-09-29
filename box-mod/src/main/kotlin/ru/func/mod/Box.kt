package ru.func.mod

import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.utility.Rotation
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.WHITE
import ru.cristalix.uiengine.utility.rectangle

const val NAMESPACE = "box"
const val FILE_STORE = "http://51.38.128.132/"

class Box : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        RewardManager()

        // Загрузка фотографий
        loadTextures(
            load("box.png", "08832C088F83D8890128127"),
        ).thenRun {
            val banner = Context3D(V3(-250.0, 122.0, 38.0))

            banner.addChild(rectangle {
                textureLocation = ResourceLocation.of(NAMESPACE, "box.png")
                size = V3(250.0, 120.0, 1.0)
                color = WHITE
                rotation = Rotation(Math.PI, 0.0, 1.0, -0.1)
            })

            UIEngine.worldContexts.add(banner)
        }
    }
    private fun load(path: String, hash: String): RemoteTexture {
        return RemoteTexture(ResourceLocation.of(NAMESPACE, path), hash)
    }
}